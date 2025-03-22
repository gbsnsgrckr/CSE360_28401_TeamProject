package application;

import java.util.ArrayList;
import java.util.List;

public class AnswersSet {
	private Question question;
	private List<Answer> answers;

	// Setters
	public AnswersSet() {
		this.answers = new ArrayList<>();
	}

	public AnswersSet(Answer answer) {
		answers.add(answer);
	}

	public void setAnswers(List<Answer> answers) {
		this.answers = answers;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

	public void addAnswer(Answer answer) {
		answers.add(answer);
	}

	// Getters
	public List<Answer> getAnswers() {
		return answers;
	}

	public Question getQuestion() {
		return question;
	}

	public String toString() {
		String answerString = new String();
		for (Answer answer : answers) {
			answerString += (answer.toString() + "\n\n");
		}
		return answerString;
	}
	
	public int size() {
		return answers.size();
	}
}
