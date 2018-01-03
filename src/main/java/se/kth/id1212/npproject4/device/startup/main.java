/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1212.npproject4.device.startup;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ProtocolException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import se.kth.id1212.npproject4.device.model.DeviceFileRetrieve;
import se.kth.id1212.npproject4.device.view.DeviceSimulator;

/**
 *
 * @author aleks_uuia3ly
 */
public class main {
    public static void main(String[] args) {
        /*    DeviceConnectionController test2;
        try {
            test2 = new DeviceConnectionController("2");
            test2.start();
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        } catch (InvalidKeyException ex) {
            ex.printStackTrace();
        }
            //DeviceDisplayController displaytest2 = new DeviceDisplayController("1");
            //displaytest2.start();*/
        DeviceSimulator sim = new DeviceSimulator();
        sim.start();
    }
}
