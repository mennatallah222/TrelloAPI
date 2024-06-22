package DTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SprintDTO {
  public Long sprintId;
  public Date startDate;
  public Date endDate;
  public String status;
  public List<CardDTO> cards;
  
   public SprintDTO() {
          this.cards = new ArrayList<>();
      }
  public void addCard(CardDTO card) {
        if (cards == null) {
            cards = new ArrayList<>();
        }
        cards.add(card);
    }
}