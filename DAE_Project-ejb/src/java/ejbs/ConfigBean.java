package ejbs;

import dtos.AdministratorDTO;
import dtos.ClientDTO;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;


@Singleton
@Startup
public class ConfigBean {

    @EJB
    private AdministratorBean administratorBean;
    
    @EJB
    private ClientBean clientBean;
    
    @PostConstruct
    public void populateBD() {
        try {
            
            for(int i = 1; i < 6; i++){
                administratorBean.create(new AdministratorDTO("Admin" + i, "Admin", "admin" + i + "@admin.com", "admin" + i, "password"));
                clientBean.create(new ClientDTO("Company" + i, "Street" + i, "PersonOfContact" + i, "Client" + i, "password"));
            }
            
        } catch (Exception e) {
            System.err.println("[ERROR] @ Application bootstrap | Cause: " + e.getMessage());
        }
    }
}
