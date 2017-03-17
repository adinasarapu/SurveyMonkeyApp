package eicc.sm.model;

import java.util.List;

public class ResponsePage {
	String id;
	List<Question> questions;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<Question> getQuestions() {
		return questions;
	}
	public void setQuestions(List<Question> questions) {
		this.questions = questions;
	}
}
