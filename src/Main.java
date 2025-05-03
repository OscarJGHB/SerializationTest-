import java.io.File;

public class Main {
    public static void main(String[] args) {
        Book doawk = new Book("Diary of a Wimpy Kid", "Greg Heffley", "Humor", 2017);
        File books = new File("books.csv");
        Book.serialize(doawk, books);
        Book doawk2 = Book.deserialize(books);



    }
}