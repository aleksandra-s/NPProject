
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
            while (true) { //Loop that consumes and prints laser pulses as they become available
                //If subscription has not expired, print infinite pulses
                if(!subscriptionDate.before(Calendar.getInstance().getTime()) && initialFileCheck){
                    warningIssued = false;
                    outputHandler.printLine("Using subscription pulses until " + subscriptionString);
                    while(!subscriptionDate.before(Calendar.getInstance().getTime())){
                        outputHandler.printSubscriptionPulse();
                        Thread.sleep(5 * 1000);
                    }
                    
                }
                //If subscription has expired, but individual pulses are available, print them until they run out
                else if(pulsesToUse != 0 && initialFileCheck){
                    warningIssued = false;
                    outputHandler.printLine("Using individual pulses");
                    while(pulsesToUse != 0){
                        pulsesToUse--;
                        fileRetrieve.storeUsedPulsesFromDeviceInFile(deviceSerialNumber);
                        outputHandler.printPulse(pulsesToUse);
                        Thread.sleep(5 * 1000);
                    }
                }
                //If subscription has expired and no individual pulses are available, print 'out of pulses' warning
                else if(!warningIssued && initialFileCheck){
                    outputHandler.printOutOfPulsesWarning();
                    warningIssued = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //Private class that polls device file for updated pulse and subscription info
    private class FileChecker implements Runnable {

        @Override
        public void run() {
            try{
                for (;;) {
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
                int pulsesFromServer = Integer.parseInt(deviceDataFromFile.substring(0,i));
                int usedPulses = Integer.parseInt(deviceDataFromFile.substring(j+1));
                if(pulsesFromServer >= usedPulses){
                    pulsesToUse = pulsesFromServer - usedPulses;
                }
            }
        }
    }
}
