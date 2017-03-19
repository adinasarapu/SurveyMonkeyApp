package eicc.sm.model;

import java.util.List;

public class Pages {
	
	int total;
	List<Page> pages;
	
	public int getTotalPageCount() {
		return total;
	}
	
	public void setTotalPageCount(int total) {
		this.total = total;
	}
	
	public List<Page> getPages() {
		return pages;
	}
	
	public void setPages(List<Page> pages) {
		this.pages = pages;
	}
}
