package org.BookManager;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.io.StreamException;

import java.io.*;
import java.util.*;

@XStreamAlias("Book")
public class Book implements Serializable, Comparable<Book> {

    //TODO String pathname for book cover
    private String title = "";
    private String author = "";
    private String genre = "";
    private String bookCoverFile = "";
    private int year = -1;
    private boolean pictures = false;

    private static final String csvHeader = "title,author,genre,year,pictures,bookCoverFile";
    private static final XStream xstream = setUpXstream();

    public Book(){};
    public Book(String title, String author, String genre, int year, boolean pictures) {
        setTitle(title);
        setAuthor(author);
        setGenre(genre);
        setYear(year);
        setPictures(pictures);
    }
    public Book(String title, String author, String genre, int year, boolean pictures, String bookCoverFile) {
        this(title, author, genre, year, pictures);
        setBookCoverFile(bookCoverFile);
    }

    //GETTERS
    public String getTitle() {
        return title;
    }
    public String getAuthor() {
        return author;
    }
    public String getGenre() {
        return genre;
    }
    public int getYear() {
        return year;
    }
    public boolean getPictures() {
        return pictures;
    }
    public String getBookCoverFile() {return bookCoverFile;}

    //SETTERS
    public void setTitle(String title) {
        this.title = (title == null ? this.title : title).trim();
    }
    public void setAuthor(String author) {this.author = (author == null ? this.author : author).trim();}
    public void setGenre(String genre) {this.genre = (genre == null ? this.genre : genre).trim();}
    public void setYear(int year) {
        this.year = year;
    }
    public void setPictures(boolean pictures) {
        this.pictures = pictures;
    }
    public void setBookCoverFile(String bookCoverFile) {this.bookCoverFile = (bookCoverFile == null ? this.bookCoverFile : bookCoverFile).trim();}

    @Override
    public int compareTo(Book b){
        return Integer.compare(this.hashCode(), b.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this)
        {
            return true;
        }
        else if (obj == null || getClass() != obj.getClass())
        {
            return false;
        }
        Book book = (Book) obj;
        return (book.author.equals(this.author)
                && book.title.equals(this.title)
                && book.genre.equals(this.genre)
                && book.year == this.year
                && book.pictures==this.pictures
                && book.bookCoverFile.equals(this.bookCoverFile));
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, title, genre, year, pictures, bookCoverFile);
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%d,%b,%s", title, author, genre, year, pictures, bookCoverFile);
    }

    public static String getCSVHeader(){
        return csvHeader;
    }

    public static XStream setUpXstream(){
        XStream xstream = new XStream();
        xstream.alias("Library", Set.class);
        xstream.allowTypes(new Class[] { Book.class });
        xstream.processAnnotations(Book.class);
        xstream.registerConverter(new BookConverter());
        return xstream;
    }

    //IO METHODS
    //returns true if file is a file/created
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean testFileForSerialization(File file, String fileExt){
        String fileName = file.getName().trim().toLowerCase();
        fileExt = fileExt.trim().toLowerCase();
        try{
            if(fileName.endsWith(fileExt)) {
                if (file.exists() && file.isFile()) {
                    return true;
                }
                else if (!file.exists() && file.createNewFile()) {
                    System.out.printf("New File \"%10s\" created\n", file.getName());
                    return true;
                }
            }
            else {
                System.out.printf("File \"%10s\" is not valid\n", file.getName());
                return false;
            }
        }
        catch(IOException e) {
            System.out.println("Error making file");
            return false;
        }
        return false;
    }

    //returns true if file is valid
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean testFileForDeserialization(File file, String fileExt){
        String fileName = file.getName().trim().toLowerCase();
        fileExt = fileExt.trim().toLowerCase();

        if (!(file.exists() && file.isFile() && fileName.endsWith(fileExt) && file.length() != 0)) {
            System.out.printf("File \"%10s\" is invalid to deserialize\n", file.getName());
            return false;
        }
        return true;
    }


    @SuppressWarnings("unchecked")
    public static void serializeToXML(Collection<Book> listOfBooks, File file) throws IllegalArgumentException {
        if(listOfBooks == null || listOfBooks.isEmpty()){
            throw new IllegalArgumentException("Nothing given to serialize");
        }
        else if(!testFileForSerialization(file,".xml")){
            throw new IllegalArgumentException("File unable to be serialized to");
        }

        try{
            ObjectSerializer.serializeToXML(listOfBooks, xstream, "Library" , file, false);
        }
        catch (IOException e){
            System.out.println("Error serializing to file, stopping serialization");
        }
    }


    public static void serializeToCSV(Collection<Book> listOfBooks, File file) throws IllegalArgumentException {
        if(listOfBooks == null || listOfBooks.isEmpty() ){
            throw new IllegalArgumentException("Nothing given to serialize");
        }
        else if( !testFileForSerialization(file,".csv")){
            throw new IllegalArgumentException("File unable to be serialized to");
        }

        try(FileWriter fw = new FileWriter(file))
        {
            fw.write(csvHeader + "\n");
            fw.flush();
            for(Book book : listOfBooks){
                fw.write(book.getTitle() + "," + book.getAuthor()
                        + "," + book.getGenre() + "," + book.getYear()
                        + "," + book.getPictures()+ "," + book.getBookCoverFile() + "\n");
            }
        }
        catch(IOException e) {
            System.out.println("Error accessing file");
        }

    }


    public static void serializeToBinary(Collection<Book> listOfBooks, File file) throws IllegalArgumentException {
        if (listOfBooks == null || listOfBooks.isEmpty()){
            throw new IllegalArgumentException("Nothing given to serialize");
        }
        else if(!testFileForSerialization(file,".ser")){
            throw new IllegalArgumentException("File unable to be serialized to");
        }

        try{
            ObjectSerializer.serializeToBinary(listOfBooks, file);
        }
        catch (IOException e){
            System.out.println("Error serializing to file, stopping serialization");
        }
    }

    @SuppressWarnings("unchecked")
    public static TreeSet<Book> deserializeFromXML(File file) throws IllegalArgumentException {

        if (!testFileForDeserialization(file,".xml")){
            throw new IllegalArgumentException("File given is unable to deserialize");
        }

        TreeSet<Book> treeSetOfBooks;

        try{
            treeSetOfBooks = new TreeSet<>(ObjectSerializer.deserializeObjFromXML(Book.class, xstream, file, false));
        }
        catch(IOException e) {
            System.out.println("Error accessing file");
            return null;
        }
        catch (ConversionException | StreamException e) {
            System.out.println("File is corrupted, aborting deserialization");
            return null;
        }

        return treeSetOfBooks;

    }

    public static TreeSet<Book> deserializeFromCSV(File file) throws IllegalArgumentException {
        if (!testFileForDeserialization(file,".csv")){
            throw new IllegalArgumentException("File given is unable to deserialize");
        }

        TreeSet<Book> treeSetOfBooks = new TreeSet<>();

        try(Scanner scnr = new Scanner(file)){
            if(!(scnr.hasNextLine() && scnr.nextLine().trim().equals(Book.csvHeader.trim()))){
                System.out.printf("File \"%10s\"'s header is invalid\n", file.getName());
                return null;
            }
            while(scnr.hasNextLine()) {
                int lineNum = 0; //error printing purposes
                String line = scnr.nextLine();
                String[] bookStats = line.split(",", -1);
                if(bookStats.length == 6) {
                    int yearReleased;
                    try{
                        yearReleased = Integer.parseInt(bookStats[3]);
                        boolean pictures = switch (bookStats[4].trim().toLowerCase()) {
                            case "true" -> true;
                            case "false" -> false;
                            default -> throw new IllegalArgumentException();
                        };
                        treeSetOfBooks.add(new Book(bookStats[0].trim(), bookStats[1].trim(), bookStats[2].trim(), yearReleased,pictures,bookStats[5]));
                    }
                    catch(IllegalArgumentException e) {
                        //skipping
                    }
                }
                else {
                    System.out.printf("Invalid Entry: %s @ ln %d Skipping...",line, lineNum);
                    System.out.println("Actual length: " + bookStats.length);
                }
            }
        }
        catch(FileNotFoundException e) {
            System.out.println("Error accessing file");
            return null;
        }
        return treeSetOfBooks;
    }

    public static TreeSet<Book> deserializeFromBinary(File file) throws IllegalArgumentException {
        if (!testFileForDeserialization(file,".ser")){
            throw new IllegalArgumentException("File given is unable to deserialize");
        }
        TreeSet<Book> treeSetOfBooks = new TreeSet<>();
        try{
            for(Serializable obj: ObjectSerializer.deserializeFromBinary(file)){
                try{
                    treeSetOfBooks.add((Book)obj);
                }
                catch (ClassCastException e){
                    //skip
                    System.out.println("Class Cast Exception");
                }
            }
        }
        catch(IOException e) {
            System.out.println("Error accessing file");
            return null;
        }
        catch (ConversionException | StreamException e) {
            System.out.println("File is corrupted, aborting deserialization");
            return null;
        }
        return treeSetOfBooks;
    }

}