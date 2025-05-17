package org.Book;

import com.thoughtworks.xstream.XStream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;

public class ObjectSerializer {
    public static void serializeToXML(Object obj, XStream xstream, String rootName, File file){
        try(ObjectOutputStream out = xstream.createObjectOutputStream(new FileOutputStream(file),rootName)){
            if(obj instanceof Collection){
                for(Object o : (Collection<?>)obj){
                    out.writeObject(o);
                }
            }
            else{out.writeObject(obj);}
        } catch (Exception e) {
            System.out.println("Error serializing object");
        }
    }
}
