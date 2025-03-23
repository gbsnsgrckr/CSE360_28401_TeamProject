package application;

import java.util.List;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Review {
	private Integer id;
	private Boolean forQuestion;
	private Integer relatedId;
	private String text;
	private Integer authorId;
	private LocalDateTime createdOn;
	private LocalDateTime updatedOn;
	private User author;
	private String authorName;
	private Integer messageCount;
	private Integer voteCount;

	// Constructor mainly for when getAllQuestions() method is used in QAHelper.java
	public Review(Integer id, Boolean forQuestion, Integer relatedId, String text, Integer authorId, LocalDateTime createdOn,
			LocalDateTime updatedOn, User author, String authorName, Integer messageCount, Integer voteCount) {
		this.id = id;
		this.forQuestion = forQuestion;
		this.relatedId = relatedId;
		this.text = text;
		this.authorId = authorId;
		this.createdOn = createdOn;
		this.updatedOn = updatedOn;
		this.author = author;
		this.authorName = authorName;
		this.messageCount = messageCount;
		this.voteCount = voteCount;
	}
	
	public Review(Boolean forQuestion, Integer relatedId, String text, Integer authorId, User author, String authorName) {
		this.id = null;
		this.forQuestion = forQuestion;
		this.relatedId = relatedId;
		this.text = text;
		this.authorId = authorId;
		this.createdOn = null;
		this.updatedOn = null;
		this.author = author;
		this.authorName = authorName;
	}
	
	public Review(Boolean forQuestion, Integer relatedId, String text, Integer authorId) {
		this.id = null;
		this.forQuestion = forQuestion;
		this.relatedId = relatedId;
		this.text = text;
		this.authorId = authorId;
		this.createdOn = null;
		this.updatedOn = null;
	}
	
	public Review() {
		this.id = null;
		this.forQuestion = null;
		this.relatedId = null;
		this.text = null;
		this.authorId = null;
		this.createdOn = null;
		this.updatedOn = null;
		this.voteCount = null;
	}

	// Getters
	public Integer getId() {
		return id;
	}
	
	public Integer getVoteCount() {
		return voteCount;
	}
	
	public Integer getMessageCount() {
		return messageCount;
	}
	
	public Boolean getForQuestion() {
		return forQuestion;
	}

	public String getText() {
		return text;
	}

	public Integer getAuthorId() {
		return authorId;
	}

	public LocalDateTime getCreatedOn() {
		return createdOn;
	}

	public LocalDateTime getUpdatedOn() {
		return updatedOn;
	}
	
	public User getAuthor() {
		return author;
	}

	public int getDaysSinceCreated() {
		if (createdOn == null) {
			return 0;
		}
		return (int) ChronoUnit.DAYS.between(createdOn, LocalDateTime.now());
	}
	
	public String getAuthorName() {
		return authorName;
	}
	
	public Integer getRelatedId() {
		return relatedId;
	}
	
	// Setters
	public void setId(Integer id) {
		this.id = id;
	}
	
	public void setVoteCount(Integer voteCount) {
		this.voteCount = voteCount;
	}
	
	public void setMessageCount(Integer messageCount) {
		this.messageCount = messageCount;
	}
	
	public void setForQuestion(Boolean forQuestion) {
		this.forQuestion = forQuestion;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setAuthorId(Integer authorId) {
		this.authorId = authorId;
	}

	public void setCreatedOn(LocalDateTime createdOn) {
		this.createdOn = createdOn;
	}

	public void setUpdatedOn(LocalDateTime updatedOn) {
		this.updatedOn = updatedOn;
	}
	
	public void setAuthor(User author) {
		this.author = author;
	}
	
	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}
	
	public void setRelatedId(Integer relatedId) {
		this.relatedId = relatedId;
	}

	public String toString() {
		return String.format(
				"\nQUESTIONID:\n\t%s\nTitle:\n\t%s\nText:\n\t%s\nRelatedIds:\n\t%s\nAuthorId:\n\t%s\nAuthor Name:\n\t%s  \nCreated On:\n\t%s\nUpdated On:\n\t%s",
				id, forQuestion, relatedId, text, authorId, authorName, createdOn, updatedOn);
	}

	public String toDisplay() {
		String displayAuthor;
		int daysSinceCreated = getDaysSinceCreated();


			// If author returns null(In case of test cases or populated database without
			// proper users)
			// then an empty sting is display for the author.
			if (author == null) {
				displayAuthor = "User";
			} else {
				displayAuthor = authorName;
			}

			return String.format("%s             %smsg\n%s             	         				      	  %sd", text, messageCount, displayAuthor, daysSinceCreated);
		}

	
	public String toDisplayWithText() {
		String displayAuthor;
		String voteValue;
		int daysSinceCreated = getDaysSinceCreated();
		// Adds a " - " before the value if it is positive for display
		if (voteCount <= 0) {
			voteValue = String.valueOf(voteCount);
		} else {
			voteValue = "+" + String.valueOf(voteCount);
		}

			// If author returns null(In case of test cases or populated database without
			// proper users)
			// then an empty sting is display for the author.
			if (author == null) {
				displayAuthor = "User";
			} else {
				displayAuthor = author.getName();
			}

			return String.format("Review Id: %s\n\n%s           					    		Rating: %s\n\n%s           					    			 						%sd", id, text, voteValue, displayAuthor, daysSinceCreated);
		}
	

}
