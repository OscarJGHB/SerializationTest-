package org.Book;

import com.thoughtworks.xstream.XStream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class Main {
    public static void main(String[] args) {

        File file = new File("Library.xml");

        Book doawk = new Book("Diary of a Wimpy Kid: Long Haul", "Greg Heffley", "Humor", 2020);
        Book doawk1 = new Book("Diary of a Wimpy Kid: Dog Days", "Greg Heffley", "Humor", 2017);
        Book tkamb = new Book("TKAMB","DOUG","FUNNY",2022);
        LibraryIO.serializeToXML(new ArrayList<Book>(List.of(doawk1,doawk,tkamb,doawk1)),file,false);

//           Book tjck = new Book("TKCS", "Greg Heffley", "Humor", 2017);
//        Book.serializeToXML(tjck, file);
//        Collection<Book> someBooks = LibraryIO.deserializeFromXML(file);
//        someBooks.forEach(System.out::println);
        //file.delete();




    }
}