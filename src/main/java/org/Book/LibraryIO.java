package org.Book;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

import com.sun.source.tree.Tree;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.io.StreamException;

public class LibraryIO
{
    private static final String csvHeader = "title,author,genre,year";
    private static File dsFile;
    private static int dsCurrentLine;
    private static final XStream xstream = setUpXstream();


    public static String getCSVHeader(){
        return csvHeader;
    }

    public static XStream setUpXstream(){
        XStream xstream = new XStream();
        xstream.alias("Book", Book.class);
        xstream.alias("Library", Set.class);
        xstream.allowTypes(new Class[] { Book.class });
        return xstream;
    }

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

    public static void serializeToXML(Book book, File file ){
        serializeToXML(new HashSet<Book>(Set.of(book)), file);
    }

    @SuppressWarnings("unchecked")
    public static void serializeToXML(Collection<Book> listOfBooks, File file) throws IllegalArgumentException {
        if(listOfBooks == null || listOfBooks.isEmpty() || listOfBooks.contains(null) || !testFileForSerialization(file,".xml")){
            throw new IllegalArgumentException("File unable to be serialized to-- Books passed in may not exist...");
        }

        Collection<Object> listOfObjFromFile = new HashSet<>();
        try{
            listOfObjFromFile.addAll(ObjectSerializer.deserializeObjFromXML(Book.class ,xstream, file, false));
            listOfObjFromFile.addAll(listOfBooks);
        }
        catch (IOException e){
            System.out.println("Error serializing to file, stopping serialization");
        }
        catch(NullPointerException e){
            System.out.println("Null Pointer Exception, stopping serialization");
        }
        catch (ConversionException | StreamException e) {
            System.out.println("File is empty or corrupted, rewriting file");
            listOfObjFromFile.add(listOfBooks);
        }

        try{
            ObjectSerializer.serializeToXML(listOfObjFromFile, xstream, "Library" , file, false);
        }
        catch (IOException e){
            System.out.println("Error serializing to file, stopping serialization");
        }
    }

    public static void serializeToCSV(Book book, File file) {
        serializeToCSV(new HashSet<Book>(Set.of(book)), file);
    }

    public static void serializeToCSV(Collection<Book> listOfBooks, File file) throws IllegalArgumentException {
        if(listOfBooks == null || listOfBooks.isEmpty() || listOfBooks.contains(null) || !testFileForSerialization(file,".csv")){
            throw new IllegalArgumentException("File unable to be serialized to-- Books passed in may not exist...");
        }

        TreeSet<Book> treeSetOfBooks = new TreeSet<>();
        try {
            treeSetOfBooks.addAll(deserializeFromCSV(file));
        } catch (IllegalArgumentException e) {
            //do nothing, set will be empty.
        }

        try(FileWriter fw = new FileWriter(file))
        {
            fw.write(csvHeader + "\n");
            fw.flush();
            treeSetOfBooks.addAll(listOfBooks);
            for(Book book : treeSetOfBooks){
                fw.write(book.getTitle() + "," + book.getAuthor() + "," + book.getGenre() + "," + book.getYear() + "\n");
            }
        }
        catch(IOException e) {
            System.out.println("Error accessing file");
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
            if(!(scnr.hasNextLine() && scnr.nextLine().trim().equals(LibraryIO.csvHeader.trim()))){
                System.out.printf("File \"%10s\"'s header is invalid\n", file.getName());
                return null;
            }
            while(scnr.hasNextLine()) {
                int lineNum = 0; //error printing purposes
                String line = scnr.nextLine();
                String[] bookStats = line.split(",");
                if(bookStats.length == 4) {
                    int yearReleased;
                    try{
                         yearReleased = Integer.parseInt(bookStats[3]);
                         treeSetOfBooks.add(new Book(bookStats[0].trim(), bookStats[1].trim(), bookStats[2].trim(), yearReleased));
                    }
                    catch(NumberFormatException e) {
                        System.out.printf("Corrupt Entry: %s @ ln %d Skipping...",line, lineNum);
                    }
                }
                else {
                    System.out.printf("Invalid Entry: %s @ ln %d Skipping...",line, lineNum);
                }
            }
        }
        catch(FileNotFoundException e) {
            System.out.println("Error accessing file");
            return null;
        }
        return treeSetOfBooks;
    }
}
