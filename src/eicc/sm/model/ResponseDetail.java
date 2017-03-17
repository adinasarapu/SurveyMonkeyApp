package eicc.sm.model;

import java.util.List;

public class ResponseDetail {
	String id;
	String ip_address;
	String total_time;
	String date_modified;
	String response_status;
	List<ResponsePage> pages;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getIp_address() {
		return ip_address;
	}
	public void setIp_address(String ip_address) {
		this.ip_address = ip_address;
	}
	public String getTotal_time() {
		return total_time;
	}
	public void setTotal_time(String total_time) {
		this.total_time = total_time;
	}
	public String getDate_modified() {
		return date_modified;
	}
	public void setDate_modified(String date_modified) {
		this.date_modified = date_modified;
	}
	public String getResponse_status() {
		return response_status;
	}
	public void setResponse_status(String response_status) {
		this.response_status = response_status;
	}
	public List<ResponsePage> getPages() {
		return pages;
	}
	public void setPages(List<ResponsePage> pages) {
		this.pages = pages;
	}
}
