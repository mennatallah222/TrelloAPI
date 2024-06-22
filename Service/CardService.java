package Service;
import java.util.List;
import javax.ejb.MessageDriven;
import javax.jms.JMSContext;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.inject.Inject;
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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import DTO.CardDTO;
import Messaging.JMSClient;
import model.Card;
import model.CommentList;
import model.Lists;
import model.Sprint;
import model.User;
@Path("/cards")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Stateless
public class CardService {
	ObjectMapper objectMapper = new ObjectMapper();
	@Resource
	(mappedName="java:/jms/queue/MyTrelloQueue")
	private Queue MyTrelloQueue;
	@Inject
	JMSContext context;
	@Inject
	JMSClient Jmsutil;
	@PersistenceContext(unitName = "TrelloAPI")
    private EntityManager em;
	public Sprint sprint;
	@POST
	@Path("/{listId}/createCard")
	@PermitAll
	public Response createCard(@PathParam("listId") Long listId, Card c) {
		
		try {
			 Lists list = em.find(Lists.class, listId);
			 
			 
		        if (list == null) {
		        	throw new NotFoundException("List not found");
		        }
		        //list.getCards().add(c) ;
		        c.list=list;
		        c.setStatus(list.getName());
		        c.setUser(null);
		        em.merge(list);
		        em.persist(c);
		        //em.merge(c);
		        return Response.status(Response.Status.OK).build();
			}
		catch (EJBException e) {
		        e.printStackTrace();
		        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		    }
	}
	
    @PUT
    @Path("/{listId}/{cardId}/assignTask")
    @PermitAll
    public Response assignTask(@PathParam("listId") Long listId, @PathParam("cardId") Long cardId, User AssignedUser) {
        try {
            Card card = em.find(Card.class, cardId);
            if (card == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Card not found").build();
            }

            Lists list = em.find(Lists.class, listId);
            if (list == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("List not found").build();
            }

           TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);
            query.setParameter("email", AssignedUser.getEmail());
            List<User> users = query.getResultList();
            if (users.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity("User not found!").build();
            }
            User user = users.get(0);
            card.setUser(user);
            em.merge(card);
            em.merge(AssignedUser);
            CardDTO cd=new CardDTO(list.listId,card.getName(),card.getStatus(),user.getUsername());
            Jmsutil.sendMessage("New task assigned to "+user.getUsername()+objectMapper.writeValueAsString(cd));
            return Response.status(Response.Status.OK).entity(cd).build();
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
		        card.setStatus(newList.name);
		        em.merge(card);
		        //CardDTO cd=new CardDTO(newList.listId,card.getName(),card.getStatus(),card.assignee.username);
		        Jmsutil.sendMessage("Card "+card.getName()+" Status updated to "+card.getStatus());
		        return Response.status(Response.Status.OK).entity("Card is moved from "+oldList.name+" to "+newList.name).build();
		        
			}
		catch (EJBException e) {
	        e.printStackTrace();
	        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	@PUT
    @Path("/{cardId}/updateCard")
    @PermitAll
    public Response updateCard(@PathParam("cardId") Long cardId, Card c)
	{
      //  List<String> cs=comments.getComments();
        Card card = em.find(Card.class, cardId);
        //List<String> cs=card.getComments();
        if (card == null) {
             return Response.status(Response.Status.NOT_FOUND).entity("Card not found").build();

         }
        List<String> cs=card.getComments();
        List<String> comments=c.getComments();
        if(card.description!=null) {
            card.setDescription(c.description);
            Jmsutil.sendMessage("Description of the task is changed:  "+card.getDescription());
        }
        if (cs != null) {
            card.comments.addAll(comments);
            Jmsutil.sendMessage("New comment added "+card.getName());
        }
        em.merge(card);

        return Response.status(Response.Status.OK).entity("Comments/Description are added ").build();
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
