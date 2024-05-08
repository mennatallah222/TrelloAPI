import java.util.List;

import javax.annotation.security.PermitAll;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.spi.NotFoundException;

@Path("/cards/userId")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Stateless
public class CardService {

	@PersistenceContext(unitName = "TrelloAPI")
    private EntityManager em;
	
	
	
	public boolean isUserTeamLeader(Long userID) {
		
		return false;
		
	}
	@POST
	@Path("/{listId}/createCard")
	@PermitAll
	public Card createCard(@PathParam("listId") Long listId, Card c) {
		try {
			 Lists list = em.find(Lists.class, listId);
		        if (list == null) {
		        	throw new NotFoundException("List not found");
		        }
		        list.getCards().add(c);
		        em.persist(c);
		        em.merge(list);
		        return c;
			}
		catch (EJBException e) {
		        e.printStackTrace();
		        return null;
		    }
	}
	
	
    @PUT
    @Path("/{listId}/{cardId}/assignTask")
    @PermitAll
    public Response assignTask(@PathParam("listId") Long listId, @PathParam("cardId") Long cardId, String name, String email) {
        try {
            Card card = em.find(Card.class, cardId);
            if (card == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Card not found").build();
            }

            Lists list = em.find(Lists.class, listId);
            if (list == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("List not found").build();
            }

            TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.username = :username OR u.email = :email", User.class);
            query.setParameter("username", name);
            query.setParameter("email", email);
            List<User> users = query.getResultList();
            if (users.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
            }
            User existingUser = users.get(0);

            card.setUser(existingUser);
            em.merge(card);

            return Response.status(Response.Status.OK).entity(card).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("An error occurred: " + e.getMessage()).build();
        }
    }

	
	@PUT   //from            //to
	@Path("/{fromListId}/{cardId}/move/{toListId}")
	@PermitAll
	public Response moveCard(@PathParam("cardId") Long cardId, @PathParam("fromListId") Long fl, @PathParam("toListId") Long tl) {
		try {
			 Card card = em.find(Card.class, cardId);
			 Lists oldList = em.find(Lists.class, fl);
			 Lists newList = em.find(Lists.class, tl);
			 if (oldList==null ||newList == null) {
		         return Response.status(Response.Status.NOT_FOUND).entity("List not found").build();
		     }
		     if (card == null) {
		         return Response.status(Response.Status.NOT_FOUND).entity("Card not found").build();
			        
		     }
		        oldList.getCards().remove(card);
		        newList.getCards().add(card);
		        
		        card.list=newList;
		        em.merge(oldList);
		        em.merge(newList);
		        em.merge(card);
		        return Response.status(Response.Status.OK).entity("Card is moved from "+oldList.name+" to "+newList.name).build();
		        
			}
		catch (EJBException e) {
	        e.printStackTrace();
	        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	
	
	@PUT
	@Path("/{cardId}/addComments")
	@PermitAll
	public Response addComment(@PathParam("cardId") Long cardId, CommentList comments) {
		List<String> cs=comments.getComments();
		Card card = em.find(Card.class, cardId);
		if (card == null) {
	         return Response.status(Response.Status.NOT_FOUND).entity("Card not found").build();
		        
	     }
		if (cs != null) {
            card.comments.addAll(cs);
        }
		em.merge(card);
		return Response.status(Response.Status.OK).entity("Comments are added ").build();
        
	}
	
	@GET
	@Path("/{cardId}/getcomments")
	@PermitAll
	public List<String> getComments(@PathParam("cardId") Long cardId) {
	    Card card = em.createQuery("SELECT c FROM Card c LEFT JOIN FETCH c.comments WHERE c.cardId= :cId", Card.class).setParameter("cId", cardId).getSingleResult();
	    if (card == null) {
	    	throw new NotFoundException("card is not found");
	    }
	    List<String> comments = card.comments;
	    return comments;
	}

	
}
