package org.Book;

import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.file.Files;
import java.util.Scanner;



public class Book
{
    private static final String csvHeader = "title,author,genre,year";
    private static File dsFile;
    private static int dsCurrentLine;
    private String title;
    private String author;
    private String genre;
    private int year;


    public Book(String title, String author, String genre, int year)
    {
        this.title = title.trim();
        this.author = author.trim();
        this.genre = genre.trim();
        this.year = year;


    }


    public static String getFormat(){
        return csvHeader;
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

            System.out.println("Error accessing file");
            return;
        }


        try(Scanner scnr = new Scanner(file)){
            boolean emptyOrInvalid = (file.length() == 0 || !(scnr.hasNextLine() && scnr.nextLine().trim().equals(Book.csvHeader.trim())));
            try(FileWriter fw = new FileWriter(file, !emptyOrInvalid))
            {
                if(emptyOrInvalid)
                {
                    fw.write(csvHeader + "\n");
                }
                fw.write(book.title + "," + book.author + "," + book.genre + "," + book.year + "\n");

            }
        }
        catch(IOException e) {
            System.out.println("Error accessing file");
        }

    }

    public static Book deserialize(File file)
    {

        //tests to see if we already have a file
        try {
            if (dsFile == null || (Files.isSameFile(dsFile.toPath(), file.toPath()))) {
                //checks validity
                if (!(file.exists() && file.isFile() && file.getName().endsWith(".csv") && file.length() != 0)) {
                    System.out.printf("File \"%10s\" is invalid to deserialize\n", file.getName());
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
            if(!(scnr.hasNextLine() && scnr.nextLine().trim().equals(Book.csvHeader.trim()))){
                System.out.printf("File \"%10s\"'s header is invalid\n", file.getName());
                return null;
            };

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
