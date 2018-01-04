/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1212.npproject4.device.controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import se.kth.id1212.npproject4.device.model.DeviceFileRetrieve;
import se.kth.id1212.npproject4.device.view.DeviceSimulatorOutput;

/**
 *
 * @author aleks_uuia3ly
 */
public class DeviceDisplayController implements Runnable{
    private final String deviceSerialNumber;
    private final DeviceFileRetrieve fileRetrieve;
    private int pulsesToUse;
    private int pulsesUsed;
    private Date subscriptionDate;
    private String subscriptionString;
    private boolean warningIssued;
    private boolean initialFileCheck;
    private DeviceSimulatorOutput outputHandler;
    private boolean pulsesAvailable = false;
    private boolean subscriptionAvailable = false;
    
    
    public DeviceDisplayController(String id){
        fileRetrieve = new DeviceFileRetrieve();
        deviceSerialNumber = id;
        outputHandler = new DeviceSimulatorOutput();
        subscriptionDate = new Date(0002,10,30);
        warningIssued = false;
        initialFileCheck = false;
        pulsesUsed = 0;
    } 
    
    public void start(){
        new Thread(this).start();
        new Thread(new FileChecker()).start();
    }
    
    @Override
    public void run() {
        try {
            while (true) {
                //getDeviceInfo();
                if(!subscriptionDate.before(Calendar.getInstance().getTime()) && initialFileCheck){
                    //pulsesToUse = pulsesToUse + 100;
                    //System.out.println("subscription: " + subscriptionDate + " " + subscriptionString);
                    warningIssued = false;
                    outputHandler.printLine("Using subscription pulses until " + subscriptionString);
                    while(!subscriptionDate.before(Calendar.getInstance().getTime())){
                        outputHandler.printSubscriptionPulse();
                        Thread.sleep(5 * 1000);
                    }
                    
                }
                else if(pulsesToUse != 0 && initialFileCheck){
                    warningIssued = false;
                    //System.out.println("subscription: " + subscriptionDate + " " + subscriptionString);
                    outputHandler.printLine("Using individual pulses");
                    while(pulsesToUse != 0){
                        pulsesToUse--;
                        //pulsesUsed++;
                        fileRetrieve.storeUsedPulsesFromDeviceInFile(deviceSerialNumber);
                        outputHandler.printPulse(pulsesToUse);
                        Thread.sleep(5 * 1000);
                    }
                    //fileRetrieve.storeUsedPulsesFromDeviceInFile(deviceSerialNumber, Integer.toString(pulsesUsed));
                    //pulsesUsed = 0;
                }
                else if(!warningIssued && initialFileCheck){
                    outputHandler.printOutOfPulsesWarning();
                    warningIssued = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //public getAvailablePulsesInFile
    private class FileChecker implements Runnable {

        @Override
        public void run() {
            try{
                for (;;) {
                    //fileRetrieve.getDataFromFile(deviceSerialNumber);
                    checkSubscription();
                    checkPulses();
                    if(!initialFileCheck){
                        initialFileCheck = true;
                    }
                    Thread.sleep(10 * 1000);
                    
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        
        public void checkSubscription() throws IOException, ParseException{
            String deviceDataFromFile = fileRetrieve.getDataFromFile(deviceSerialNumber);

            int j = 0;
            int k = 0;
            int commaCounter = 0;
            Date subscriptionFromFile = null;
            for(int i = 0; i < deviceDataFromFile.length(); i++){
            //System.out.println(deviceDataFromFile.charAt(i));
            if(deviceDataFromFile.charAt(i) == ',' && commaCounter == 0){
                commaCounter++;
                j = i;
                //System.out.println("Comma counter " + commaCounter + " j: " + j);
            }
            else if(deviceDataFromFile.charAt(i) == ',' && commaCounter == 1){
                //commaCounter++;
                //System.out.println("k " + k + " i " + i);
                k = i;
                break;
            }
        }
        //System.out.println("J: " + j);
            String subscriptionData = deviceDataFromFile.substring(j+1,k);
            
            //String subscriptionData = deviceDataFromFile.substring(j+1);
            subscriptionFromFile = new SimpleDateFormat("yyyy-MM-dd").parse(subscriptionData);
            if(subscriptionFromFile.after(subscriptionDate)){
                subscriptionDate = subscriptionFromFile;
                subscriptionString = subscriptionData;
            }
        }
        
        public void checkPulses() throws IOException{
            if(pulsesToUse == 0 || !initialFileCheck){
                String deviceDataFromFile = fileRetrieve.getDataFromFile(deviceSerialNumber);
                int i;
                int j;
                for(i = 0; i < deviceDataFromFile.length(); i++){
                    if(deviceDataFromFile.charAt(i) == ','){
                        break;
                    }
                }
                for(j = i + 1; j < deviceDataFromFile.length(); j++){
                    if(deviceDataFromFile.charAt(j) == ','){
                        break;
                    }
                }
                //System.out.println("Checking pulses data " + deviceDataFromFile);
                //System.out.println("J " + j);
                int pulsesFromServer = Integer.parseInt(deviceDataFromFile.substring(0,i));
                int usedPulses = Integer.parseInt(deviceDataFromFile.substring(j+1));
                if(pulsesFromServer >= usedPulses){
                    pulsesToUse = pulsesFromServer - usedPulses;
                }
            }
        }
    }
}
