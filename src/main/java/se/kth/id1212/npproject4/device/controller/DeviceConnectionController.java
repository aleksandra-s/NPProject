/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1212.npproject4.device.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
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
    private Date subscriptionDate;
    private String subscriptionString;
    private final DeviceFileRetrieve fileRetrieve;
    private final String keyString = "devicekey";
    private final String deviceHash;
    
    public DeviceConnectionController(String id) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException{
        fileRetrieve = new DeviceFileRetrieve();
        deviceSerialNumber = id;
        deviceHash = sha1(deviceSerialNumber,keyString);
        deviceURL = REST_URI + "/" + id + "/" + deviceHash;
    }
    
    public void start(){
        new Thread(this).start();
    }
    
    @Override
    public void run() {
        try {
            while (true) {
                getDeviceInfo();
                updatePulses(this.numberOfPulses);
                updateSubscriptionDate(this.subscriptionDate, this.subscriptionString);
                Thread.sleep(60 * 1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void getDeviceInfo() throws MalformedURLException, ProtocolException, IOException {
        URL url = new URL(deviceURL);
        System.out.println(deviceURL);
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
           parsePulses(line); //Parsing out pulses
           //System.out.println("Pulses received from server: " + numberOfPulses);
           parseSubscription(line); //Parsing out subscription
           //System.out.println("Subscription received from server " + subscriptionString);
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
        //System.out.println(numberOfPulses);
    }
    
    public void updatePulses(int receivedPulses) throws IOException{
        String deviceDataFromFile = fileRetrieve.getDataFromFile(deviceSerialNumber);
        int i;
        for(i = 0; i < deviceDataFromFile.length(); i++){
            if(deviceDataFromFile.charAt(i) == ','){
                break;
            }
        }
        int pulsesFromFile = Integer.parseInt(deviceDataFromFile.substring(0,i)); //get pulses from file
        /*int compare = pulsesFromFile - receivedPulses;
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
        }*/
        String pulsesToStore = Integer.toString(pulsesFromFile + receivedPulses);
        fileRetrieve.storePulsesFromServerInFile(deviceSerialNumber, pulsesToStore);
        updateServerDatabase();
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
        this.subscriptionString = temp.toString();
        try {
            this.subscriptionDate = new SimpleDateFormat("yyyy-MM-dd").parse(subscriptionString);
            //System.out.println("Printing date after parsing:" + this.subscriptionEnd);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        //this.subscriptionEnd = ;
        //System.out.println(subscriptionDate);
    }
    
    public void updateSubscriptionDate(Date receivedSubscription, String subscriptionString) throws IOException{
        String deviceDataFromFile = fileRetrieve.getDataFromFile(deviceSerialNumber);
        //System.out.println("device data from file: " + deviceDataFromFile);
        int j = 0;
        int k = 0;
        int commaCounter = 0;
        Date subscriptionFromFile = null;
        for(int i = 0; i < deviceDataFromFile.length(); i++){
            //System.out.println(deviceDataFromFile.charAt(i));
            if(deviceDataFromFile.charAt(i) == ',' && commaCounter == 0){
                commaCounter++;
                j = i;
                //System.out.println("Comma counter " + commaCounter);
            }
            else if(deviceDataFromFile.charAt(i) == ',' && commaCounter == 1){
                //commaCounter++;
                k = i;
                break;
            }
        }
        //System.out.println("J: " + j);
        String subscriptionData = deviceDataFromFile.substring(j+1,k); //get pulses from file
        try {
            subscriptionFromFile = new SimpleDateFormat("yyyy-MM-dd").parse(subscriptionData);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }; //get pulses from file
        if(!receivedSubscription.equals(subscriptionFromFile)){
            fileRetrieve.storeSubscriptionDateFromServerInFile(deviceSerialNumber, subscriptionString);
        }
    }
    
    public void updateServerDatabase() throws MalformedURLException, IOException{
        URL url = new URL(deviceURL + "/0");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setInstanceFollowRedirects(false);
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Content-Type", "application/xml");
        //OutputStream os = connection.getOutputStream();
        //os.write("<customer id='333'/>".getBytes());
       // os.flush();
        /*if (connection.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
           throw new RuntimeException("Failed to create customer" + connection.getResponseCode());
        }*/
        System.out.println("Location: " + connection.getHeaderField("Location"));
        connection.disconnect();
    }
    private String sha1(String s, String keyString) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {

        SecretKeySpec key = new SecretKeySpec((keyString).getBytes("UTF-8"), "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(key);

        byte[] bytes = mac.doFinal(s.getBytes("UTF-8"));
        //String temp = new String(Base64.getEncoder().encode(bytes));
        Base64.Encoder enc = Base64.getUrlEncoder();
        
        return new String(enc.encode(bytes));

    }
}

