package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIQuestBackManager {

      public Communicator transport;

      public APIQuestBackManager(Communicator transport){
           this.transport = transport;
      }

     /**
     * Return a list of all tests that the logged in user is assigned to.
     *
     * @return
     */
     public JsonElement answerQuestions(Object testId, Object applicationId, Object pageId, Object answers)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("testId",new Gson().toJson(testId));
          gs_json_object_data.args.put("applicationId",new Gson().toJson(applicationId));
          gs_json_object_data.args.put("pageId",new Gson().toJson(pageId));
          gs_json_object_data.args.put("answers",new Gson().toJson(answers));
          gs_json_object_data.method = "answerQuestions";
          gs_json_object_data.interfaceName = "core.questback.IQuestBackManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns the calculated result in percent (0 - 100)
     * @param userId
     * @param testId
     * @return
     */
     public void assignTestsToUsers(Object testIds, Object userids)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("testIds",new Gson().toJson(testIds));
          gs_json_object_data.args.put("userids",new Gson().toJson(userids));
          gs_json_object_data.method = "assignTestsToUsers";
          gs_json_object_data.interfaceName = "core.questback.IQuestBackManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Return a list of all tests that the logged in user is assigned to.
     *
     * @return
     */
     public void assignUserToTest(Object testId, Object userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("testId",new Gson().toJson(testId));
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "assignUserToTest";
          gs_json_object_data.interfaceName = "core.questback.IQuestBackManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void createTemplatePageIfNotExists()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "createTemplatePageIfNotExists";
          gs_json_object_data.interfaceName = "core.questback.IQuestBackManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement createTest(Object testName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("testName",new Gson().toJson(testName));
          gs_json_object_data.method = "createTest";
          gs_json_object_data.interfaceName = "core.questback.IQuestBackManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public void deleteTest(Object testId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("testId",new Gson().toJson(testId));
          gs_json_object_data.method = "deleteTest";
          gs_json_object_data.interfaceName = "core.questback.IQuestBackManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns the calculated result in percent (0 - 100)
     * @param userId
     * @param testId
     * @return
     */
     public JsonElement exportToExcel()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "exportToExcel";
          gs_json_object_data.interfaceName = "core.questback.IQuestBackManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getAllTests()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getAllTests";
          gs_json_object_data.interfaceName = "core.questback.IQuestBackManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Return a list of all tests that the logged in user is assigned to.
     *
     * @return
     */
     public JsonElement getBestCategoryResultForCompany(Object userId, Object catId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.args.put("catId",new Gson().toJson(catId));
          gs_json_object_data.method = "getBestCategoryResultForCompany";
          gs_json_object_data.interfaceName = "core.questback.IQuestBackManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Return a list of all tests that the logged in user is assigned to.
     *
     * @return
     */
     public JsonElement getCategories()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getCategories";
          gs_json_object_data.interfaceName = "core.questback.IQuestBackManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns the calculated result in percent (0 - 100)
     * @param userId
     * @param testId
     * @return
     */
     public JsonElement getCategoriesForTest(Object testId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("testId",new Gson().toJson(testId));
          gs_json_object_data.method = "getCategoriesForTest";
          gs_json_object_data.interfaceName = "core.questback.IQuestBackManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns the calculated result in percent (0 - 100)
     * @param userId
     * @param testId
     * @return
     */
     public JsonElement getCompanyScoreForTestForCurrentUser(Object testId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("testId",new Gson().toJson(testId));
          gs_json_object_data.method = "getCompanyScoreForTestForCurrentUser";
          gs_json_object_data.interfaceName = "core.questback.IQuestBackManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Return a list of all tests that the logged in user is assigned to.
     *
     * @return
     */
     public JsonElement getNextQuestionPage(Object testId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("testId",new Gson().toJson(testId));
          gs_json_object_data.method = "getNextQuestionPage";
          gs_json_object_data.interfaceName = "core.questback.IQuestBackManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns the calculated result in percent (0 - 100)
     * @param userId
     * @param testId
     * @return
     */
     public JsonElement getOptionsByPageId(Object pageId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pageId",new Gson().toJson(pageId));
          gs_json_object_data.method = "getOptionsByPageId";
          gs_json_object_data.interfaceName = "core.questback.IQuestBackManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getPageId(Object questionId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("questionId",new Gson().toJson(questionId));
          gs_json_object_data.method = "getPageId";
          gs_json_object_data.interfaceName = "core.questback.IQuestBackManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Return a list of all tests that the logged in user is assigned to.
     *
     * @return
     */
     public JsonElement getProgress(Object testId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("testId",new Gson().toJson(testId));
          gs_json_object_data.method = "getProgress";
          gs_json_object_data.interfaceName = "core.questback.IQuestBackManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Return a list of all tests that the logged in user is assigned to.
     *
     * @return
     */
     public JsonElement getProgressForUser(Object userId, Object testId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.args.put("testId",new Gson().toJson(testId));
          gs_json_object_data.method = "getProgressForUser";
          gs_json_object_data.interfaceName = "core.questback.IQuestBackManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Return a list of all tests that the logged in user is assigned to.
     *
     * @return
     */
     public JsonElement getQuestion(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "getQuestion";
          gs_json_object_data.interfaceName = "core.questback.IQuestBackManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getQuestionTitle(Object pageId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pageId",new Gson().toJson(pageId));
          gs_json_object_data.method = "getQuestionTitle";
          gs_json_object_data.interfaceName = "core.questback.IQuestBackManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns the calculated result in percent (0 - 100)
     * @param userId
     * @param testId
     * @return
     */
     public JsonElement getResult(Object testId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("testId",new Gson().toJson(testId));
          gs_json_object_data.method = "getResult";
          gs_json_object_data.interfaceName = "core.questback.IQuestBackManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Return a list of all tests that the logged in user is assigned to.
     *
     * @return
     */
     public JsonElement getResultRequirement()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getResultRequirement";
          gs_json_object_data.interfaceName = "core.questback.IQuestBackManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns the calculated result in percent (0 - 100)
     * @param userId
     * @param testId
     * @return
     */
     public JsonElement getResultWithReference(Object testId, Object referenceId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("testId",new Gson().toJson(testId));
          gs_json_object_data.args.put("referenceId",new Gson().toJson(referenceId));
          gs_json_object_data.method = "getResultWithReference";
          gs_json_object_data.interfaceName = "core.questback.IQuestBackManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns the calculated result in percent (0 - 100)
     * @param userId
     * @param testId
     * @return
     */
     public JsonElement getScoreForTest(Object userId, Object testId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.args.put("testId",new Gson().toJson(testId));
          gs_json_object_data.method = "getScoreForTest";
          gs_json_object_data.interfaceName = "core.questback.IQuestBackManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getTest(Object testId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("testId",new Gson().toJson(testId));
          gs_json_object_data.method = "getTest";
          gs_json_object_data.interfaceName = "core.questback.IQuestBackManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Return a list of all tests that the logged in user is assigned to.
     *
     * @return
     */
     public JsonElement getTestResult(Object testId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("testId",new Gson().toJson(testId));
          gs_json_object_data.method = "getTestResult";
          gs_json_object_data.interfaceName = "core.questback.IQuestBackManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Return a list of all tests that the logged in user is assigned to.
     *
     * @return
     */
     public JsonElement getTestResultForUser(Object testId, Object userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("testId",new Gson().toJson(testId));
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "getTestResultForUser";
          gs_json_object_data.interfaceName = "core.questback.IQuestBackManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Return a list of all tests that the logged in user is assigned to.
     *
     * @return
     */
     public JsonElement getTestResults(Object userId, Object testId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.args.put("testId",new Gson().toJson(testId));
          gs_json_object_data.method = "getTestResults";
          gs_json_object_data.interfaceName = "core.questback.IQuestBackManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Return a list of all tests that the logged in user is assigned to.
     *
     * @return
     */
     public JsonElement getTests()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getTests";
          gs_json_object_data.interfaceName = "core.questback.IQuestBackManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Return a list of all tests that the logged in user is assigned to.
     *
     * @return
     */
     public JsonElement getTestsForUser(Object userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "getTestsForUser";
          gs_json_object_data.interfaceName = "core.questback.IQuestBackManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns the calculated result in percent (0 - 100)
     * @param userId
     * @param testId
     * @return
     */
     public JsonElement getTypeByPageId(Object pageId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pageId",new Gson().toJson(pageId));
          gs_json_object_data.method = "getTypeByPageId";
          gs_json_object_data.interfaceName = "core.questback.IQuestBackManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Return a list of all tests that the logged in user is assigned to.
     *
     * @return
     */
     public JsonElement hasAnswered(Object pageId, Object testId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pageId",new Gson().toJson(pageId));
          gs_json_object_data.args.put("testId",new Gson().toJson(testId));
          gs_json_object_data.method = "hasAnswered";
          gs_json_object_data.interfaceName = "core.questback.IQuestBackManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns the calculated result in percent (0 - 100)
     * @param userId
     * @param testId
     * @return
     */
     public JsonElement importExcel(Object base64, Object language)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("base64",new Gson().toJson(base64));
          gs_json_object_data.args.put("language",new Gson().toJson(language));
          gs_json_object_data.method = "importExcel";
          gs_json_object_data.interfaceName = "core.questback.IQuestBackManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns the calculated result in percent (0 - 100)
     * @param userId
     * @param testId
     * @return
     */
     public JsonElement isQuestBackSent(Object userId, Object testId, Object reference)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.args.put("testId",new Gson().toJson(testId));
          gs_json_object_data.args.put("reference",new Gson().toJson(reference));
          gs_json_object_data.method = "isQuestBackSent";
          gs_json_object_data.interfaceName = "core.questback.IQuestBackManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public void questionTreeChanged(Object applicationId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("applicationId",new Gson().toJson(applicationId));
          gs_json_object_data.method = "questionTreeChanged";
          gs_json_object_data.interfaceName = "core.questback.IQuestBackManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns the calculated result in percent (0 - 100)
     * @param userId
     * @param testId
     * @return
     */
     public void saveQuestBackAnswerResponse(Object answerId, Object answer)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("answerId",new Gson().toJson(answerId));
          gs_json_object_data.args.put("answer",new Gson().toJson(answer));
          gs_json_object_data.method = "saveQuestBackAnswerResponse";
          gs_json_object_data.interfaceName = "core.questback.IQuestBackManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Return a list of all tests that the logged in user is assigned to.
     *
     * @return
     */
     public void saveQuestBackResultRequirement(Object requirement)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("requirement",new Gson().toJson(requirement));
          gs_json_object_data.method = "saveQuestBackResultRequirement";
          gs_json_object_data.interfaceName = "core.questback.IQuestBackManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement saveTest(Object test)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("test",new Gson().toJson(test));
          gs_json_object_data.method = "saveTest";
          gs_json_object_data.interfaceName = "core.questback.IQuestBackManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns the calculated result in percent (0 - 100)
     * @param userId
     * @param testId
     * @return
     */
     public void sendQuestBack(Object testId, Object userId, Object reference, Object event)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("testId",new Gson().toJson(testId));
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.args.put("reference",new Gson().toJson(reference));
          gs_json_object_data.args.put("event",new Gson().toJson(event));
          gs_json_object_data.method = "sendQuestBack";
          gs_json_object_data.interfaceName = "core.questback.IQuestBackManager";
          String result = transport.send(gs_json_object_data);
     }

}
