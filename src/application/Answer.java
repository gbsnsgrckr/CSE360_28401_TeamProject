package application;

import java.util.List;
import java.time.LocalDateTime;

public class Answer {
	private Integer id;
	private String text;
	private Integer author;
	private LocalDateTime createdOn;
	private LocalDateTime updatedOn;

	public Answer(Integer id, String text, Integer author, LocalDateTime createdOn, LocalDateTime updatedOn) {
		this.id = id;
		this.text = text;
		this.author = author;
		this.createdOn = createdOn;
		this.updatedOn = updatedOn;
	}

	public Answer(String text, Integer author) {
		this.text = text;
		this.author = author;
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

	public Integer getAuthor() {
		return author;
	}

	public LocalDateTime getCreatedOn() {
		return createdOn;
	}

	public LocalDateTime getUpdatedOn() {
		return updatedOn;
	}

	// Setters
	public void setId(Integer id) {
		this.id = id;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setAuthor(Integer author) {
		this.author = author;
	}

	public void setCreatedOn(LocalDateTime createdOn) {
		this.createdOn = createdOn;
	}

	public void setUpdatedOn(LocalDateTime updatedOn) {
		this.updatedOn = updatedOn;
	}

	public String toString() {
		return String.format("\nANSWER: \nID:\n	%s\nText:\n		%s\nAuthor:\n	%s\nCreated On:\n	%s\nUpdated On:\n	%s\n", id, text, author,
				createdOn, updatedOn);
	}

}
