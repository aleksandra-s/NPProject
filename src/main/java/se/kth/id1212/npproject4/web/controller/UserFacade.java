
package se.kth.id1212.npproject4.web.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import se.kth.id1212.npproject4.web.integration.DeviceEntityFacadeREST;
import se.kth.id1212.npproject4.web.model.DeviceEntity;
//import javax.persistence.EntityNotFoundException;
//import se.kth.id1212.nphomework4.currencyconv.integration.ConversionDAO;
//import se.kth.id1212.nphomework4.currencyconv.model.Conversion;


@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Stateless
public class UserFacade {
    @EJB DeviceEntityFacadeREST rest;
    Client client = ClientBuilder.newClient();
    //Convert inputted amount 
    public DeviceEntity findDevice(Long serialNumber){
        //return rest.find(serialNumber);
        WebTarget target = client.target("http://localhost:8080/NPProject4/webresources/se.kth.id1212.npproject4.web.model.deviceentity/" + serialNumber.toString());
        
        return target.request(MediaType.APPLICATION_XML).get(DeviceEntity.class);
        //return rest.find(serialNumber);
    }
    
    public void addCredits(DeviceEntity updatedDevice){
        //rest.editFromWeb(updatedDevice.getId(), updatedDevice);
        //Client client = ClientBuilder.newClient();
        String serialNumber = updatedDevice.getId().toString();
        WebTarget target = client.target("http://localhost:8080/NPProject4/webresources/se.kth.id1212.npproject4.web.model.deviceentity/" + serialNumber);
        target.request().put(Entity.entity(updatedDevice, MediaType.APPLICATION_XML));  
    }
    
    public void updateSubscription(DeviceEntity updatedDevice, String subscriptionChoice){
        //DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date newSubscription = new Date();
        if(subscriptionChoice.equals("day")){
            Calendar c = Calendar.getInstance();
            c.setTime(newSubscription);
            c.add(Calendar.DATE, 1);  // number of days to add
            //dt = df.format(c.getTime());
            newSubscription = c.getTime();
        }
        else if(subscriptionChoice.equals("week")){
            Calendar c = Calendar.getInstance();
            c.setTime(newSubscription);
            c.add(Calendar.DATE, 7);  // number of days to add
            //dt = df.format(c.getTime());
            newSubscription = c.getTime();
        }
        else if(subscriptionChoice.equals("month")){
            Calendar c = Calendar.getInstance();
            c.setTime(newSubscription);
            c.add(Calendar.DATE, 30);  // number of days to add
            //dt = df.format(c.getTime());
            newSubscription = c.getTime();
        }
        if (!newSubscription.before(updatedDevice.getSubscriptionDate())){
            updatedDevice.setSubscriptionDate(newSubscription);
            //rest.edit(updatedDevice.getId(), updatedDevice);
            String serialNumber = updatedDevice.getId().toString();
            WebTarget target = client.target("http://localhost:8080/NPProject4/webresources/se.kth.id1212.npproject4.web.model.deviceentity/" + serialNumber);
            target.request().put(Entity.entity(updatedDevice, MediaType.APPLICATION_XML));  
        }
    }
}
