package entities;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2018-12-15T15:45:57")
@StaticMetamodel(Client.class)
public class Client_ extends User_ {

    public static volatile SingularAttribute<Client, String> address;
    public static volatile SingularAttribute<Client, String> nameOfCompany;
    public static volatile SingularAttribute<Client, String> personOfContact;

}