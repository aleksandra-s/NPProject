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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author aleks_uuia3ly
 */
public class DeviceFileRetrieve {
    public String getDataFromFile(String deviceSerialNumber) throws IOException {
        // Take the words from the word.txt file
        File file = new File("C:/Users/aleks_uuia3ly/Documents/NetBeansProjects/NPProject4/src/main/resources/device" + deviceSerialNumber + ".txt");
        boolean exists = file.exists();
        if(!exists){
            file.createNewFile();
            FileWriter fw = new FileWriter("C:/Users/aleks_uuia3ly/Documents/NetBeansProjects/NPProject4/src/main/resources/device" + deviceSerialNumber + ".txt");
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("0");
            bw.write("0");
            bw.close();
        }
        try (FileInputStream getFile = new FileInputStream("C:/Users/aleks_uuia3ly/Documents/NetBeansProjects/NPProject4/src/main/resources/device" + deviceSerialNumber + ".txt");
            BufferedReader getData = new BufferedReader(new InputStreamReader(new DataInputStream(getFile)))) {

            StringBuilder credits;
            StringBuilder subscription;
            //Get a random word from the txt file
            credits = new StringBuilder(getData.readLine());
            credits.append(",");
            subscription = new StringBuilder(getData.readLine());
            getData.close();
            return (credits.append(subscription)).toString();
            //return credits.toString();
        } catch (IOException ex) {
            throw ex;
        }
    }
    
    public void storePulsesFromServerInFile(String deviceSerialNumber, String numberOfCredits) throws IOException{
        File file = new File("C:/Users/aleks_uuia3ly/Documents/NetBeansProjects/NPProject4/src/main/resources/device" + deviceSerialNumber + ".txt");
        boolean exists = file.exists();
        if(!exists){
            file.createNewFile();
        }
        Path path = Paths.get("C:/Users/aleks_uuia3ly/Documents/NetBeansProjects/NPProject4/src/main/resources/device" + deviceSerialNumber + ".txt");
        List<String> fileContent = new ArrayList<>(Files.readAllLines(path, StandardCharsets.UTF_8));
        fileContent.set(0,numberOfCredits);
        /*for (int i = 0; i < fileContent.size(); i++) {
            if (fileContent.get(i).equals("old line")) {
                fileContent.set(i, "new line");
                break;
            }
        }*/

        Files.write(path, fileContent, StandardCharsets.UTF_8);
        /*FileWriter fw = new FileWriter("C:/Users/aleks_uuia3ly/Documents/NetBeansProjects/NPProject4/src/main/resources/device" + deviceSerialNumber + ".txt");
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(numberOfCredits);
        bw.close();*/
    }
    
    public void storeUsedPulsesFromDeviceInFile(String deviceSerialNumber, String pulsesUsed) throws IOException{
        File file = new File("C:/Users/aleks_uuia3ly/Documents/NetBeansProjects/NPProject4/src/main/resources/device" + deviceSerialNumber + ".txt");
        boolean exists = file.exists();
        
        if(!exists){
            file.createNewFile();
        }
        
        Path path = Paths.get("C:/Users/aleks_uuia3ly/Documents/NetBeansProjects/NPProject4/src/main/resources/device" + deviceSerialNumber + ".txt");
        List<String> fileContent = new ArrayList<>(Files.readAllLines(path, StandardCharsets.UTF_8));
        fileContent.set(2,pulsesUsed);

        Files.write(path, fileContent, StandardCharsets.UTF_8);
    }
}
