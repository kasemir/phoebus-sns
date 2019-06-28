package org.phoebus.applications.eliza;

import Eliza.ElizaMain;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

@SuppressWarnings("nls")
public class ElizaUI extends VBox
{
    private final ElizaMain eliza = new ElizaMain();
    private final TextFlow replies = new TextFlow();
    private final TextField entry = new TextField();

    public ElizaUI() throws Exception
    {
        eliza.readScript(ElizaMain.class.getResourceAsStream("/Eliza/script"));

        createComponents();
        getChildren().addAll(replies, entry);

        entry.setText("Hello!");
        onTextEntered();
    }

    private void createComponents()
    {
        replies.setPadding(new Insets(5));
        VBox.setVgrow(replies, Priority.ALWAYS);
        entry.setPromptText("Enter message");
        entry.setOnAction(event -> onTextEntered());

        showInfo();
    }

    private void showInfo()
    {
        Text text = new Text(
            "Welcome to Eliza, the AI Assistant!\n");
        text.setFont(Font.font(14));
        replies.getChildren().add(text);

        text = new Text(
            "\n" +
            "Eliza is a natural language processing program created by Joseph Weizenbaum in the 1960s at the MIT Artificial Intelligence Laboratory.\n" +
            "\n" +
            "Enter your message or question in the text field below, and Eliza will try to be of assistance.\n" +
            "\n");
        text.setFill(Color.DARKGRAY);
        replies.getChildren().add(text);
    }

    private void onTextEntered()
    {
        handle(entry.getText().trim());
        entry.clear();
        Platform.runLater(() -> entry.requestFocus());
    }

    private void handle(final String input)
    {
        // Show entered text
        Text text = new Text("> " + input + "\n");
        replies.getChildren().add(text);

        // Get reply
        final String reply = eliza.processInput(input);

        // Show the reply
        text = new Text(reply + "\n");
        text.setFill(Color.BLUE);
        replies.getChildren().add(text);
    }
}
