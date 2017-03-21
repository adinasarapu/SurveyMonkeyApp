package eicc.sm.model;

import java.util.List;

public class SurveyDetail {
	String id;
	String servey_title;
	String dateCreated;
	String responseCount; 
	String questionCount;
	String language; 
	String dateModified;
	
	List<Response> reponseList;
	
	
	public List<Response> getReponseList() {
		return reponseList;
	}
	public void setReponseList(List<Response> reponseList) {
		this.reponseList = reponseList;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getServeyTitle() {
		return servey_title;
	}
	public void setSurveyTitle(String name) {
		this.servey_title = name;
	}
	public String getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}
	public String getResponseCount() {
		return responseCount;
	}
	public void setResponseCount(String responseCount) {
		this.responseCount = responseCount;
	}
	public String getQuestionCount() {
		return questionCount;
	}
	public void setQuestionCount(String questionCount) {
		this.questionCount = questionCount;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getDateModified() {
		return dateModified;
	}
	public void setDateModified(String dateModified) {
		this.dateModified = dateModified;
	}
	
	
}
