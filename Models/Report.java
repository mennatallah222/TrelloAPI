package model;
import java.io.Serializable;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Stateless
@Entity
public class Report implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long repotId;
    
    @ElementCollection(fetch = FetchType.EAGER)
    public List<String> tasks;

    private Long sprintId;
    private int completed;
    private int ucompleted;


    public void setSprintId(Long sid) {
        // TODO Auto-generated method stub
        this.sprintId=sid;
    }

    public void setTotalCompletedStoryPoints(int c) {
        // TODO Auto-generated method stub
        this.completed=c;
    }

    public void setTotalUncompletedStoryPoints(int u) {
        // TODO Auto-generated method stub
        this.ucompleted=u;
    }

    public int getUcompleted() {
        return ucompleted;
    }

    public Long getSprintId() {
        return sprintId;
    }

    public int getCompleted() {
        return completed;
    }
}