package org.BookManager;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;


public class BookConverter implements Converter {

    @Override
    public boolean canConvert(Class clazz) {
        return Book.class.isAssignableFrom(clazz);
    }

    @Override
    public void marshal(Object obj, HierarchicalStreamWriter writer, MarshallingContext context) {
        Book book = (Book) obj;

        writer.addAttribute("title",book.getTitle());

        writer.startNode("author");
        writer.setValue(book.getAuthor());
        writer.endNode();

        writer.startNode("genre");
        writer.setValue(book.getGenre());
        writer.endNode();

        writer.startNode("released");
        writer.setValue(String.valueOf(book.getYear()));
        writer.endNode();

        writer.startNode("thumbnail");
        writer.setValue(book.getBookCoverFile());
        writer.endNode();

        writer.startNode("pictures");
        writer.setValue(book.getPictures() ? "yes" : "no");
        writer.endNode();
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        Book book = new Book();
        book.setTitle(reader.getAttribute("title"));

        reader.moveDown();
        book.setAuthor(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        book.setGenre(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        book.setYear(Integer.parseInt(reader.getValue()));
        reader.moveUp();

        reader.moveDown();
        book.setBookCoverFile(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        String pictures = reader.getValue();
        book.setPictures(pictures.equals("yes"));
        reader.moveUp();

        return book;
    }
}
