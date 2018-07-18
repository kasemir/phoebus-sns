package org.phoebus.sns.logbook;

import java.util.Collection;
import java.util.Optional;

import org.phoebus.logbook.LogClient;
import org.phoebus.logbook.LogEntry;
import org.phoebus.logbook.LogEntryImpl.LogEntryBuilder;
import org.phoebus.logbook.LogFactory;
import org.phoebus.logbook.LogService;
import org.phoebus.logbook.Logbook;
import org.phoebus.logbook.LogbookImpl;
import org.phoebus.logbook.Tag;
import org.phoebus.security.tokens.SimpleAuthenticationToken;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;

public class DemoSNSLogClient extends Application
{
    private final static LogService logService = LogService.getInstance();
    private final static LogFactory logFactory = logService.getLogFactories().get("org.phoebus.sns.logbook");
    private static LogClient logClient;
    
    private VBox     vbox;
    private TextArea textArea;
    private Button   makeScratchPadEntry;
    
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
            grid.add(new Label("Password: "), 0, 1);
            grid.add(password, 1, 1);
            
            getDialogPane().setContent(grid);
            getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
            
            this.setResultConverter(button -> 
            {
                return button == ButtonType.OK ? new SimpleAuthenticationToken(username.getText(), password.getText()) : null;
            });
        }
    }
    
    private class TitleTextDialog extends Dialog<Pair<String, String>>
    {
        private final TextField title;
        private final TextArea  body;
        
        public TitleTextDialog()
        {
            super();
            
            setTitle("Make ScratchPad Entry");
            
            title = new TextField();
            body  = new TextArea();
            
            GridPane grid = new GridPane();
            
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(10));
            
            grid.add(new Label("Title: "), 0, 0);
            grid.add(title, 1, 0);
            grid.add(new Label("Body: "), 0, 1);
            grid.add(body, 1, 1);
            
            getDialogPane().setContent(grid);
            getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
            
            this.setResultConverter(button -> 
            {
                return button == ButtonType.OK ? new Pair<String, String>(title.getText(), body.getText()) : null;
            });
        }
    }
    
    private String username = "???", password = "???";
    
    /** Demonstrate ELog.getLogbooks() by listing all the current log books. */
    private void DemoELogListLogbooks() throws Exception
    {
        Collection<Logbook> logbooks = logClient.listLogbooks();
        
        textArea.appendText("Current Logbooks:\n");
        
        for (Logbook logbook : logbooks)
        {
           textArea.appendText("\t" + logbook + "\n");
        }
        
        textArea.appendText("\n");
    }
    
    /** Demonstrate ELog.getCategories by printing all the current categories. */
    private void DemoELogGetCategories() throws Exception
    {
       Collection<Tag> tags = logClient.listTags();
       
       textArea.appendText("Current Tags:\n");
       
       for (Tag tag : tags)
       {
           textArea.appendText("\t" + tag.getName() + "\n");
       }
       
       textArea.appendText("\n");
    }
    
    /** Demonstrate ELog.getEntries() by getting the last hour's log entries. */
    private void DemoELogGetEntries() throws Exception
    {
        
        Collection<LogEntry> logEntries = logClient.listLogs();
        
        textArea.appendText("The last hour's log entries: \n");
        
        for (LogEntry entry : logEntries)
        {
            textArea.appendText("\t" + entry.toString() + "\n");
        }
        
    }

    /** Demonstrate ELog.createEntry() by creating an entry in the "Scratch Pad" log book. */
    
    private void createEntry()
    {
        
        TitleTextDialog entryDialog = new TitleTextDialog();
        Optional<Pair<String, String>> result = entryDialog.showAndWait();
        
        if (result.isPresent())
        {
            String title = result.get().getKey();
            String body  = result.get().getValue();
            
            LogEntryBuilder logEntryBuilder = new LogEntryBuilder();
            Logbook scratchPad = LogbookImpl.of("Scratch Pad");
            LogEntry logEntry = logEntryBuilder
                                    .title(title)
                                    .description(body)
                                    .appendToLogbook(scratchPad)
                                    .build();
            logClient.set(logEntry);
        }
    }
    
    
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        vbox                = new VBox();
        makeScratchPadEntry = new Button("Make Log Entry in ScratchPad");
        textArea            = new TextArea();
        
        
        VBox.setVgrow(textArea, Priority.ALWAYS);
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(10));
        //textArea.setPrefHeight(Double.MAX_VALUE);
        vbox.setPrefHeight(Double.MAX_VALUE);
        makeScratchPadEntry.setPrefWidth(Double.MAX_VALUE);
        vbox.getChildren().addAll(makeScratchPadEntry, textArea);
        
        primaryStage.setTitle("Demo SNSLogClient");
        primaryStage.setScene(new Scene(vbox, 1000, 1200));
        primaryStage.show();
        
        try
        {
            CredentialDialog cd = new CredentialDialog();
            
            Optional<SimpleAuthenticationToken> result = cd.showAndWait();
            
            if (result.isPresent())
            {
                username = result.get().getUsername();
                password = result.get().getPassword();
            }
            else
            {
                System.exit(0);
            }
            
            if (username.isEmpty() || password.isEmpty())
            {
                textArea.appendText("User name or password is empty.");
            }
            else
            {
                makeScratchPadEntry.setOnAction(action -> createEntry());
                logClient = logFactory.getLogClient(new SimpleAuthenticationToken(username, password));
            
                DemoELogListLogbooks();
                DemoELogGetCategories();
                DemoELogGetEntries();
            }
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
