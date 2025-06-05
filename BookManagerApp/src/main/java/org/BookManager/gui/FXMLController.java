package org.BookManager.gui;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.BookManager.Book;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.BookManager.Main;

import java.io.File;
import java.util.ArrayList;

public class FXMLController {

    //TODO close libraries option
    private ObservableMap<String,File> librarySaves = FXCollections.observableHashMap();

    //Menu Page
    @FXML
    private HBox toolBarContainer;
    @FXML
    private MenuBar menuBar;
    @FXML
    private Menu fileOptionsMenu;
    @FXML
    private MenuItem menuItem0;
    @FXML
    private MenuItem menuItem1;
    //TODO new MenuItem: set default file ext
    @FXML
    private Menu openLibrariesMenu;

    //Table Page
    @FXML
    private AnchorPane tablePane;
    @FXML
    private Label label;
    @FXML
    private Button button0;
    @FXML //TODO add
    private Button button1;
    @FXML //TODO delete
    private Button button2;
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
        fileOptionsMenu.setText("File");
        menuItem0.setText("New Library");
        menuItem1.setText("Import Library");
        openLibrariesMenu.setText("Open Libraries");
        librarySaves.addListener((MapChangeListener<String,File>)change -> {
            updateMenuItems();
        });
        setUpTableTask();
    }

    private void setStageName(String string){
        Stage stage = (Stage)toolBarContainer.getScene().getWindow();
        stage.setTitle(string);
    }

    private void updateMenuItems(){
        openLibrariesMenu.getItems().clear();
        for(String key : librarySaves.keySet()){
            final String k = key;
            MenuItem item = new MenuItem(key);
            item.setOnAction(event -> {setCurrentLibraryTask(librarySaves.get(k));});
            openLibrariesMenu.getItems().add(item);
        }
    }

    private void setCurrentLibraryTask(File file){
        openLibrariesMenu.setVisible(true);
        ObservableList<Book> books = null;
        try {
                switch((file.getName().split("\\."))[1]){
                case "csv":
                    books = FXCollections.observableList(new ArrayList<>(Book.deserializeFromCSV(file)));
                    break;
                case "xml":
                    books = FXCollections.observableList(new ArrayList<>(Book.deserializeFromXML(file)));
                    break;
                case "bin":
                    books = FXCollections.observableList(new ArrayList<>(Book.deserializeFromBinary(file)));
                    break;
                default:
                    throw new IllegalArgumentException("Unknown file type");
            }
        } catch (IllegalArgumentException e) {
            label.setText(e.getMessage());
            return;
        }
        bookTable.setItems(books);
        setStageName(MainApp.appName +" "+ file.getName());
    }


    //opens up a blank sheet
    private void setUpTableTask(){
        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        label.setText("Hello, JavaFX " + javafxVersion + "\nRunning on Java " + javaVersion + ".");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        yearCol.setCellValueFactory(new PropertyValueFactory<>("year"));
        genreCol.setCellValueFactory(new PropertyValueFactory<>("genre"));
        pictureCol.setCellValueFactory(new PropertyValueFactory<>("pictures"));
    }

    //file Explorer
    private File selectFileTask(){

        Stage stage = (Stage) toolBarContainer.getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Serializable File");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")).getParentFile());
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Supported File Types", "*.xml", "*.csv", "*.bin"),
                new FileChooser.ExtensionFilter("xml files","*.xml"),
                new FileChooser.ExtensionFilter("csv files","*.csv"),
                new FileChooser.ExtensionFilter("bin files","*.bin")
        );
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            System.out.println("Selected file: " + file.getAbsolutePath());
        }
        return file;
    }

    //GUI Object Tasks
    @FXML
    public void button0Task(){
        setCurrentLibraryTask(new File(textField0.getText()));
    }

    //blank file
    @FXML
    public void menuItem0Task(){
        tablePane.setVisible(true);
        //TODO force user to name file if they add anything
        //Popup window
        //reset stage name

    }

    //get existing file
    @FXML
    public void menuItem1Task(){
        tablePane.setVisible(true);
        try{
            File file = selectFileTask();
            setCurrentLibraryTask(file);
            librarySaves.put(file.getName(), file);
        }
        catch(IllegalArgumentException e){
            label.setText(e.getMessage());
        }

    }





}