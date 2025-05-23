package org.openjfx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.Book.Book;
import org.Book.LibraryIO;

import java.io.File;

public class FXMLController {

    @FXML
    private Label label;
    @FXML
    private Button button0;
    @FXML
    private TableView<Book> bookTable;
    @FXML
    private TextField textField0;
    @FXML
    private TableColumn<Book, String> titleCol;
    @FXML
    private TableColumn<Book, String> authorCol;
    @FXML
    private TableColumn<Book,String> yearCol;
    @FXML
    private TableColumn<Book, String> genreCol;
    @FXML
    private TableColumn<Book, String> pictureCol;

    public void initialize() {
        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        label.setText("Hello, JavaFX " + javafxVersion + "\nRunning on Java " + javaVersion + ".");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        yearCol.setCellValueFactory(new PropertyValueFactory<>("year"));
        genreCol.setCellValueFactory(new PropertyValueFactory<>("genre"));
        pictureCol.setCellValueFactory(new PropertyValueFactory<>("pictures"));
    }

    public void button0Task(){
        String fileName = textField0.getText().toLowerCase().trim();
        ObservableList<Book> books = null;
        try {
            if(fileName.endsWith(".csv")){
                books = FXCollections.observableList(new ArrayList<>(LibraryIO.deserializeFromCSV(new File(fileName))));
            }
            else if(fileName.endsWith(".xml")){
                books = FXCollections.observableList(new ArrayList<>(LibraryIO.deserializeFromXML(new File(fileName))));
            }
            if(books == null)
                throw new IllegalArgumentException();
        } catch (IllegalArgumentException e) {
            label.setText(e.getMessage());
            return;
        }


        bookTable.setItems(books);


    }
}