import java.io.*;
import java.util.Scanner;



public class Book
{
    private static final String csvFormat= "title,author,genre,year";
    private String title;
    private String author;
    private String genre;
    private int year;


    public Book(String title, String author, String genre, int year)
    {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.year = year;


    }

    public static void serialize(Book book, File file)
    {

        try{
            if(file.getName().endsWith(".csv")){ //checks if file name is valid
                if(!file.exists()) { //if valid, checks if it exists
                    if(file.createNewFile()){ //creates a new file if not
                        System.out.printf("New File \"%10s\" created\n", file.getName());
                    }
                }
                else if(!file.isFile()){ //if valid, checks if possible directory
                    System.out.printf("File \"%10s\" is not a file\n", file.getName());
                    return;
                }
            }
            else{
                System.out.println("File is not a CSV file");
                return;
            }

        }
        catch(IOException e) {
            System.out.println("Error creating file");
        }


        try{
            Scanner scnr = new Scanner(file);
            FileWriter fw = null;
            if(file.length() == 0 || !(scnr.nextLine()).equals(Book.csvFormat) ){
                scnr.close();
                fw = new FileWriter(file);
                fw.write("title,author,genre,year\n");
            }
            else {
                fw = new FileWriter(file, true);
            }

            fw.write(book.title + "," + book.author + "," + book.genre + "," + book.year + "\n");
            fw.close();
        }
        catch(IOException e) {
            System.out.println("Error accessing file");
        }
        catch(NullPointerException e){
            System.out.println("Error writing to file");
        }

    }

    public static Book deserialize(File file)
    {

        //deserialize into a Book obj
        return null;
    }


    @Override
    public boolean equals(Object obj)
    {
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
}
