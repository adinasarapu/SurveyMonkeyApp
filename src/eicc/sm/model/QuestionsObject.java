package eicc.sm.model;

import java.util.List;

public class QuestionsObject {
	
	int total;
	List<Question> questions = null;
	
	public int getTotalQuestionCount() {
		return total;
	}
	
	public void setTotalQuestionCount(int total) {
		this.total = total;
	}
	
	public List<Question> getQuestions() {
		return questions;
	}
	
	public void setQuestions(List<Question> questions) {
		this.questions = questions;
	}
}