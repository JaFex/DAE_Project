package ejbs;

import dtos.UserDTO;
import entities.User;
import javax.ejb.Stateless;

@Stateless
public class UserBean<E extends User, D extends UserDTO> extends BaseBean<E, D, Long> {
    
}
