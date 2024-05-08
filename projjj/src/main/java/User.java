//package Models;

//import java.util.List;

//import javax.ejb.Stateless;
import java.io.Serializable;
import java.util.*;

import javax.ejb.Stateless;
import javax.persistence.*;
//import javax.persistence.OneToMany;


@Entity
@Stateless
public class User implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long userId;
    public String username;
    public String email;
    public String password;
    public String role;


    @ManyToMany(cascade = CascadeType.ALL, fetch=FetchType.LAZY)
    @JoinTable(
        name = "user_board",
        joinColumns = @JoinColumn(name = "userId"),
        inverseJoinColumns = @JoinColumn(name = "boardId")
    )
    public List<Board> boards;
    
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch=FetchType.LAZY)
    public List<Board> ownedBoards;
    
    @OneToOne(mappedBy="assignee", cascade = CascadeType.ALL, fetch=FetchType.EAGER)
    private Card card;
    private boolean isSignedIn;

    public User() {}
    
    public boolean isSignedIn() {
        return isSignedIn;
    }

    public void setSignedIn(boolean signedIn) {
        isSignedIn = signedIn;
    }
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
    
}
