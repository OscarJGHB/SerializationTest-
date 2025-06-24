package org.BookManager.gui;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.BookManager.Book;

import java.util.ArrayList;

public class BookTableClass extends TableView<Book> {
    private TableColumn<Book, String> titleCol;
    private TableColumn<Book, String> authorCol;
    private TableColumn<Book,String> yearCol;
    private TableColumn<Book, String> genreCol;
    private TableColumn<Book, String> pictureCol;

    @SuppressWarnings("unchecked")
    public BookTableClass() {
        super();

        titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));

        authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));

        yearCol = new TableColumn<>("Year");
        yearCol.setCellValueFactory(new PropertyValueFactory<>("year"));

        genreCol = new TableColumn<>("Genre");
        genreCol.setCellValueFactory(new PropertyValueFactory<>("genre"));

        pictureCol = new TableColumn<>("Picture");
        pictureCol.setCellValueFactory(new PropertyValueFactory<>("pictures"));

        this.getColumns().addAll(titleCol, authorCol, yearCol, genreCol, pictureCol);

    }

    public void toggleTableVisOn(){
        this.setVisible(true);
        this.setDisable(false);
    }

    public void toggleTableVisOff(){
        this.setVisible(false);
        this.setDisable(true);
    }

}
