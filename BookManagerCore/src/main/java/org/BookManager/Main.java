package org.BookManager;

import java.io.*;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;


public class Main {
    public static void main(String[] args) {

        File fileXML = new File("Library.xml");
//
//        File fileCSV = new File("Library.csv");
//        File fileBIN = new File("Library.bin");
////
        Book tkamb = new Book("TKAMB","DOUG","FUNNY",2022,false);
        Book tkamb2 = new Book("Te","D","Romance",2022,false);
        Book invisible = new Book("Invisible","Rob","Novel",2013,true);


        ArrayList<Book> books = new ArrayList<>(Set.of(tkamb2,tkamb,invisible));
        try {
            Book.serializeToXML(books,fileXML);
            TreeSet<Book> treeSetOfBooks = Book.deserializeFromXML(fileXML);
            treeSetOfBooks.forEach(System.out::println);

        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


//
////
//        LibraryIO.serializeToXML(tkamb, fileXML);
//        LibraryIO.serializeToXML(tkamb2, fileXML);
//        LibraryIO.serializeToXML(invisible, fileXML);
//
//        TreeSet<Book> books = LibraryIO.deserializeFromXML(fileXML);
//        System.out.println(books.first().toString());
//        System.out.println(books.last().toString());

//        LibraryIO.serializeToCSV(tkamb, fileCSV);
//        LibraryIO.serializeToCSV(tkamb2, fileCSV);
//        LibraryIO.serializeToCSV(invisible, fileCSV);
//
//        Book doawk = new Book("Diary of a Wimpy Kid: Long Haul", "Greg Heffley", "Humor", 2020);
//        Book doawk1 = new Book("Diary of a Wimpy Kid: Dog Days", "Greg Heffley", "Humor", 2017);
//        Collection<Book> books = new ArrayList<>(Set.of(doawk, doawk1));
//        LibraryIO.serializeToXML(books, fileXML);
//        LibraryIO.serializeToCSV(books, fileCSV);
//
//        LibraryIO.serializeToXML(books, fileXML);
//        LibraryIO.serializeToCSV(books, fileCSV);
//
//        TreeSet<Book> set = LibraryIO.deserializeFromXML(fileXML);
//        TreeSet<Book> set1 = LibraryIO.deserializeFromCSV(fileCSV);
//
//        set.forEach(System.out::println);
//        set1.forEach(System.out::println);






    }
}