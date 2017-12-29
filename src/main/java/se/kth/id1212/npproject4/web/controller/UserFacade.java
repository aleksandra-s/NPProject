
package se.kth.id1212.npproject4.web.controller;

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
}
