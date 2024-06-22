package Service;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;
import DTO.*;
import model.*;
@Stateless
@Path("/sprintApi")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SprintService {

  @PersistenceContext(unitName = "TrelloAPI")
    private EntityManager em;
  
  
  @POST
  @Path("/startSprint")
  public Response startSprint(@QueryParam("boardId") Long boardId) {
      Board board = em.find(Board.class, boardId);
      if (board == null) {
          return Response.status(Response.Status.NOT_FOUND).build();
      }
      Sprint sprint = new Sprint();
      sprint.setBoard(board);
      sprint.setStartDate(new Date());
      sprint.setStatus("Active");
      em.persist(sprint);

      return Response.ok().build();
  }

  
    @PUT
    @Path("/addtosprint/{sprintId}/{taskId}")
    public Response addToSprint(@PathParam("sprintId") Long sid, @PathParam("taskId") Long tid) {
        Sprint sprint = em.find(Sprint.class, sid);
        if (sprint == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        
        Card c = em.find(Card.class, tid);
        if (c == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        c.setSprint(sprint);
        sprint.cards.add(c);
        em.merge(c);
        em.persist(sprint);
        em.merge(sprint);
        SprintDTO sdto=new SprintDTO();
        sdto.sprintId=sprint.sprintId;
        sdto.startDate=sprint.getStartDate();
        sdto.endDate=sprint.getEndDate();
        sdto.status=sprint.getStatus();
    
        CardDTO cdto=new CardDTO();
        
        cdto.cardname=c.getName();
        cdto.storyPoint=c.getStoryPoint();

        sdto.addCard(cdto);
    
        return Response.status(Response.Status.OK).entity(sdto).build();
    }
  
  @GET
  @Path("/sprints")
  public Response getAllSprints() {
      TypedQuery<Sprint> query = em.createQuery("SELECT s FROM Sprint s", Sprint.class);
      List<Sprint> sprints = query.getResultList();
      if (sprints.isEmpty())
      {
    	  return Response.status(Response.Status.NOT_FOUND).build();
      }
      List<SprintDTO> sprintDTOs = new ArrayList<>();
      for (Sprint sprint : sprints) {
          SprintDTO sprintDTO = new SprintDTO();
          sprintDTO.sprintId=sprint.sprintId;
          sprintDTO.startDate=sprint.getStartDate();
          sprintDTO.endDate=sprint.getEndDate();
          sprintDTO.status=sprint.getStatus();
            if (sprint.getCards() != null) {         	
                List<CardDTO> cardDTOs = new ArrayList<>();
                for (Card card : sprint.getCards()) {
                    CardDTO cardDTO = new CardDTO();
                    cardDTO.cardname = card.getName();
                    cardDTO.status = card.getStatus();
                    cardDTO.storyPoint = card.getStoryPoint();
                    cardDTOs.add(cardDTO);
                }
                sprintDTO.cards.addAll(cardDTOs);
            }
            sprintDTOs.add(sprintDTO);
            
      }

      return Response.ok().entity(sprintDTOs).build();
  }
  @PUT
  @Path("/endSprint/{sid}")
  public Response endSprint(@PathParam("sid")Long sid) {
    Sprint s=em.find(Sprint.class, sid);  
    Sprint s2=new Sprint();
    s2.cards=new ArrayList<>();
    s2.setStatus("Active");
    s2.setStartDate(new Date());
    
    SprintDTO sprintDTO2 = new SprintDTO();
    sprintDTO2.sprintId=s2.sprintId;
    sprintDTO2.startDate=s2.getStartDate();
    sprintDTO2.status=s2.getStatus();
    Iterator<Card> iterator = s.getCards().iterator();
    List<CardDTO> cardDTOs = new ArrayList<>();
    if (s == null) {
          return Response.status(Response.Status.NOT_FOUND).entity("Sprint is not found").build();
      }
    if (!s.getStatus().equals("Active")) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Sprint is not active").build();
      }
    for (Card card : s.getCards()) {
    	if(!card.status.equals("Done"))
    	{System.out.println(card.getName());
    	 System.out.println(card.getStatus());
    	  CardDTO cardDTO=new CardDTO();
    	   cardDTO.cardname = card.getName();
           cardDTO.status = card.getStatus();
           cardDTO.storyPoint = card.getStoryPoint();
           cardDTOs.add(cardDTO);
    		//s.getCards().remove(card);
    		card.setSprint(s2);
    		s2.cards.add(card);
    		em.merge(card);
    		cardDTOs.add(cardDTO);
    	}
    	sprintDTO2.cards.addAll(cardDTOs);
    }
    em.persist(s2);
    s.setEndDate(new Date());
    s.setStatus("Ended");
    em.merge(s);
    SprintDTO sdto=new SprintDTO();
        sdto.sprintId=s.sprintId;
        sdto.startDate=s.getStartDate();
        sdto.endDate=s.getEndDate();
        sdto.status=s.getStatus();
        
         
    return Response.status(Response.Status.OK).entity("New sprint is started ").build();
  }
  
  
  @GET
  @Path("/{id}/report")
  public Response getSprintReport(@PathParam("id") Long id) {
      Sprint sprint = em.find(Sprint.class, id);
      if (sprint == null) {
          return Response.status(Response.Status.NOT_FOUND).build();
      }

      int completedStorypoints = 0;
      int uncompletedStoryPoints = 0;
      List<String> tasksnames=new ArrayList<>();
      for (Card card : sprint.getCards()) {
          if ("Done".equals(card.getStatus())) {
              completedStorypoints += card.getStoryPoint();
          } else {
              uncompletedStoryPoints += card.getStoryPoint();
          }
          tasksnames.add(card.getName());
      }

      Report report = new Report();
      report.setSprintId(sprint.sprintId);
      report.setTotalCompletedStoryPoints(completedStorypoints);
      report.setTotalUncompletedStoryPoints(uncompletedStoryPoints);

      report.tasks=tasksnames;
      return Response.status(Response.Status.CREATED).entity(report).build();
  }
  
  
  
  
  
  
  
  
  
  
  
}