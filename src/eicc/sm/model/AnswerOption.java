package eicc.sm.model;

import java.util.List;

public class AnswerOption {
	
	List<AnswerRow> answerRowList;
	List<AnswerChoice> answerChoiceList;
	AnswerOther answerOther;
	
	public List<AnswerRow> getAnswerRowList() {
		return answerRowList;
	}
	public void setAnswerRowList(List<AnswerRow> answerRowList) {
		this.answerRowList = answerRowList;
	}
	public List<AnswerChoice> getAnswerChoiceList() {
		return answerChoiceList;
	}
	public void setAnswerChoiceList(List<AnswerChoice> answerChoiceList) {
		this.answerChoiceList = answerChoiceList;
	}
	public AnswerOther getAnswerOther() {
		return answerOther;
	}
	public void setAnswerOther(AnswerOther answerOther) {
		this.answerOther = answerOther;
	}
}
