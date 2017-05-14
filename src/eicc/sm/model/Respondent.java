package eicc.sm.model;

import java.util.List;
import java.util.Map;

public class Respondent {
	String id;
	String ip_address;
	Map<String, Response> response;
	List<ResponsePage> responsePages;
	
	public List<ResponsePage> getResponsePages() {
		return responsePages;
	}
	public void setResponsePages(List<ResponsePage> responsePages) {
		this.responsePages = responsePages;
	}
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
	public Map<String, Response> getResponse() {
		return response;
	}
	public void setResponse(Map<String, Response> response) {
		this.response = response;
	}
	
}
