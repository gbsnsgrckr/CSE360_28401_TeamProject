package application;

import java.util.ArrayList;
import java.util.List;

public class QuestionsSet {
	private Answer answer;
	private List<Question> questions;

	// Setters
	public QuestionsSet() {
		this.questions = new ArrayList<>();
	}

	public QuestionsSet(Question question) {
		questions.add(question);
	}	
	
	public void setQuestions(List<Question> questions) {
		this.questions = questions;
	}
	
	public void setAnswer(Answer answer) {
		this.answer = answer;
	}
	
	public void addQuestion(Question question) {
		questions.add(question);
	}

	// Getters
	public List<Question> getQuestions() {
		return questions;
	}
	
	public Answer getAnswer() {
		return answer;
	}

	public String toString() {
		String questionString = new String();
		for (Question question : questions) {
			questionString += (question.toString() + "\n\n");
		}
		return questionString;
	}
	
	public int size() {
		return questions.size();
	}

}
