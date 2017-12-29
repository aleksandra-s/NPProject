/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1212.npproject4.device.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author aleks_uuia3ly
 */
public class DeviceFileRetrieve {
    public String getAvailablePulsesFromFile(String deviceSerialNumber) throws IOException {
        // Take the words from the word.txt file
        File file = new File("C:/Users/aleks_uuia3ly/Documents/NetBeansProjects/NPProject4/src/main/resources/device" + deviceSerialNumber + ".txt");
        boolean exists = file.exists();
        if(!exists){
            file.createNewFile();
            FileWriter fw = new FileWriter("C:/Users/aleks_uuia3ly/Documents/NetBeansProjects/NPProject4/src/main/resources/device" + deviceSerialNumber + ".txt");
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("0");
            bw.close();
        }
        try (FileInputStream getFile = new FileInputStream("C:/Users/aleks_uuia3ly/Documents/NetBeansProjects/NPProject4/src/main/resources/device" + deviceSerialNumber + ".txt");
            BufferedReader getData = new BufferedReader(new InputStreamReader(new DataInputStream(getFile)))) {

            String credits = "";
            //Get a random word from the txt file
            credits = getData.readLine();
            
            return credits;
        } catch (IOException ex) {
            throw ex;
        }
    }
    
    public void storeAvailablePulsesInFile(String deviceSerialNumber, String numberOfCredits) throws IOException{
        File file = new File("C:/Users/aleks_uuia3ly/Documents/NetBeansProjects/NPProject4/src/main/resources/device" + deviceSerialNumber + ".txt");
        boolean exists = file.exists();
        if(!exists){
            file.createNewFile();
        }
        FileWriter fw = new FileWriter("C:/Users/aleks_uuia3ly/Documents/NetBeansProjects/NPProject4/src/main/resources/device" + deviceSerialNumber + ".txt");
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(numberOfCredits);
        bw.close();
    }
}
