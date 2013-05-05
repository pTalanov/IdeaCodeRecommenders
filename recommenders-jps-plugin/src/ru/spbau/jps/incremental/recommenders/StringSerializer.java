package ru.spbau.jps.incremental.recommenders;

import java.io.*;

/**
 * @author Goncharova Irina
 *         Date: 01.05.13
 */
public class StringSerializer<T extends Serializable> {
    public String serialize(T arg) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oStream = new ObjectOutputStream(baos);
        oStream.writeObject(arg);
        byte[] data = baos.toByteArray();
        oStream.close();
        return new String(data, "ISO-8859-1");
    }

    public T deserialize(String arg) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(arg.getBytes("ISO-8859-1"));
        ObjectInputStream iStream = new ObjectInputStream(bais);
        T result = null;
        try {
            result = (T) iStream.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Serializer malfunction: ClassNotFoundException in ObjectInputStream");
        } finally {
            iStream.close();
        }
        return result;
    }
}
