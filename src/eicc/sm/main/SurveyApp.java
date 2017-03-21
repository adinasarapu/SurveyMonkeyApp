package eicc.sm.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
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
import eicc.sm.model.AnswerChoice;
import eicc.sm.model.AnswerOption;
import eicc.sm.model.AnswerOther;
import eicc.sm.model.AnswerRow;
import eicc.sm.model.Collector;
import eicc.sm.model.Page;
import eicc.sm.model.Pages;
import eicc.sm.model.Question;
import eicc.sm.model.QuestionDetail;
import eicc.sm.model.QuestionRequired;
import eicc.sm.model.QuestionSorting;
import eicc.sm.model.QuestionValidation;
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
	
	/*12088715	EDSP/CHD/COG- Maternal Questionnaire version 4.0
	112574091	New Survey
	8149251	Mother's Follow-up Questionnaire
	21915957	EDSP/CHD/COG- Maternal Questionnaire version 4.0 Spanish version
	16046660	DOWN SYNDROME PHENOTYPE PROJECT BACKGROUND QUESTIONNAIRE
	15059497	Emory DS Cognition Project - Site PHI Risk Assessment
	12100941	Training version EDSP/CHD/COG- Maternal Questionnaire version 4.0
	3622815	Emory Down Syndrome Project - Maternal Questionnaire
	12012987	IDDRC investigators
	3621895	Emory Down Syndrome Project - Paternal (Father's) Questionnaire
	5254205	TRAINING ONLY  Emory Down Syndrome Project - Maternal Questionnaire
	2960590	old version- training -EDSP - Maternal Questionnaire
	3115009	TRAINING ONLY - EDSP - Paternal (Father's) Questionnaire*/
			
	static String ENDPOINT = null;
	static String REST_METHOD = null;
	static String Access_Token = null;
	static String SID = null;
	
	public static void main(String args[]) {
		// ENDPOINT = HOST+ENDPOINT;
		// System.out.println(ENDPOINT);
		ResourceBundle resourceBundle = ResourceBundle.getBundle("config");
		Access_Token = resourceBundle.getString("Access_Token");
		REST_METHOD = resourceBundle.getString("REST_METHOD");
		ENDPOINT = resourceBundle.getString("ENDPOINT");
		
		SID = "3622815";
		
		System.out.println(Access_Token+"\n"+REST_METHOD+"\n"+ENDPOINT);

		/*List<SurveyDetail> sds = getAllServeys();
		Iterator<SurveyDetail> sdIt = sds.iterator();
		while(sdIt.hasNext()){
			SurveyDetail sd = sdIt.next();
			System.out.println(sd.getId() + "\t" + sd.getServeyTitle());
			//Pages p = getServeyPages(sd.getId());
		} */
	
		Pages p = getServeyPages(SID);
		int totalPages = p.getTotalPageCount();
		System.out.println("Survey ID : "+SID+"\t"+"Total Pages: "+totalPages);
		List<Page> pagelst = p.getPages();
		Iterator<Page> pageIt = pagelst.iterator();
		int y = 1;
		while(pageIt.hasNext()){
			Page page1 = pageIt.next();
			String id = page1.getId();
			System.out.println("Page:"+y + "\tP.ID:" + id);
			y = y+1;
			QuestionsObject questionsObject = page1.getQuestionsObject();
			List<Question> questions = questionsObject.getQuestions();
			Iterator<Question> questionsIt = questions.iterator();
			while(questionsIt.hasNext()){
				Question question = questionsIt.next();
				System.out.println("Q.ID"+question.getId()+"\t"+question.getHeading()); 
			}
		}
	}

	private static List<SurveyDetail> getAllServeys() {
		String serveys = getJsonWithEndpointRestMethod(ENDPOINT, REST_METHOD);
		// System.out.println(serveys);
		JSONObject surveyObj = new JSONObject(serveys);
		JSONArray arr = surveyObj.getJSONArray("data");
		// Map<String, String> surveyMap = new HashMap<String, String>();
		List<SurveyDetail> surs = new ArrayList<SurveyDetail>();
		for (int i = 0; i < arr.length(); i++) {
			String survey_id = arr.getJSONObject(i).getString("id");
			String survey_title = arr.getJSONObject(i).getString("title");
			// surveyMap.put(survey_id, survey_title);
		
			// SURVEY DETAILS
			String serveyDetails = getJsonWithEndpointRestMethod(ENDPOINT + "/" + survey_id, REST_METHOD);
			// date_created; response_count; question_count; language;
			// date_modified
			JSONObject surveyDetailObj = new JSONObject(serveyDetails);
			SurveyDetail sd = new SurveyDetail();

			sd.setId(survey_id);
			sd.setSurveyTitle(survey_title);
			sd.setDateCreated(surveyDetailObj.get("date_created").toString());
			sd.setResponseCount(surveyDetailObj.get("response_count").toString());
			sd.setQuestionCount(surveyDetailObj.get("question_count").toString());
			sd.setLanguage(surveyDetailObj.get("language").toString());
			sd.setDateModified(surveyDetailObj.get("date_modified").toString());

			surs.add(sd);
		}
		return surs;
	}

	public static String readFile(String filename) {
	    String result = "";
	    try {
	        BufferedReader br = new BufferedReader(new FileReader(filename));
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();
	        while (line != null) {
	            sb.append(line);
	            line = br.readLine();
	        }
	        result = sb.toString();
	    } catch(Exception e) {
	        e.printStackTrace();
	    }
	    return result;
	}
	
	private static Pages getServeyPages(String survey_id) {
		
		Pages pagesObj = new Pages();
		
		// READ the local JSON file, if not DOWNLOAD IT!
		String PAGES_JSON = "/Users/adinasarapu/Documents/SurveyMonkey/pages.json";
		String pages = readFile(PAGES_JSON);
		if("".equals("pages")){
			pages = getJsonWithEndpointRestMethod(ENDPOINT + "/" + survey_id + "/pages", REST_METHOD);
			writeFile(PAGES_JSON, pages);
		}
		
		JSONObject pagesJsonObj = new JSONObject(pages);
		if (!pagesJsonObj.isNull("total")) {
			pagesObj.setTotalPageCount(pagesJsonObj.getInt("total"));
		}
		
		JSONArray pagesObjArr = null;
		if (!pagesJsonObj.isNull("data")) {
			pagesObjArr = pagesJsonObj.getJSONArray("data");
		}
		
		if (pagesObjArr != null) {
			List<Page> pageList = new ArrayList<Page>();
			for (int j = 0; j < pagesObjArr.length(); j++) {
				Page page = getPage(pagesObjArr, j);
	
				// READ the local JSON file, if not DOWNLOAD IT!
				String QUESTIONS_JSON = "/Users/adinasarapu/Documents/SurveyMonkey/questions.json";
				String questions = readFile(QUESTIONS_JSON);
				if("".equals(questions)){
					questions = getJsonWithEndpointRestMethod(ENDPOINT + "/" + survey_id + "/pages/" + page.getId() + "/questions", REST_METHOD);
					writeFile(QUESTIONS_JSON, questions);
				}

				// System.out.println("questions --> "+questions);
				JSONObject questionsJsonObj = new JSONObject(questions);
				JSONArray questionArr = null;
				if (!questionsJsonObj.isNull("data")) {
					questionArr = questionsJsonObj.getJSONArray("data");
				}
				int tot = questionsJsonObj.getInt("total");
				// System.out.println("tot questions : "+ tot);
				if (questionArr != null && questionArr.length() > 0) {
						QuestionsObject questionObj = getQuestionsInPage(questionArr, survey_id, page);
						questionObj.setTotalQuestionCount(tot);
						page.setQuestionsObject(questionObj);
				}
				pageList.add(page);
			}
			pagesObj.setPages(pageList);
		}
		return pagesObj;
	}

	private static void writeFile(String PAGES_JSON, String pages) {
		FileWriter fw = null;
		try {
			fw = new FileWriter(PAGES_JSON);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		  BufferedWriter bw = new BufferedWriter(fw);
		  try {
			bw.write(pages);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	private static QuestionsObject getQuestionsInPage(JSONArray questionsArr, String survey_id, Page page) {
		QuestionsObject questions = new QuestionsObject();
		List<Question> qList = new ArrayList<Question>();
		for(int k = 0; k < questionsArr.length(); k++){
			JSONObject questionArrObj = questionsArr.getJSONObject(k);
			Question question = new Question();
			question.setPosition(questionArrObj.getInt("position"));
			question.setHeading(questionArrObj.getString("heading"));
			String qid = questionArrObj.getString("id");
			question.setId(qid);
			
			// Detailed description of question
			// READ the local JSON file, if not DOWNLOAD IT!
			String QUESTIONS_DETAIL_JSON = "/Users/adinasarapu/Documents/SurveyMonkey/questionDetail.json";
			String questionDetailJSON = readFile(QUESTIONS_DETAIL_JSON);
			if("".equals(questions)){
				questionDetailJSON = getJsonWithEndpointRestMethod(ENDPOINT+"/"+survey_id+"/pages/"+page.getId()+"/questions/"+qid, REST_METHOD);
				writeFile(QUESTIONS_DETAIL_JSON, questionDetailJSON);
			}
			
			// System.out.println("questionDetail = :" +questionDetailJSON);
			JSONObject questionDetailObj = new JSONObject(questionDetailJSON);
			
			QuestionDetail questionDetail = new QuestionDetail();
			question.setQuestionDetails(questionDetail);
			
			// ADD SORTING DETAILS
			QuestionSorting questionSorting = new QuestionSorting();
			if(!questionDetailObj.isNull("sorting")){
				JSONObject srt = (JSONObject)questionDetailObj.get("sorting");
				questionSorting.setText(srt.get("text").toString());
				questionSorting.setAmount(srt.get("amount").toString());
				questionSorting.setType(srt.get("type").toString());
				// System.out.println("sorting ====" + srt.get("text").toString()+"\t"+srt.get("amount").toString()+"\t"+srt.get("type").toString());
			} else {
				questionSorting = null;
			}
			questionDetail.setQuestionSorting(questionSorting);
			
			// ADD FAMILY DETAILS
			if(!questionDetailObj.isNull("family")){
				questionDetail.setQuestionFamily(questionDetailObj.get("family").toString());
				// System.out.println("family ==== "+questionDetailObj.get("family").toString());
			}
			
			// ADD SUBTYPE DETAILS
			if(!questionDetailObj.isNull("subtype")){
				questionDetail.setQuestionFamily(questionDetailObj.get("subtype").toString());
				// System.out.println("subtype ==== "+questionDetailObj.get("subtype").toString());
			}
			
			// ADD REQUIRED DETAILS
			QuestionRequired questionRequired = new QuestionRequired(); 
			if(!questionDetailObj.isNull("required")){
				JSONObject rqed = (JSONObject)questionDetailObj.get("required");
				questionRequired.setText(rqed.get("text").toString());
				questionRequired.setAmount(rqed.get("amount").toString());
				questionRequired.setType(rqed.get("type").toString());	
				// System.out.println("required = " +rqed.get("text").toString() +"\t"+ rqed.get("amount").toString()+"\t"+rqed.get("type").toString());
			} else {
				questionRequired = null;
			}
			questionDetail.setQuestionRequired(questionRequired);
			
			// ADD VISIBLE boolean
			if(questionDetailObj.get("visible").toString().equals(true)){
				questionDetail.setQuestionVisible(true);
			} else {
				questionDetail.setQuestionVisible(false);
			}
			// System.out.println("visible = " + questionDetailObj.get("visible").toString());
			
			
			// ADD ANSWER OPTIONS
			AnswerOption answerOption = new AnswerOption();
			if(!questionDetailObj.isNull("answers")){
				JSONObject answers = (JSONObject)questionDetailObj.get("answers");
				if(!answers.isNull("rows")){
					List<AnswerRow> answerRowList = new ArrayList<AnswerRow>();
					JSONArray rowsArr = answers.getJSONArray("rows");
					for(int m1 = 0; m1 < rowsArr.length(); m1++){
						String visible = rowsArr.getJSONObject(m1).get("visible").toString();
						AnswerRow answerRow = new AnswerRow();
						if(visible.equals(true)){
							answerRow.setVisible(true);
						} else {
							answerRow.setVisible(false);
						}
						String text = rowsArr.getJSONObject(m1).get("text").toString();
						answerRow.setText(text);
						String id = rowsArr.getJSONObject(m1).get("id").toString();
						answerRow.setId(id);
						int position = rowsArr.getJSONObject(m1).getInt("position");
						answerRow.setPosition(position);
						// System.out.println("answerRow = " + text +"\t"+ visible +"\t"+id + "\t" + position);
						answerRowList.add(answerRow);
					}
					// {"visible","text","position","id"}
					answerOption.setAnswerRowList(answerRowList);
				}
				if(!answers.isNull("choices")){
					List<AnswerChoice> answerChoiceList = new ArrayList<AnswerChoice>();
					JSONArray choicesArr = answers.getJSONArray("choices");
					for(int m2 = 0; m2 < choicesArr.length(); m2++){
						String visible = choicesArr.getJSONObject(m2).get("visible").toString();
						AnswerChoice answerChoice = new AnswerChoice();
						if(visible.equals(true)){
							answerChoice.setVisible(true);
						} else {
							answerChoice.setVisible(false);
						}
						String text = choicesArr.getJSONObject(m2).get("text").toString();
						answerChoice.setText(text);
						String id = choicesArr.getJSONObject(m2).get("id").toString();
						answerChoice.setId(id);
						int position = choicesArr.getJSONObject(m2).getInt("position");
						answerChoice.setPosition(position);
						// System.out.println("answerChoice = " + text +"\t"+ visible +"\t"+id + "\t" + position);
						answerChoiceList.add(answerChoice);
					}
					// {"visible","text","position","id"}
					answerOption.setAnswerChoiceList(answerChoiceList);
				}
				
				if(!answers.isNull("other")){
					JSONObject otherObj = (JSONObject)answers.get("other");
					AnswerOther answerOther = new AnswerOther();
					String text = otherObj.get("text").toString();
						answerOther.setText(text);
						int num_lines = otherObj.getInt("num_lines");
						answerOther.setNum_lines(num_lines);
						String id = otherObj.get("id").toString();
						answerOther.setId(id);
						String visible = otherObj.get("visible").toString();
						if(visible.equals(true)){
							answerOther.setVisible(true);
						} else {
							answerOther.setVisible(false);
						}
						String apply_all_rows = otherObj.get("apply_all_rows").toString();
						if(apply_all_rows.equals(true)){
							answerOther.setApply_all_rows(true);
						} else {
							answerOther.setApply_all_rows(false);
						}
						String is_answer_choice = otherObj.get("is_answer_choice").toString();
						if(is_answer_choice.equals(true)){
							answerOther.setIs_answer_choice(true);
						} else {
							answerOther.setIs_answer_choice(false);
						}
						int position = otherObj.getInt("position");
						answerOther.setPosition(position);
						int num_chars = otherObj.getInt("num_chars");
						answerOther.setNum_chars(num_chars);
						String error_text = otherObj.get("error_text").toString();
						answerOther.setError_text(error_text);
						// answerOtherList.add(answerOther);
						// System.out.println("answerOther = " + text +"\t"+ visible +"\t"+id + "\t" + position);
					
					// {"num_lines","text","id","visible",
					// "apply_all_rows","is_answer_choice",
					// "position","num_chars","error_text"}
					answerOption.setAnswerOther(answerOther);
				}
				questionDetail.setAnswerOption(answerOption);
				// System.out.println("required = " +rqed.get("text").toString() +"\t"+ rqed.get("amount").toString()+"\t"+rqed.get("type").toString());
			} 
			
			// ADD VALIDATION
			QuestionValidation questionValidation = new QuestionValidation();
			if(!questionDetailObj.isNull("validation")){
				JSONObject v = (JSONObject)questionDetailObj.get("validation");
				questionValidation.setSum_text(v.getString("sum_text"));
				questionValidation.setMin(v.get("min").toString());
				questionValidation.setText(v.getString("text"));
				questionValidation.setSum(v.get("sum").toString());
				questionValidation.setMax(v.get("max").toString());
				questionValidation.setType(v.getString("type"));
				// System.out.println("validation ====" + v.getString("text")+"\t"+v.getString("type"));
			} else {
				questionValidation = null;
			}
			questionDetail.setQuestionValidation(questionValidation);
			
			// ADD HEADINGS LIST
			if(!questionDetailObj.isNull("headings")){
				JSONArray headingsArr = questionDetailObj.getJSONArray("headings");
				List<String> headingList = new ArrayList<String>();
				for(int q = 0; q < headingsArr.length(); q++){
					headingList.add(headingsArr.getJSONObject(q).get("heading").toString());
					// System.out.println("heading =" + headingsArr.getJSONObject(q).get("heading").toString());
				}
				questionDetail.setHeadings(headingList);
			}
		
			// ADD ID
			questionDetail.setId(questionDetailObj.get("id").toString());
			// System.out.println("id = "+questionDetailObj.get("id").toString());
			
			// ADD POSITION
			questionDetail.setPosition(questionDetailObj.getInt("position"));

			// FORCED_RANKING
			String forced_ranking = questionDetailObj.get("forced_ranking").toString();
			if(forced_ranking.equals(true)){
				questionDetail.setForced_ranking(true);
			} else {
				questionDetail.setForced_ranking(false);
			}
			// System.out.println("forced_ranking = "+ forced_ranking);
			
			//questionDetail.setForced_ranking(forced_ranking);
			// "sorting": 
			//		null OR
			//		{text, amount, type}
			// "family": 
			//		"open_ended" OR
			//		"datetime" OR
			//		"single_choice" --> verticle
			// "subtype":
			//			"multi" OR
			//			"single" OR
			//			"date_only" OR 
			//			"vertical" OR 
			//			"both" 
			// "required": 
			//			null OR
			// 			{"text", "amount", "type"}
			// "visible": 
			//			true
			// "answers": --> text elements
			//			"rows" --> array (each element contains) ">= 1"
			// 					{"visible","text","position","id"}
			//			"choices" --> array (each element contains) ">= 1"
			//					{"visible","text","position","id"}
			//			"other" --> text elmenets
			//					{"num_lines","text","id","visible",
			//					"apply_all_rows","is_answer_choice",
			//					"position","num_chars","error_text"}
			//						
			// "id": "45112236" 
			// "position": 4
			//			
			// "validation": --> text elements 
			//				null OR
			//				{"sum_text","min","text","sum",
			//				"max","type"}
			//				
			//				
			// headings: --> array (each element contains)
			//				"heading"
			// "forced_ranking": false
			
			qList.add(question);
		}
		questions.setQuestions(qList);
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
			
			// System.out.println("The response code received is " + k);
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