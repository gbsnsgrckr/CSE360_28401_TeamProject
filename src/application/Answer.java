package application;

import java.util.List;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Answer {
	private Integer id;
	private String text;
	private Integer authorId;
	private LocalDateTime createdOn;
	private LocalDateTime updatedOn;
	private User author;
	private String authorName;

	public Answer(Integer id, String text, Integer authorId, LocalDateTime createdOn, LocalDateTime updatedOn,
			User author, String authorName) {
		this.id = id;
		this.text = text;
		this.authorId = authorId;
		this.createdOn = createdOn;
		this.updatedOn = updatedOn;
		this.author = author;
		this.authorName = authorName;
	}
	
	public Answer(Integer id, String text, Integer authorId, LocalDateTime createdOn, LocalDateTime updatedOn,
			User author) {
		this.id = id;
		this.text = text;
		this.authorId = authorId;
		this.createdOn = createdOn;
		this.updatedOn = updatedOn;
		this.author = author;
	}

	public Answer(Integer id, String text, Integer authorId, LocalDateTime createdOn, LocalDateTime updatedOn) {
		this.id = id;
		this.text = text;
		this.authorId = authorId;
		this.createdOn = createdOn;
		this.updatedOn = updatedOn;
	}

	public Answer(String text, Integer authorId) {
		this.text = text;
		this.authorId = authorId;
	}

	public Answer(String text) {
		this.text = text;
	}

	// Getters
	public Integer getId() {
		return id;
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

	// Setters
	public void setId(Integer id) {
		this.id = id;
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

	public String toString() {
		return String.format(
				"\nANSWER: \nID:\n	%s\nText:\n		%s\nAuthorId:\n	  %s\nAuthor Name:\n	  %s\nCreated On:\n	%s\nUpdated On:\n	%s\n",
				id, text, authorId, authorName, createdOn, updatedOn);
	}

	public String toDisplay() {
		String displayAuthor;
		int daysSinceCreated = getDaysSinceCreated();

		// If title is empty then return an empty string
		if (text == "") {
			return "";
		} else {
			// If author returns null(In case of test cases or populated database without
			// proper users)
			// then an empty sting is display for the author.
			if (author == null) {
				displayAuthor = "User";
			} else {
				displayAuthor = getAuthorName();
			}

			return String.format("%s\n\n\n%s             					  					%sd", text, displayAuthor, daysSinceCreated);
		}
	}

}
