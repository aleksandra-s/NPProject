
package se.kth.id1212.npproject4.web.controller;

import java.util.Calendar;
import java.util.Date;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import se.kth.id1212.npproject4.web.model.DeviceEntity;


@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Stateless
public class UserFacade {
    
    Client client = ClientBuilder.newClient(); 
    public DeviceEntity findDevice(Long serialNumber){
        WebTarget target = client.target("http://localhost:8080/NPProject4/webresources/se.kth.id1212.npproject4.web.model.deviceentity/" + serialNumber.toString());   
        return target.request(MediaType.APPLICATION_XML).get(DeviceEntity.class);
    }
    
    public void addCredits(DeviceEntity updatedDevice){
        String serialNumber = updatedDevice.getId().toString();
        WebTarget target = client.target("http://localhost:8080/NPProject4/webresources/se.kth.id1212.npproject4.web.model.deviceentity/" + serialNumber);
        target.request().put(Entity.entity(updatedDevice, MediaType.APPLICATION_XML));  
    }
    
    public void updateSubscription(DeviceEntity updatedDevice, String subscriptionChoice){
        Date newSubscription = new Date();
        if(subscriptionChoice.equals("day")){
            Calendar c = Calendar.getInstance();
            c.setTime(newSubscription);
            c.add(Calendar.DATE, 1); 
            newSubscription = c.getTime();
        }
        else if(subscriptionChoice.equals("week")){
            Calendar c = Calendar.getInstance();
            c.setTime(newSubscription);
            c.add(Calendar.DATE, 7);
            newSubscription = c.getTime();
        }
        else if(subscriptionChoice.equals("month")){
            Calendar c = Calendar.getInstance();
            c.setTime(newSubscription);
            c.add(Calendar.DATE, 30);  
            newSubscription = c.getTime();
        }
        if (!newSubscription.before(updatedDevice.getSubscriptionDate())){
            updatedDevice.setSubscriptionDate(newSubscription);
            String serialNumber = updatedDevice.getId().toString();
            WebTarget target = client.target("http://localhost:8080/NPProject4/webresources/se.kth.id1212.npproject4.web.model.deviceentity/" + serialNumber);
            target.request().put(Entity.entity(updatedDevice, MediaType.APPLICATION_XML));  
        }
    }
}
