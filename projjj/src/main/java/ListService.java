

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

@Path("/lists")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Stateless
public class ListService {

	@PersistenceContext(unitName = "TrelloAPI")
    private EntityManager em;


	@POST
	@Path("/{boardId}/createLists")
	@PermitAll
	public Response createList(@PathParam("boardId") Long boardId, String ListName) {
		try {
			
			TypedQuery<Board> query = em.createQuery("SELECT b FROM Board b WHERE b.boardId = :boardId", Board.class);
			query.setParameter("boardId", boardId);
			Board b = query.getSingleResult();
			if(b==null) {
			     return Response.status(Response.Status.NOT_FOUND).entity("No such a board created!").build();
			}
			Lists l=new Lists();
			TypedQuery<Long> isCreated = em.createQuery("SELECT COUNT(l) FROM Lists l WHERE l.name = :ListName", Long.class);
			isCreated.setParameter("ListName", ListName);
		
		    if(isCreated.getSingleResult()>0) {
		    	return Response.status(Response.Status.CONFLICT).entity("List name already exists!").build();
		    }
		    b.lists.add(l);
		    l.board=b;
		    l.name=ListName;
		    em.persist(b);
		    em.persist(l);
		    return Response.status(Response.Status.CREATED).entity("List created successfully!").build();
		    }
		catch (EJBException e) {
		        e.printStackTrace();
		        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		    }
	}

    @DELETE
    @Path("/{boardId}/lists/{listId}")
    @PermitAll
    public Response deleteList(@PathParam("boardId") Long boardId, @PathParam("listId") Long listId) {
    	Board b=em.find(Board.class, boardId);
    	if(b==null) {
    		return Response.status(Response.Status.NOT_FOUND).entity("No such a board created!").build();
    	}
    	Lists l=em.find(Lists.class, listId);
    	if(l==null) {
    		return Response.status(Response.Status.NOT_FOUND).entity("No such a list created!").build();
    	}
    	b.lists.remove(l);
    	em.remove(l);
        return Response.status(Response.Status.OK).build();
    }


    
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
                ldto.setLists(l.name);
                
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
