package Entities;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.*;

@Entity
@Stateless
public class Board  implements Serializable{

	 @Id
	 @GeneratedValue(strategy = GenerationType.IDENTITY)
	 private Long boardId;
	 private String name;
	 
	 @ManyToMany(mappedBy = "boards", cascade = CascadeType.ALL, fetch=FetchType.LAZY)
	    private List<User> users;

	 @ManyToOne(fetch=FetchType.EAGER)
	 @JoinColumn(name = "userId")
	 private User owner;
	 
     @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, fetch=FetchType.EAGER)
     public List<Lists> lists;
     
     
     @ManyToOne(fetch=FetchType.EAGER)
     @JoinColumn(name="sprintId")
     private Sprint sprint;

     
     public Board() {}
     public Long getBoardId() {
    	 return boardId;
     }
     
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setUser(User u) {
		// TODO Auto-generated method stub
		this.owner=u;
	}
	public User getOwner() {
		return owner;
	}
	public void setCollaborator(User userToInvite) {
	    if (users == null) {
	        users = new ArrayList<>();
	    }
	    users.add(userToInvite);
	}
	public List<User> getUsers() {
        return users;
    }

}
