/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1212.npproject4.device.controller;

import java.io.IOException;
import java.net.ProtocolException;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.kth.id1212.npproject4.device.model.DeviceFileRetrieve;

/**
 *
 * @author aleks_uuia3ly
 */
public class main {
    public static void main(String[] args) {
        DeviceConnectionController test = new DeviceConnectionController("1");
        DeviceFileRetrieve filetest = new DeviceFileRetrieve();
        try {
            test.getDeviceInfo();
            System.out.println(filetest.getAvailablePulsesFromFile("2"));
            filetest.storeAvailablePulsesInFile("2","10");
            System.out.println(filetest.getAvailablePulsesFromFile("2"));
        } catch (ProtocolException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
