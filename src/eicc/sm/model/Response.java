package eicc.sm.model;

import java.util.List;

public class Response {
	
	String id;
	String totalTime;
	String ipAddress;
	String dateModified;
	String responseStatus;
	String collectorId;
	String dateCreated;
	String surveyId;
	Collector collector;
	ResponseDetail responseDetail;
	
	public ResponseDetail getResponseDetail() {
		return responseDetail;
	}
	public void setResponseDetail(ResponseDetail responseDetail) {
		this.responseDetail = responseDetail;
	}
	public Collector getCollector() {
		return collector;
	}
	public void setCollector(Collector collector) {
		this.collector = collector;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTotalTime() {
		return totalTime;
	}
	public void setTotalTime(String totalTime) {
		this.totalTime = totalTime;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getDateModified() {
		return dateModified;
	}
	public void setDateModified(String dateModified) {
		this.dateModified = dateModified;
	}
	public String getResponseStatus() {
		return responseStatus;
	}
	public void setResponseStatus(String responseStatus) {
		this.responseStatus = responseStatus;
	}
	public String getCollectorId() {
		return collectorId;
	}
	public void setCollectorId(String collectorId) {
		this.collectorId = collectorId;
	}
	public String getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}
	public String getSurveyId() {
		return surveyId;
	}
	public void setSurveyId(String surveyId) {
		this.surveyId = surveyId;
	}
	
}
