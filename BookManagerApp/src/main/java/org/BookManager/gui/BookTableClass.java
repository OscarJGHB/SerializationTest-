package org.BookManager.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.BookManager.Book;

import java.io.IOException;
import java.util.ArrayList;

public class BookTableClass extends TableView<Book> {

    private TableColumn<Book, String> titleCol;
    private TableColumn<Book, String> authorCol;
    private TableColumn<Book,String> yearCol;
    private TableColumn<Book, String> genreCol;
    private TableColumn<Book, String> pictureCol;

    private ContextMenu rowContextMenu = new ContextMenu();

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

        this.setRowFactory(tv -> {
            TableRow<Book> row = new TableRow<>();
            row.addEventFilter(MouseEvent.MOUSE_CLICKED,event -> {
                if (row.getItem() != null ) {
                    if(event.getButton() == MouseButton.SECONDARY) {
                        rowContextMenuSet(row,event.getScreenX(),event.getScreenY());

                    }
                }
            });
            return row;
        });
    }

    private void rowContextMenuSet(TableRow<Book> row, double x, double y) {
        //gets book from the event
        rowContextMenu.setMinWidth(100);

        MenuItem editItem = new MenuItem("Edit");
        editItem.setOnAction(event -> {
            ObservableList<Book> books = this.getItems();
            Book bookToBeAdded = bookForm(row.getItem(),editItem.getText());
            if(bookToBeAdded != null) {
                books.remove(row.getItem());
                books.add(bookToBeAdded);
            }
        });
        rowContextMenu.getItems().clear();
        rowContextMenu.getItems().addAll(editItem);
        rowContextMenu.show(row,x,y);
    }

    //TODO make function elsewhere to return controller instead
    private Book bookForm(Book book, String instruction){
        Parent root;
        FXMLLoader loader = null;
        try {
            loader = new FXMLLoader(getClass().getClassLoader().getResource("org/BookManager/gui/BookForm.fxml"));
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        BookFormController controller = (BookFormController)loader.getController();

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("org/BookManager/gui/styles.css").toExternalForm());
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(instruction);
        controller.setTextFields(book);
        stage.showAndWait();

        return controller.getAcceptedBookObj();
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
