package eicc.sm.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.json.JSONArray;
import org.json.JSONObject;

import eicc.sm.model.Answer;
import eicc.sm.model.Collector;
import eicc.sm.model.Page;
import eicc.sm.model.Pages;
import eicc.sm.model.Question;
import eicc.sm.model.QuestionsObject;
import eicc.sm.model.Response;
import eicc.sm.model.ResponseDetail;
import eicc.sm.model.ResponsePage;
import eicc.sm.model.SurveyDetail;

public class SurveyApp {
	
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
	// static String HOST = "https://api.surveymonkey.net/v3/";
	//static String Access_Token = "bearer DR729crsCRsLBsqLMtAFjZnhiaihuC4cQqULmHqSsrcZILrfvO18Wh9evj8Bnj2DP-8MzVX3uNlAL63qpY.-MtfS2lW.1HwXx0OiJn2MOA3xWeTx.lFtRumFVXaZqizl";
	static String ENDPOINT = null;
	static String REST_METHOD = null;
	static String Access_Token = null;
	
	public static void main(String args[]) {
		//ENDPOINT = HOST+ENDPOINT;
		// System.out.println(ENDPOINT);
		ResourceBundle resourceBundle = ResourceBundle.getBundle("config");
		Access_Token = resourceBundle.getString("Access_Token");
		REST_METHOD = resourceBundle.getString("REST_METHOD");
		ENDPOINT = resourceBundle.getString("ENDPOINT");
		
		System.out.println(Access_Token+"\n"+REST_METHOD+"\n"+ENDPOINT);
		
		/*List<SurveyDetail> sds = getAllServeys();
		Iterator<SurveyDetail> sdIt = sds.iterator();
		while(sdIt.hasNext()){
			SurveyDetail sd = sdIt.next();
			System.out.println(sd.getId() + "\t" + sd.getName());
			Pages p = getServeyPages(sd.getId());
		}*/
	}

	private static List<SurveyDetail> getAllServeys() {
		String serveys = getJsonWithEndpointRestMethod(ENDPOINT, REST_METHOD);
		JSONObject surveyObj = new JSONObject(serveys);
		JSONArray arr = surveyObj.getJSONArray("data");
		// Map<String, String> surveyMap = new HashMap<String, String>();
		List<SurveyDetail> surs = new ArrayList<SurveyDetail>();
		for (int i = 0; i < arr.length(); i++) {
			String survey_id = arr.getJSONObject(i).getString("id");
			// String survey_title = arr.getJSONObject(i).getString("title");
			// surveyMap.put(survey_id, survey_title);
		
			// SURVEY DETAILS
			String serveyDetails = getJsonWithEndpointRestMethod(ENDPOINT + "/" + survey_id, REST_METHOD);
			// date_created; response_count; question_count; language;
			// date_modified
			JSONObject surveyDetailObj = new JSONObject(serveyDetails);
			SurveyDetail sd = new SurveyDetail();

			sd.setId(survey_id);
			sd.setName(surveyDetailObj.get("name").toString());
			sd.setDateCreated(surveyDetailObj.get("date_created").toString());
			sd.setResponseCount(surveyDetailObj.get("response_count").toString());
			sd.setQuestionCount(surveyDetailObj.get("question_count").toString());
			sd.setLanguage(surveyDetailObj.get("language").toString());
			sd.setDateModified(surveyDetailObj.get("date_modified").toString());

			surs.add(sd);
		}
		return surs;
	}

	private static Pages getServeyPages(String survey_id) {
		Pages pagesObj = new Pages();
		String pages = getJsonWithEndpointRestMethod(ENDPOINT + "/" + survey_id + "/pages", REST_METHOD);
		JSONObject pagesJsonObj = new JSONObject(pages);
		if (!pagesJsonObj.isNull("total")) {
			pagesObj.setTotalPageCount(pagesJsonObj.getInt("total"));
		}
		JSONArray pagesObjArr = null;
		// System.out.println("total pages --> "+
		// pagesJsonObj.getString("total"));
		if (!pagesJsonObj.isNull("data")) {
			pagesObjArr = pagesJsonObj.getJSONArray("data");
		}
		if (pagesObjArr != null) {
			List<Page> pageList = new ArrayList<Page>();
			for (int j = 0; j < pagesObjArr.length(); j++) {
				Page page = getPage(pagesObjArr, j);
				String questions = getJsonWithEndpointRestMethod(
						ENDPOINT + "/" + survey_id + "/pages/" + page.getId() + "/questions", REST_METHOD);
				// System.out.println(questions);
				JSONObject questionsJsonObj = new JSONObject(questions);
				JSONArray questionArr = null;
				if (!questionsJsonObj.isNull("data")) {
					questionArr = questionsJsonObj.getJSONArray("data");
				}
				if (questionArr != null) {
					for (int k = 0; k < questionArr.length(); k++) {
						int tot = questionsJsonObj.getInt("total");
						if (tot > 0) {
							System.out.println("tot > 0");
							QuestionsObject questionObj = getQuestions(questionArr, survey_id, page, k);
							questionObj.setTotalQuestionCount(tot);
						}
					}
				}
				pageList.add(page);
			}
			pagesObj.setPages(pageList);
		}
		return pagesObj;
	}
			
			// sd.setReponse(res);
			// System.out.println(survey_id+"\t"+survey_title+"\t"+date_created+"\t"+response_count+"\t"+question_count+"\t"+language+"\t"+date_modified);
			
			/*String responses = getJsonWithEndpointRestMethod(ENDPOINT+"/"+survey_id+"/responses", REST_METHOD);
			// System.out.println(response);
			JSONObject responsesObj = new JSONObject(responses);
			JSONArray resArr = responsesObj.getJSONArray("data");
			List<Response> resList = new ArrayList<Response>();
			for (int j = 0; j < resArr.length(); j++){
				Response res = new Response();
				// String resId = resArr.get(j).toString(); 
				String resId = resArr.getJSONObject(j).getString("id");
				//resIds.add(resId);
				// System.out.print("Response ID = "+ resId);
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
				// /surveys/{id}/pages/{id}/questions
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
					ResponsePage page = getPageWithQuestionAndAnswers(resDetailArr, m);
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
				 
				 
			}*/
			//sd.setReponseList(resList);


	private static QuestionsObject getQuestions(JSONArray questionsArr, String survey_id, Page page, int k) {
		QuestionsObject questions = new QuestionsObject();
		JSONObject questionArrObj = questionsArr.getJSONObject(k);
		JSONArray questionArr = null;
		if(!questionArrObj.isNull("data")){
			questionArr = questionArrObj.getJSONArray("data");
		}
		List<Question> questionList = new ArrayList<Question>();
		if(questionArr != null){
			for (int p = 0; p < questionArr.length(); p++) {
				Question question = new Question();
				int position = questionArr.getJSONObject(p).getInt("position");
				String heading = questionArr.getJSONObject(p).getString("heading");
				String id = questionArr.getJSONObject(p).getString("id");
				question.setHeading(heading);
				question.setId(id);
				question.setPosition(position);
				String questionDetail = getJsonWithEndpointRestMethod(ENDPOINT+"/"+survey_id+"/pages/"+page.getId()+"/questions/"+id, REST_METHOD);
				System.out.println("questionDetail = :" +questionDetail);
				questionList.add(question);
			}
		}
		questions.setQuestions(questionList);
		/*question.setId(questionArrObj.getString(""));
		JSONArray answerArr = questionArrObj.getJSONArray("answers");
		List<Answer> answers = new ArrayList<Answer>();
		if(answerArr.length() > 0){
			for(int p = 0; p < answerArr.length(); p++){
				Answer answer = getAnswer(answerArr, p);
				answers.add(answer);
			}
		}*/
		
		return questions;
	}

	private static Page getPage(JSONArray pagesObjArr, int j) {
		Page page = new Page();
		String pageId = pagesObjArr.getJSONObject(j).getString("id");
		page.setId(pageId);
		String description = pagesObjArr.getJSONObject(j).getString("description");
		page.setDescription(description);
		String title = pagesObjArr.getJSONObject(j).getString("title");
		page.setTitle(title);
		int position = pagesObjArr.getJSONObject(j).getInt("position");
		page.setPosition(position);
		return page;
	}

	/*private static ResponsePage getPageWithQuestionAndAnswers(JSONArray resDetailArr, int m) {
		ResponsePage page = new ResponsePage();
		JSONObject resDetailArrObj = resDetailArr.getJSONObject(m);
		String resPageId = resDetailArrObj.getString("id");
		page.setId(resPageId);
		JSONArray questionArr = resDetailArrObj.getJSONArray("questions");
		List<Question> questions = new ArrayList<Question>();
		for(int n = 0; n < questionArr.length(); n++){
			Question question = getQuestionWithAnswers(questionArr, n);
			questions.add(question);
		}
		page.setQuestions(questions);
		return page;
	}

	private static Question getQuestionWithAnswers(JSONArray questionArr, int n) {
		Question question = new Question();
		JSONObject questionArrObj = questionArr.getJSONObject(n);
		question.setId(questionArrObj.getString("id"));
		JSONArray answerArr = questionArrObj.getJSONArray("answers");
		List<Answer> answers = new ArrayList<Answer>();
		if(answerArr.length() > 0){
			for(int p = 0; p < answerArr.length(); p++){
				Answer answer = getAnswer(answerArr, p);
				answers.add(answer);
			}
		}
		question.setAnswers(answers);
		return question;
	}*/

	private static Answer getAnswer(JSONArray answerArr, int p) {
		Answer answer = new Answer();
		JSONObject answerArrObj = answerArr.getJSONObject(p);
		// System.out.println(answerArrObj);
		String choice_id = null;
		String row_id = null;
		String text = null;
		if(!answerArrObj.isNull("choice_id")){
			//System.out.println("choice_id --> "+ answerArr.getJSONObject(p).getString("choice_id"));
			choice_id = answerArr.getJSONObject(p).getString("choice_id");
			answer.setChoice_id(choice_id);
		}
		if(!answerArrObj.isNull("row_id")){
			row_id = answerArr.getJSONObject(p).getString("row_id");
			answer.setRow_id(row_id);
		}
		if(!answerArrObj.isNull("text")){
			text = answerArr.getJSONObject(p).getString("text");
			answer.setText(text);
		}
		return answer;
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
			
			System.out.println("The response code received is " + k);
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