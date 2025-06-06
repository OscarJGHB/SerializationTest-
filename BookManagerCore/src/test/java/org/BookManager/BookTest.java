package org.BookManager;

import com.thoughtworks.xstream.XStream;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class BookTest {

    public static XStream setUpXstream(){
        XStream xstream = new XStream();
        xstream.alias("Library", Set.class);
        xstream.alias("Book", Book.class);
        xstream.allowTypes(new Class[] { Book.class });
        xstream.processAnnotations(Book.class);
        xstream.registerConverter(new BookConverter());
        return xstream;
    }

    File makeNewFile(String csvName){
        File file = new File(csvName);
        if(file.exists()){
            file.delete();
        }
        return file;
    }

    @Test
    void comparingSameBooks(){
        Book book1 = new Book("Pride and Prejudice", "Jane Austen", "Romance Novel", 1813,true);
        Book alsoBook1 = new Book("Pride and Prejudice", "Jane Austen", "Romance Novel", 1813,true);
        assertTrue(book1.equals(alsoBook1));
    }

    @Test
    void comparingDifferentBooks(){
        Book book1 = new Book("Pride and Prejudice", "Jane Austen", "Romance Novel", 1813,true);
        Book alsoBook1 = new Book("Pride with Prejudice", "Jane Austen", "Romance Novel", 1813,true);
        assertFalse(book1.equals(alsoBook1));
    }


    //Tests singular instance of serialization
    //a header should be made as well as one entry
    @Test
    void testingSerialization() {
        File file = makeNewFile("books.csv");
        Book mb = new Book("To Kill a MockingBird", "Harper Lee", "Thriller",1960,true);
        Book.serializeToCSV(mb, file);
        List<String> csvEntries;
        try {
            csvEntries = Files.readAllLines(file.toPath());
        }
        catch (IOException e) {
            System.out.println("Something went wrong when reading the file, aborting test");
            return;
        }
        assertEquals(Book.getCSVHeader(), csvEntries.get(0));
        assertEquals("To Kill a MockingBird,Harper Lee,Thriller,1960,true", csvEntries.get(1));
        file.delete();
    }

    //tests one instance of deserialization
    @Test
    void testDeserialization() {
        File file = makeNewFile("books.csv");
        Book mb = new Book("To Kill a MockingBird", "Harper Lee", "Thriller",1960,true);
        Book.serializeToCSV(mb, file);
        TreeSet<Book> mbCpy = Book.deserializeFromCSV(file);
        assertTrue(mb.equals(mbCpy.first()));
        file.delete();
    }

    //nothing should be added to file
    @Test
    void badFileNameToSerializeToCSV(){
        File file = makeNewFile("books.cst");
        Book mb = new Book("To Kill a MockingBird", "Harper Lee", "Thriller",1960,true);
        try {
            Book.serializeToCSV(mb, file);
        } catch (IllegalArgumentException e) {
        }
        assertEquals(0, file.length());
    }

    @Test
    void giveCSVDirectoryNameToSerializeToCSV(){
        File file = makeNewFile("books.csv");
        file.mkdir();
        Book mb = new Book("To Kill a MockingBird", "Harper Lee", "Thriller",1960,true);
        try {
            Book.serializeToCSV(mb, file);
        } catch (Exception e) {
            assertEquals("File unable to be serialized to",e.getMessage());
        }
        file.delete();
    }

    @Test
    void deserializeFromCSVFromInvalidFileNames(){

        //Nonexistent File
        File nonExistentFile = makeNewFile("books.csv");
        try {
            Book.deserializeFromCSV(nonExistentFile);
        } catch (Exception e) {
            assertEquals("File given is unable to deserialize",e.getMessage());
        }

        //Directory
        File dir = makeNewFile("books");
        dir.mkdir();
        try{
            Book.deserializeFromCSV(dir);
        }
        catch (Exception e) {
            assertEquals("File given is unable to deserialize",e.getMessage());
        }

        //wrong file extension
        File invFileExt = makeNewFile("books.txt");
        try{
            Book.deserializeFromCSV(invFileExt);
        }
        catch (Exception e){
            assertEquals("File given is unable to deserialize",e.getMessage());
        }

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

        assertEquals(null, Book.deserializeFromCSV(exampleCSV));
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
            assertEquals(0,Book.deserializeFromCSV(exampleCSV).size());
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
            assertEquals(0,Book.deserializeFromCSV(exampleCSV).size());
        }
        catch(IOException e){
            System.out.println("Something went wrong when writing to the file, aborting test");
        }

        //no data found
        try(FileWriter fw = new FileWriter(exampleCSV)){
            //erase file and restart
            fw.write("title,author,genre,year"+"\n");
            fw.flush();
            assertEquals(0,Book.deserializeFromCSV(exampleCSV).size());
        }
        catch(IOException e){
            System.out.println("Something went wrong when writing to the file, aborting test");
        }

        exampleCSV.delete();


    }

    @Test
    void testOneBookXMLFileSerialization(){
        File file = makeNewFile("Books.xml");
        Book book1 = new Book("1","2","3",4,true);
        Book.serializeToXML(book1,file);
        assertEquals(book1, Book.deserializeFromXML(file).first());
        file.delete();
    }

    @Test
    void testManyBooksXMLFileSerialization(){
        File file = makeNewFile("Books.xml");

        Book book1 = new Book("1","2","3",4,true);
        Book book2 = new Book("a","b","c",4,true);
        Book book3 = new Book("T","O","M",4,true);
        Book book4 = new Book("A","R","F",4,true);

        Collection<Book> books = new TreeSet<>(Set.of(book1,book2,book3,book4));
        TreeSet<Book> booksFromXML;

        Book.serializeToXML(books,file);

        XStream xstream = setUpXstream();
        try {
            booksFromXML = new TreeSet<Book>(ObjectSerializer.deserializeObjFromXML(Book.class,xstream,file,false));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assertEquals(books, booksFromXML, "Deserialized books do not match the original set");

        file.delete();
    }

    @Test
    void testManyBooksXMLFileDeserialization(){
        File file = makeNewFile("Books.xml");

        Book book1 = new Book("1","2","3",4,true);
        Book book2 = new Book("a","b","c",4,true);
        Book book3 = new Book("T","O","M",4,true);
        Book book4 = new Book("A","R","F",4,true);

        Collection<Book> books = new TreeSet<>(Set.of(book1,book2,book3,book4));

        XStream xstream = setUpXstream();
        try(FileOutputStream fos = new FileOutputStream(file)){
            xstream.toXML(books,fos);
        }
        catch(IOException e){
            throw new RuntimeException(e);
        }

        TreeSet<Book> resultBooks = Book.deserializeFromXML(file);
        assertEquals(books, resultBooks, "Deserialized books do not match the original set");

        file.delete();
    }

    @Test
    void testRandomEntryInBooksXMLFileDeserialization(){
        File fileWithoutStructure = makeNewFile("Anything.xml");
        File fileWithStructure = makeNewFile("Anything1.xml");
        Book book1 = new Book("1","2","3",4,true);
        Book book2 = new Book("a","b","c",4,true);
        Book book3 = new Book("T","O","M",4,true);
        Book book4 = new Book("A","R","F",4,true);
        String wrong1 = "Not a Book";
        Integer wrong2 = 0;
        Collection<Book> correctBooks = new TreeSet<>(Set.of(book1,book2,book3,book4));
        Collection<Object> randomObjects = new HashSet<>(Set.of(book1,book2,book3,book4,wrong1,wrong2));
        TreeSet<Book> booksFromXML = new TreeSet<Book>();
        try{
            XStream xstream = setUpXstream();
            ObjectSerializer.serializeToXML(randomObjects,xstream,"",fileWithoutStructure,false);
            ObjectSerializer.serializeToXML(randomObjects,xstream,"",fileWithStructure,true);
            booksFromXML = Objects.requireNonNull(Book.deserializeFromXML(fileWithoutStructure));
            assertTrue(booksFromXML.equals(Book.deserializeFromXML(fileWithStructure)));
            assertTrue(booksFromXML.equals(correctBooks));

        }
        catch(IOException e){
            throw new RuntimeException(e);
        }
        catch (NullPointerException e){
            throw new RuntimeException(e);
        }
    }

    @Test
    void testBooksBinarySerialization(){
        File file = makeNewFile("Books.bin");
        Book book1 = new Book("1","2","3",4,true);
        Book book2 = new Book("a","b","c",4,true);
        Book book3 = new Book("T","O","M",4,true);
        Book book4 = new Book("A","R","F",4,true);
        Collection<Book> books = new TreeSet<>(Set.of(book1,book2,book3,book4));
        TreeSet<Book> booksFromBinary = new TreeSet<>();

        Book.serializeToBinary(books,file);
        try{
            ObjectSerializer.deserializeFromBinary(file).forEach(b -> booksFromBinary.add(((Book)b)));
        }
        catch(IOException e){
            throw new RuntimeException(e);
        }

        assertEquals(books, booksFromBinary, "Deserialized books do not match the original set");
        file.delete();
    }

    @Test
    void testBooksBinaryDeserialization(){
        File file = makeNewFile("Books.bin");
        Book book1 = new Book("1","2","3",4,true);
        Book book2 = new Book("a","b","c",4,true);
        Book book3 = new Book("T","O","M",4,true);
        Book book4 = new Book("A","R","F",4,true);
        Collection<Book> books = new TreeSet<>(Set.of(book1,book2,book3,book4));
        TreeSet<Book> booksFromBinary = new TreeSet<>();

        try {
            ObjectSerializer.serializeToBinary(books,file);
            booksFromBinary = Book.deserializeFromBinary(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assertEquals(books, booksFromBinary, "Deserialized books do not match the original set");
    }

    @Test
    void testRandomEntryInBooksBinaryFileDeserialization(){
        File file = makeNewFile("Anything.bin");
        Book book1 = new Book("1","2","3",4,true);
        Book book2 = new Book("a","b","c",4,true);
        Book book3 = new Book("T","O","M",4,true);
        Book book4 = new Book("A","R","F",4,true);
        String wrong1 = "Not a Book";
        Integer wrong2 = 0;
        Collection<Book> correctBooks = new TreeSet<>(Set.of(book1,book2,book3,book4));
        Collection<Object> randomObjects = new HashSet<>(Set.of(book1,book2,book3,book4,wrong1,wrong2));
        TreeSet<Book> booksFromBIN = new TreeSet<Book>();
        try{
            ObjectSerializer.serializeToBinary(randomObjects,file);
            booksFromBIN = Objects.requireNonNull(Book.deserializeFromBinary(file));
            assertTrue(booksFromBIN.equals(correctBooks));

        }
        catch(IOException e){
            throw new RuntimeException(e);
        }
        catch (NullPointerException e){
            throw new RuntimeException(e);
        }
    }

    @Test
    void testFileForSerialization() {
        File file = makeNewFile("Books.csv");
        assertTrue(Book.testFileForSerialization(file, ".csv"));
        assertFalse(Book.testFileForSerialization(file, ".txt"));
        file.delete();

        file = makeNewFile("Book.xml");
        assertTrue(Book.testFileForSerialization(file, ".xml"));
        assertFalse(Book.testFileForSerialization(file, ".txt"));
        file.delete();

        file = makeNewFile("Book");
        assertFalse(Book.testFileForSerialization(file, ".csv"));
        assertFalse(Book.testFileForSerialization(file, ".txt"));
        file.delete();
    }

}
