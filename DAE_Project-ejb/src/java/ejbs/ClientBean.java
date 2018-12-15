package ejbs;

import dtos.ClientDTO;
import entities.Client;
import javax.ejb.Stateless;
import javax.ws.rs.Path;



@Stateless
@Path("/clients")
public class ClientBean extends BaseBean<Client, ClientDTO, Long> {

    
}
