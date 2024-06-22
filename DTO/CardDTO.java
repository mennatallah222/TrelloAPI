package DTO;


import java.io.Serializable;
import java.util.List;

public class CardDTO {
	
	    public Long listId;
	    public String cardname;
	    public String status;
	    public String Assignee;
        public List<String>comments;
        public int storyPoint;
	    public CardDTO() {}
	    public CardDTO(Long listId , String name, String status,String Assignee) {
	       this.listId=listId;
	       this.cardname=name;
	       this.status=status;
	       this.Assignee=Assignee;
	    }


}
