package DTO;


import java.util.Map;


public class BoardDTO {
    private Long userId;
    private String username;
    private String email;
    private String boards;
    private Map<String, String> collaborators;
    
    public String getBoard() {
        return boards;
    }

    public void setBoard(String boards) {
        this.boards = boards;
    }
    public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Map<String, String> getCollaborators() {
		return collaborators;
	}

	public void setCollaborators(Map<String, String> collaborators) {
		this.collaborators = collaborators;
	}
    
   }

