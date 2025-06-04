package org.BookManager;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.mapper.CannotResolveClassException;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class ObjectSerializer {

    public static void serializeToBinary(Object obj, File file) throws IOException, IllegalArgumentException {
        if(!(obj instanceof Serializable)){
            throw new IllegalArgumentException("Object passed in must implement Serializable");
        }

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {

            List<Serializable> listOfObjects = new ArrayList<>();
            //unravel collections
            if(obj instanceof Collection){
                getNonCollectionObjs(Serializable.class,(Collection<?>) obj,listOfObjects);
            }
            else{
                listOfObjects.add((Serializable) obj);
            }
            for(Object o : listOfObjects){
                out.writeObject(o);
            }
        }
    }

    public static List<Serializable> deserializeFromBinary(File file) throws IOException {
        List<Serializable> listOfObjects = new ArrayList<>();
        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            while(true){
                try{
                    Object o = in.readObject();
                    if (o instanceof Collection) {
                        getNonCollectionObjs(Serializable.class, (Collection<?>) o, listOfObjects);
                    }
                    else if(o instanceof Serializable){
                        listOfObjects.add((Serializable) o);
                    }
                }
                catch(EOFException e){
                    break;
                }
            }
        }
        catch(EOFException e){
            //
        }
        catch(ClassNotFoundException e){
            System.out.println("Class not found: " + e.getMessage());
        }
        return listOfObjects;
    }
    /**
     * <h6>Description:</h6>
     * <li>Serializes any object by overwriting the entire file with the objects given using XStream.
     * </li>
     *
     *
     * @param obj  any object to be serialized... can be a collection(integrity determined by @param <strong>flatten</strong>
     * @param xstream  an instance of XStream with its own aliases set
     * @param rootName  String, can be custom, null, or blank
     * @param file  file to be parsed from.
     * @param keepStructure if Objects given should be written individually or with the data structure they are contained in
     * @throws IOException  In the event where file cannot be read from or written to
     *
     */
    public static void serializeToXML(Object obj, XStream xstream, String rootName, File file, boolean keepStructure) throws IOException {
        if (rootName == null || rootName.isEmpty()){
            rootName = "Root";
        }
        try (FileOutputStream fos = new FileOutputStream(file);
             ObjectOutputStream out = xstream.createObjectOutputStream(fos,rootName)){

            if (!keepStructure && obj instanceof Collection) {
                List<Object> objList = new ArrayList<>();
                getNonCollectionObjs(Object.class, (Collection<?>) obj, objList);
                for (Object o : objList) {
                    out.writeObject(o);
                }
            }
            else{
                out.writeObject(obj);
            }
        }
    }


    /**
     * <h6>Description:</h6>
     *      <l1>Deserializes any objects desired via <strong>desiredClass</strong> parameter. Will skip over corrupted material
     *      in an effort to preserve the data.</l1>
     * @param desiredClass  the desired class to be retrieved from the XML file. If all contents are to be retrieved, desired class should
     *                     be set to Object
     * @param xstream  instance of xstream
     * @param file  file to be parsed from
     * @param keepStructure if Objects given should be written individually or with the data structure they are contained in
     * @return HashSet<Object>  returns a hashSet of <strong>Object</strong> for the user to parse
     * @throws IOException  In the event where file cannot be read or written to

     */
    public static <T> List<T> deserializeObjFromXML(Class<T> desiredClass, XStream xstream, File file, boolean keepStructure) throws IOException {
        List<T> deserializedObjects = new ArrayList<>();
        try(FileInputStream fis = new FileInputStream(file);
            ObjectInputStream in = xstream.createObjectInputStream(fis)) {
            while(true){
                try{
                    Object obj = in.readObject();
                    if(!keepStructure && obj instanceof Collection<?>){
                            getNonCollectionObjs(desiredClass, (Collection<?>) obj, deserializedObjects);
                    }
                    else if(desiredClass.isInstance(obj)){
                        deserializedObjects.add(desiredClass.cast(obj));
                    }
                }
                catch(EOFException e){
                    break;
                }
                catch(ConversionException | StreamException | ClassNotFoundException e){
                    //skipping unknown class
                }
                catch(CannotResolveClassException e){
                    System.out.println("Object encountered in file needs to be allowed via XStream");
                }
            }
        }
        catch (NullPointerException e) {
            System.out.println("Null Pointer Exception");
            return deserializedObjects;
        }



        return deserializedObjects;
    }



    public static <T> void getNonCollectionObjs(Class<T> desiredClass, Collection<?> collection, List<T> ts){
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
