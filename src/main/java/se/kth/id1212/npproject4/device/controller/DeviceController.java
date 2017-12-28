/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1212.npproject4.device.controller;

import java.io.PrintStream;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
//import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import se.kth.id1212.npproject4.model.DeviceEntity;

/**
 *
 * @author aleks_uuia3ly
 */
public class DeviceController {
    private static final String REST_URI = "http://localhost:8080/NPProject4/webresources/se.kth.id1212.npproject4.model.deviceentity";
    private Client client = ClientBuilder.newClient();
    private DeviceEntity device;
    
    public void someMethod() {
        
            long id = 1;
            device = client.target(REST_URI).path(String.valueOf((Long) id)).request(MediaType.TEXT_PLAIN).get(DeviceEntity.class);
            System.out.println(device);
        }
    }

