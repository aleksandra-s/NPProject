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
import se.kth.id1212.npproject4.device.controller.DeviceController;

/**
 *
 * @author aleks_uuia3ly
 */
public class DeviceSimulator implements Runnable{
    private boolean receivingCmds = false;
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private DeviceController deviceController;
    
    //Start thread for run method
    public void start(){
        if(receivingCmds){
            return;
        }
        receivingCmds = true;
        new Thread(this).start();
        //deviceController = new DeviceController();
    }
    
    @Override
    public void run(){  
        System.out.println("Laser Device Simulator");
        System.out.println("Please enter your device serial number:");
        System.out.print("> ");
        try {
            String id = reader.readLine();
            deviceController = new DeviceController(id);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
    }
}
