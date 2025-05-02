import java.io.*;
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
            if(file.length() == 0 || !(scnr.nextLine()).equals(Book.csvHeader) ) {
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
        if(dsFile == null || !file.getAbsolutePath().equals(dsFile.getAbsolutePath()))
        {
            if(!(file.exists() && file.isFile() && file.getName().endsWith(".csv") && file.length() != 0)) {
                System.out.printf("File \"%10s\" is invalid to deserialize\n", file.getName());
                return null;
            }
            dsFile = file;
            dsCurrentLine = 1;
        }

        try(Scanner scnr = new Scanner(file)){
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
                    return new Book(bookStats[0], bookStats[1], bookStats[2], Integer.parseInt(bookStats[3]));
                }
                else {
                    System.out.printf("Corrupted entry within \"%10s\"\n",file.getName());
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
