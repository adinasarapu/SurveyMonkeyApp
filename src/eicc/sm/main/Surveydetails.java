package eicc.sm.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import eicc.sm.model.Answer;
import eicc.sm.model.Collector;
import eicc.sm.model.Question;
import eicc.sm.model.Response;
import eicc.sm.model.ResponseDetail;
import eicc.sm.model.ResponsePage;
import eicc.sm.model.SurveyDetail;

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
	 	 * 
	 	/surveys/{id}/responses/{id}
	 	/surveys/{id}/responses/{id}/details
	 	
	 	/surveys/{id}/rollups
	 	 
	 	/surveys/{id}/pages/{id}/questions
	 	/surveys/{id}/pages/{id}/questions/{id}
	 	/surveys/{id}/pages/{id}/questions/{id}/rollups
	 	/surveys/{id}/pages/{id}/questions/{id}/trends
	 	 
	 	/contact_lists
	 	/contact_lists/{id}
	 	/contact_lists/{id}/contacts
	 	 
	 	/contacts
	 	/contacts/{id}
	 	/collectors/{id}/messages/{id}/recipients
	 	/collectors/{id}/recipients
	 	 
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
		List<SurveyDetail> surs = new ArrayList<SurveyDetail>();
		for (int i = 0; i < arr.length(); i++)
		{
		    String survey_id = arr.getJSONObject(i).getString("id");
		    String survey_title = arr.getJSONObject(i).getString("title");
		    surveyMap.put(survey_id, survey_title);
			// SURVEY DETAILS
			String serveyDetails = getJsonWithEndpointRestMethod(ENDPOINT+"/"+survey_id, REST_METHOD);
			// date_created; response_count; question_count; language; date_modified
			JSONObject surveyDetailObj = new JSONObject(serveyDetails);
			SurveyDetail sd = new SurveyDetail();
			//System.out.println(serveyDetails);
			
			String date_created = surveyDetailObj.get("date_created").toString();
			String response_count = surveyDetailObj.get("response_count").toString();
			String question_count = surveyDetailObj.get("question_count").toString();
			String language = surveyDetailObj.get("language").toString();
			String date_modified = surveyDetailObj.get("date_modified").toString();
			
			sd.setDateCreated(date_created);
			sd.setResponseCount(response_count);
			sd.setQuestionCount(question_count);
			sd.setLanguage(language);
			sd.setDateModified(date_modified);
			
			surs.add(sd);
			
			//sd.setReponse(res);
			
			// System.out.println(survey_id+"\t"+survey_title+"\t"+date_created+"\t"+response_count+"\t"+question_count+"\t"+language+"\t"+date_modified);
			String responses = getJsonWithEndpointRestMethod(ENDPOINT+"/"+survey_id+"/responses", REST_METHOD);
			// System.out.println(response);
			JSONObject responsesObj = new JSONObject(responses);
			JSONArray resArr = responsesObj.getJSONArray("data");
			List<Response> resList = new ArrayList<Response>();
			for (int j = 0; j < resArr.length(); j++){
				Response res = new Response();
				// String resId = resArr.get(j).toString(); 
				String resId = resArr.getJSONObject(j).getString("id");
				//resIds.add(resId);
				System.out.print("Response ID = "+ resId);
				String resposnse = getJsonWithEndpointRestMethod(ENDPOINT+"/"+survey_id+"/responses/"+resId, REST_METHOD);	
				res.setId(new JSONObject(resposnse).get("id").toString());
				res.setTotalTime(new JSONObject(resposnse).get("total_time").toString());
				res.setIpAddress(new JSONObject(resposnse).get("ip_address").toString());
				res.setDateModified(new JSONObject(resposnse).get("date_modified").toString());
				res.setResponseStatus(new JSONObject(resposnse).get("response_status").toString());
				
				String collector_id1 = new JSONObject(resposnse).get("collector_id").toString();
				res.setCollectorId(collector_id1);
				res.setDateCreated(new JSONObject(resposnse).get("date_created").toString());
				res.setSurveyId(new JSONObject(resposnse).get("survey_id").toString());
				
				// System.out.println("Collector ID1 = "+ collector_id1);
				
				String resDetails = getJsonWithEndpointRestMethod(ENDPOINT+"/"+survey_id+"/responses/"+resId+"/details", REST_METHOD);
				
				ResponseDetail resDetail = new ResponseDetail();
				resDetail.setId(new JSONObject(resDetails).get("id").toString());
				resDetail.setIp_address(new JSONObject(resDetails).get("ip_address").toString());
				resDetail.setTotal_time(new JSONObject(resDetails).get("total_time").toString());
				resDetail.setDate_modified(new JSONObject(resDetails).get("date_modified").toString());
				resDetail.setResponse_status(new JSONObject(resDetails).get("response_status").toString());
				
				JSONArray resDetailArr = new JSONObject(resDetails).getJSONArray("pages");
				List<ResponsePage> pages = new ArrayList<ResponsePage>();
				for (int m = 0; m < resDetailArr.length(); m++){
					ResponsePage page = new ResponsePage();
					JSONObject resDetailArrObj = resDetailArr.getJSONObject(m);
					String resPageId = resDetailArrObj.getString("id");
					page.setId(resPageId);
					JSONArray questionArr = resDetailArrObj.getJSONArray("questions");
					List<Question> questions = new ArrayList<Question>();
					for(int n = 0; n < questionArr.length(); n++){
						Question question = new Question();
						JSONObject questionArrObj = questionArr.getJSONObject(m);
						question.setId(questionArrObj.getString("id"));
						JSONArray answerArr = questionArrObj.getJSONArray("answers");
						List<Answer> answers = new ArrayList<Answer>();
						for(int p = 0; p < answerArr.length(); p++){
							Answer answer = new Answer();
							JSONObject answerArrObj = answerArr.getJSONObject(p);
							System.out.println("choice_id "+ answerArrObj.getString("choice_id"));
							String choice_id = answerArrObj.getString("choice_id");
							String row_id = answerArrObj.getString("row_id");
							String text = answerArrObj.getString("text");
							if(choice_id != null)
								answer.setChoice_id(choice_id);
							if(row_id != null)
								answer.setRow_id(row_id);
							if(text != null)
								answer.setText(text);
							answers.add(answer);
						}
						question.setAnswers(answers);
						questions.add(question);
					}
					page.setQuestions(questions);
					pages.add(page);
				}
				resDetail.setPages(pages);
				res.setResponseDetail(resDetail);
			
				System.out.println(resDetails);
				
				Collector collector = new Collector();
				// System.out.print ("Collector ID1 = "+ collector_id1);
				String collectDetail = getJsonWithEndpointRestMethod(HOST+"collectors/"+collector_id1, REST_METHOD);
				collector.setId(new JSONObject(collectDetail).get("id").toString());
				//System.out.println(" Collector ID2 = "+ new JSONObject(collectDetail).get("id").toString());
				collector.setStatus(new JSONObject(collectDetail).get("status").toString());
				collector.setSender_email(new JSONObject(collectDetail).get("sender_email").toString());
				collector.setName(new JSONObject(collectDetail).get("name").toString());
				collector.setThank_you_message(new JSONObject(collectDetail).get("thank_you_message").toString());
				collector.setResponse_count(new JSONObject(collectDetail).get("response_count").toString());
				collector.setClosed_page_message(new JSONObject(collectDetail).get("closed_page_message").toString());
				collector.setClose_date(new JSONObject(collectDetail).get("close_date").toString());
				collector.setDisplay_survey_results(new JSONObject(collectDetail).get("display_survey_results").toString());
				collector.setAnonymous_type(new JSONObject(collectDetail).get("anonymous_type").toString());
				collector.setDisqualification_message(new JSONObject(collectDetail).get("disqualification_message").toString());
				collector.setPassword_enabled(new JSONObject(collectDetail).get("password_enabled").toString());
				collector.setDate_modified(new JSONObject(collectDetail).get("date_modified").toString());
				collector.setEdit_response_type(new JSONObject(collectDetail).get("edit_response_type").toString());
				collector.setRedirect_type(new JSONObject(collectDetail).get("redirect_type").toString());
				collector.setDate_created(new JSONObject(collectDetail).get("date_created").toString());
				collector.setResponse_limit(new JSONObject(collectDetail).get("response_limit").toString());
				res.setCollector(collector);
				resList.add(res);
				/*
				String collectorResponses = getJsonWithEndpointRestMethod(HOST+"collectors/"+collector_id1+"/responses", REST_METHOD);
				JSONArray colResArr = new JSONObject(collectorResponses).getJSONArray("data");
				List<String> lst = new ArrayList<String>();
				for (int k = 0; k < colResArr.length(); k++){
					// Response colRes = new Response();
					String colResponseId = colResArr.getJSONObject(j).getString("id");
					// System.out.println("Collector response ID = "+ colResponseId);
					lst.add(colResponseId);
					// String colResponse = getJsonWithEndpointRestMethod(HOST+"collectors/"+collector_id1+"/responses/"+colResponseId, REST_METHOD);
					// System.out.println(colResponse);
				}
				System.out.println("Collector response ID = "+ lst.toString());
				// System.out.println(collectorResponses);
				 
				 */
			}
			sd.setReponseList(resList);
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