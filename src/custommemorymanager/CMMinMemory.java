/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package custommemorymanager;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.HashMap;

/**
 *
 * @author schoubey
 */
public class CMMinMemory {
    
    private class Location {
        int location;
        int length;
    }
    HashMap<String, Location> availableObjects;
    byte[] myBytes;
    boolean[] freeChunks;
    public CMMinMemory(int n) {
        availableObjects = new HashMap<>();
        freeChunks = new boolean[n * 4 * 1024];
        for (int i = 0; i < freeChunks.length; i++) {
            freeChunks[i] = true;
        }
        myBytes = new byte[n * 1024 * 1024];
    }
    
    public boolean persist(String objectName, Object obj) 
            throws FileNotFoundException, IOException {
        if (availableObjects.containsKey(objectName)) {
            this.evict(objectName);
        }
        try (ByteArrayOutputStream bs = new ByteArrayOutputStream();
             ObjectOutputStream os = new ObjectOutputStream(bs)) {
            os.writeObject(obj);
            byte[] ba = bs.toByteArray();
            int length = ba.length;
            System.out.println("length of data " + Integer.toString(length));
            boolean success = false;
            int pos = 0;
            if (length <= 256) {
                for (pos = 0; pos < freeChunks.length; pos++) {
                    if (freeChunks[pos] == true) {
                        System.arraycopy(ba, 0, myBytes, pos * 256, length);
                        success = true;
                        freeChunks[pos] = false;
                        break;
                    }
                }
            } else {
                for (pos = 0; pos < freeChunks.length; pos++) {
                    if (freeChunks[pos] == true) {
                        boolean flag = false;
                        int i = pos;
                        for (i = pos; i <= Math.min(pos + length / 256, freeChunks.length - 1); i++) {
                            if (freeChunks[i] == true) {
                                flag = true;
                            } else {
                                flag = false;
                                break;
                            }
                        }
                        if (flag && (i < freeChunks.length)) {
                            System.arraycopy(ba, 0, myBytes, pos * 256, length);
                            for (int j = pos; j <= pos + length / 256; j++) {
                                freeChunks[j] = false;
                            }
                            success = true;
                            break;
                        }
                    }
                }
            }
            
            if (success) {
                Location loc = new Location();
                loc.length = length;
                loc.location = pos;
                availableObjects.put(objectName, loc);
                System.out.println(objectName + " saved");
            }
            os.close();
            bs.close();
            return success;
        }
    }

    public Object fetch(String objectName)
            throws FileNotFoundException, IOException, ClassNotFoundException {
        if (availableObjects.containsKey(objectName)) {
            Object obj;
            try (
                ByteArrayInputStream bs = new ByteArrayInputStream(myBytes, 
                    availableObjects.get(objectName).location * 256,
                    availableObjects.get(objectName).length); 
                ObjectInputStream os = new ObjectInputStream(bs);) {
                obj = os.readObject();
                return obj;
            }
        } else {
            return null;
        }
    }
    
    public void evict(String objectName) {
        if (availableObjects.containsKey(objectName)) {
            Location loc = availableObjects.get(objectName);
            if (loc.length <= 256) {
                freeChunks[loc.location] = true;
            } else {
                for (int i = loc.location; i <= (loc.location + (loc.length) / 256); i++) {
                    freeChunks[i] = true;
                }
            }
            availableObjects.remove(objectName);
        }
    }
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws java.io.FileNotFoundException
     * @throws java.lang.ClassNotFoundException
     */
    public static void main(String[] args) 
            throws IOException, FileNotFoundException, ClassNotFoundException {
       CMMinMemory cmm = new CMMinMemory(4);
       Integer[] arr = new Integer[10000];
       for (int i = 0; i < arr.length; i++) {
           arr[i] = i;
       }
       cmm.persist("arr", arr);
       arr = null;
       arr = (Integer[]) cmm.fetch("arr");
       System.out.println(Arrays.toString(arr));
       for (int i = 0; i < arr.length; i = i+3) {
           System.out.println(arr[i]);
       }
       cmm.evict("arr");
       arr = (Integer[]) cmm.fetch("i");
       if (arr == null) {
           System.out.println("i is null");
       } 
    }
}
