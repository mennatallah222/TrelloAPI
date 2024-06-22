package Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import DTO.ListDTO;
import model.Board;
import model.Card;
import model.Lists;
import model.User;

@Path("/lists")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Stateless
public class ListService {

	@PersistenceContext(unitName = "TrelloAPI")
    private EntityManager em;


	@POST
	@Path("/{boardId}/createLists/{TeamLeaderId}")
	@PermitAll
	public Response createList(@PathParam("boardId") Long boardId, Lists l,@PathParam("TeamLeaderId")long TeamLeaderId) {
		try {
			User Teamleader=em.find(User.class, TeamLeaderId);
			TypedQuery<Board> query = em.createQuery("SELECT b FROM Board b WHERE b.boardId = :boardId", Board.class);
			query.setParameter("boardId", boardId);
			Board b = query.getSingleResult();
			if(b==null) {
			     return Response.status(Response.Status.NOT_FOUND).entity("No such a board created!").build();
			}
			//Lists l=new Lists();
			if(b.getOwner()==Teamleader)
			{	
			TypedQuery<Long> isCreated = em.createQuery("SELECT COUNT(l) FROM Lists l WHERE l.name = :ListName", Long.class);
			isCreated.setParameter("ListName", l.getName());
		
		    if(isCreated.getSingleResult()>0) {
		    	return Response.status(Response.Status.CONFLICT).entity("List name already exists!").build();
		    }
		    b.lists.add(l);
		    l.board=b;
		    em.persist(b);
		    em.persist(l);
		    return Response.status(Response.Status.CREATED).entity("List created successfully!").build();
		    }
			else
			{
				return Response.status(Response.Status.FORBIDDEN).entity("you are not authorized to access this resource").build();
			}
		}catch (EJBException e) {
		        e.printStackTrace();
		        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		    }
	}
	
	

    @DELETE
    @Path("/{boardId}/Deletelists/{listId}/{TeamLeaderId}")
    @PermitAll
    public Response deleteList(@PathParam("boardId") Long boardId, @PathParam("listId") Long listId,@PathParam("TeamLeaderId")long TeamLeaderId) {
    	Board b=em.find(Board.class, boardId);
    	User TeamLeader=em.find(User.class,TeamLeaderId );
    	if(b==null)
    	{
    		return Response.status(Response.Status.NOT_FOUND).entity("No such a board created!").build();
    	}
    	if(b.getOwner()==TeamLeader)
    	{ 
    	Lists l=em.find(Lists.class, listId);
    	if(l==null) 
    	{
    		return Response.status(Response.Status.NOT_FOUND).entity("No such a list created!").build();
    	}
    	b.lists.remove(l);
    	em.remove(l);
        return Response.status(Response.Status.OK).build();
        }
    	else
    	{
    		return Response.status(Response.Status.FORBIDDEN).entity("Please sign in to access this resource").build();
    	}
    		
    	}


  /*  @GET
    @PermitAll
    @Path("/getAllLists/{boardId}")
    public List<ListDTO> getAllListsForBoard(@PathParam("boardId")Long bId) {
    	
        TypedQuery<Board> uq = em.createQuery("SELECT b FROM Board b WHERE b.boardId = :boardId", Board.class);
        uq.setParameter("boardId", bId);
        List<Board> boards=uq.getResultList();
        
        if (boards != null) {
        	Board b=boards.get(0);
        	List<ListDTO> listDTOs = new ArrayList<>();
        	for(Lists l:b.lists) {
        		ListDTO ldto=new ListDTO();
        		ldto.setBoardId(bId);
        		ldto.setName(b.getName());
        		ldto.setLists(l.name);
        		listDTOs.add(ldto);
        	}
        	
        	return listDTOs;
        }
        return Collections.emptyList();
    }*/
    
    @GET
    @PermitAll
    @Path("/getAllLists/{boardId}")
    public List<ListDTO> getAllListsForBoard(@PathParam("boardId")Long bId) {
        TypedQuery<Board> uq = em.createQuery("SELECT DISTINCT b FROM Board b LEFT JOIN FETCH b.lists WHERE b.boardId = :boardId", Board.class);
        uq.setParameter("boardId", bId);
        List<Board> boards = uq.getResultList();

        List<ListDTO> listDTOs = new ArrayList<>();
        for (Board b : boards) {
            for (Lists l : b.lists) {
                ListDTO ldto = new ListDTO();
                ldto.setBoardId(bId);
                ldto.setName(b.getName());
                ldto.setLists(l.getName());
                
                StringBuilder cardsName = new StringBuilder();
                for (Card c : l.getCards()) {
                    cardsName.append(c.getName()).append(", ");
                }
                if (cardsName.length() > 0) {
                    cardsName.setLength(cardsName.length() - 2);
                }
                ldto.setCards(cardsName.toString());
                listDTOs.add(ldto);
            }
        }
        return listDTOs;
    }

    
}
