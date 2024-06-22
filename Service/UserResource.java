package Service;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.*;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.ejb3.annotation.SecurityDomain;

import DTO.UserDTO;
import model.User;
import security.AuthenticationFileManager;


@Stateless
@Path("/usersApi")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
//@SecurityDomain("userRoles")
public class UserResource {
	
	
	
	@PersistenceContext(unitName = "TrelloAPI")
    private EntityManager entityManager;
    
    @Inject
    private AuthenticationFileManager a;

    @POST
    @Path("/signup")
    @PermitAll
    //@RolesAllowed({"Teamleader"})
    public Response signUp(User u) {
    	try {
    		//List<User> users=entityManager.createQuery("SELECT u FROM User u", User.class).getResultList();
    		
    		TypedQuery<User> users = entityManager.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);

    		users.setParameter("email", u.getEmail());
    		List<User> existingUsers=users.getResultList();
            if (!existingUsers.isEmpty()) {
            	
                return Response.status(Response.Status.BAD_REQUEST).entity("User already exists!").build();
            }
            u.setRole("User");
            
            entityManager.persist(u);
            a.addUser(u);
            
            //updateUserToFile(u);
            return Response.status(Response.Status.CREATED).entity("User created successfully!").build();
    	}
    	catch(Exception e){
    		e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error occurred during sign-up").build();
    	}
        
    }
 
    @POST
    @Path("/signin")
    @PermitAll
    public Response signIn(User u) {
    	try {
    		/*//to check if the user is a team leader from the users file
            if (isTeamLeaderCredentials(u.getUsername(), u.getPassword())) {
                User teamLeader = getTeamLeaderFromFile();
                teamLeader.setSignedIn(true);
                entityManager.merge(teamLeader);
                return Response.status(Response.Status.OK).entity("Team leader logged in!").build();
            }*/

    		TypedQuery<User> users = entityManager.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);

    		users.setParameter("email", u.getEmail());
    		List<User> existingUsers=users.getResultList();
            if (!existingUsers.isEmpty()) {
            	existingUsers.get(0).setSignedIn(true);
            	entityManager.merge(existingUsers.get(0));
            	return Response.status(Response.Status.OK).entity("You're logged in!").build();
            }
            else {
	        	
	            return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid credentials").build();
	        }
	        
	    } 
    	catch (Exception e) {
	        e.printStackTrace();
	        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error occurred during sign-in").build();
	    }
    }
    

  /*  private boolean isTeamLeaderCredentials(String username, String password) {
        Map<String, String> userCredentials = loadUserCredentialsFromFile(USERS_FILE);
        String storedPassword = userCredentials.get(username);
        return storedPassword != null && storedPassword.equals(password);
    }

    private User getTeamLeaderFromFile() {
        Map<String, String> userRoles = loadUserRolesFromFile(ROLES_FILE);
        for (Map.Entry<String, String> entry : userRoles.entrySet()) {
            if ("Teamleader".equals(entry.getValue())) {
                User teamLeader = new User();
                teamLeader.setUsername(entry.getKey());
                teamLeader.setRole(entry.getValue());
                return teamLeader;
            }
        }
        return null;
    }

    private Map<String, String> loadUserCredentialsFromFile(String filePath) {
        Map<String, String> credentials = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))){
            String line;
            while ((line = reader.readLine()) != null){
                String[] parts = line.split("=");
                if (parts.length >1) {
                    credentials.put(parts[0], parts[1]);
                }
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return credentials;
    }*/

   /* private Map<String, String> loadUserRolesFromFile(String filePath) {
        Map<String, String> roles = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    roles.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return roles;
    }*/
    
    @PUT
    @Path("/update/{id}")
    @PermitAll
    public Response updateUser(@PathParam("id") Long id, User u) {
        User existingUser = entityManager.find(User.class, id);
        if (existingUser != null) {
            existingUser.setUsername(u.getUsername());
            existingUser.setEmail(u.getEmail());
            existingUser.setPassword(u.getPassword());
            entityManager.merge(existingUser);
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("No user found").build();
        }
    }


    @GET
    @Path("/get")
    @PermitAll
    public Response getUsersDB() {
        try {
            List<User> users = entityManager.createQuery("SELECT u FROM User u", User.class).getResultList();
            System.out.println("Retrieved users: " + users.size());
            
            List<UserDTO> udtos=new ArrayList<>();
            for(User u:users) {
            	UserDTO udto=new UserDTO();
            	udto.setUsername(u.getUsername());
            	udto.setUserId(u.userId);
            	udto.setEmail(u.getEmail());
            	udto.setPassword(u.getPassword());
            	udtos.add(udto);
            }
            return Response.ok().entity(udtos).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
    
}
