
package se.kth.id1212.npproject4.web.integration;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import se.kth.id1212.npproject4.web.model.DeviceEntity;

/**
 *
 * @author aleks_uuia3ly
 */
@Stateless
@Path("se.kth.id1212.npproject4.web.model.deviceentity")
public class DeviceEntityFacadeREST extends AbstractFacade<DeviceEntity> {
    
    private final String keyString = "devicekey";
    
    @PersistenceContext(unitName = "se.kth.id1212_NPProject4_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    public DeviceEntityFacadeREST() {
        super(DeviceEntity.class);
    }

    @POST
    @Override
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public void create(DeviceEntity entity) {
        super.create(entity);
    }

    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public void editFromWeb(@PathParam("id") Long id, DeviceEntity entity) {
        super.edit(entity);
    }
    
    @PUT
    @Path("{id}/{hash}/{credits}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public void editFromDevice(@PathParam("id") Long id, @PathParam("credits") int creditBalance, @PathParam("hash") String hash) {
        try {
            String stringId = id.toString();
            if(sha1(stringId,keyString).equals(hash)){
                DeviceEntity entity = super.find(id);
                entity.setCreditBalance(creditBalance);
                super.edit(entity);
            }
        } catch (UnsupportedEncodingException ex) {
            
        } catch (NoSuchAlgorithmException ex) {
            
        } catch (InvalidKeyException ex) {
            
        }
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Long id) {
        super.remove(super.find(id));
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public DeviceEntity find(@PathParam("id") Long id) {
        return super.find(id);
    }
    
    @GET
    @Path("{id}/{hash}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public DeviceEntity findFromDevice(@PathParam("id") Long id, @PathParam("hash") String hash) {
        try {
            String stringId = id.toString();
            String temp = sha1(stringId,keyString);
            System.out.println(temp);
            if(temp.equals(hash)){
                return super.find(id);
            }
            return null;
        } catch (UnsupportedEncodingException ex) {
            return null;
        } catch (NoSuchAlgorithmException ex) {
            return null;
        } catch (InvalidKeyException ex) {
            return null;
        }
    }

    @GET
    @Override
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public List<DeviceEntity> findAll() {
        return super.findAll();
    }

    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String countREST() {
        return String.valueOf(super.count());
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    //method sha1() adapted from user evilone's answer to Stack Overflow question "How to Hash String using SHA-1 with key?"
    private String sha1(String s, String keyString) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {

        SecretKeySpec key = new SecretKeySpec((keyString).getBytes("UTF-8"), "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(key);

        byte[] bytes = mac.doFinal(s.getBytes("UTF-8"));

        Base64.Encoder enc = Base64.getUrlEncoder();
        
        return new String(enc.encode(bytes));

    }
}
