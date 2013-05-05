package ru.spbau.jps.incremental.recommenders;

import java.io.*;
import java.util.ArrayList;

/**
 * @author Goncharova Irina
 * Date: 01.05.13
 */
public class StringSerializer <T extends Serializable> {
    public String serialize(T arg) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oStream = new ObjectOutputStream(baos);
        oStream.writeObject(arg);
        return new String( baos.toByteArray(), "ISO-8859-1");
    }

    public T deserialize (String arg) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(arg.getBytes("ISO-8859-1"));
        ObjectInputStream iStream = new ObjectInputStream(bais);
        return (T) iStream.readObject();
    }

    public static void main(String [] args) {
        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        for (int i =0 ; i < 10; ++i) {
            arrayList.add(i);
        }
        StringSerializer<ArrayList<Integer> > serializer = new StringSerializer<ArrayList<Integer> >();
        try {
            String s = serializer.serialize(arrayList);
            ArrayList<Integer> result = serializer.deserialize(s);
            for (int i = 0; i < 10; ++i) {
                System.out.println(result.get(i));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
