package dtos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name = "Administrator")
@XmlAccessorType(XmlAccessType.FIELD)
public class AdministratorDTO extends UserDTO {
    
    private @Getter @Setter String name;
    private @Getter @Setter String role;
    private @Getter @Setter String email;

    public AdministratorDTO() {
    }

    public AdministratorDTO(String name, String role, String email, String username, String password) {
        super(username, password);
        this.name = name;
        this.role = role;
        this.email = email;
    } 
    
    @Override
    public void clear() {
        name = null;
        role = null;
        email = null;
    }
}
