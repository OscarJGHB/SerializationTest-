package org.Book;

import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

public class BookTest {

    File makeNewFile(String csvName){
        File file = new File(csvName);
        if(file.exists()){
            file.delete();
        }
        return file;
    }

    @Test
    void comparingSameBooks(){
        Book book1 = new Book("Pride and Prejudice", "Jane Austen", "Romance Novel", 1813);
        Book alsoBook1 = new Book("Pride and Prejudice", "Jane Austen", "Romance Novel", 1813);
        assertTrue(book1.equals(alsoBook1));
    }

    @Test
    void comparingDifferentBooks(){
        Book book1 = new Book("Pride and Prejudice", "Jane Austen", "Romance Novel", 1813);
        Book alsoBook1 = new Book("Pride with Prejudice", "Jane Austen", "Romance Novel", 1813);
        assertFalse(book1.equals(alsoBook1));
    }


    //Tests singular instance of serialization
    //a header should be made as well as one entry
    @Test
    void testingSerialization() {
        File file = makeNewFile("books.csv");
        Book mb = new Book("To Kill a MockingBird", "Harper Lee", "Thriller",1960);
        LibraryIO.serializeToCSV(mb, file);
        List<String> csvEntries;
        try {
            csvEntries = Files.readAllLines(file.toPath());
        }
        catch (IOException e) {
            System.out.println("Something went wrong when reading the file, aborting test");
            return;
        }
        assertEquals(LibraryIO.getCSVHeader(), csvEntries.get(0));
        assertEquals("To Kill a MockingBird,Harper Lee,Thriller,1960", csvEntries.get(1));
        file.delete();
    }

    //tests one instance of deserialization
    @Test
    void testDeserialization() {
        File file = makeNewFile("books.csv");
        Book mb = new Book("To Kill a MockingBird", "Harper Lee", "Thriller",1960);
        LibraryIO.serializeToCSV(mb, file);
        TreeSet<Book> mbCpy = LibraryIO.deserializeFromCSV(file);
        assertTrue(mb.equals(mbCpy));
        file.delete();
    }

    //nothing should be added to file
    @Test
    void badFileNameToSerializeToCSV(){
        File file = makeNewFile("books.cst");
        Book mb = new Book("To Kill a MockingBird", "Harper Lee", "Thriller",1960);
        LibraryIO.serializeToCSV(mb, file);
        assertEquals(0, file.length());
    }

    @Test
    void giveCSVDirectoryNameToSerializeToCSV(){
        File file = makeNewFile("books.csv");
        file.mkdir();
        Book mb = new Book("To Kill a MockingBird", "Harper Lee", "Thriller",1960);
        LibraryIO.serializeToCSV(mb, file);
        file.delete();
    }

    @Test
    void deserializeFromCSVFromInvalidFileNames(){

        //Nonexistent File
        File nonExistentFile = makeNewFile("books.csv");
        assertEquals(null, LibraryIO.deserializeFromCSV(nonExistentFile));

        //Directory
        File dir = makeNewFile("books");
        dir.mkdir();
        assertEquals(null, LibraryIO.deserializeFromCSV(dir));

        //wrong file extension
        File invFileExt = makeNewFile("books.txt");
        assertEquals(null, LibraryIO.deserializeFromCSV(invFileExt));

        nonExistentFile.delete();
        dir.delete();
        invFileExt.delete();

    }

    @Test
    void deserializeFromCSVWithInvalidHeader(){
        File exampleCSV = makeNewFile("books.csv");
        try(FileWriter fw = new FileWriter(exampleCSV)){
            fw.write("Author,Genre,Year,Title"+"\n");
            fw.write("Jane Austen,Romance Novel,1891,Pride and Prejudice"+"\n");
        }
        catch(IOException e){
            System.out.println("Something went wrong when writing to the file, aborting test");
        }

        assertEquals(null, LibraryIO.deserializeFromCSV(exampleCSV));
        exampleCSV.delete();


    }

    @Test
    void deserializeFromCSVWithInvalidEntries(){
        File exampleCSV = makeNewFile("books.csv");

        //invalid year
        try(FileWriter fw = new FileWriter(exampleCSV)){
            fw.write("title,author,genre,year"+"\n");
            fw.write("Jane Austen,Romance Novel,1891,Pride and Prejudice"+"\n");
            fw.flush();
            assertNull(LibraryIO.deserializeFromCSV(exampleCSV));
        }
        catch(IOException e){
            System.out.println("Something went wrong when writing to the file, aborting test");
        }

        //not enough
        try(FileWriter fw = new FileWriter(exampleCSV)){
            //erase file and restart
            fw.write("title,author,genre,year"+"\n");
            fw.write("Pride and Prejudice, Jane Austen, Romance Novel"+"\n");
            fw.flush();
            assertNull(LibraryIO.deserializeFromCSV(exampleCSV));
        }
        catch(IOException e){
            System.out.println("Something went wrong when writing to the file, aborting test");
        }

        //no data found
        try(FileWriter fw = new FileWriter(exampleCSV)){
            //erase file and restart
            fw.write("title,author,genre,year"+"\n");
            fw.flush();
            assertNull(LibraryIO.deserializeFromCSV(exampleCSV));
        }
        catch(IOException e){
            System.out.println("Something went wrong when writing to the file, aborting test");
        }

        exampleCSV.delete();


    }


    @Test
    void testFileForSerialization() {
        File file = makeNewFile("Books.csv");
        assertTrue(LibraryIO.testFileForSerialization(file, ".csv"));
        assertFalse(LibraryIO.testFileForSerialization(file, ".txt"));
        file.delete();

        file = makeNewFile("Book.xml");
        assertTrue(LibraryIO.testFileForSerialization(file, ".xml"));
        assertFalse(LibraryIO.testFileForSerialization(file, ".txt"));
        file.delete();

        file = makeNewFile("Book");
        assertFalse(LibraryIO.testFileForSerialization(file, ".csv"));
        assertFalse(LibraryIO.testFileForSerialization(file, ".txt"));
        file.delete();
    }
}
