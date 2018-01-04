
package se.kth.id1212.npproject4.device.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import se.kth.id1212.npproject4.device.model.DeviceFileRetrieve;

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
            while (true) { //Loop that polls database and updates info in device file
                getDeviceInfo();
                updatePulses(this.numberOfPulses);
                updateSubscriptionDate(this.subscriptionDate, this.subscriptionString);
                Thread.sleep(60 * 1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //Method to receive device information (credit balance and subscription expiry date) from database
    public void getDeviceInfo() throws MalformedURLException, ProtocolException, IOException {
        //Invoke RESTful GET method
        URL url = new URL(deviceURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/xml"); 

        if (connection.getResponseCode() != 200) {
          throw new RuntimeException("Operation failed: "
                                      + connection.getResponseCode());
        }

        //System.out.println("Content-Type: " + connection.getContentType());
        
        //Read content received
        BufferedReader reader = new BufferedReader(new
                      InputStreamReader(connection.getInputStream()));

        String line = reader.readLine();
       
        //Parse content received
        while (line != null) {
           //System.out.println(line);
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
    }
    
    //Update new amount of available pulses to device file
    public void updatePulses(int receivedPulses) throws IOException{
        //Get available pulse amount from file and parse 
        String deviceDataFromFile = fileRetrieve.getDataFromFile(deviceSerialNumber); 
        int i;
        for(i = 0; i < deviceDataFromFile.length(); i++){
            if(deviceDataFromFile.charAt(i) == ','){
                break;
            }
        }
        
        int pulsesFromFile = Integer.parseInt(deviceDataFromFile.substring(0,i)); 
        String pulsesToStore = Integer.toString(pulsesFromFile + receivedPulses);
        fileRetrieve.storePulsesFromServerInFile(deviceSerialNumber, pulsesToStore); //Store total amount of pulses ever available to device in file
        updateServerDatabase();  //Update the device credit balance stored in database to 0 because device has consumed them
    }
    
    public void parseSubscription (String httpResponse){
        int charCounter = 0;
        StringBuilder temp = new StringBuilder();
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
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
    }
    
    //Update new subscription expiry date to device file
    public void updateSubscriptionDate(Date receivedSubscription, String subscriptionString) throws IOException{
        //Get subscription expiry date from file and parse 
        String deviceDataFromFile = fileRetrieve.getDataFromFile(deviceSerialNumber);
        int j = 0;
        int k = 0;
        int commaCounter = 0;
        Date subscriptionFromFile = null;
        for(int i = 0; i < deviceDataFromFile.length(); i++){
            if(deviceDataFromFile.charAt(i) == ',' && commaCounter == 0){
                commaCounter++;
                j = i;
            }
            else if(deviceDataFromFile.charAt(i) == ',' && commaCounter == 1){
                k = i;
                break;
            }
        }
        String subscriptionData = deviceDataFromFile.substring(j+1,k); 
        try {
            subscriptionFromFile = new SimpleDateFormat("yyyy-MM-dd").parse(subscriptionData);
        } catch (ParseException ex) {
            ex.printStackTrace();
        } 
        
        //Store new subscription expiry date in file
        if(!receivedSubscription.equals(subscriptionFromFile)){
            fileRetrieve.storeSubscriptionDateFromServerInFile(deviceSerialNumber, subscriptionString);
        }
    }
    
    //Update the device credit balance stored in database to 0 
    public void updateServerDatabase() throws MalformedURLException, IOException{
        //Invoke RESTful PUT method
        URL url = new URL(deviceURL + "/0");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setInstanceFollowRedirects(false);
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Content-Type", "application/xml");
        connection.disconnect();
    }
    
    //method sha1() adapted from user evilone's answer to Stack Overflow question "How to Hash String using SHA-1 with key?"
    private String sha1(String s, String keyString) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {

        SecretKeySpec key = new SecretKeySpec((keyString).getBytes("UTF-8"), "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(key);

        byte[] bytes = mac.doFinal(s.getBytes("UTF-8"));
        Base64.Encoder enc = Base64.getUrlEncoder();
        
        return new String(enc.encode(bytes));

    }
}

