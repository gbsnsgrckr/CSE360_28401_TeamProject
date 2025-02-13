package application;

import java.util.List;
import java.time.LocalDateTime;

public class Question {
	private Integer id;
	private String title;
	private String text;
	private Integer author;
	private LocalDateTime createdOn;
	private LocalDateTime updatedOn;
	private List<String> comp;
	
	// Constructor mainly for when getAllQuestions() method is used in QAHelper.java
	public Question(Integer id, String title, String text, Integer author, LocalDateTime createdOn, LocalDateTime updatedOn, List<String> comp) {
		this.id = id;
		this.title = title;
		this.text = text;
		this.author = author;
		this.createdOn = createdOn;
		this.updatedOn = updatedOn;
		this.comp = comp;
	}

	public Question(Integer id, String title, String text, Integer author, LocalDateTime createdOn, LocalDateTime updatedOn) {
		this.id = id;
		this.title = title;
		this.text = text;
		this.author = author;
		this.createdOn = createdOn;
		this.updatedOn = updatedOn;
	}

	public Question(String title, String text, Integer author) {
		this.title = title;
		this.text = text;
		this.author = author;
	}

	public Question(String title, String text) {
		this.title = title;
		this.text = text;
	}

	// Getters
	public Integer getId() {
		return id;
	}

	public String getTitle() {
		return title;
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
	
	public List<String> getComp() {
		return comp;
	}

	// Setters
	public void setId(Integer id) {
		this.id = id;
	}

	public void setTitle(String title) {
		this.title = title;
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
	
	public void setComp(List<String> comp) {
		this.comp = comp;
	}

	public String toString() {
		return String.format(
				"\nQUESTION: \nID:\n	%s\nTitle:\n	%s\nText:\n	%s\nAuthor:\n	%s\nCreated On:\n	%s\nUpdated On:\n	%s", id,
				title, text, author, createdOn, updatedOn);
	}

}
