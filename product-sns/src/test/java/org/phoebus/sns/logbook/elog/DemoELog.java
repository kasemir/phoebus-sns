package org.phoebus.sns.logbook.elog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.phoebus.security.tokens.SimpleAuthenticationToken;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class DemoELog extends Application
{
    
    private TextArea textArea;
    
    private class CredentialDialog extends Dialog<SimpleAuthenticationToken>
    {
        private final TextField     username;
        private final PasswordField password;
        
        public CredentialDialog()
        {
            super();
            
            setTitle("Log In");
            
            GridPane grid = new GridPane();
            
            username = new TextField();
            password = new PasswordField();
            
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(10));
            
            grid.add(new Label("Username: "), 0, 0);
            grid.add(username, 1, 0);
            grid.add(new Label("Password"), 0, 1);
            grid.add(password, 1, 1);
            
            getDialogPane().setContent(grid);
            getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
            
            this.setResultConverter(button -> 
            {
                return button == ButtonType.OK ? new SimpleAuthenticationToken(username.getText(), password.getText()) : null;
            });
        }
    }
    
    private String url = "???", user = "???", password = "???";
    
    /** Demonstrate ELog.getLogbooks() by listing all the current log books. */
    private void DemoELogListLogbooks() throws Exception
    {
        try
        (
            ELog elog = new ELog(url, user, password);
        )
        {
            textArea.appendText("Current Logbooks:\n");
            for (String logbook : elog.getLogbooks())
            {
               textArea.appendText("\t" + logbook + "\n");
            }
            textArea.appendText("\n");
        }
    }
    
    /** Demonstrate ELog.getCategories by printing all the current categories. */
    private void DemoELogGetCategories() throws Exception
    {
        try
        (
            ELog elog = new ELog(url, user, password);
        )
        {
            textArea.appendText("Current Log Categories:\n");
            for (ELogCategory category : elog.getCategories())
            {
                textArea.appendText("\t" + category.toString() + "\n");
            }
            textArea.appendText("\n");
        }
    }
    
    /** Demonstrate ELog.getEntries() by getting the last hour's log entries. */
    private void DemoELogGetEntries() throws Exception
    {

        try
        (
            ELog elog = new ELog(url, user, password);
        )
        {
            // Get dates for now and an hour ago.
            Long hourAgoSeconds = (long) 3600;
            
            Instant now     = Instant.now();
            Instant hourAgo = Instant.ofEpochSecond(now.getEpochSecond() - hourAgoSeconds);
   
            Date start = Date.from(hourAgo);
            Date end   = Date.from(now);
            
            // Get all log entries from the last hour.
            List<ELogEntry> entries = elog.getEntries(start, end);
            
            textArea.appendText("The last hour's log entries: \n");
            for (ELogEntry entry : entries)
            {
                textArea.appendText("\t" + entry.toString() + "\n");
            }
        }
    }

    /** Demonstrate ELog.createEntry() by creating an entry in the "Scratch Pad" log book. */
    /*
    private void DemoELogCreateEntry() throws Exception
    {
        try
        (
            ELog elog = new ELog(url, user, password);
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        )
        {
            System.out.print("Please enter message to send as a test to the Scratch Pad Logbook: ");
            final String testMessage = reader.readLine();
            
            System.out.println("\nSending message \"" + testMessage +"\" to Scratch Pad.\n");
            
            System.out.print("Type \"send\" to confirm log entry creation: ");
            final String confirm = reader.readLine();
            System.out.println();
            if (confirm.equalsIgnoreCase("send"))
            {
                    elog.createEntry("Scratch Pad", "Test", testMessage, ELogPriority.Normal);
                    System.out.println("Entry created.");
            }
            else
                System.out.println("Entry creation canceled.");
        }
    }
    */
    
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        textArea = new TextArea();
        primaryStage.setTitle("Demo ELog");
        primaryStage.setScene(new Scene(textArea, 700, 1000));
        primaryStage.show();
        try
        {
            /**
             * Credential File "test_cred" is a file containing the following.
             * 
             * # URL of relational database.
             * url=url_to_rdb
             * 
             * Any lines beginning with # will be ignored.
             * The terms after the "=" should be replaced with the user's specific information.
             * 
             * It is expected to be located in the user's home directory.
             */
            
            File credentialFile = new File(System.getProperty("user.home") + "/test_cred");
            CredentialDialog cd = new CredentialDialog();
            Optional<SimpleAuthenticationToken> result = cd.showAndWait();
            if (result.isPresent())
            {
                user = result.get().getUsername();
                password = result.get().getPassword();
            }
            else
            {
                System.exit(0);
            }
            
            try
            (
                FileReader fReader = new FileReader(credentialFile);
                BufferedReader reader = new BufferedReader(fReader);
            )
            {
                String line = null;
                while ( null != (line = reader.readLine()) )
                {
                    if (line.startsWith("#") || line.isEmpty())
                        continue;
                    
                    if (line.startsWith("url="))
                    {
                        url = line.substring(4);
                    }
                }
            }

            DemoELogListLogbooks();
            DemoELogGetCategories();
            DemoELogGetEntries();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
    
    public static void main(String[] args)
    {
        launch(args);
    }

}
