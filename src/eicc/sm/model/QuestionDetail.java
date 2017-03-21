package eicc.sm.model;

import java.util.List;

public class QuestionDetail {
	
	QuestionSorting questionSorting;
	String questionFamily;
	String questionSubType;
	QuestionRequired questionRequired;
	boolean questionVisible;
	AnswerOption answerOption;
	String id;
	int position;
	QuestionValidation questionValidation;
	List<String> headings;
	boolean forced_ranking;
	public QuestionSorting getQuestionSorting() {
		return questionSorting;
	}
	public void setQuestionSorting(QuestionSorting questionSorting) {
		this.questionSorting = questionSorting;
	}
	public String getQuestionFamily() {
		return questionFamily;
	}
	public void setQuestionFamily(String questionFamily) {
		this.questionFamily = questionFamily;
	}
	public String getQuestionSubType() {
		return questionSubType;
	}
	public void setQuestionSubType(String questionSubType) {
		this.questionSubType = questionSubType;
	}
	public QuestionRequired getQuestionRequired() {
		return questionRequired;
	}
	public void setQuestionRequired(QuestionRequired questionRequired) {
		this.questionRequired = questionRequired;
	}
	public boolean isQuestionVisible() {
		return questionVisible;
	}
	public void setQuestionVisible(boolean questionVisible) {
		this.questionVisible = questionVisible;
	}
	public AnswerOption getAnswerOption() {
		return answerOption;
	}
	public void setAnswerOption(AnswerOption answerOption) {
		this.answerOption = answerOption;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public QuestionValidation getQuestionValidation() {
		return questionValidation;
	}
	public void setQuestionValidation(QuestionValidation questionValidation) {
		this.questionValidation = questionValidation;
	}
	public List<String> getHeadings() {
		return headings;
	}
	public void setHeadings(List<String> headings) {
		this.headings = headings;
	}
	public boolean isForced_ranking() {
		return forced_ranking;
	}
	public void setForced_ranking(boolean forced_ranking) {
		this.forced_ranking = forced_ranking;
	}
	
	
	// "sorting": 
	//		null OR
	//		{text, amount, type}
	// "family": 
	//		"open_ended" OR
	//		"datetime" OR
	//		"single_choice" --> verticle
	// "subtype":
	//			"multi" OR
	//			"single" OR
	//			"date_only" OR 
	//			"vertical" OR 
	//			 "both" 
	// "required": 
	//			null OR
	// 			{"text", "amount", "type"}
	// "visible": 
	//			true
	// "answers": --> text elements
	//			"rows" --> array (each element contains) ">= 1"
	// 					{"visible","text","position","id"}
	//			"choices" --> array (each element contains) ">= 1"
	//					{"visible","text","position","id"}
	//			"other" --> text elmenets
	//					{"num_lines","text","id","visible",
	//					"apply_all_rows","is_answer_choice",
	//					"position","num_chars","error_text"}
	//						
	// "id": "45112236" 
	// "position": 4
	//			
	// "validation": --> text elements 
	//				null OR
	//				{"sum_text","min","text","sum",
	//				"max","type"}
	//				
	//				
	// headings: --> array (each element contains)
	//				"heading"
	// "forced_ranking": false
}
