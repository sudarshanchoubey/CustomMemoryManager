/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package custommemorymanager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

/**
 *
 * @author schoubey
 */
public class CMM {
    HashSet<String> availableObjects;
    CMMinFile cFile;
    CMMinMemory cMem;
    
    public CMM() {
        cFile = new CMMinFile();
        cMem = new CMMinMemory(4);
        availableObjects = new HashSet<>();
    }
    
    public void persist(String objectName, Object obj) throws IOException {
        if (availableObjects.contains(objectName)) {
            cMem.evict(objectName);
            cFile.evict(objectName);
        }
        if (cMem.persist(objectName, obj)) {
            System.out.println("Using in memory locations");
            availableObjects.add(objectName);
        } else {
            if (cFile.persist(objectName, obj)) {
                System.out.println("Using in file locations");
                availableObjects.add(objectName);
                return;
            }
            System.out.println("Failed to save object");
        }
    }
    
    public Object fetch(String objectName) 
            throws IOException, FileNotFoundException, ClassNotFoundException {
        Object obj = null;
        if (availableObjects.contains(objectName)) {
            obj = cMem.fetch(objectName);
            if (obj == null) {
                System.out.println(objectName + " not found in memory");
                obj = cFile.fetch(objectName);   
            }
        }
        return obj;
    }
    
    public void evict(String objectName) {
        if (availableObjects.contains(objectName)) {
            cMem.evict(objectName);
            cFile.evict(objectName);
            availableObjects.remove(objectName);
        }
    }
    
    public static void main(String[] args) 
            throws IOException, FileNotFoundException, ClassNotFoundException {
       CMM cmm = new CMM();
       //create array which will fill up half the in memory system
       Integer[] arr = new Integer[209700];
       for (int i = 0; i < arr.length; i++) {
           arr[i] = i;
       }
       //save the array
       cmm.persist("arr", arr);
       arr = (Integer[]) cmm.fetch("arr");
       for (int i = 0; i < arr.length; i = i+50000) {
           System.out.println(arr[i]);
       }
       //save a string
       String test = "testing string";
       cmm.persist("test", test);
       test = null;
       test = (String) cmm.fetch("test");
       System.out.println("test is " + test);
       //save another array this will go in file system
       Integer[] arr2 = new Integer[209700];
       for (int i = 0; i < arr2.length; i++) {
           arr2[i] = i;
       }
       cmm.persist("arr2", arr2);
       arr2 = null;
       arr2 = (Integer[]) cmm.fetch("arr2");
       for (int i = 0; i < arr2.length; i = i+10000) {
           System.out.println(arr2[i]);
       }
       cmm.evict("arr2");
       arr2 = (Integer[]) cmm.fetch("arr2");
       if (arr2 == null) {
           System.out.println("Passed:arr2 was evicted");
       } else {
           System.out.println("Failed:arr2 was  not evicted");
       }
       cmm.evict("test");
       test = (String) cmm.fetch("test");
       if (test == null) {
           System.out.println("Passed:test was evicted");
       } else {
           System.out.println("Failed:test was  not evicted");
       }
       cmm.evict("arr");
       arr = (Integer[]) cmm.fetch("arr");
       if (arr == null) {
           System.out.println("Passed:arr was evicted");
       } else {
           System.out.println("Failed:arr was  not evicted");
       } 
    }
}
