package com.jean.music.utils;

import java.io.*;

/**
 * 序列化工具
 *
 * @author Jinshubao
 */
public class SerializeUtil {

    public static void serialize(Object object, String filePath) {
        ObjectOutputStream stream = null;
        try {
            stream = new ObjectOutputStream(new FileOutputStream(filePath));
            stream.writeObject(object);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static Object deSerialize(String filePath) {
        ObjectInputStream stream = null;
        try {
            stream = new ObjectInputStream(new FileInputStream(filePath));
            return stream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
