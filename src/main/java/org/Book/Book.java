package org.Book;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.basic.BooleanConverter;

import java.io.Serializable;
import java.util.Objects;

@XStreamAlias("Book")
public class Book implements Serializable, Comparable<Book> {

    @XStreamAsAttribute
    private String title;
    private String author;
    private String genre;
    private int year;
    @XStreamConverter(value = BooleanConverter.class,booleans = {true,false},strings = {"yes","no"})
    private boolean pictures;

    public Book(){
        this.title = "";
        this.author = "";
        this.genre = "";
        this.year = 0;
    }
    public Book(String title, String author, String genre, int year, boolean pictures) {
        this.title = title.trim();
        this.author = author.trim();
        this.genre = genre.trim();
        this.year = year;
        this.pictures = pictures;
    }

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
                && book.year == this.year);
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, title, genre, year);
    }

    @Override
    public String toString() {
        return title + "," + author + "," + genre + "," + year;
    }

}