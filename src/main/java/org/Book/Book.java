package org.Book;

import java.util.Objects;

public class Book
{
    private String title;
    private String author;
    private String genre;
    private int year;

    public Book(){
        this.title = "";
        this.author = "";
        this.genre = "";
        this.year = 0;
    }
    public Book(String title, String author, String genre, int year) {
        this.title = title.trim();
        this.author = author.trim();
        this.genre = genre.trim();
        this.year = year;
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