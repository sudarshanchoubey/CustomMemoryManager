/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package custommemorymanager;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;

/**
 *
 * @author schoubey
 */
public class CMMinFile {
    HashSet<String> availableObjects;
    
    public CMMinFile() {
        availableObjects = new HashSet<>();
    }
    
    public boolean persist(String objectName, Object obj) 
            throws FileNotFoundException, IOException {
        try (FileOutputStream fs = new FileOutputStream(objectName + ".ser"); 
             ObjectOutputStream os = new ObjectOutputStream(fs)) {
            os.writeObject(obj);
            availableObjects.add(objectName);
            os.close();
            fs.close(); 
        } catch (Exception e) {
            return false;
        }
        System.out.println(objectName + " saved");
        return true;
    }

    public Object fetch(String objectName)
            throws FileNotFoundException, IOException, ClassNotFoundException {
        if (availableObjects.contains(objectName)) {
            Object obj;
            try (FileInputStream fs = new FileInputStream(objectName + ".ser"); 
                 ObjectInputStream os = new ObjectInputStream(fs)) {
                 obj = os.readObject();
            }
            return obj;
        } else {
            return null;
        }
    }
    
    public void evict(String objectName) {
        if (availableObjects.contains(objectName)) {
            File file = new File(objectName + ".ser");
            if (file.delete()) {
                availableObjects.remove(objectName);
            }
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
       CMMinFile cmm = new CMMinFile();
       Integer i = 5;
       cmm.persist("i", i);
       i = (Integer) cmm.fetch("i");
       System.out.println(i);
       cmm.evict("i");
       i = (Integer) cmm.fetch("i");
       if (i == null) {
           System.out.println("i is null");
       } 
    }
}
