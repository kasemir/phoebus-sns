/*******************************************************************************
 * Copyright (c) 2010-2022 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.phoebus.sns.logbook.elog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.phoebus.framework.rdb.RDBConnectionPool;
import org.phoebus.framework.util.IOUtils;

import oracle.jdbc.OracleTypes;

/** SNS 'ELog' support
 * 
 *  Note mismatch between SNS logbook and generic logbook API.
 *  The generic API allows adding "Tags" to any entry.
 *  The SNS logbook started out with "Categories" which were like Tags.
 *  But by now the same category name ("Electrical") may be used with more
 *  than one ID ("ELEC", "FAC10", ...),
 *  and that ID may then be used with more than one logbook.
 *  
 *  In principle, a category should only be used with those more-than-one but
 *  specific logbooks, but we cannot enforce that in the generic GUI.
 *  We list categories as tags named "logbook : category" and user should only
 *  select tags that match the selected logbook.
 *  When we submit the tag, we look for one that has the correct category as well
 *  as logbook name, but if that fails, we submit just by matching category name,
 *  since that shows up in the logbook web page. 
 *  
 *  @author Delphy Nypaver Armstrong - Original version
 *  @author Kay Kasemir
 *  @author Evan Smith - Adapted to use RDBCollectionPool
 */
@SuppressWarnings("nls")
public class ELog implements Closeable
{
    /**
     *  NOTE:
     *
     *  When requesting connections, it is important to keep the Connection out of try with resource
     *  blocks. The RDBConnectionPool keeps all the connections in its pool open and expects the connection
     *  to be released, _not_ closed, when it is done being used. The connections in the pool are all closed
     *  when the RDBCollectionPool.clear() method is called.
     *
     *  If the connections are put in try with resource blocks they will be closed automatically,
     *  which the connection pool does not expect.
     */
    final private RDBConnectionPool rdb;

    /** Maximum allowed size for title and text entry */
    final private int MAX_TITLE_SIZE, MAX_TEXT_SIZE;

    private static final String DEFAULT_BADGE_NUMBER = "999992"; //$NON-NLS-1$
    final private String badge_number;

    private static List<ELogbook> logbooks = null;

    private static List<ELogCategory> categories = null;

    /** Initialize
     *  @param url  RDB URL
     *  @param user User (for which we'll try to get the badge number)
     *  @param password Password
     *  @throws Exception on error
     */
    public ELog(final String url, final String user, final String password)
            throws Exception
    {
        try
        {
            this.rdb = new RDBConnectionPool(url, user, password);
            badge_number = getBadgeNumber(user);
        }
        catch (Exception ex)
        {
            throw new Exception("Cannot connect, check user/password or network", ex);
        }
        MAX_TITLE_SIZE = getMaxEntryColumnLength("TITLE");
        MAX_TEXT_SIZE = getMaxEntryColumnLength("CONTENT");
        // Only initialize logbooks and categories once per JVM
        synchronized (ELog.class)
        {
            if (logbooks == null)
                logbooks = readLogbooks();
            if (categories == null)
                categories = readCategories();
        }
    }

    /** Get the badge number for the user in the connection dictionary
     *
     *  @param user user id of person logging in.
     *  @return the badge number for the specified user or a default
     *  @throws Exception
     */
    private String getBadgeNumber(final String user) throws Exception
    {
        final Connection connection = rdb.getConnection();
        try
        (
            final PreparedStatement statement = connection.prepareStatement(
               "SELECT bn FROM OPER.EMPLOYEE_V WHERE user_id=?");
        )
        {
            // OPER.EMPLOYEE_V seems to only keep uppercase user_id entries
            statement.setString(1, user.trim().toUpperCase());
            statement.execute();
            final ResultSet result = statement.getResultSet();
            if (result.next())
            {
                final String badge = result.getString("bn");
                if (badge.length() > 1)
                    return badge;
            }
            // No error, but also not found: fall through
        }
        finally
        {
            rdb.releaseConnection(connection);
        }

        return DEFAULT_BADGE_NUMBER;
    }

    /** Query the RDB for the max length of an entry's column
     * @param column Name of the column (uppercase)
     * @throws Exception
     * @return Content length specified in the RDB
     */
    private int getMaxEntryColumnLength(final String column) throws Exception
    {
        final Connection connection = rdb.getConnection();
        final ResultSet tables = connection.getMetaData()
                .getColumns(null, "LOGBOOK", "LOG_ENTRY", column);
        if (!tables.next())
            throw new Exception("Unable to locate LOGBOOK.LOG_ENTRY." + column);
        final int max_elog_text = tables.getInt("COLUMN_SIZE");
        rdb.releaseConnection(connection);

        return max_elog_text;
    }

    /** Read available logbooks from RDB
     *  @return Available logbooks
     *  @throws Exception on error
     */
    private List<ELogbook> readLogbooks() throws Exception
    {
        final List<ELogbook> logbooks = new ArrayList<>();
        final Connection connection = rdb.getConnection();
        try
        (
            final PreparedStatement statement = connection.prepareStatement(
                "SELECT logbook_nm, logbook_id FROM LOGBOOK.logbook_v");
        )
        {
            final ResultSet result = statement.executeQuery();
            while (result.next())
                logbooks.add(new ELogbook(result.getString(1), result.getString(2)));
        }
        finally
        {
            rdb.releaseConnection(connection);
        }

        return logbooks;
    }

    /** Locate logbook by name
     *  @param name Name of logbook
     *  @return {@link ELogbook}
     *  @throws Exception if no known logbook for that name
     */
    private ELogbook findLogbook(final String name) throws Exception
    {
        for (ELogbook logbook : logbooks)
            if (logbook.getName().equals(name))
                return logbook;
        throw new Exception("Unknown logbook '" + name + "'");
    }

    /** List available logbooks
     *  @return List of known logbooks
     */
    public List<String> getLogbooks()
    {
        final List<String> books = new ArrayList<>(logbooks.size());
        for (ELogbook logbook : logbooks)
            books.add(logbook.getName());
        return books;
    }

    /** Read available logbook categories from RDB
     *  @return Available categories
     *  @throws Exception on error
     */
    private List<ELogCategory> readCategories() throws Exception
    {
        final List<ELogCategory> tags = new ArrayList<>();
        final Connection connection = rdb.getConnection();
        try
        (
            final Statement statement = connection.createStatement();
        )
        {
            final ResultSet result = statement.executeQuery(
                    "SELECT lc.logbook_id, l.logbook_nm, lc.cat_id, c.cat_nm FROM logbook.logbook_log_categories_v lc" +
                    " JOIN logbook.log_categories_v c ON lc.cat_id = c.cat_id" +
                    " JOIN logbook.logbook_v l        ON l.logbook_id = lc.logbook_id");
            while (result.next())
            {
                final String category_id = result.getString(3);
                final String logbook_name = result.getString(2);
                final String category_name = result.getString(4);
                final ELogCategory cat = new ELogCategory(category_id, logbook_name, category_name);
                tags.add(cat);
            }
        }
        rdb.releaseConnection(connection);

        return tags;
    }

    /** @return supported categories */
    public List<ELogCategory> getCategories()
    {
        return categories;
    }

    /** Read logbook entry
     *  @param entry_id Log entry ID
     *  @return {@link ELogEntry}
     *  @throws Exception on error
     */
    public ELogEntry getEntry(final long entry_id) throws Exception
    {
        final ELogPriority prio;
        final String user;
        final Date date;
        final String title;
        final String text;

        final Connection connection = rdb.getConnection();

        try
        (
            final PreparedStatement statement = connection.prepareStatement(
                "SELECT e.log_entry_id, p.prior_nm, d.pref_first_nm, d.pref_last_nm," +
                "  e.orig_post, e.title, e.content " +
                " FROM LOGBOOK.log_entry e" +
                " LEFT JOIN oper.employee_v d ON d.bn = e.bn" +
                " JOIN LOGBOOK.log_entry_prior p ON p.prior_id = e.prior_id" +
                " WHERE (e.pub_stat_id = 'P' OR e.pub_stat_id IS NULL)" +
                " AND e.log_entry_id = ?");
        )
        {
            statement.setLong(1, entry_id);
            final ResultSet result = statement.executeQuery();
            if (! result.next())
                return null;
            prio = ELogPriority.forName(result.getString(2));
            user = result.getString(3) + " " + result.getString(4);
            date = new Date(result.getTimestamp(5).getTime());
            title = result.getString(6);
            text = result.getString(7);
        }
        finally
        {
            rdb.releaseConnection(connection);
        }

        final List<String> logbooks = getLogbooks(entry_id);
        final List<ELogCategory> categories = getCategories(entry_id);

        // Get attachments
        final List<ELogAttachment> images = getImageAttachments(entry_id);
        final List<ELogAttachment> attachments = getOtherAttachments(entry_id);

        // Return entry
        return new ELogEntry(entry_id, prio, user, date, title, text, logbooks, categories, images, attachments);
    }

    /** Read logbook entries
     *  @param start Start date
     *  @param end End date
     *  @return List of {@link ELogEntry}
     *  @throws Exception on error
     */
    public List<ELogEntry> getEntries(final Date start, final Date end) throws Exception
    {
        final List<ELogEntry> entries = new ArrayList<>();
        final Connection connection = rdb.getConnection();
        try
        (
            final PreparedStatement statement = connection.prepareStatement(
                "SELECT e.log_entry_id, p.prior_nm, d.pref_first_nm, d.pref_last_nm," +
                "  e.orig_post, e.title, e.content " +
                " FROM LOGBOOK.log_entry e" +
                " LEFT JOIN oper.employee_v d ON d.bn = e.bn" +
                " JOIN LOGBOOK.log_entry_prior p ON p.prior_id = e.prior_id" +
                " WHERE (e.pub_stat_id = 'P' OR e.pub_stat_id IS NULL)" +
                " AND e.orig_post BETWEEN ? AND ?" +
                " ORDER BY e.orig_post DESC");
        )
        {
            statement.setTimestamp(1, new java.sql.Timestamp(start.getTime()));
            statement.setTimestamp(2, new java.sql.Timestamp(end.getTime()));
            final ResultSet result = statement.executeQuery();
            rdb.releaseConnection(connection);
            while (result.next())
            {
                final long entry_id = result.getLong(1);
                final ELogPriority prio = ELogPriority.forName(result.getString(2));
                final String user = result.getString(3) + " " + result.getString(4);
                final Date date = new Date(result.getTimestamp(5).getTime());
                final String title = result.getString(6);
                final String text = result.getString(7);
                final List<String> logbooks = getLogbooks(entry_id);
                final List<ELogCategory> categories = getCategories(entry_id);
                final List<ELogAttachment> images = getImageAttachments(entry_id);
                final List<ELogAttachment> attachments = getOtherAttachments(entry_id);
                entries.add(new ELogEntry(entry_id, prio, user, date, title, text, logbooks, categories, images, attachments));
            }
        }

        return entries;
    }

    /** @param entry_id Log entry ID
     *  @return Names of logbooks for this entry
     *  @throws Exception on error
     */
    private List<String> getLogbooks(final long entry_id) throws Exception
    {
        final List<String> logbooks = new ArrayList<>();
        final Connection connection = rdb.getConnection();
        try
        (
            final PreparedStatement statement = rdb.getConnection().prepareStatement(
                "SELECT b.logbook_nm" +
                " FROM LOGBOOK.entry_logbook e" +
                " JOIN LOGBOOK.logbook_v b ON e.logbook_id = b.logbook_id" +
                " AND e.log_entry_id = ?");
        )
        {
            statement.setLong(1, entry_id);
            final ResultSet result = statement.executeQuery();
            while (result.next())
                logbooks.add(result.getString(1));
            result.close();
        }
        finally
        {
            rdb.releaseConnection(connection);
        }

        return logbooks;
    }

    /** @param entry_id Log entry ID
     *  @return Categories used for this entry
     *  @throws Exception on error
     */
    private List<ELogCategory> getCategories(final long entry_id) throws Exception
    {
        final List<ELogCategory> logbooks = new ArrayList<>();
        final Connection connection = rdb.getConnection();
        try
        (
            // Example entry:
            // SELECT e.cat_id, le.logbook_id, lb.logbook_nm, c.cat_nm
            // FROM LOGBOOK.LOG_ENTRY_CATEGORIES e
            // JOIN LOGBOOK.entry_logbook   le ON le.log_entry_id = e.log_entry_id
            // JOIN LOGBOOK.logbook_v       lb ON lb.logbook_id = le.logbook_id
            // JOIN LOGBOOK.log_categories_v c ON e.cat_id = c.cat_id
            // WHERE e.log_entry_id = 756476;
            final PreparedStatement statement = connection.prepareStatement(
                    "SELECT e.cat_id, lb.logbook_nm, c.cat_nm" +
                    " FROM LOGBOOK.LOG_ENTRY_CATEGORIES e" +
                    " JOIN LOGBOOK.entry_logbook   le ON le.log_entry_id = e.log_entry_id" +
                    " JOIN LOGBOOK.logbook_v       lb ON lb.logbook_id = le.logbook_id" +
                    " JOIN LOGBOOK.log_categories_v c ON e.cat_id = c.cat_id" +
                    " WHERE e.log_entry_id = ?");
        )
        {
            statement.setLong(1, entry_id);
            final ResultSet result = statement.executeQuery();
            while (result.next())
                logbooks.add(new ELogCategory(result.getString(1), result.getString(2), result.getString(3)));
            result.close();
        }
        finally
        {
            rdb.releaseConnection(connection);
        }

        return logbooks;
    }

    /** Create entry
     *  @param logbook
     *  @param title
     *  @param text
     *  @param filenames
     *  @param captions
     *  @throws Exception
     *  @return Entry ID
     */
    public long createEntry(final String logbook, String title, String text, final ELogPriority priority) throws Exception
    {
        final long entry_id; // Entry ID from RDB

        if (title.length() >= MAX_TITLE_SIZE)
        {   // Shorten title.
            // If the title and body are actually the same, that's it.
            // Otherwise add title overflow to text.
            if (! title.equals(text))
                text = "..." + title.substring(MAX_TITLE_SIZE-4) + "\n" + text;
            title = title.substring(0, MAX_TITLE_SIZE-4) + "...";
        }

        if (text.length() < MAX_TEXT_SIZE)
        {
            // Short text goes into entry
            entry_id = createBasicEntry(logbook, title, text);
        }
        else
        {
            // If text is made into an attachment due to size restraints,
            // explain why there is an attachment
            final String info = "Input text exceeds " + MAX_TEXT_SIZE
                    + " characters, see attachment.";
            entry_id = createBasicEntry(logbook, title, info);

            // Attach text
            final InputStream stream = new ByteArrayInputStream(text.getBytes());
            // Add the text attachment to the elog
            addAttachment(entry_id, "FullEntry.txt", "Full Text", stream);
            stream.close();
        }

        final Connection connection = rdb.getConnection();
        try
        (
            final PreparedStatement statement = connection.prepareStatement(
                "UPDATE LOGBOOK.log_entry SET prior_id=? WHERE log_entry_id=?");
        )
        {
            statement.setInt(1, priority.getID());
            statement.setLong(2, entry_id);
            statement.executeQuery();
        }
        catch (Exception ex)
        {
            Logger.getLogger(getClass().getName()).log(Level.WARNING,
                "Cannot set priority of log entry " + entry_id + " to " + priority, ex);
        }
        finally
        {
            rdb.releaseConnection(connection);
        }

        return entry_id;
    }

    /** Create basic ELog entry with title and text, obtaining entry ID which
     *  would allow addition of attachments.
     *
     *  @param logbook Logbook
     *  @param title title of the elog entry
     *  @param text text for the elog entry
     *  @throws Exception on error
     */
    private long createBasicEntry(final String logbook, final String title, final String text)
            throws Exception
    {
        final Connection connection = rdb.getConnection();
        final long result;
        try
        (
            final CallableStatement statement = connection.prepareCall(
                "call logbook.logbook_pkg.insert_logbook_entry(?, ?, ?, ?, ?, ?)");
        )
        {
            statement.setString(1, badge_number);
            statement.setString(2, logbook);
            statement.setString(3, title);
            statement.setString(4, "");
            statement.setString(5, text);
            statement.registerOutParameter(6, OracleTypes.NUMBER);
            statement.executeQuery();

            result = statement.getLong(6);
        }
        finally
        {
            rdb.releaseConnection(connection);
        }

        return result;
    }

    /** Fetch type ID for image
     *  @param extension File extension of image file.
     *  @return image_type_id from the RDB, -1 if not found
     *  @throws Exception on error
     */
    private long fetchImageTypes(final String extension) throws Exception
    {
        final Connection connection = rdb.getConnection();
        try
        (
            final PreparedStatement statement = connection.prepareStatement(
                "SELECT image_type_id FROM LOGBOOK.IMAGE_TYPE WHERE ?=UPPER(file_extension)");
        )
        {
            statement.setString(1, extension.toUpperCase());
            return fetchLongResult(statement);
        }
        finally
        {
        rdb.releaseConnection(connection);
        }
    }

    /** Execute statement and return the first 'long' result
     *  @param statement Statement to execute
     *  @return First result, -1 if nothing was found
     *  @throws Exception on error
     */
    private long fetchLongResult(final PreparedStatement statement) throws Exception
    {
        try
        {
            final ResultSet result = statement.executeQuery();
            final long id;
            if (result.next())
                id = result.getLong(1);
            else
                id = -1;
            result.close();
            return id;
        }
        finally
        {
            statement.close();
        }
    }

    /** Fetch ID for attachment
     *  @param extension Attachment file extension.
     *  @return attachment_type_id from the RDB, -1 if not found
     *  @throws Exception on error
     */
    private long fetchAttachmentTypes(final String extension) throws Exception
    {
        Connection connection = rdb.getConnection();
        try
        (
            final PreparedStatement statement = connection.prepareStatement(
                "SELECT attachment_type_id FROM LOGBOOK.ATTACHMENT_TYPE WHERE ?=UPPER(file_extension)");
        )
        {
            statement.setString(1, extension.toUpperCase());
            return fetchLongResult(statement);
        }
        finally
        {
            rdb.releaseConnection(connection);
        }
    }

    /** Add another logbook reference to existing entry.
     *
     *  @param entry_id ID of entry to which to add a logbook reference
     *  @param logbook_name Name of the logbook where this entry should also appear
     *  @throws Exception on error
     */
    public void addLogbook(final long entry_id, final String logbook_name) throws Exception
    {
        final ELogbook logbook = findLogbook(logbook_name);
        final Connection connection = rdb.getConnection();
        try
        (
            final PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO LOGBOOK.ENTRY_LOGBOOK(log_entry_id, logbook_id) VALUES(?,?)");
        )
        {
            statement.setLong(1, entry_id);
            statement.setString(2, logbook.getId());
            statement.executeUpdate();
        }
        finally
        {
            rdb.releaseConnection(connection);
        }
    }

    /** Determine the type of attachment, based on file extension, and add it
     *  to the elog entry with the entry_id.
     *
     *  @param entry_id ID of entry to which to add this file
     *  @param fname input filename, must have a file extension
     *  @param caption Caption or 'title' for the attachment
     *  @param stream Stream with attachment content
     *  @return Attachment that was added
     *  @throws Exception on error
     */
    public ELogAttachment addAttachment(final long entry_id,
            final String fname, final String caption,
            final InputStream stream) throws Exception
    {
        // Determine file extension
        final int ndx = fname.lastIndexOf(".");
        if (ndx <= 0)
            throw new Exception("Attachment has no file extension: " + fname);
        final String extension = fname.substring(ndx + 1);

        // Determine file type ID used in RDB. First try image
        long fileTypeID = fetchImageTypes(extension);
        final boolean is_image = fileTypeID != -1;
        // Try non-image attachment
        if (! is_image)
            fileTypeID = fetchAttachmentTypes(extension);
        if (fileTypeID == -1)
            throw new Exception("Unsupported file type for '" + fname + "'");

        // Buffer the attachment data so that we can return it
        ByteArrayOutputStream data_buf = new ByteArrayOutputStream();
        IOUtils.copy(stream, data_buf);
        stream.close();
        final byte[] data = data_buf.toByteArray();
        data_buf.close();
        data_buf = null;

        // Submit to RDB
        final Connection connection = rdb.getConnection();
        try
        (
            final CallableStatement statement = connection.prepareCall(
                "call logbook.logbook_pkg.add_entry_attachment(?, ?, ?, ?, ?)");
        )
        {
            statement.setLong(1, entry_id);
            statement.setString(2, is_image ? "I" : "A");
            statement.setString(3, caption);
            statement.setLong(4, fileTypeID);
            statement.setBinaryStream(5, new ByteArrayInputStream(data));
            statement.executeQuery();
        }
        finally
        {
            rdb.releaseConnection(connection);
        }

        return new ELogAttachment(is_image, fname, caption, data);
    }

    /** Obtain image attachments
     *  @param entry_id Log entry ID
     *  @return Image Attachments
     *  @throws Exception on error
     */
    public List<ELogAttachment> getImageAttachments(final long entry_id) throws Exception
    {
        final List<ELogAttachment> images = new ArrayList<>();
        final Connection connection = rdb.getConnection();
        try
        (
            final PreparedStatement statement = connection.prepareStatement(
                "SELECT e.image_id, i.image_nm, t.image_type_nm, i.image_data" +
                        " FROM LOGBOOK.LOG_ENTRY_IMAGE e" +
                        " JOIN LOGBOOK.IMAGE i ON e.image_id = i.image_id" +
                        " JOIN LOGBOOK.IMAGE_TYPE t ON i.image_type_id = t.image_type_id" +
                " WHERE log_entry_id=?");
        )
        {
            statement.setLong(1, entry_id);
            final ResultSet result = statement.executeQuery();
            while (result.next())
            {
                final String name = result.getString(2);
                final String type = result.getString(3);
                final Blob blob = result.getBlob(4);
                final byte[] data = blob.getBytes(1, (int) blob.length());
                images.add(new ELogAttachment(true, name, type, data));
            }
        }
        finally
        {
            rdb.releaseConnection(connection);
        }

        return images;
    }

    /** Obtain non-image attachments
     *  @param entry_id Log entry ID
     *  @return Attachments
     *  @throws Exception on error
     */
    public List<ELogAttachment> getOtherAttachments(final long entry_id) throws Exception
    {
        final List<ELogAttachment> attachments = new ArrayList<>();
        final Connection connection = rdb.getConnection();
        try
        (
            final PreparedStatement statement = connection.prepareStatement(
                "SELECT e.attachment_id, a.attachment_nm, t.attachment_type_nm, a.attachment_data, t.file_extension" +
                        " FROM LOGBOOK.LOG_ENTRY_ATTACHMENT e" +
                        " JOIN LOGBOOK.ATTACHMENT a ON e.attachment_id = a.attachment_id" +
                        " JOIN LOGBOOK.ATTACHMENT_TYPE t ON a.attachment_type_id = t.attachment_type_id" +
                " WHERE log_entry_id=?");
        )
        {
            statement.setLong(1, entry_id);
            final ResultSet result = statement.executeQuery();
            while (result.next())
            {
                final String name = result.getString(2);
                final String type = result.getString(5);
                final Blob blob = result.getBlob(4);
                final byte[] data = blob.getBytes(1, (int) blob.length());
                attachments.add(new ELogAttachment(false, name, type, data));
            }
        }
        finally
        {
            rdb.releaseConnection(connection);
        }

        return attachments;
    }

    /** Add category to entry
     *  @param entry_id Log entry ID
     *  @param tag_name Name of tag to add
     *  @throws Exception on error
     */
    public void addCategory(final long entry_id, final String logbook_and_category) throws Exception
    {
        final String[] logbook_category_names = getCategoryFromLogbookCategoryString(logbook_and_category);
        final String tag_id = getTagID(logbook_category_names[0], logbook_category_names[1]);
        final Connection connection = rdb.getConnection();
        try
        (
            final PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO LOGBOOK.LOG_ENTRY_CATEGORIES(LOG_ENTRY_ID, CAT_ID)" +
                " VALUES(?, ?)");
        )
        {
            statement.setLong(1, entry_id);
            statement.setString(2, tag_id);
            statement.executeUpdate();
        }
        finally
        {
            rdb.releaseConnection(connection);
        }
    }

    /**
     * Get the category name from a string containing a category name and logbook name that are delimited by a colon.
     * @param logbook_and_category "Logbook : Category"
     * @return Array with logbook, category
     * @throws Exception if logbook name, category name, or delimiter are missing.
     */
    public static String[] getCategoryFromLogbookCategoryString(final String logbook_and_category) throws Exception
    {
        final String[] tokens = logbook_and_category.split(":");

        if (tokens.length < 2)
            throw new Exception("Expected 'Logbook : Category', got '" + logbook_and_category + "'");

        final String logbook_name  = tokens[0].trim();
        final String category_name = tokens[1].trim();

        if (logbook_name.isEmpty())
            throw new Exception("Empty Logbook in '" + logbook_and_category + "'");

        if (category_name.isEmpty())
            throw new Exception("Empty Category in '" + logbook_and_category + "'");

        return new String[] { logbook_name, category_name };
    }

    /** @param logbook_name Logbook name
     *  @param category_name Category name
     *  @return Category ID
     *  @throws Exception when category not known
     */
    private String getTagID(final String logbook_name, final String category_name) throws Exception
    {
        // The same category name might be defined with different IDs.
        // Check if there is an exact match of the cat.name for that logbook.
        // Otherwise use the last one found

        // System.out.println("Looking for " + logbook_name + " : " + category_name);

        String id = null;
        for (ELogCategory category : categories)
        {
            if (category.getCategory().equalsIgnoreCase(category_name))
            {
                id = category.getID();
                // System.out.println("Found " + id + " = " + category);

                if (category.getLogbook().equalsIgnoreCase(logbook_name))
                {
                    // System.out.println("Perfect match!");
                    return id;
                }
            }
        }
        if (id == null)
            throw new Exception("Unknown logbook category '" + category_name + "'");
        // Category name is known, albeit not for the requested logbook...
        return id;
    }

    /** Close RDB connection. Must be called when done using the logbook. */
    @Override
    public void close()
    {
        rdb.clear();
    }
}
