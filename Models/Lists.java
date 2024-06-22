package model;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.*;

@Stateless
@Entity
public class Lists implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long listId;  
    public String name;
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "boardId")
    public Board board;
    @OneToMany(mappedBy = "list", cascade = CascadeType.ALL, fetch=FetchType.EAGER)
	public List<Card> cards;
	public List<Card> getCards() {
		return cards;
	}
	public void setName(String name)
	{
		this.name=name;
	}
	public String getName() {
		return name;
	}


	
}
