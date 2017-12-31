/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1212.npproject4.device.view;

/**
 *
 * @author aleks_uuia3ly
 */
public class DeviceSimulatorOutput {
    public void printDeviceInfo(String serialNumber, String pulsesAvailable, String subscription){
        System.out.println("Device " + serialNumber + " has " + pulsesAvailable + " individual pulses and a subscription until " + subscription);
    }
    
    public void printOutOfPulsesWarning(){
        System.out.println("This device is out of laser pulses. Purchase more credits on http://localhost:8080/NPProject4/");
    }
    
    public void printPulse(int pulseNumber){
        System.out.println(pulseNumber + "----- BZZT");
    }
    
    public void print(String toPrint){
        System.out.print(toPrint);
    }
    
    public void printLine(String toPrint){
        System.out.println(toPrint);
    }
}
