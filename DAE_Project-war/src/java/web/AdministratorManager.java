/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package web;


import dtos.AdministratorDTO;
import dtos.ClientDTO;
import ejbs.AdministratorBean;
import ejbs.ClientBean;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import lombok.Getter;
import lombok.Setter;


@ManagedBean(name = "am")
@SessionScoped
public class AdministratorManager implements Serializable {
    private javax.ws.rs.client.Client client;
    private final String baseUri = "http://localhost:8080/DAE_Project-war/webapi";
    
    private static final Logger logger = Logger.getLogger("web.AdministratorManager");
    
    //Curent
    private @Getter @Setter ClientDTO currentClient;
    private @Getter @Setter AdministratorDTO  currentAdministrator;
    
    //New
    private @Getter @Setter ClientDTO  newClient;
    private @Getter @Setter AdministratorDTO  newAdministrator;
    
    @EJB
    private ClientBean cb;
    
    @EJB
    private AdministratorBean ab;

    public AdministratorManager() {
        this.client = ClientBuilder.newClient();
        this.cb = null;
        this.ab = null;
        this.currentClient = new ClientDTO();
        this.newClient = new ClientDTO();
        this.currentAdministrator = new AdministratorDTO();
        this.newAdministrator = new AdministratorDTO();
    }
    
    public List<ClientDTO> getAllClientsREST(){
        List<ClientDTO> returned = null;
        try{
            returned = client.target(baseUri).path("/clients/").request(MediaType.APPLICATION_XML).get(new GenericType<List<ClientDTO>>() {});
        }catch(Exception e){
            logger.log(Level.WARNING, "Problem get all clients in method getAllClientsREST: {0}", e.getMessage());
        }
        return returned;
    }
    
    public ClientDTO getClientsREST(){
        ClientDTO returned = null;
        try{
            returned = client.target(baseUri).path("/clients/"+currentClient.getUsername()).request(MediaType.APPLICATION_XML).get(new GenericType<ClientDTO>() {});
        }catch(Exception e){
            logger.log(Level.WARNING, "Problem get student in method getClientsREST: {0}", e.getMessage());
        }
        return returned;
    }
    
    public String postClientsREST(){
        try{
            client.target(baseUri).path("/clients/").request(MediaType.APPLICATION_XML).post(Entity.xml(newClient));
            return "admin_client_list?faces-redirect=true";
        }catch(Exception e){
            logger.log(Level.WARNING, "Problem post student in method postClientsREST: {0}", e.getMessage());
            return "admin_client_create";
        }
    }
    
    public String putClientsREST(){
        try{
            client.target(baseUri).path("/clients/").request(MediaType.APPLICATION_XML).put(Entity.xml(currentClient));
            return "admin_client_list?faces-redirect=true";
        }catch(Exception e){
            logger.log(Level.WARNING, "Problem put student in method putClientsREST: {0}", e.getMessage());
            return "admin_client_update";
        }
    }
    
    public String deleteClientsREST(){
        try{
            client.target(baseUri).path("/clients/"+currentClient.getId()).request(MediaType.APPLICATION_XML).delete();
            return "admin_client_list?faces-redirect=true";
        }catch(Exception e){
            logger.log(Level.WARNING, "Problem delete student in method deleteClientsREST: {0}", e.getMessage());
            return "admin_client_list";
        }
    }
    
    
    public List<AdministratorDTO> getAllAdministratorsREST(){
        List<AdministratorDTO> returned = null;
        try{
            returned = client.target(baseUri).path("/administrators/").request(MediaType.APPLICATION_XML).get(new GenericType<List<AdministratorDTO>>() {});
        }catch(Exception e){
            logger.log(Level.WARNING, "Problem get all administrators in method getAllAdministratorsREST: {0}", e.getMessage());
        }
        return returned;
    }
    
    public AdministratorDTO getAdministratorsREST(){
        AdministratorDTO returned = null;
        try{
            returned = client.target(baseUri).path("/administrators/"+currentAdministrator.getUsername()).request(MediaType.APPLICATION_XML).get(new GenericType<AdministratorDTO>() {});
        }catch(Exception e){
            logger.log(Level.WARNING, "Problem get administrator in method getAdministratorsREST: {0}", e.getMessage());
        }
        return returned;
    }
    
    public String postAdministratorsREST(){
        try{
            client.target(baseUri).path("/administrators/").request(MediaType.APPLICATION_XML).post(Entity.xml(newAdministrator));
            return "admin_administrator_list?faces-redirect=true";
        }catch(Exception e){
            logger.log(Level.WARNING, "Problem post administrator in method postAdministratorsREST: {0}", e.getMessage());
            return "admin_administrator_create";
        }
    }
    
    public String putAdministratorsREST(){
        try{
            client.target(baseUri).path("/administrators/").request(MediaType.APPLICATION_XML).put(Entity.xml(currentAdministrator));
            return "admin_administrator_list?faces-redirect=true";
        }catch(Exception e){
            logger.log(Level.WARNING, "Problem put administrator in method putAdministratorsREST: {0}", e.getMessage());
            return "admin_administrator_update";
        }
    }
    
    public String deleteAdministratorsREST(){
        try{
            client.target(baseUri).path("/administrators/"+currentAdministrator.getId()).request(MediaType.APPLICATION_XML).delete();
            return "admin_administrator_list?faces-redirect=true";
        }catch(Exception e){
            logger.log(Level.WARNING, "Problem delete administrator in method deleteAdministratorsREST: {0}", e.getMessage());
            return "admin_administrator_list";
        }
    }
    

}