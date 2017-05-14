package eicc.sm.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
import java.util.Set;

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
import eicc.sm.model.Respondent;
import eicc.sm.model.Response;
import eicc.sm.model.ResponseDetail;
import eicc.sm.model.ResponsePage;
import eicc.sm.model.SurveyOne;

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

	 	 * 
	 	/surveys/{id}/responses/{id}
	 	/surveys/{id}/responses/{id}/details
	 	
	 	/surveys/{id}/rollups
	 	 
	 	
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
	static String Access_Token = null;
	static String JSON_FOLDER = null;
	static String OUT_FOLDER = null;
	static String SID = null;
	

	public static void main(String args[]) throws FileNotFoundException {

		ResourceBundle rB = ResourceBundle.getBundle("config");
		Access_Token = rB.getString("Access_Token");
		ENDPOINT = rB.getString("ENDPOINT");
		JSON_FOLDER = rB.getString("JSON_FOLDER");
		OUT_FOLDER = rB.getString("OUT_FOLDER");
		SID = rB.getString("SID");
			
		SurveyOne sd = getOneServey(ENDPOINT, "GET", SID, JSON_FOLDER);
		File theDir = createSurveyDir();

		@SuppressWarnings("resource")
		PrintWriter out = new PrintWriter(theDir + "/Survey_" + SID + ".txt");
		out.println("\n*****SURVEY DETAILS*****");
		out.println("Survey ID:\t" + sd.getId());
		out.println("Servey Title:\t" + sd.getServeyTitle());
		out.println("Data-Created:\t" + sd.getDateCreated());
		String DateCreated = sd.getDateCreated();
		String DateModified = sd.getDateModified();
		out.println("Data-Modified:\t" + sd.getDateModified());
		out.println("Language:\t" + sd.getLanguage());
		out.println("Question Count:\t" + sd.getQuestionCount());
		out.println("Response Count:\t" + sd.getResponseCount() + "\n");
		
		int q = 0;
		
		// StringBuffer buffer = new StringBuffer();
		
		// out.print("RespondentID\t" + "StartDate\t" + "EndDate\t" + "IPAddress\t");
		Map<String, List<Respondent>> x = getRespondent(SID);
		Pages p = getServeyPages(SID, ENDPOINT, "GET", JSON_FOLDER);
		int totalPages = p.getTotalPageCount();
		System.out.println("Survey ID : " + SID + "\t" + "Total Pages: " + totalPages);
		List<Page> pagelst = p.getPageList();
		Iterator<Page> pageIt = pagelst.iterator();
		int y = 1;
		// out.println(buffer.toString());
		while (pageIt.hasNext()) {
			Page page = pageIt.next();
			y = y + 1;
			QuestionsObject questionsObject = page.getQuestionsObject();
			if (questionsObject != null) {
				List<Question> questions = questionsObject.getQuestions();
				Iterator<Question> questionsIt = questions.iterator();
				while (questionsIt.hasNext()) {
					// StringBuffer buffer2 = new StringBuffer();
					int bn = 0;
					//q = q + 1;
					Question question = questionsIt.next();
					QuestionDetail qd = question.getQuestionDetails();
					QuestionSorting qs = qd.getQuestionSorting();
					String QuestionTitle = question.getHeading();
					out.println("RespondentID\t" + "StartDate\t" + "EndDate\t" + "IPAddress\t"+QuestionTitle);
					// out.println(QuestionTitle);
					AnswerOption ao = qd.getAnswerOption();
					String choice_id = null;
					String choice_name = null;
					String ans_other_id = null;
					String ans_other_name = null;
					Map<String, String> row_ids = null;
					if (ao != null) {
						if (ao.getAnswerChoiceList() != null && !ao.getAnswerChoiceList().isEmpty()) {
							Iterator<AnswerChoice> it = ao.getAnswerChoiceList().iterator();
							while (it.hasNext()) {
								AnswerChoice ac = it.next();
								choice_id = ac.getId();
								choice_name = ac.getText();
							}
						}
						if (ao.getAnswerOther() != null) {
							AnswerOther ao2 = ao.getAnswerOther();
							ans_other_id = ao2.getId();
							ans_other_name = ao2.getText();
						}
						if (ao.getAnswerRowList() != null && !ao.getAnswerRowList().isEmpty()) {
							Iterator<AnswerRow> it = ao.getAnswerRowList().iterator();
							row_ids = new HashMap<String, String>();
							while (it.hasNext()) {
								AnswerRow ar = it.next();
								row_ids.put(ar.getId(), ar.getText());
							}
						}
					}
					List<Respondent> people = x.get(sd.getId());
					Iterator<Respondent> peopleIt = people.iterator();
					while (peopleIt.hasNext()) {
						String resRow = extractRespondentRow(DateCreated, DateModified, question, choice_id, choice_name, row_ids,
								peopleIt);
						out.print(resRow);
					}
				} 
			}
		}
		out.close();
	}

	private static String extractRespondentRow(String DateCreated, String DateModified, Question question,
			String choice_id, String choice_name, Map<String, String> row_ids, Iterator<Respondent> peopleIt) {
		StringBuffer buffer = new StringBuffer();
		
		Respondent respondent = peopleIt.next();
		
		String RespondentID = respondent.getId();
		String RespondentIP = respondent.getIp_address();
		buffer.append(RespondentID + "\t");
		buffer.append(DateCreated + "\t");
		buffer.append(DateModified + "\t");
		buffer.append(RespondentIP + "\t");

		Iterator<ResponsePage> xxx = respondent.getResponsePages().iterator();
		while (xxx.hasNext()) {
			ResponsePage rp = xxx.next();
			Iterator<Question> questionIt = rp.getQuestions().iterator();
			while (questionIt.hasNext()) { //
				Question quest = questionIt.next();
				if (quest.getId().equals(question.getId())) {
					Iterator<Answer> it4 = quest.getAnswers().iterator();
					while (it4.hasNext()) {
						Answer answer = it4.next();
						if (answer.getChoice_id() != null) {
							if (choice_id != null && choice_id.equals(answer.getChoice_id().trim()))
								buffer.append(choice_name);
						}
						if (answer.getRow_id() != null) {
							String out_row = "";
							Set<String> ids = row_ids.keySet();
							Iterator<String> idsit = ids.iterator();
							while (idsit.hasNext()) {
								String id = idsit.next();
								if (id != null && answer.getRow_id().equals(id)) {
									out_row = out_row + row_ids.get(id.trim()) + ";";
								}
							}
							buffer.append(out_row);
						}
						if (answer.getText() != null) {
							buffer.append(answer.getText());
						}
					}
				}
			}
		}
		return buffer.toString();
	}

	private static File createSurveyDir() {
		File theDir = new File(OUT_FOLDER + SID);
		// if the directory does not exist, create it
		if (!theDir.exists()) {
			System.out.println("creating directory: " + theDir.getName());
			boolean result = false;
			try {
				theDir.mkdir();
				result = true;
			} catch (SecurityException se) {
				// handle it
			}
			if (result) {
				System.out.println("DIR created");
			}
		}
		return theDir;
	}

	private static SurveyOne getOneServey(String eNDPOINT2, String rest_get, String sID2, String jSON_FOLDER2) {
		SurveyOne so = new SurveyOne();
		String survey_id = sID2;
		
		// String survey_title = arr.getJSONObject(i).getString("title");
		
		// /surveys/{id}/details
		
		String SURVEY_JSON = jSON_FOLDER2 +"Survey_"+survey_id+".json";
		
		String serveyOne = parseJsonFile(SURVEY_JSON);
		
		if("".equals(serveyOne)){
			System.out.println("downloading file " + SURVEY_JSON);
			serveyOne = downloadSurveyJson(eNDPOINT2 + "/" + survey_id, rest_get, SURVEY_JSON);
			// String serveyDetails = getJsonWithEndpointRestMethod(ep_surveys + "/" + survey_id + "/details", rest_get);
		}
		
		// date_created; response_count; question_count; language;
		// date_modified
		JSONObject surveyOneObj = new JSONObject(serveyOne);
		
		so.setId(survey_id);
		so.setSurveyTitle(surveyOneObj.get("title").toString());
		so.setDateCreated(surveyOneObj.get("date_created").toString());
		so.setResponseCount(surveyOneObj.get("response_count").toString());
		so.setQuestionCount(surveyOneObj.get("question_count").toString());
		so.setLanguage(surveyOneObj.get("language").toString());
		so.setDateModified(surveyOneObj.get("date_modified").toString());
		return so;
	}

	private static void exportResponse(SurveyOne sd, Map<String, List<Respondent>> x, PrintWriter out_res) {
		List<Respondent> rpdt = x.get(sd.getId());
		Iterator<Respondent> it1 = rpdt.iterator();
		while (it1.hasNext()) {
			Respondent respondent = it1.next();
			out_res.println("\n*****RESPONDENT DETAILS*****");
			out_res.println("Respondent ID:\t" + respondent.getId());
			out_res.println("Respondent IP:\t" + respondent.getIp_address());
			Iterator<ResponsePage> it2 = respondent.getResponsePages().iterator();
			while (it2.hasNext()) {
				ResponsePage responsePage = it2.next();
				out_res.println("\n*****RESPONSE PAGE*****");
				out_res.println("RESPONSE ID:\t" + responsePage.getId());
				Iterator<Question> it3 = responsePage.getQuestions().iterator();
				while (it3.hasNext()) {
					Question question = it3.next();
					out_res.println("Question ID:\t" + question.getId());
					Iterator<Answer> it4 = question.getAnswers().iterator();
					while (it4.hasNext()) {
						Answer answer = it4.next();
						if (answer.getChoice_id() != null)
							out_res.println("Choice_id:\t" + answer.getChoice_id());
						if (answer.getRow_id() != null)
							out_res.println("Row_id:\t" + answer.getRow_id());
						if (answer.getText() != null)
							out_res.println("Text:\t" + answer.getText());
					}
				}
			}
		}
	}

	private static List<SurveyOne> getAllServeys(String ep_surveys, String rest_get, String out_folder) {
		
		String SURVEYS_JSON = out_folder +"Surveys.json";
		String serveyAll = parseJsonFile(SURVEYS_JSON);
		if("".equals(serveyAll)){
			System.out.println("downloading file " + SURVEYS_JSON);
			serveyAll = downloadSurveysJson(ep_surveys, rest_get, SURVEYS_JSON);
		}
		
		// System.out.println(serveys);
		JSONObject surveyAllObj = new JSONObject(serveyAll);
		JSONArray arr = surveyAllObj.getJSONArray("data");
		// Map<String, String> surveyMap = new HashMap<String, String>();
		List<SurveyOne> surs = new ArrayList<SurveyOne>();
		for (int i = 0; i < arr.length(); i++) {
			SurveyOne so = new SurveyOne();
			String survey_id = arr.getJSONObject(i).getString("id");
			
			String survey_title = arr.getJSONObject(i).getString("title");
			
			// /surveys/{id}/details
			
			String SURVEY_JSON = out_folder +"Survey_"+survey_id+".json";
			
			String serveyOne = parseJsonFile(SURVEY_JSON);
			
			if("".equals(serveyOne)){
				System.out.println("downloading file " + SURVEY_JSON);
				serveyOne = downloadSurveyJson(ep_surveys + "/" + survey_id, rest_get, SURVEY_JSON);
				// String serveyDetails = getJsonWithEndpointRestMethod(ep_surveys + "/" + survey_id + "/details", rest_get);
			}
			
			// date_created; response_count; question_count; language;
			// date_modified
			JSONObject surveyOneObj = new JSONObject(serveyOne);
			
			so.setId(survey_id);
			so.setSurveyTitle(survey_title);
			so.setDateCreated(surveyOneObj.get("date_created").toString());
			so.setResponseCount(surveyOneObj.get("response_count").toString());
			so.setQuestionCount(surveyOneObj.get("question_count").toString());
			so.setLanguage(surveyOneObj.get("language").toString());
			so.setDateModified(surveyOneObj.get("date_modified").toString());

			// /surveys/{id}/details
			String SURVEY_DETAIL_JSON = out_folder +"SurveyDetail_"+survey_id+".json";
			String serveyDetails = parseJsonFile(SURVEY_DETAIL_JSON);
			if("".equals(serveyDetails)){
				System.out.println("downloading file " + SURVEY_DETAIL_JSON);
				serveyDetails = downloadSurveyDetailsJson(ep_surveys + "/" + survey_id + "/details", rest_get, SURVEY_DETAIL_JSON);
				// String serveyDetails = getJsonWithEndpointRestMethod(ep_surveys + "/" + survey_id + "/details", rest_get);
			}
			surs.add(so);
		}
		return surs;
	}

	private static Map<String, List<Respondent>> getRespondent(String survey_id) {
		
		Map<String, List<Respondent>> surResMap = new HashMap<String, List<Respondent>>();
		
		String SURVEY_RESPONSES_JSON = JSON_FOLDER + "SurveyResponsesBulk_" + survey_id + ".json";
		String serveyResponses = parseJsonFile(SURVEY_RESPONSES_JSON);
		if ("".equals(serveyResponses)) {
			System.out.println("downloading file " + SURVEY_RESPONSES_JSON);
			serveyResponses = downloadSurveyDetailsJson(ENDPOINT 
					+ "/" + survey_id 
					+ "/responses/bulk", "GET", SURVEY_RESPONSES_JSON);
		}
		JSONObject responsesObj = new JSONObject(serveyResponses);
		JSONArray responsesArr = null;
		if (!responsesObj.isNull("data")) {
			responsesArr = responsesObj.getJSONArray("data");
		}
		// Map<String, List<Respondent>> respondentsMap = new HashMap<String, List<Respondent>>();
		List<Respondent> respondents = new ArrayList<Respondent>();
		if (responsesArr != null) {
			for (int j = 0; j < responsesArr.length(); j++) {
				addRespondent(responsesArr, respondents, j);
			}
			surResMap.put(survey_id, respondents);
		}
		return surResMap;
	}

	private static void addRespondent(JSONArray responsesArr,
			List<Respondent> respondents, int j) {
		Respondent respondent = new Respondent();
		respondent.setId(responsesArr.getJSONObject(j).get("id").toString());
		respondent.setIp_address(responsesArr.getJSONObject(j).get("ip_address").toString());
		List<ResponsePage> responsePages = new ArrayList<ResponsePage>();
		JSONArray responsePageArr = null;
		if (!responsesArr.getJSONObject(j).isNull("pages")) {
			responsePageArr = responsesArr.getJSONObject(j).getJSONArray("pages");
		}
		if (responsePageArr != null) {
			JSONArray responseQArr = null;
			for (int k = 0; k < responsePageArr.length(); k++) {
				addResponsePage(respondents, responsePageArr, responsePages, responseQArr, k);
			}
			respondent.setResponsePages(responsePages);
		}
		respondents.add(respondent);
	}

	private static void addResponsePage(List<Respondent> respondents,
			JSONArray responsePageArr, List<ResponsePage> responsePages, JSONArray responseQArr, int k) {
		ResponsePage responsePage = new ResponsePage();
		String res_page_id = responsePageArr.getJSONObject(k).get("id").toString();
		responsePage.setId(res_page_id);
		
		if (!responsePageArr.getJSONObject(k).isNull("questions")) {
			responseQArr = responsePageArr.getJSONObject(k).getJSONArray("questions");
		}
		List<Question> qs = new ArrayList<Question>();
		if (responseQArr != null) {
			for (int l = 0; l < responseQArr.length(); l++) {
				Question q = new Question();
				String res_q_id = responseQArr.getJSONObject(l).get("id").toString();
				q.setId(res_q_id);
				addAnswers(responseQArr, qs, l, q);
				//respondentsMap.put(q.getId(), respondents);
			}
			responsePage.setQuestions(qs);
		}
		responsePages.add(responsePage);
	}

	private static void addAnswers(JSONArray responseQArr, List<Question> qs, int l, Question q) {
		JSONArray responseAArr = null;
		if (!responseQArr.getJSONObject(l).isNull("answers")) {
			responseAArr = responseQArr.getJSONObject(l).getJSONArray("answers");
		}
		List<Answer> anss = new ArrayList<Answer>();
		if (responseAArr != null) {
			for(int t = 0; t < responseAArr.length(); t++){
				addAnswer(responseAArr, anss, t);
			}
		q.setAnswers(anss);	
		}
		qs.add(q);
	}

	private static void addAnswer(JSONArray responseAArr, List<Answer> anss, int t) {
		Answer ans = new Answer();
		
		if(!responseAArr.getJSONObject(t).isNull("text")){
			ans.setText(responseAArr.getJSONObject(t).get("text").toString());
		}
		if(!responseAArr.getJSONObject(t).isNull("row_id")){
			ans.setRow_id(responseAArr.getJSONObject(t).get("row_id").toString());
		}
		if(!responseAArr.getJSONObject(t).isNull("choice_id")){
			ans.setChoice_id(responseAArr.getJSONObject(t).get("choice_id").toString());
		}
		anss.add(ans);
	}

	/*
	 * 
	 */
	private static String downloadSurveyJson(String ep_surveys, String rest_get, String json) {
		String s;
		s = getJsonWithEndpointRestMethod(ep_surveys, rest_get);
		try {
			writeFile(json, s);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return s;
	}

	private static String downloadSurveyDetailsJson(String ep_surveys, String rest_get, String json) {
		String sDetails;
		sDetails = getJsonWithEndpointRestMethod(ep_surveys, rest_get);
		try {
			writeFile(json, sDetails);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sDetails;
	}

	private static String downloadSurveysJson(String ep_surveys, String rest_get, String json) {
		String serveyAll;
		serveyAll = getJsonWithEndpointRestMethod(ep_surveys, rest_get);
		try {
			writeFile(json, serveyAll);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return serveyAll;
	}

	public static String parseJsonFile(String filename) {
	    String result = "";
	    File f = new File(filename);
		if(f.exists()){
			// System.out.println("Reading local JSON file "+filename);
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
		}
		return result;  
	}
	
	private static Pages getServeyPages(String survey_id,String ep_surveys, String rest_get, String out_folder) {
		
		Pages pagesObj = new Pages();
		
		// READ the local JSON file, if NOT available then DOWNLOAD it!
		// String PAGES_JSON = "/Users/adinasarapu/Documents/SurveyMonkey/pages.json";
		String PAGES_JSON = out_folder +"Pages_"+survey_id+".json";
		String pages = parseJsonFile(PAGES_JSON);
		if("".equals(pages)){
			System.out.println("downloading file " +PAGES_JSON);
			pages = downloadPagesJson(survey_id, ep_surveys, rest_get, PAGES_JSON);
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
				Page page = getPage(ep_surveys, rest_get, out_folder, survey_id, pagesObjArr, j);
				pageList.add(page);
			}
			pagesObj.setPageList(pageList);
		}
		return pagesObj;
	}

	private static Page getPage(String ep_surveys, String rest_get, String out_folder, String survey_id,
			JSONArray pagesObjArr, int pageNum) {
		
		Page page = getPage(pagesObjArr, pageNum);
		// READ the local JSON file, if not DOWNLOAD IT!
		// String QUESTIONS_JSON = "/Users/adinasarapu/Documents/SurveyMonkey/questions.json";

		String QUESTIONS_JSON = out_folder +"Questions_"+survey_id+"_"+page.getId()+".json";
		String questions = parseJsonFile(QUESTIONS_JSON);
		if("".equals(questions)){
			System.out.println("downloading file " +QUESTIONS_JSON);
			questions = downloadQuestionsJson(survey_id, ep_surveys, rest_get, page, QUESTIONS_JSON);
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
				QuestionsObject questionObj = getQuestionsInPage(ep_surveys, rest_get, out_folder, questionArr, survey_id, page);
				questionObj.setTotalQuestionCount(tot);
				page.setQuestionsObject(questionObj);
		}
		return page;
	}

	private static String downloadPagesJson(String survey_id, String ep_surveys, String rest_get, String PAGES_JSON) {
		String pages;
		pages = getJsonWithEndpointRestMethod(ep_surveys + "/" + survey_id + "/pages", rest_get);
		try {
			writeFile(PAGES_JSON, pages);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return pages;
	}

	private static String downloadQuestionsJson(String survey_id, String ep_surveys, String rest_get, Page page,
			String QUESTION_JSON) {
		String questions;
		questions = getJsonWithEndpointRestMethod(ep_surveys + "/" + survey_id + "/pages/" + page.getId() + "/questions", rest_get);
		try {
			writeFile(QUESTION_JSON, questions);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return questions;
	}

	private static void writeFile(String FILE_JSON, String pages) throws IOException {
		FileWriter fw = new FileWriter(FILE_JSON);
		fw.write(pages);
		fw.close();
	}
	
	private static QuestionsObject getQuestionsInPage(String ep,String rest_get, String out_folder, JSONArray questionsArr, String survey_id, Page page) {
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
			// String QUESTIONS_DETAIL_JSON = "/Users/adinasarapu/Documents/SurveyMonkey/questionDetail.json";
			String DETAIL_JSON = out_folder +"Question_"+survey_id+"_"+page.getId()+"_"+qid+".json";
			String questionDetailJSON = parseJsonFile(DETAIL_JSON);
			if("".equals(questionDetailJSON)){
				questionDetailJSON = getJsonWithEndpointRestMethod(ep+"/"+survey_id+"/pages/"+page.getId()+"/questions/"+qid, rest_get);
				try {
					writeFile(DETAIL_JSON, questionDetailJSON);
				} catch (IOException e) {
					e.printStackTrace();
				}
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