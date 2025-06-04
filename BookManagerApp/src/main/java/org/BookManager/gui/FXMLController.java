package org.BookManager.gui;
import javafx.stage.Stage;
import org.BookManager.Book;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.File;
import java.util.ArrayList;

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
    @F
    private TableColumn<Book, String> authorCol;
    @FXML
    private TableColumn<Book,String> yearCol;
    @FXML
    private TableColumn<Book, String> genreCol;
    @FXML
    private TableColumn<Book, String> pictureCol;
    private Stage stage;

    public void setStage(Stage stage){
        this.stage = stage;
    }
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
                books = FXCollections.observableList(new ArrayList<>(Book.deserializeFromCSV(new File("../"+fileName))));
            }
            else if(fileName.endsWith(".xml")){
                books = FXCollections.observableList(new ArrayList<>(Book.deserializeFromXML(new File("../"+fileName))));
            }
        } catch (IllegalArgumentException e) {
            label.setText(e.getMessage());
            return;
        }
        bookTable.setItems(books);
    }
}