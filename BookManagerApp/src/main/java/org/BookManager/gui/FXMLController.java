package org.BookManager.gui;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.BookManager.Book;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class FXMLController {

    //TODO swap string key with file key to fix save function
    //TODO move more over to FXML
    private ObservableMap<String,ObservableList<Book>> librarySaves = FXCollections.observableHashMap();
    private String defaultFileFormat = ".xml";
    private File currentFile;

    //Menu Page
    @FXML
    private HBox toolBarHBox;
    @FXML
    private MenuBar menuBar;
    @FXML
    private Menu fileOptionsMenu;
    @FXML
    private MenuItem filesOptionsMenuItem0;
    @FXML
    private MenuItem filesOptionsMenuItem1;
    @FXML
    private MenuItem filesOptionsMenuItem2;
    @FXML
    private MenuItem filesOptionsMenuItem3;
    @FXML
    private Menu generalOptionsMenu;
    @FXML
    private Menu generalOptionsSubMenu0;
    @FXML
    private MenuItem generalOptionsSubMenuItem0;
    @FXML
    private MenuItem generalOptionsSubMenuItem1;
    @FXML
    private MenuItem generalOptionsSubMenuItem2;
    @FXML
    private Label label;
    @FXML
    private Label instructionLabel;


    //Open Library Tabs
    @FXML
    private TabPane openLibrariesTabPane;


    //Table Page
    @FXML
    private AnchorPane tablePane;
    @FXML
    private Button addBookButton;
    @FXML
    private Button deleteBookButton;
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
        label.setText(MainApp.appName);
        instructionLabel.setText(String.format("Welcome to %s!\n", MainApp.appName));
        fileOptionsMenu.setText("File");
        filesOptionsMenuItem0.setText("New Library");
        filesOptionsMenuItem1.setText("Import Library");
        filesOptionsMenuItem2.setText("Save Library");
        filesOptionsMenuItem3.setText("Save as");
        generalOptionsMenu.setText("Options");
        generalOptionsSubMenu0.setText("setDefaultFileFormat");
        generalOptionsSubMenuItem0.setText(".xml");
        generalOptionsSubMenuItem1.setText(".bin");
        generalOptionsSubMenuItem2.setText(".csv");
        librarySaves.addListener((MapChangeListener<String,ObservableList<Book>>)change -> {
            updateMenuItems();
        });
        setUpTable();
    }

    private void setStageName(String string){
        Stage stage = (Stage) toolBarHBox.getScene().getWindow();
        stage.setTitle(string);
    }

    private void updateMenuItems(){
        openLibrariesTabPane.getTabs().clear();
        for(String key : librarySaves.keySet()){
            final String k = key;
            MenuItem item = new MenuItem(key);
            Tab tab = new Tab(k);
            item.setOnAction(event -> {
                setCurrentLibrary(librarySaves.get(k),k);});
            tab.setOnSelectionChanged(event -> {
                if(tab.isSelected()){
                    setCurrentLibrary(librarySaves.get(k),k);}
            });
            tab.setOnCloseRequest(event -> {
                removeLibrary(k);
            });
            tab.setClosable(true);
            openLibrariesTabPane.getTabs().add(tab);

        }
    }

    private void nodeTurnOn(Node node){
        node.setVisible(true);
        node.setDisable(false);
    }
    private void nodeTurnOff(Node node){
        node.setVisible(false);
        node.setDisable(true);
    }

    private void setDefaultFileFormat(String defaultFileFormat) {
        this.defaultFileFormat = defaultFileFormat;
    }

    //sets current library and uses in memory list
    private void setCurrentLibrary(ObservableList<Book> library, String fileName){
        nodeTurnOn(bookTable);
        bookTable.setItems(library);
        setStageName(MainApp.appName +" "+ fileName);
        this.currentFile = new File(fileName);
    }

    //sets current library and uses file
    //first time opening
    private ObservableList<Book> setCurrentLibrary(File file){
        nodeTurnOn(bookTable);
        ObservableList<Book> books = FXCollections.observableArrayList();
        if(file.length() > 0){
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
                return null;
            }
        }
        bookTable.setItems(books);
        setStageName(MainApp.appName +" "+ file.getName());
        this.currentFile = file;

        return books;
    }

    public void removeLibrary(String libraryName){
        librarySaves.remove(libraryName);
        if(openLibrariesTabPane.getTabs().isEmpty()){
            ObservableList<Book> books = FXCollections.observableArrayList();
            setCurrentLibrary(books,"");
            nodeTurnOff(bookTable);
        }
        else{
            openLibrariesTabPane.getSelectionModel().select(0);
        }
    }


    //opens up a blank sheet
    private void setUpTable(){
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        yearCol.setCellValueFactory(new PropertyValueFactory<>("year"));
        genreCol.setCellValueFactory(new PropertyValueFactory<>("genre"));
        pictureCol.setCellValueFactory(new PropertyValueFactory<>("pictures"));
    }

    //file Explorer
    private File selectFile(){

        Stage stage = (Stage) toolBarHBox.getScene().getWindow();

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

    private Book bookForm(String instruction){
        Parent root = null;
        FXMLLoader loader = null;
        try {
            loader = new FXMLLoader(getClass().getClassLoader().getResource("org/BookManager/gui/BookForm.fxml"));
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        BookFormController controller = (BookFormController)loader.getController();
        controller.setInstruction(instruction);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("org/BookManager/gui/styles.css").toExternalForm());
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();

        return controller.getAcceptedBookObj();
    }

    private void saveFile(ObservableList<Book> books, File file){
        try{
            System.out.println("Saving file to: " + file.getAbsolutePath());

            FileWriter writer = new FileWriter(file);
            writer.close();

            switch((file.getName().split("\\."))[1]){
                case "csv":
                    Book.serializeToCSV(books,file.getAbsoluteFile());
                    break;
                case "xml":
                    Book.serializeToXML(books,file.getAbsoluteFile());
                    break;
                case "bin":
                    Book.serializeToBinary(books,file.getAbsoluteFile());
                    break;
                default:
                    throw new IllegalArgumentException("Unknown file type");
            }
        }
        catch(IllegalArgumentException e){
            System.out.println(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private
 void button0Task(){
        setCurrentLibrary(new File(textField0.getText()));
    }

    @FXML
    private
 void setDefaultFileFormat(ActionEvent event){
        setDefaultFileFormat(((MenuItem)event.getSource()).getText());
        System.out.println("Default File Format: " + defaultFileFormat);
    }

    //blank file
    @FXML
    private
 void makeBlankLibrary(){
        nodeTurnOn(tablePane);
        File file = new File("Untitled"+defaultFileFormat);


        int i = 0;
        while(file.exists()){
            file = new File(String.format("Untitled(%d).%s",i,defaultFileFormat));
            i++;
        }
        librarySaves.put(file.getName(), Objects.requireNonNull(setCurrentLibrary(file)));

    }

    //get existing file
    @FXML
    private
 void importLibrary(){
        try{
            File file = selectFile();
            librarySaves.put(file.getName(), Objects.requireNonNull(setCurrentLibrary(file)));
            nodeTurnOn(tablePane);
        }
        catch(IllegalArgumentException e){
            label.setText(e.getMessage());
        }
        catch(NullPointerException e){
            //bad file, blank set
        }
    }

    @FXML
    private
 void addABookToTable(){
        Book book = bookForm("Adding book to library");
        String fileName = currentFile.getName();
        ObservableList<Book> currentLib = librarySaves.get(fileName);
        currentLib.add(book);
        setCurrentLibrary(currentLib,fileName);
    }

    @FXML
    private
 void removeABookFromTable(){
        Book book = null;
        try {
            book = Objects.requireNonNull(bookForm("Removing everything that matches given text fields\n" +
                    "Fields left blank will include all"));
        }
        catch(NullPointerException e){
            label.setText("Nothing was typed in");
        }

        String fileName = currentFile.getName();
        ObservableList<Book> currentLib = librarySaves.get(fileName);
        ArrayList<Book> toBeRemoved = new ArrayList<>();

        for(Book b : currentLib){
            boolean found = true;

            if (!book.getAuthor().isEmpty() && !book.getAuthor().equals(b.getAuthor())) {
                found = false;
            }
            if (!book.getTitle().isEmpty() && !book.getTitle().equals(b.getTitle())) {
                found = false;
            }
            if (!book.getGenre().isEmpty() && !book.getGenre().equals(b.getGenre())) {
                found = false;
            }
            if (book.getYear() != -1 && book.getYear() != b.getYear()) {
                found = false;
            }

            if (found) {
                toBeRemoved.add(b);
            }

        }

        currentLib.removeAll(toBeRemoved);
    }

    @FXML
    private void saveCurrentLibrary(){
        try {
            File file = new File(currentFile.getParentFile().getParentFile()+"/"+currentFile.getName());
            saveFile(librarySaves.get(currentFile.getName()),file);
            label.setText("Current Library Saved");
        }
        catch(NullPointerException e){
            label.setText("No file loaded in");
        }
    }

    @FXML
    private void saveToFile(){
        try {
            File file = selectFile();
            saveFile(librarySaves.get(currentFile.getName()), file);
        }
        catch(NullPointerException e){
            label.setText("No file loaded in");
        }
    }











}