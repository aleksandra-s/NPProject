
package se.kth.id1212.npproject4.web.view;

import java.io.Serializable;
import java.sql.Time;
import javax.ejb.EJB;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import se.kth.id1212.npproject4.web.controller.UserFacade;
import se.kth.id1212.npproject4.web.model.DeviceEntity;

//Handles all interaction with the conversion JSF page.

@Named("webPresentationManager")
@ConversationScoped
public class WebPresentationManager implements Serializable {
    @EJB
    private UserFacade userFacade;
    private DeviceEntity currentDevice;
    private Long soughtDevice;
    private String subscriptionTimeToString;
    private int purchaseAmount;
    private Exception conversionFailure;
    @Inject
    private Conversation conversation;

    private void startConversation() {
        if (conversation.isTransient()) {
            conversation.begin();
        }
    }

    private void stopConversation() {
        if (!conversation.isTransient()) {
            conversation.end();
        }
    }

    private void handleException(Exception e) {
        stopConversation();
        e.printStackTrace(System.err);
        conversionFailure = e;
    }
    
    public void addCredits(){
        try {
            startConversation();
            conversionFailure = null;
            currentDevice.setCreditBalance(this.purchaseAmount);
            userFacade.addCredits(this.currentDevice);
        } catch (Exception e) {
            handleException(e);
        }
    }
    
      public void findDevice(){
        try {
            startConversation();
            conversionFailure = null;
            currentDevice = userFacade.findDevice(this.soughtDevice);
        } catch (Exception e) {
            handleException(e);
        }
    }
    
    public void setSoughtDevice(Long soughtDevice){
        this.soughtDevice = soughtDevice;
    }
    
    public Long getSoughtDevice(){
        return null;
    }
    
    public void setPurchaseAmount(int purchaseAmount){
        this.purchaseAmount = purchaseAmount;
    }
    
    public int getPurchaseAmount(){
        return purchaseAmount;
    }
    
    public void setSubscriptionTimeToString(Time subscriptionTime){
        this.subscriptionTimeToString = subscriptionTime.toString();
    }
    
     public String getSubscriptionTimeToString(){
        return subscriptionTimeToString;
    }
    
    public DeviceEntity getCurrentDevice(){
        return currentDevice;
    }

    
    public boolean getSuccess() {
        return conversionFailure == null;
    }

    public Exception getException() {
        return conversionFailure;
    }
}