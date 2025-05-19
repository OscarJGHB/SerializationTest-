package org.Book;

import com.thoughtworks.xstream.XStream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;


public class Main {
    public static void main(String[] args) {

        File file = new File("Library.xml");

        Book doawk = new Book("Diary of a Wimpy Kid: Long Haul", "Greg Heffley", "Humor", 2020);
        Book doawk1 = new Book("Diary of a Wimpy Kid: Dog Days", "Greg Heffley", "Humor", 2017);
        Book tkamb = new Book("TKAMB","DOUG","FUNNY",2022);
        Book tkamb2 = new Book("Te","D","Romance",2022);
        Book invisible = new Book("Invisible","Rob","Novel",2013);

        Collection<Book> books = new ArrayList<>(Set.of(doawk, doawk1, tkamb, tkamb2));
        LibraryIO.serializeToXML(books, file, true, false);


        TreeSet<Book> set = LibraryIO.deserializeFromXML(file);
        set.forEach(System.out::println);






    }
}