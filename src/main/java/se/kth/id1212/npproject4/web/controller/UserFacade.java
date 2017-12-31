
package se.kth.id1212.npproject4.web.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import se.kth.id1212.npproject4.web.integration.DeviceEntityFacadeREST;
import se.kth.id1212.npproject4.web.model.DeviceEntity;
//import javax.persistence.EntityNotFoundException;
//import se.kth.id1212.nphomework4.currencyconv.integration.ConversionDAO;
//import se.kth.id1212.nphomework4.currencyconv.model.Conversion;


@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Stateless
public class UserFacade {
    @EJB DeviceEntityFacadeREST rest;
    //Convert inputted amount 
    public DeviceEntity findDevice(Long serialNumber){
        return rest.find(serialNumber);
    }
    
    public void addCredits(DeviceEntity updatedDevice){
        rest.edit(updatedDevice.getId(), updatedDevice);
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
            rest.edit(updatedDevice.getId(), updatedDevice);
        }
    }
}
