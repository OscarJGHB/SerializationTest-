import java.io.File;

public class Book
{
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
        //TODO:
        //check if there is a valid book obj
        //check if file exists
            //if doesn't exist then make it using the path name specified as long as it is valid
        //if does:
            //check if empty
            //if not empty then check for correct column names
            //if empty then create correct column names, then add the book obj
        //to .csv files

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
        return ((Book) obj).author.equals(this.author)
                && ((Book) obj).title.equals(this.title)
                && ((Book) obj).genre.equals(this.genre)
                && ((Book) obj).year == this.year;
    }
}
