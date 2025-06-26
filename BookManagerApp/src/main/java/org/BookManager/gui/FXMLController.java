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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

public class FXMLController {

    //TODO edit in real time without form
    private ObservableMap<File,ObservableList<Book>> librarySaves = FXCollections.observableHashMap();
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
    private Menu toolsMenu;
    @FXML
    private Menu toolsSubMenu0;
    @FXML
    private Menu toolsSubMenu1;
    @FXML
    private Label label;
    @FXML
    private Label instructionLabel;
    @FXML
    private Label defaultSerializationLabel;


    //Open Library Tabs
    @FXML
    private TabPane openLibrariesTabPane;



    //Table Page
    @FXML //fits the table TODO dynamic resizing for multiple open tables
    private AnchorPane mainAppPane;
    @FXML
    private AnchorPane tablePane;
    @FXML
    private Button addBookButton;
    @FXML
    private Button deleteBookButton;
    @FXML
    private TextField textField0;

    private BookTableClass bookTable1 = new BookTableClass();



    public void initialize() {
        label.setText(MainApp.appName);
        defaultSerializationLabel.setText("New libraries set to: " + defaultFileFormat);
        instructionLabel.setText(String.format("Welcome to %s!\n", MainApp.appName));
        librarySaves.addListener((MapChangeListener<File,ObservableList<Book>>)change -> {
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
        toolsSubMenu0.getItems().clear();
        toolsSubMenu1.getItems().clear();

        if(librarySaves.isEmpty()){
            toolsMenu.setDisable(true);
            nodeTurnOff(mainAppPane);
        }
        else {
            toolsMenu.setDisable(false);
            nodeTurnOn(mainAppPane);
            for (File key : librarySaves.keySet()) {
                final File thisFile = key;
                Tab tab = new Tab(thisFile.getName());
                MenuItem mergeLibraryOption = new MenuItem(thisFile.getName());
                MenuItem compareLibraryOption = new MenuItem(thisFile.getName());

                //tabs
                tab.setOnSelectionChanged(event -> {
                    if (tab.isSelected()) {
                        setCurrentLibrary(thisFile, librarySaves.get(thisFile));
                    }
                });
                tab.setOnCloseRequest(event -> {
                    removeLibrary(thisFile);
                });

                //merge with
                mergeLibraryOption.setOnAction(event -> {
                    mergeLibrary(thisFile);
                });

                //compare
                compareLibraryOption.setOnAction(event -> {
                    compareLibrary(thisFile);
                });

                tab.setClosable(true);
                openLibrariesTabPane.getTabs().add(tab);

                toolsSubMenu0.getItems().add(mergeLibraryOption);
                toolsSubMenu1.getItems().add(compareLibraryOption);
            }
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
        defaultSerializationLabel.setText("Currently serializing to " + defaultFileFormat);
    }


    //sets current library and uses in memory list
    private void setCurrentLibrary(File file , ObservableList<Book> library){
        bookTable1.setItems(library);
        setStageName(MainApp.appName +" "+ file.getName());
        this.currentFile = file;
    }

    //sets current library and uses file
    //first time opening
    private ObservableList<Book> setCurrentLibrary(File file){
        ObservableList<Book> books = FXCollections.observableArrayList();
        if(file.length() > 0){
            try {
                    switch((file.getName().split("\\."))[1]){
                    case "csv":
                        books = FXCollections.observableList(new ArrayList<>(Objects.requireNonNull(Book.deserializeFromCSV(file))));
                        break;
                    case "xml":
                        books = FXCollections.observableList(new ArrayList<>(Objects.requireNonNull(Book.deserializeFromXML(file))));
                        break;
                    case "ser":
                        books = FXCollections.observableList(new ArrayList<>(Objects.requireNonNull(Book.deserializeFromBinary(file))));
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown file type");
                }
            }
            catch (IllegalArgumentException | NullPointerException e) {
                label.setText(e.getMessage());
                return null;
            }
        }
        bookTable1.setItems(books);
        setStageName(MainApp.appName +" "+ file.getName());
        this.currentFile = file;


        return books;
    }

    public void removeLibrary(File library){
        librarySaves.remove(library);
        if(openLibrariesTabPane.getTabs().isEmpty()){
            nodeTurnOff(mainAppPane);
        }
        else{
            openLibrariesTabPane.getSelectionModel().select(0);
        }
    }


    //opens up a blank sheet
    private void setUpTable(){
        tablePane.getChildren().add(bookTable1);
        bookTable1.prefWidthProperty().bind(tablePane.widthProperty());
        bookTable1.prefHeightProperty().bind(tablePane.heightProperty());
    }

    //file Explorer
    private File selectFile(){

        Stage stage = (Stage) toolBarHBox.getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Serializable File");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Supported File Types", "*.xml", "*.csv", "*.bin"),
                new FileChooser.ExtensionFilter("xml files","*.xml"),
                new FileChooser.ExtensionFilter("csv files","*.csv"),
                new FileChooser.ExtensionFilter("bin files","*.ser")
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

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("org/BookManager/gui/styles.css").toExternalForm());
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(instruction);
        stage.showAndWait();

        return controller.getAcceptedBookObj();
    }

    private void saveFile(ObservableList<Book> books, File file){
        try{
            System.out.println("Saving file to: " + file.getAbsolutePath());


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
        }
    }

    private void mergeLibrary(File file){
        if(!file.equals(currentFile)){
            librarySaves.get(currentFile).addAll(librarySaves.get(file));
            setCurrentLibrary(currentFile, librarySaves.get(currentFile));
            label.setText("Library successfully merged");
            return;
        }
        label.setText("Attempted to merge the same Library...");
    }

    //Compares in memory libraries and outputs new file yet to be saved
    private void compareLibrary(File file){
        if(!file.equals(currentFile)){
            HashSet<Book> currentLib = new HashSet<>(librarySaves.get(currentFile));
            HashSet<Book> libInQuestion = new HashSet<>(librarySaves.get(file));
            ObservableList<Book> uniqueItemsLib = FXCollections.observableArrayList();

            for(Book book : libInQuestion){
                if(!currentLib.contains(book)){
                    uniqueItemsLib.add(book);
                }
            }
            for(Book book : currentLib){
                if(!libInQuestion.contains(book)){
                    uniqueItemsLib.add(book);
                }
            }

            File uniqueItemsFile = new File(currentFile.getName().split("\\.")[0]+"AND"+file.getName().split("\\.")[0]+defaultFileFormat);
            librarySaves.put(uniqueItemsFile, uniqueItemsLib);
            setCurrentLibrary(uniqueItemsFile, uniqueItemsLib);
            label.setText("Libraries compared created new file in: " + uniqueItemsFile.getAbsolutePath());
            return;
        }
        label.setText("Attempted to compare the same Library...");
    }

    @FXML
    private void setDefaultFileFormat(ActionEvent event){
        setDefaultFileFormat(((MenuItem)event.getSource()).getText());
        System.out.println("Default File Format: " + defaultFileFormat);
    }

    //blank file
    @FXML
    private void makeBlankLibrary(){
        File file = new File("Untitled"+defaultFileFormat);
        int i = 0;
        while(file.exists() || librarySaves.get(file) != null){
            file = new File(String.format("Untitled(%d)%s",i,defaultFileFormat));
            i++;
        }
        librarySaves.put(file, Objects.requireNonNull(setCurrentLibrary(file)));
    }

    //get existing file
    @FXML
    private void importLibrary(){
        try{
            File file = Objects.requireNonNull(selectFile());
            librarySaves.put(file, Objects.requireNonNull(setCurrentLibrary(file)));
        }
        catch(IllegalArgumentException e){
            label.setText(e.getMessage());
        }
        catch(NullPointerException e){
            //bad file, blank set
        }
    }

    @FXML
    private void addBookToTable(){
        try{
            Book book = Objects.requireNonNull(bookForm("Adding book to library"));
            ObservableList<Book> currentLib = librarySaves.get(currentFile);
            currentLib.add(book);
            setCurrentLibrary(currentFile, currentLib);
        }
        catch (NullPointerException e){
            label.setText("No book added");
        }
    }

    @FXML
    private void removeABookFromTable(){
        Book book = null;
        try {
            book = Objects.requireNonNull(bookForm("Removing everything that matches given text fields\n" +
                    "Fields left blank will include all"));
        }
        catch(NullPointerException e){
            label.setText("Nothing was removed");
            return;
        }

        ObservableList<Book> currentLib = librarySaves.get(currentFile);
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
            saveFile(librarySaves.get(currentFile),currentFile);
            label.setText("Current Library Saved");
        }
        catch(NullPointerException e){
            label.setText("No file loaded in");
        }
    }

    @FXML
    private void saveAsLibrary(){
        try {
            Stage saveToFileStage = (Stage)label.getScene().getWindow();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save to file");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("All Supported File Types", "*.xml", "*.csv", "*.ser"),
                    new FileChooser.ExtensionFilter("XML files", "*.xml"),
                    new FileChooser.ExtensionFilter("CSV files", "*.csv"),
                    new FileChooser.ExtensionFilter("Binary files", "*.ser"));
            fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
            File file = fileChooser.showSaveDialog(saveToFileStage);
            saveFile(librarySaves.get(currentFile), file);
            label.setText("Library saved to " + file.getAbsolutePath());
        }
        catch(NullPointerException e){
            label.setText("No file loaded in");
        }
    }
}