package eicc.sm.model;

import java.util.List;

public class Question {
	int position;
	String heading;
	String id;
	List<QuestionDetail> questionDetails;
	
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public String getHeading() {
		return heading;
	}
	public void setHeading(String heading) {
		this.heading = heading;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
}
