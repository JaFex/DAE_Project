package ejbs;

import dtos.DTO;
import java.io.Serializable;
import javax.ws.rs.PathParam;

public interface Bean<E extends Serializable, D extends DTO, PK> {
    D create(D dto);
    D retrieve(@PathParam("primaryKey") PK primaryKey);
    D update(D dto);
    boolean delete(@PathParam("primaryKey") PK primaryKey);
    
    boolean exists(Object primaryKey);
    
    Iterable<D> all();
}
