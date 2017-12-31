/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1212.npproject4.device.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        //deviceController = new DeviceConnectionController();
    }
    
    @Override
    public void run(){  
        outputHandler.printLine("Laser Device Simulator");
        outputHandler.printLine("Please enter your device serial number:");
        outputHandler.printLine("> ");
        try {
            String id = reader.readLine();
            outputHandler.printLine("Initializing...");
            deviceConnectionController = new DeviceConnectionController(id);
            deviceDisplayController = new DeviceDisplayController(id);
            deviceConnectionController.start();
            deviceDisplayController.start();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
    }
}
