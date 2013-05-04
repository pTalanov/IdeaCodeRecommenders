package ru.spbau.recommenders.plugin;

import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

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
//        String result = new String();
//        byte[] byteArray = baos.toByteArray();
//        for (int i = 0; i < byteArray.length; ++i) {
//            result += byteArray[i] + " ";
//        }
//        return result;
    }

    public T deserialize (String arg) throws IOException, ClassNotFoundException {
//        StringTokenizer strTok = new StringTokenizer(arg);
//        ArrayList<Byte> bytesBuf = new ArrayList<Byte>();
//        while (strTok.hasMoreTokens())  {
//            byte data = Byte.parseByte(strTok.nextToken());
//            bytesBuf.add(data);
//        }
//        byte[] bytes = new byte[bytesBuf.size()];
//        for (int i = 0; i < bytesBuf.size(); ++i) {
//            bytes[i] = bytesBuf.get(i);
//        }
//        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
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
            //System.out.println(s);
            ArrayList<Integer> result = serializer.deserialize(s);
            for (int i = 0; i < 10; ++i) {
                System.out.println(result.get(i));
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ClassNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

}
