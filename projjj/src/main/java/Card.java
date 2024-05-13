package Entities;

import java.io.Serializable;
import java.util.*;

import javax.ejb.Stateless;
import javax.json.JsonValue;
import javax.persistence.*;


@Stateless
@Entity
public class Card implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long cardId;
    private String name;
    //description and comments
    public String description;
    @ElementCollection(fetch = FetchType.EAGER)
    public List<String> comments;
    public String status;
    @OneToOne
    @JoinColumn(name="assigneeId")
    private User assignee;
    
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "listId")
    public Lists list;
    
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="sprintId")
    private Sprint sprint;
    
    private int storyPoint;
    
    
    public List<String> getComments() {
    return comments;
  }

  public Long getId() {
    // TODO Auto-generated method stub
    return cardId;
  }

  public String getDescription() {
    // TODO Auto-generated method stub
    return description;
  }

  public void setDescription(String desc) {
    // TODO Auto-generated method stub
    this.description=desc;
  }

  public void setComments(List<String> c) {
    // TODO Auto-generated method stub
    this.comments=c;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setUser(User u) {
    // TODO Auto-generated method stub
    this.assignee=u;
  }
  public void setStatus(String status) {
      this.status = status;
  }

  public String getStatus() {
      return status;
  }

  public int getStoryPoint() {
    return storyPoint;
  }

  public void setStoryPoint(int storyPoint) {
    this.storyPoint = storyPoint;
  }

  public void setSprint(Sprint s) {
    this.sprint=s;
  }
  public Sprint gdetSprint() {
    return sprint;
  }
}
