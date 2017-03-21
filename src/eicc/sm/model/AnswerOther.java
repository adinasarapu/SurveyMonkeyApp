package eicc.sm.model;

public class AnswerOther {
	
	int num_lines;
	String text;
	String id;
	boolean visible;
	boolean apply_all_rows;
	boolean is_answer_choice;
	int position;
	int num_chars;
	String error_text;
	public int getNum_lines() {
		return num_lines;
	}
	public void setNum_lines(int num_lines) {
		this.num_lines = num_lines;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public boolean isVisible() {
		return visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	public boolean isApply_all_rows() {
		return apply_all_rows;
	}
	public void setApply_all_rows(boolean apply_all_rows) {
		this.apply_all_rows = apply_all_rows;
	}
	public boolean isIs_answer_choice() {
		return is_answer_choice;
	}
	public void setIs_answer_choice(boolean is_answer_choice) {
		this.is_answer_choice = is_answer_choice;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public int getNum_chars() {
		return num_chars;
	}
	public void setNum_chars(int num_chars) {
		this.num_chars = num_chars;
	}
	public String getError_text() {
		return error_text;
	}
	public void setError_text(String error_text) {
		this.error_text = error_text;
	}
}
