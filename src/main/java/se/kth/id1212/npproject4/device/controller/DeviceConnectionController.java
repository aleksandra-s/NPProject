/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1212.npproject4.device.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
//import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import se.kth.id1212.npproject4.device.model.DeviceFileRetrieve;
import se.kth.id1212.npproject4.web.model.DeviceEntity;

/**
 *
 * @author aleks_uuia3ly
 */
public class DeviceConnectionController implements Runnable{
    private static final String REST_URI = "http://localhost:8080/NPProject4/webresources/se.kth.id1212.npproject4.web.model.deviceentity";
    private final String deviceURL;
    private final String deviceSerialNumber;
    private int numberOfPulses;
    private Date subscriptionEnd;
    private final DeviceFileRetrieve fileRetrieve;
    
    public DeviceConnectionController(String id){
        deviceURL = REST_URI + "/" + id;
        fileRetrieve = new DeviceFileRetrieve();
        deviceSerialNumber = id;
    }
    
    public void start(){
        new Thread(this).start();
    }
    
    @Override
    public void run() {
        try {
            while (true) {
                getDeviceInfo();
                updatePulses(numberOfPulses);
                Thread.sleep(5 * 1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void getDeviceInfo() throws MalformedURLException, ProtocolException, IOException {
        URL url = new URL(deviceURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/xml");

        if (connection.getResponseCode() != 200) {
          throw new RuntimeException("Operation failed: "
                                      + connection.getResponseCode());
        }

        System.out.println("Content-Type: " + connection.getContentType());

        BufferedReader reader = new BufferedReader(new
                      InputStreamReader(connection.getInputStream()));

        String line = reader.readLine();
        while (line != null) {
           System.out.println(line);
           parsePulses(line);
           parseSubscription(line);
           line = reader.readLine();
        }
        connection.disconnect();
    }
    
    public void parsePulses(String httpResponse){
        int charCounter = 0;
        boolean setJ = false;
        int j = 0;
        int k = 0;
        for(int i = 0; i < httpResponse.length(); i++){
            if(httpResponse.charAt(i) == '<' || httpResponse.charAt(i) == '>'){
                charCounter++;
            }
            if(charCounter == 6 && !setJ){
                j = i;
                setJ = true;
            }
            if(charCounter == 7){
                k = i;
                break;
            }
        }
        String pulses = httpResponse.substring(j + 1,k);
        this.numberOfPulses = Integer.parseInt(pulses);
        System.out.println(numberOfPulses);
    }
    
    public void updatePulses(int receivedPulses) throws IOException{
        int pulsesFromFile = Integer.parseInt(fileRetrieve.getDataFromFile(deviceSerialNumber)); //get pulses from file
        int compare = pulsesFromFile - receivedPulses;
        if(compare <= 0){
            //send receivedPulses + pulsesFromFile to file
            String pulsesToStore = Integer.toString(pulsesFromFile + receivedPulses);
            fileRetrieve.storePulsesFromServerInFile(deviceSerialNumber, pulsesToStore);
        }
        else if(compare > 0){
            if(compare != pulsesFromFile){
                //send receivedPulses + pulsesFromFile to file
                String pulsesToStore = Integer.toString(pulsesFromFile + receivedPulses);
                fileRetrieve.storePulsesFromServerInFile(deviceSerialNumber, pulsesToStore);
            }
        }
    }
    
    public void parseSubscription (String httpResponse){
        int charCounter = 0;
        StringBuilder temp = new StringBuilder();
        /*boolean setJ = false;
        int j = 0;
        int k = 0;*/
        for(int i = 0; i < httpResponse.length(); i++){
            if(httpResponse.charAt(i) == '>'){
                charCounter++;
            }
            if(charCounter == 7){
                if(httpResponse.charAt(i + 1) == 'T'){
                    break;
                }
                temp.append(httpResponse.charAt(i + 1));
            }
        }
        String subscriptionDate = temp.toString();
        try {
            subscriptionEnd = new SimpleDateFormat("yyyy-MM-dd").parse(subscriptionDate);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        //this.subscriptionEnd = ;
        System.out.println(subscriptionDate);
    }
}

