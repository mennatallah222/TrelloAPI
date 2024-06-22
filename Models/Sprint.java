package model;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.*;

@Stateless
@Entity
public class Sprint implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long sprintId;
    private Date startDate;
    private Date endDate;
    private String status;
    public int totalStoryPoints;
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "boardId")
    public Board board;
    @OneToMany(mappedBy = "sprint", cascade = CascadeType.ALL, fetch=FetchType.EAGER)
    public List<Card> cards; //Tasks
    public Date getStartDate() {
        return startDate;
    }
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    public Date getEndDate() {
        return endDate;
    }
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public List<Card> getCards() {
        // TODO Auto-generated method stub
        return cards;
    }
    public Board getBoard() {
        // TODO Auto-generated method stub
        return board;
    }
    public void setBoard(Board b) {
        // TODO Auto-generated method stub
        this.board=b;
    }
}