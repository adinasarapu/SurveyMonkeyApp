package eicc.sm.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class Surveydetails {
	
	/*	
	 * http://jsonviewer.stack.hu
	 * http://www.jsonschema2pojo.org
	 * 
	 * GET https://api.surveymonkey.net/v3/groups
	 * https://api.surveymonkey.net/v3/surveys/87447384/responses/bulk
	 * {survey_id} = 87447384, 112575325
	 * {collector_id} = 94856062, 152319856
	 * /surveys/{survey_id}/responses/{reponse_id}
	 * {reponse_id} = 5164961828, 5164963322, bulk
	 * 
	 * /collectors/{collector_id}/responses/{reponse_id}
	 * 
	 */ 
	// http://theoryapp.com/parse-json-in-java/
	
	static String HOST = "https://api.surveymonkey.net/v3/";
	/* 
	 	/surveys
	 	/surveys/{id}, 
	 	/surveys/{id}/details
	 	/surveys/{id}/responses/{id}
	 	/collectors/{id}/responses/{id}
	*/

	static String Access_Token = "bearer DR729crsCRsLBsqLMtAFjZnhiaihuC4cQqULmHqSsrcZILrfvO18Wh9evj8Bnj2DP-8MzVX3uNlAL63qpY.-MtfS2lW.1HwXx0OiJn2MOA3xWeTx.lFtRumFVXaZqizl";
	static String ENDPOINT = "surveys";
	// static String SURVEY_ID = "12088715"; // "87447384";
	static String REST_METHOD = "GET";
	
	public static void main(String args[]) {
		ENDPOINT = HOST+ENDPOINT;
		System.out.println(ENDPOINT);
		// SURVEYS
		String serveys = getJsonWithEndpointRestMethod(ENDPOINT, REST_METHOD);
		//System.out.println(serveys);
		
		JSONObject surveyObj = new JSONObject(serveys);
		JSONArray arr = surveyObj.getJSONArray("data");
		Map<String, String> surveyMap = new HashMap<String, String>();
		for (int i = 0; i < arr.length(); i++)
		{
		    String survey_id = arr.getJSONObject(i).getString("id");
		    String survey_title = arr.getJSONObject(i).getString("title");
		    surveyMap.put(survey_id, survey_title);
		    // System.out.println(survey_id+"\t"+survey_title);
		    //System.out.println(ENDPOINT+"/"+survey_id);
			// SURVEY DETAILS
			String serveyDetails = getJsonWithEndpointRestMethod(ENDPOINT+"/"+survey_id, REST_METHOD);
			// date_created; response_count; question_count; language; date_modified
			JSONObject surveyDetailObj = new JSONObject(serveyDetails);
			//System.out.println(serveyDetails);
			String date_created = surveyDetailObj.get("date_created").toString();
			String response_count = surveyDetailObj.get("response_count").toString();
			String question_count = surveyDetailObj.get("question_count").toString();
			String language = surveyDetailObj.get("language").toString();
			String date_modified = surveyDetailObj.get("date_modified").toString();
			System.out.println(survey_id+"\t"+survey_title+"\t"+date_created+"\t"+response_count+"\t"+question_count+"\t"+language+"\t"+date_modified);
		}
	}

	private static String getJsonWithEndpointRestMethod(String endPoint, String restMethod) {
		//System.out.println("request being sent");
		//System.out.println(endPoint);
		StringBuffer stringBuffer = new StringBuffer();
		try {
			URL ourl = new URL(endPoint);
			HttpURLConnection conn = (HttpURLConnection) ourl.openConnection();
			
			conn.setRequestMethod(restMethod);
			conn.setRequestProperty("Authorization",Access_Token);
			conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			conn.connect();
			
			int k = conn.getResponseCode();
			
			//System.out.println("The response code received is " + k);
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			BufferedReader bufferReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String str;
			 
			while ((str = bufferReader.readLine()) != null) 
			{ 
				stringBuffer.append(str); 
				stringBuffer.append("\n");
			}
			// System.out.println(stringBuffer.toString());
			conn.disconnect();
			
			//JCodeModel codeModel = new JCodeModel();
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stringBuffer.toString();
	}
}