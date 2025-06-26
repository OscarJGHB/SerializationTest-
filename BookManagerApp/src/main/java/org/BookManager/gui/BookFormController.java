package org.BookManager.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.BookManager.Book;

import java.io.File;

public class BookFormController {

    private Book acceptedBookObj = null;

    @FXML
    private Label coverAdded;
    @FXML
    private String coverFilePath = "";
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
    private Button addPictureButton;
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
        Book book = new Book(title, author,genre,year, picture, coverFilePath);


        //if book isn't blank, it will be set. Otherwise, it will be null when acceptedBookObj is requested
        if(!book.equals(new Book())) {
            acceptedBookObj = book;
        }
        ((Stage)submitButton.getScene().getWindow()).close();

    }

    @FXML
    private void handleAddPictureButtonAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose file");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.gif")
        );
        File file = fileChooser.showOpenDialog(((Button)event.getSource()).getScene().getWindow());
        if(file != null) {
            this.coverFilePath = file.getAbsolutePath();
            coverAdded.setText("Cover: coverFilePath");
        }

    }

    public void setTextFields(Book book) {
        titleField.setText(book.getTitle());
        authorField.setText(book.getAuthor());
        genreField.setText(book.getGenre());
        pictureField.setText(String.valueOf(book.getPictures()));
        yearField.setText(String.valueOf(book.getYear()));

    }

    public Book getAcceptedBookObj() {
        return acceptedBookObj;
    }
}
