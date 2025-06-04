package org.BookManager.gui;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.BookManager.Book;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;

import java.io.IOException;


public class FXMLMenuController {
    @FXML
    private MenuBar menuBar;
    @FXML
    private Menu menu;
    @FXML
    private MenuItem menuItem1;
    @FXML
    private MenuItem menuItem2;
    @FXML
    private HBox dataContainer;
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        menu.setText("File");

        menuItem1.setText("New Library");
        menuItem2.setText("Import Library");

    }

    @FXML
    public void menuItem1Task(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("org/BookManager/gui/main.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getClassLoader().getResource("org/BookManager/gui/styles.css").toExternalForm());
            FXMLController controller = loader.getController();

            controller.setStage(stage);

            System.out.println("Root loaded: " + root);
            stage.setScene(scene);
            stage.setTitle("Library Manager - untitled");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
