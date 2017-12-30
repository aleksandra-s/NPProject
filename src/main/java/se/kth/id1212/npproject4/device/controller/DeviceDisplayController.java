/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1212.npproject4.device.controller;

import se.kth.id1212.npproject4.device.model.DeviceFileRetrieve;

/**
 *
 * @author aleks_uuia3ly
 */
public class DeviceDisplayController implements Runnable{
    private final String deviceSerialNumber;
    private final DeviceFileRetrieve fileRetrieve;
    
    
    public DeviceDisplayController(String id){
        fileRetrieve = new DeviceFileRetrieve();
        deviceSerialNumber = id;
    } 
    
    public void start(){
        new Thread(this).start();
    }
    
    @Override
    public void run() {
        
    }
}
