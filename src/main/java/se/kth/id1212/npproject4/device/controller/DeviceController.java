/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1212.npproject4.device.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.Time;
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
    private static final String REST_URI = "http://localhost:8080/NPProject4/webresources/se.kth.id1212.npproject4.model.deviceentity/1";
    /*private Client client = ClientBuilder.newClient();
    private DeviceEntity device;*/
    private int numberOfPulses;
    private String user;
    private Time subscriptionEnd;
    
    public void getDeviceInfo() throws MalformedURLException, ProtocolException, IOException {
        /*
            long id = 1;
            device = client.target(REST_URI).path(String.valueOf((Long) id)).request(MediaType.TEXT_PLAIN).get(DeviceEntity.class);
            System.out.println(device);*/
        URL url = new URL(REST_URI);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/xml");

        if (connection.getResponseCode() != 200) {
          throw new RuntimeException("Operation failed: "
                                      + connection.getResponseCode());
        }

        System.out.println("Content-Type: " + connection.getContentType());

        BufferedReader reader = new BufferedReader(new
                      InputStreamReader(connection.getInputStream()));

        String line = reader.readLine();
        while (line != null) {
           System.out.println(line);
           line = reader.readLine();
        }
        connection.disconnect();
    }
}

