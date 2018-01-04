
package se.kth.id1212.npproject4.device.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import se.kth.id1212.npproject4.device.controller.DeviceConnectionController;
import se.kth.id1212.npproject4.device.controller.DeviceDisplayController;

/**
 *
 * @author aleks_uuia3ly
 */
public class DeviceSimulator implements Runnable{
    private boolean receivingCmds = false;
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private DeviceConnectionController deviceConnectionController;
    private DeviceDisplayController deviceDisplayController;
    private DeviceSimulatorOutput outputHandler;
    
    //Start thread for run method
    public void start(){
        outputHandler = new DeviceSimulatorOutput();
        new Thread(this).start();
    }
    
    @Override
    public void run(){  
        outputHandler.printLine("Laser Device Simulator");
        outputHandler.printLine("Please enter your device serial number:");
        outputHandler.print("> ");
        try {
            String id = reader.readLine();
            outputHandler.printLine("Initializing...");
            deviceConnectionController = new DeviceConnectionController(id);
            deviceDisplayController = new DeviceDisplayController(id);
            deviceConnectionController.start();
            Thread.sleep(5*1000);
            deviceDisplayController.start();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        } catch (InvalidKeyException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        
    }
}
