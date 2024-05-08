
import javax.ejb.Stateless;

import java.io.*;

@Stateless
public class AuthenticationFileManager {

    private static final String USERS_FILE = "C:\\Users\\hp\\Downloads\\jboss-eap-7.3.0\\jboss-eap-7.3\\standalone\\configuration\\application-users.properties";
    private static final String ROLES_FILE = "C:\\Users\\hp\\Downloads\\jboss-eap-7.3.0\\jboss-eap-7.3\\standalone\\configuration\\application-roles.properties";

    public void addUser(User user) {
        try (FileWriter w1=new FileWriter(USERS_FILE, true)){
        	w1.write(user.getUsername()+"="+user.getPassword()+"\n");
        	
        	w1.flush();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        try (FileWriter w2 = new FileWriter(ROLES_FILE, true)) {
            w2.write(user.getUsername() + "=" + user.getRole() +"\n");
            w2.flush();
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    

}
