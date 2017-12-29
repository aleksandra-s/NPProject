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

/**
 *
 * @author aleks_uuia3ly
 */
public class DeviceSimulator implements Runnable{
    private boolean receivingCmds = false;
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private DeviceConnectionController deviceController;
    
    //Start thread for run method
    public void start(){
        if(receivingCmds){
            return;
        }
        receivingCmds = true;
        new Thread(this).start();
        //deviceController = new DeviceConnectionController();
    }
    
    @Override
    public void run(){  
        System.out.println("Laser Device Simulator");
        System.out.println("Please enter your device serial number:");
        System.out.print("> ");
        try {
            String id = reader.readLine();
            deviceController = new DeviceConnectionController(id);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
    }
}
