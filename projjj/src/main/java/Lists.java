
import java.io.Serializable;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.*;

@Stateless
@Entity
public class Lists implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long listId;
    
    String name;
    
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "boardId")
    public Board board;
    
    
    @OneToMany(mappedBy = "list", cascade = CascadeType.ALL, fetch=FetchType.LAZY)
	private List<Card> cards;


	public List<Card> getCards() {
		return cards;
	}


	public void setCards(List<Card> cards) {
		this.cards = cards;
	}
}
