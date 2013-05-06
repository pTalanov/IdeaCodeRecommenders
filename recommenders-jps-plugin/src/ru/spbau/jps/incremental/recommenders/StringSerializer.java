package ru.spbau.jps.incremental.recommenders;

import org.jetbrains.annotations.NotNull;

import java.io.*;

/**
 * @author Goncharova Irina
 */
@SuppressWarnings("unchecked")
public final class StringSerializer<T extends Serializable> {

    public String serialize(@NotNull T arg) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oStream = new ObjectOutputStream(baos);
        oStream.writeObject(arg);
        byte[] data = baos.toByteArray();
        oStream.close();
        return new String(data, "ISO-8859-1");
    }

    @NotNull
    public T deserialize(@NotNull String arg) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(arg.getBytes("ISO-8859-1"));
        ObjectInputStream iStream = new ObjectInputStream(bais);
        try {
            return (T) iStream.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Serializer malfunction: ClassNotFoundException in ObjectInputStream");
        } finally {
            iStream.close();
        }
    }
}
