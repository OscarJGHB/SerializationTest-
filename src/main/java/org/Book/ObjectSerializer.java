package org.Book;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.io.StreamException;

import java.io.*;
import java.util.Collection;
import java.util.HashSet;

public class ObjectSerializer {
    /**
     * <h6>Description:</h6>
     * <li>Serializes any object by overwriting the entire file with the objects given using XStream.
     * </li>
     *
     *
     * @param obj  any object to be serialized... can be a collection
     * @param xstream  an instance of XStream with its own aliases set
     * @param rootName  String, can be custom, null, or blank
     * @param file  file to be parsed from.
     * @param flatten if Objects given should be written individually or with the data structure they are contained in
     * @throws IOException  In the event where file cannot be read from or written to
     *
     */
    public static void serializeToXML(Object obj, XStream xstream, String rootName, File file, boolean flatten) throws IOException {
        if (rootName == null || rootName.isEmpty()){
            rootName = "Root";
        }
        ObjectOutputStream out = xstream.createObjectOutputStream(new FileOutputStream(file),rootName);
        if (flatten && obj instanceof Collection) {
            HashSet<Object> objHashSet = new HashSet<>();
            getNonCollectionObjs(Object.class, (Collection<?>) obj, objHashSet);
            for (Object o : objHashSet) {
                out.writeObject(o);
            }
        }
        else{
            out.writeObject(obj);
        }
        out.close();
    }


    /**
     * <h6>Description:</h6>
     *      <l1>Deserializes any objects desired via <strong>desiredClass</strong> parameter. Will skip over corrupted material
     *      in an effort to preserve the data.</l1>
     * @param desiredClass  the desired class to be retrieved from the XML file. If all contents are to be retrieved, desired class should
     *                     be set to Object
     * @param xstream  instance of xstream
     * @param file  file to be parsed from
     * @return HashSet<Object>  returns a hashSet of <strong>Object</strong> for the user to parse
     * @throws IOException  In the event where file cannot be read or written to

     */
    public static <T> HashSet<T> deserializeObjFromXML(Class<T> desiredClass, XStream xstream, File file) throws IOException {
        HashSet<T> deserializedObjects = new HashSet<>();
        try(ObjectInputStream in = xstream.createObjectInputStream(new FileInputStream(file))) {
            while(true){
                try{
                    Object obj = in.readObject();
                    if(desiredClass.isInstance(obj)){
                        deserializedObjects.add(desiredClass.cast(obj));
                    }
                    else if(obj instanceof Collection<?>){
                            getNonCollectionObjs(desiredClass, (Collection<?>) obj, deserializedObjects);
                    }
                }
                catch(EOFException e){
                    break;
                }
                catch(ConversionException | StreamException | ClassNotFoundException e){
                    //skipping unknown class
                }
            }
        }
        catch (NullPointerException e) {
            System.out.println("Null pointer exception");
            return deserializedObjects;
        }

        return deserializedObjects;
    }

    public static <T> void getNonCollectionObjs(Class<T> desiredClass, Collection<?> collection, HashSet<T> ts){
        for(Object o : collection){
            if(o instanceof Collection<?>){
                getNonCollectionObjs(desiredClass, (Collection<?>)o, ts);
            }
            else if(desiredClass.isInstance(o)){
                ts.add(desiredClass.cast(o));
            }
        }
    }
}
