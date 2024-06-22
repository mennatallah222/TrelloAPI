package Service;


   
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.security.*;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.ejb3.annotation.SecurityDomain;

import DTO.BoardDTO;
import model.Board;
import model.User;

@Stateless
@Path("/boardsApi")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
//@SecurityDomain("userRoles")
public class BoardService{


	@PersistenceContext(unitName = "TrelloAPI")
    private EntityManager em;
    
    @POST
    @Path("/create/{Id}")
    @PermitAll
    public Response createBoard(@PathParam("Id")Long Id,Board b) {
    	try {
    	
    		 //query to find the signed-in user
    		User owner=em.find(User.class,Id);
            //TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.userId= true", User.class);
            //User owner = query.getSingleResult();
            if (owner == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("No signed-in user found").build();
            }  
            if(owner.isSignedIn()==false)
            {
            	return Response.status(Response.Status.FORBIDDEN).entity("Please sign in to access this resource").build();
            }
            TypedQuery<Long> isCreated=em.createQuery("SELECT COUNT(b) FROM Board b WHERE b.name= :name", Long.class);
            isCreated.setParameter("name", b.getName());
            if(isCreated.getSingleResult()>0) {
            	return Response.status(Response.Status.CONFLICT).entity("Board name already exists!").build();
            }
            owner.setRole("Teamleader");
            b.setUser(owner);
            em.persist(b);
            em.merge(owner);
            return Response.status(Response.Status.CREATED).entity("Board created successfully!").build();
    	}
    	catch(Exception e){
    		e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error occurred during create").build();
    	}
    }
	

    //helper function to get the boards in the database
    public List<Board> getterOfBoard(){
    	return em.createQuery("SELECT b FROM Board b", Board.class).getResultList();
    }
 //get for all boards
    @GET
    @PermitAll
    @Path("/getAll")
    public List<Board> getAllBoards() {
    	
        return getterOfBoard();
    }


    @GET
    @PermitAll
    @Path("/getOwnedBoards/{Id}")
    public Response getUserBoard(@PathParam("Id")Long Id) {
        //TypedQuery<User> uq = em.createQuery("SELECT u FROM User u WHERE u.isSignedIn = true", User.class);
        //User signedUser = uq.getSingleResult();
    	User signedUser=em.find(User.class, Id);
        if ((signedUser != null)&&(signedUser.getRole()=="Teamleader")) {
        	List<BoardDTO> bdtos=new ArrayList<>();
            TypedQuery<Board> bq = em.createQuery("SELECT DISTINCT b FROM Board b WHERE b.owner = :user", Board.class);
            bq.setParameter("user", signedUser);
            List<Board> boards = bq.getResultList();
            
            for(Board b:boards) {
            	BoardDTO boardDTO = new BoardDTO();
            	boardDTO.setUserId(signedUser.userId);
            	boardDTO.setUsername(signedUser.getUsername());
                boardDTO.setEmail(signedUser.getEmail());
                boardDTO.setBoard(b.getName());
                
                Map<String, String> collaboratorsInfo=new HashMap<>();
                List<User> collaborators=b.getUsers();
                for(User u:collaborators) {
                	String userName=u.getUsername();
                	String email=u.getEmail();
                	collaboratorsInfo.put(userName, email);
                }
                boardDTO.setCollaborators(collaboratorsInfo);
                bdtos.add(boardDTO);
            }
            
            return Response.ok(bdtos).build();
        }
        return  Response.status(Response.Status.FORBIDDEN).entity("You are not authorized to access this resource").build();
    }


    @DELETE
    @Path("/delete/{boardId}")
    @PermitAll
    public Response deleteBoard(@PathParam("boardId") Long boardId) {
        Board board = em.find(Board.class, boardId);
        if (board != null) {
            em.remove(board);
            return Response.ok().build();
        }
        else{
            return Response.status(Response.Status.NOT_FOUND).entity("Board is not found").build();
        }
    }
    
   

    @POST
    @Path("/{boardId}/invite/{TeamLeaderId}")
    @PermitAll
    public Response inviteUserToBoard(@PathParam("boardId") Long boardId, User invitedUser,@PathParam("TeamLeaderId")long TeamLeaderId) {
    		
    		TypedQuery<User> uq = em.createQuery("SELECT u FROM User u WHERE u.email = :email AND u.role != 'Teamleader'", User.class);
            uq.setParameter("email", invitedUser.getEmail());
            List<User> users = uq.getResultList();
            if (users.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity("No suitable users found!").build();
            }
            Board b = em.find(Board.class, boardId);
            if (b == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Board not found!").build();
            }
            User Teamleader=em.find(User.class, TeamLeaderId);
            
            //TypedQuery<User> queryForSignedIn = em.createQuery("SELECT u FROM User u WHERE u.isSignedIn=true", User.class);
            //User signedUser = queryForSignedIn.getSingleResult();
            /*if (!b.getOwner().getUsername().equals(signedUser.getUsername())) {
                return Response.status(Response.Status.FORBIDDEN).entity("You are not authorized to invite users to this board!").build();
            }*/
            
            if(Teamleader.getRole()!="Teamleader")
            {
            	return  Response.status(Response.Status.FORBIDDEN).entity("You are not authorized to access this resource").build();
            }
            User userToInvite = users.get(0);
            userToInvite.boards.add(b);
            b.getUsers().add(userToInvite);
            
            em.merge(b);
            em.merge(userToInvite);
            System.out.println("USERTOINVITE: "+userToInvite.getEmail()+"===========================================");
            return Response.ok().entity("User invited successfully").build();
    	
    }


   
}
