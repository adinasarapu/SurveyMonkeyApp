package eicc.sm.model;

public class Page {
	
	String description;
	String id;
	int position;
	String title;
	QuestionsObject questionsObject;
	
	public QuestionsObject getQuestionsObject() {
		return questionsObject;
	}
	public void setQuestionsObject(QuestionsObject questionsObject) {
		this.questionsObject = questionsObject;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
}
