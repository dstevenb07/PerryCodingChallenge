package com.dylanbourke.perrytestsuite;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.parser.ParseException;
import org.junit.runner.JUnitCore;

public class Setup {
	
	//Populates the database with users from JSON file
	public static HashMap<String, String> populateDatabase(ArrayList<String> userList) throws IOException, ParseException {
		HashMap<String, String> userMap = new HashMap<String, String>();
		for(String user : userList) {
			String id = Requests.createUser(user);
			userMap.put(JSONUtils.returnValueFromJSON(user, "name"), id);
		}
		return userMap;
	}
	
	//Fill the Message Table with messages :: each user messages each other user once
	public static void sendMessageToAndFromEachUser(HashMap<String, String> map) throws IOException, ParseException {
		String fromUser, toUser;
		for (HashMap.Entry<String, String> fromItem : map.entrySet()) {
		    String key = fromItem.getKey();
		    fromUser = fromItem.getValue();
		    
		    //create userMap clone without current element
		    HashMap<String, String> innerMap = (HashMap)map.clone();
		    innerMap.remove(key);
		    
		    for (HashMap.Entry<String, String> toItem : innerMap.entrySet()) {
			    toUser = toItem.getValue();
			    
			    //createMessage() returns null if the response code != 200
			    Requests.createMessage(JSONUtils.createMessageObject(fromUser, toUser, "Test message"));
			}
		}
	}
	
	//Clear the users Table
	public static void deleteAllUsers() throws IOException, ParseException {
		String response = Requests.sendGET("https://perrys-summer-vacation.herokuapp.com/api/users");
		ArrayList<String> idList = JSONUtils.getAllRecordsByKey(response, "id");
		
		for (String id: idList) {
			Requests.sendDELETE(Requests.USER_URL, id);
		}
	}
	
	//Clear the Messages Table
	public static void deleteAllMessages() throws ParseException, IOException {
		String response = Requests.getMessage("");
		ArrayList<String> idList = JSONUtils.getAllRecordsByKey(response, "id");
		
		for (String id: idList) {
			//System.out.println(id);
			Requests.sendDELETEBoolReturn(Requests.MESSAGE_URL, id);
		}
	}
	
	public static void main(String[] args) throws Exception {                    
	       JUnitCore.main("com.dylanbourke.perrytestsuite.TestSuite");
	}
	
}
	

	
