package DTO;

import java.util.List;

public class ListDTO {
	
	    private Long boardId;
	    private String name;
	    private String listsName;
	    private String cards;

	    public ListDTO() {}

	    public ListDTO(Long boardId, String name, String listsName) {
	        this.boardId = boardId;
	        this.name = name;
	        this.listsName = listsName;
	    }

	    public Long getBoardId() {
	        return boardId;
	    }

	    public void setBoardId(Long boardId) {
	        this.boardId = boardId;
	    }

	    public String getName() {
	        return name;
	    }

	    public void setName(String name) {
	        this.name = name;
	    }

	    public String getLists() {
	        return listsName;
	    }

	    public void setLists(String listsName) {
	        this.listsName = listsName;
	    }

		
		public String getCards() {
			return cards;
		}

		public void setCards(String cards) {
			this.cards = cards;
		}
	

}
