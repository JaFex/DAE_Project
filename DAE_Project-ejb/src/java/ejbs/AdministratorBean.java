package ejbs;

import dtos.AdministratorDTO;
import entities.Administrator;
import javax.ejb.Stateless;
import javax.ws.rs.Path;

@Stateless
@Path("/administrators")
public class AdministratorBean extends BaseBean<Administrator, AdministratorDTO, Long> {


    
    
}
