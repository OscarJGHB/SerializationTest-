package org.Book;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
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

    private static XStream setUpXstream(){
        XStream xstream = new XStream();
        xstream.alias("Book", Book.class);
        xstream.alias("Library", HashSet.class);
        xstream.allowTypes(new Class[] { Book.class });
        return xstream;
    }

    //returns true if file is valid
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

        if (!(file.exists() && file.isFile() && file.getName().endsWith(fileExt) && file.length() != 0)) {
            System.out.printf("File \"%10s\" is invalid to deserialize\n", file.getName());
            return false;
        }
        return true;
    }

    public static void serializeToXML(Book book, File file) {serializeToXML(new HashSet<>(Set.of(book)), file);}

    //TODO: Implement binary outputstream
    //TODO: avoid always overwriting
    @SuppressWarnings("unchecked")
    public static void serializeToXML(Collection<Book> listOfBooks, File file) {
        if(listOfBooks == null || listOfBooks.isEmpty() || !testFileForSerialization(file,".xml")){
            return;
        }
        HashSet<Book> listOfBooksFromFile;
        try (FileInputStream fis = new FileInputStream(file)){
            Object obj = xstream.fromXML(fis);
            if(obj instanceof HashSet && ((HashSet<?>)obj).stream().allMatch(o -> o instanceof LibraryIO)){
                    listOfBooksFromFile = (HashSet<Book>)obj;
            }
            else{
                listOfBooksFromFile = new HashSet<>();
            }
            if(!listOfBooksFromFile.addAll(listOfBooks)){
                System.out.println("Duplicate found- not adding");
                return;
            }
        }
        catch (IOException e) {
            System.out.println("Error accessing file");
            return;
        }
        catch (NullPointerException e) {
            System.out.println("Null pointer exception");
            return;
        }
        catch (ConversionException | StreamException e) {
            System.out.println("File is empty or corrupted, rewriting file");
            listOfBooksFromFile = new HashSet<Book>(listOfBooks);
        }

        try (FileWriter fw = new FileWriter(file)) {
            xstream.toXML(new HashSet<>(listOfBooksFromFile), fw);
            fw.write("\n");
            fw.flush();
        }
        catch(XStreamException e){
            System.out.println("Books given cannot be serialized");
        }
        catch(IOException e) {
            System.out.println("Error accessing file");
        }
    }

    public static void serializeToCSV(Book book, File file) {
        if(!testFileForSerialization(file,".csv")){
            return;
        }
        try(Scanner scnr = new Scanner(file)){
            boolean emptyOrInvalid = (file.length() == 0 || !(scnr.hasNextLine() && scnr.nextLine().trim().equals(LibraryIO.csvHeader.trim())));
            try(FileWriter fw = new FileWriter(file, !emptyOrInvalid))
            {
                if(emptyOrInvalid)
                {
                    fw.write(csvHeader + "\n");
                }
                fw.write(book.getTitle() + "," + book.getAuthor() + "," + book.getGenre() + "," + book.getYear() + "\n");
                fw.flush();

            }
        }
        catch(IOException e) {
            System.out.println("Error accessing file");
        }

    }

    @SuppressWarnings("unchecked")
    public static HashSet<Book> deserializeFromXML(File file) {

        if (!testFileForDeserialization(file,".xml")){
            return null;
        }

        HashSet<Book> someBooks = new HashSet<>();

        try (FileInputStream fis = new FileInputStream(file)) {
            someBooks = (HashSet<Book>) xstream.fromXML(fis);
        }
        catch(IOException e) {
            System.out.println("Error accessing file");
        }
        catch (ConversionException | StreamException e) {
            System.out.println("File is corrupted, stopping deserialization");
        }
        return someBooks;

    }

    public static Book deserializeFromCSV(File file) {
        try {
            if (dsFile == null || (Files.isSameFile(dsFile.toPath(), file.toPath()))) {
                //checks validity
                if (!testFileForDeserialization(file,".csv")){
                    return null;
                }
                dsFile = file;
                dsCurrentLine = 0;
            }
        }
        catch(IOException e) {
            System.out.println("File passed in does not exist");
            return null;
        }

        try(Scanner scnr = new Scanner(file)){
            if(!(scnr.hasNextLine() && scnr.nextLine().trim().equals(LibraryIO.csvHeader.trim()))){
                System.out.printf("File \"%10s\"'s header is invalid\n", file.getName());
                return null;
            }
            int i = 0;
            //at least has 1(header)
            while(scnr.hasNextLine() && i<dsCurrentLine){
                scnr.nextLine();
                i++;
            }
            if(scnr.hasNextLine()) {
                String line = scnr.nextLine();
                String[] bookStats = line.split(",");
                dsCurrentLine++;
                if(bookStats.length == 4) {
                    int yearReleased;
                    try{
                         yearReleased = Integer.parseInt(bookStats[3]);
                    }
                    catch(NumberFormatException e) {
                        System.out.println("Error parsing year released");
                        return null;
                    }
                    return new Book(bookStats[0].trim(), bookStats[1].trim(), bookStats[2].trim(), yearReleased);
                }
                else {
                    System.out.printf("Invalid number of properties in a row in file \"%10s\"\n",file.getName());
                    return null;
                }
            }
            else{
                System.out.println("No data found");
                return null;
            }
        }
        catch(FileNotFoundException e) {
            System.out.println("Error accessing file");
            return null;
        }
    }
}
