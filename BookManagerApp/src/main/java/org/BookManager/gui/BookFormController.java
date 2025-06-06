package org.BookManager.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.BookManager.Book;

public class BookFormController {

    private Book acceptedBookObj = null;

    @FXML
    private Label instruction;
    @FXML
    private TextField titleField;
    @FXML
    private TextField authorField;
    @FXML
    private TextField yearField;
    @FXML
    private TextField genreField;
    @FXML
    private TextField pictureField;
    @FXML
    private Button submitButton;

    public void initialize() {
    //
    }

    @FXML
    private void handleSubmitButtonAction(ActionEvent event) {
        String title = titleField.getText();
        String author = authorField.getText();
        String genre = genreField.getText();
        int year;
        try {
            year = Integer.parseInt(yearField.getText());
        }
        catch (NumberFormatException e) {
            year = -1;
        }
        boolean picture = Boolean.parseBoolean(pictureField.getText());
        Book book = new Book(title, author,genre,year, picture);
        if(!book.equals(new Book())) {
            acceptedBookObj = book;
        }
        ((Stage)submitButton.getScene().getWindow()).close();

    }

    public Book getAcceptedBookObj() {
        return acceptedBookObj;
    }

    public void setInstruction(String instruction) {
        this.instruction.setText(instruction);

    }
}
