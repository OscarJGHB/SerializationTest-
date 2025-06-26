package org.BookManager.gui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import org.BookManager.Book;

import java.io.File;

public class BookCoverClass extends Canvas {

    private Book book;
    private double width, height;

    //TODO add right click functions
    private BookCoverClass(Rectangle bounds, Book book) {
        this.width = bounds.getWidth();
        this.height = bounds.getHeight();
        this.book = book;

        setWidth(width);
        setHeight(height);

        addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {});
    }

    public static BookCoverClass generateBookCover(Rectangle bounds, Book book) {
        return new BookCoverClass(bounds, book);
    }

    public void paintInit(){
        getGraphicsContext2D().clearRect(0, 0, width, height);
    }

    public void repaint() {
        GraphicsContext gc = getGraphicsContext2D();
        paint(gc);
    }

    private void paint(GraphicsContext gc) {
        paintInit();
        gc.setFill(Color.AQUA);
        gc.fillRect(0, 0, width, height);

        File picFile = new File(book.getBookCoverFile());
        if (!picFile.exists()) {
            picFile = new File("BookManagerApp/src/main/resources/org/BookManager/gui/DefaultPics/black.jpg");
        }
        Image image = new Image(picFile.toURI().toString());

        gc.drawImage(image, 0, 0, width, height);

        gc.setFill(Color.PAPAYAWHIP);
        gc.fillRect(getLayoutX(), height-25, width, 25);

        gc.setFill(Color.BLACK);
        gc.setFont(new Font("Times New Roman", 12));
        gc.fillText(book.getTitle(),getLayoutX() , getHeight()-gc.getFont().getSize()/2);
    }
}
