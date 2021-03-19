import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestSuite {
		
		private static HashMap<String, String> userMap;
		private static ArrayList<String> messageIdList;
		private static final String INVALID_USER_ID = "this_user_does_not_exist";
		private static final String INVALID_MESSAGE_ID = "this_message_does_not_exist";
		private static final String regexTime = "[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}.[0-9]{3}Z";
		
		
		//SETUP
		@BeforeAll
		static void init() throws IOException, ParseException {
			//CHANGE TO DYNAMIC DIRECTORY
			ArrayList<String> users = JSONUtils.returnJsonRecords(System.getProperty("user.dir")+"/src/users.json/");
			userMap = Setup.populateDatabase(users);
			messageIdList = new ArrayList<String>();
		}
		
		//TEARDOWN
		@AfterAll
		static void tearDown() throws IOException, ParseException {
			Setup.deleteAllUsers();
			Setup.deleteAllMessages();
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		/*
		 * POST REQUESTS
		 */
		
		
		//TEST: SEND MESSAGES TO OTHER USERS
		@Order(1)
		@TestFactory
		Iterable<DynamicTest> sendMessageToOtherUser() throws IOException, ParseException {
			String fromUser, toUser;
			Collection<DynamicTest> tests = new ArrayList<DynamicTest>();
			
			//Cycle through each user with a nested for loop
			//Each user sends a message to each other user
			for (HashMap.Entry<String, String> fromItem : this.userMap.entrySet()) {
			    String key = fromItem.getKey();
			    fromUser = fromItem.getValue();
			    
			    //create userMap clone without current element
			    HashMap<String, String> innerMap = (HashMap)this.userMap.clone();
			    innerMap.remove(key);
			    
			    for (HashMap.Entry<String, String> toItem : innerMap.entrySet()) {
				    toUser = toItem.getValue();
				    
				    //createMessage() returns null if the response code != 200
				    String response = Requests.createMessage(JSONUtils.createMessageObject(fromUser, toUser, "Test message"));
				    
				  //add the created message to the list of messages for further tests
				    messageIdList.add(JSONUtils.returnValueFromJSON(response, "id"));
				    
				    tests.add(DynamicTest.dynamicTest("Verify that creating a message between two valid users returns a 200", () -> assertFalse(response == null)));
				}
			}
			return tests;
		}
		
		
		//TEST: SEND MESSAGE TO SELF
		@Order(1)
		@TestFactory
		Iterable<DynamicTest> sendMessageToSelf() throws IOException, ParseException {
		    
			Collection<DynamicTest> tests = new ArrayList<DynamicTest>();
		    String user;
			
		    //cycle through each user
			for (HashMap.Entry<String, String> item : this.userMap.entrySet()) {
			    user = item.getValue();
			    
			    //send a message to themselves
			    String response = Requests.createMessage(JSONUtils.createMessageObject(user, user, "Test message"));
			    
			    //add the created message to the list of messages for further tests
			    messageIdList.add(JSONUtils.returnValueFromJSON(response, "id"));
			    
			    tests.add(DynamicTest.dynamicTest("Verify that creating a message with the same to and from userId returns a 200", () -> assertFalse(response==null)));
			}
			return tests;
		}
		
		//TEST: SEND MESSAGE TO INVALID 'TO' USER
		@Order(1)
		@TestFactory
		Iterable<DynamicTest> sendMessageInvalidTo() throws IOException, ParseException {
			Collection<DynamicTest> tests = new ArrayList<DynamicTest>();
			String user;
			for (HashMap.Entry<String, String> item : this.userMap.entrySet()) {
			    user = item.getValue();

			    boolean response = Requests.createMessageBoolReturn(JSONUtils.createMessageObject(user, INVALID_USER_ID, "Test message"));
			    tests.add(DynamicTest.dynamicTest("Verify that sending a message to an invalid userId does not return a 200", () -> assertFalse(response)));
			}
			return tests;
		}
		
		//TEST: SEND MESSAGE TO INVALID 'FROM' USER
		@Order(1)
		@TestFactory
		Iterable<DynamicTest> sendMessageInvalidFrom() throws IOException, ParseException {
			Collection<DynamicTest> tests = new ArrayList<DynamicTest>();
			String user;
			for (HashMap.Entry<String, String> item : this.userMap.entrySet()) {
			    user = item.getValue();

			    boolean response = Requests.createMessageBoolReturn(JSONUtils.createMessageObject(INVALID_USER_ID, user, "Test message"));
			    
			    tests.add(DynamicTest.dynamicTest("Verify that sending a message from an invalid userId does not return a 200", () -> assertFalse(response)));
			}
			return tests;
		}
		
		
		//TEST: SEND MESSAGE TO AND FROM INVALID USER
		@Order(1)
		@Test
		void sendMessageInvalidFromAndTo() throws IOException, ParseException {
			    boolean response = Requests.createMessageBoolReturn(JSONUtils.createMessageObject(INVALID_USER_ID, INVALID_USER_ID, "Test message"));
			    assertFalse(response);
		}
				
		
		
		//TEST: SEND MESSAGES WITH CHINESE CHARACTERS (from each user to each other user incl. themselves)
		@Order(1)
		@TestFactory
		Iterable<DynamicTest> sendMessageWithChineseChars() throws IOException, ParseException {
			String fromUser, toUser;
			Collection<DynamicTest> tests = new ArrayList<DynamicTest>();
			
			for (HashMap.Entry<String, String> fromItem : this.userMap.entrySet()) {
			    String key = fromItem.getKey();
			    fromUser = fromItem.getValue();
			    
			    //create userMap clone without current element
			    HashMap<String, String> innerMap = (HashMap)this.userMap.clone();
			    
			    for (HashMap.Entry<String, String> toItem : innerMap.entrySet()) {
				    toUser = toItem.getValue();
				    
				    //createMessage() returns null if the response code != 200
				    boolean response = Requests.createMessageBoolReturn(JSONUtils.createMessageObject(fromUser, toUser, "測試"));
				    int x = 1;
				    
				    tests.add(DynamicTest.dynamicTest("Verify that sending a message through chinese language returns a 200", () -> assertTrue(response)));
				}
			}
			return tests;
		}
				
		//TEST: SPECIAL CHARACTERS IN MESSAGE BODY
		@Order(1)
		@ParameterizedTest
		@ValueSource(strings = { "", "!\\#$%&'()*+,-./", "∀∁∂∃∄∅∆∇∈∉∊∋∌∍∎∏∐∑", "ĀāĂăĄą" })
		void sendMessageWithSpecialContent(String content) throws IOException, ParseException {
			
			
			ArrayList<String> userArray = new ArrayList<String>(userMap.values());

			boolean result = Requests.createMessageBoolReturn(JSONUtils.createMessageObject(
										userArray.get(0), userArray.get(1), content));
			assertTrue(result);
		}
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
		/*
		 * GET REQUESTS
		 */
				
		//TEST: GET ALL EXISTING MESSAGES INDIVIDUALLY
		@Order(2)
		@TestFactory
		Iterable<DynamicTest> getMessageTest() throws IOException, ParseException {
			
			Collection<DynamicTest> tests = new ArrayList<DynamicTest>();
			String allMessagesResponse = Requests.getMessage("");
			
			ArrayList<String> idList = JSONUtils.getAllRecordsByKey(allMessagesResponse, "id");
			
			for (String id : idList){
				boolean response = Requests.sendGETBoolReturn(Requests.MESSAGE_URL, id);
				
			    tests.add(DynamicTest.dynamicTest("Was a GET of an existing message successful", () -> assertTrue(response)));
			}
			return tests;
		}
		
		//TEST: VERIFY TIME FORMAT OF ALL EXISTING MESSAGES
		@Order(2)
		@TestFactory
		Iterable<DynamicTest> verifyMessageTimeFormat() throws IOException, ParseException {
			
			Collection<DynamicTest> tests = new ArrayList<DynamicTest>();
			String allMessagesResponse = Requests.getMessage("");
			
			ArrayList<String> timeIdList = JSONUtils.getAllRecordsByKey(allMessagesResponse, "time");
			
			for (String id : timeIdList){
				
			    tests.add(DynamicTest.dynamicTest(
			    		"Verify that each 'time' id matches the specified format", 
			    		() -> assertEquals(true, id.matches(regexTime))));
			}
			return tests;
		}
		
		
		//TEST: VERIFY THAT A GET RETURNS ONLY ONE SINGLE MESSAGE AS EXPECTED
		@Order(2)
		@TestFactory
		Iterable<DynamicTest> getMessageQuantityTest() throws IOException, ParseException {
			
			Collection<DynamicTest> tests = new ArrayList<DynamicTest>();
			for (String id : messageIdList){
				
				//get the message by id
				String response = Requests.getMessage(id);
			    
				//Add all returned messages to idList :: There should only be 1 for a passed test
				ArrayList<String> mList = JSONUtils.getAllRecordsByKey(response, "message");
			    
			    tests.add(DynamicTest.dynamicTest("Was one singular message returned", () -> assertTrue(mList.size()==1)));
			}
			return tests;
		}
		
		//TEST: SEND GETMESSAGE WITH BLANK ID PARAMETER
		@Order(2)
		@Test
		void getMessageWithNoParam() throws IOException, ParseException {
			boolean response = Requests.sendGETBoolReturn(Requests.MESSAGE_URL, "");
			
			//the GET is expected to return all messages :: expected response is true (200 response code)
			assertTrue(response);
		}
		
		//TEST: SEND GETMESSAGE WITH INVALID ID PARAMETER
		@Order(2)
		@Test
		void getInvalidMessage() throws IOException, ParseException {
			boolean response = Requests.sendGETBoolReturn(Requests.MESSAGE_URL, INVALID_MESSAGE_ID);
			
			//expect that response == false :: Request should not return a 200
			assertFalse(response);
		}
				
		
				
				
				
		
		
		
		
		
		
		
		
		
		
		
				
				
		/*
		 * LIST TESTS
		 */
				
		//TEST: LIST EXISTING MESSAGES BETWEEN EXISTING USERS
		@Order(2)
		@TestFactory
		Iterable<DynamicTest> listValidMessagesTest() throws IOException, ParseException {
			
			String fromUser, toUser;
			Collection<DynamicTest> tests = new ArrayList<DynamicTest>();
			
			
			for (HashMap.Entry<String, String> fromItem : this.userMap.entrySet()) {
			    fromUser = fromItem.getValue();
			    
			    //create userMap clone without current element
			    HashMap<String, String> innerMap = (HashMap)this.userMap.clone();
			    
			    for (HashMap.Entry<String, String> toItem : innerMap.entrySet()) {
				    toUser = toItem.getValue();
				    
				    //returns false if the response code != 200
				    boolean result = Requests.listMessagesBoolReturn(fromUser, toUser);
				    
			    tests.add(DynamicTest.dynamicTest("Verify that listing messages between two existing users returns a 200", () -> assertTrue(result)));
			    }
			}
			return tests;
		}
		
		//TEST: LIST EXISTING MESSAGES BETWEEN NON-EXISTENT USERS
		@Order(2)
		@Test
		void listExistingMessagesBetweenNonExistentUsers() throws IOException {
			boolean result = Requests.listMessagesBoolReturn(INVALID_USER_ID, INVALID_USER_ID);
			assertTrue(result);
		}
		
		//TEST: LIST MESSAGES WITH BLANK FROM USER
		@Order(2)
		@TestFactory
		Iterable<DynamicTest> listMessagesWithBlankFromParameter() throws IOException, ParseException {
			
			String toUser;
			Collection<DynamicTest> tests = new ArrayList<DynamicTest>();
			
			
			for (HashMap.Entry<String, String> item : this.userMap.entrySet()) {
			    toUser = item.getValue();
			    
			    //returns false if the response code != 200
			    boolean result = Requests.listMessagesBoolReturn("", toUser);
				    
			    tests.add(DynamicTest.dynamicTest("Verify that listing messages from a blank userId returns a 200", () -> assertTrue(result)));
			
			}
			return tests;
		}
		
		
		//TEST: LIST MESSAGES WITH BLANK TO USER
		@Order(2)
		@TestFactory
		Iterable<DynamicTest> listMessagesWithBlankToParameter() throws IOException, ParseException {
			
			String fromUser;
			Collection<DynamicTest> tests = new ArrayList<DynamicTest>();
			
			
			for (HashMap.Entry<String, String> item : this.userMap.entrySet()) {
			    fromUser = item.getValue();
			    
			    //returns false if the response code != 200
			    boolean result = Requests.listMessagesBoolReturn(fromUser, "");
				    
			    tests.add(DynamicTest.dynamicTest("Verify that listing messages from a blank userId returns a 200", () -> assertTrue(result)));
			
			}
			return tests;
		}
		
		//TEST: LIST MESSAGES WITH BLANK PARAMETERS
		@Order(2)
		@Test
		void listMessagesWithBlankParameters() throws IOException {
			boolean result = Requests.listMessagesBoolReturn("", "");
			
			//expected: all messages are returned (list is a GET requests, hence result should be
			//similar to blank Get message request)
			assertTrue(result);
		}
		
		
		
		//TEST: LIST MESSAGES FOR VALID USERID WHERE FROMUSER == TOUSER
		@Order(2)
		@TestFactory
		Iterable<DynamicTest> listMessagesBetweenValidUserAndThemselves() throws IOException, ParseException {
			
			String user;
			Collection<DynamicTest> tests = new ArrayList<DynamicTest>();
			
			
			for (HashMap.Entry<String, String> item : this.userMap.entrySet()) {
			    user = item.getValue();
			    
			    //returns false if the response code != 200
			    boolean result = Requests.listMessagesBoolReturn(user, user);
				    
			    tests.add(DynamicTest.dynamicTest("Verify that listing messages between a valid user and themselves returns a 200", () -> assertTrue(result)));
			}
			return tests;
		}
		
		
		
		
		
		
		
		
		
		
		
		
		/*
		 * DELETE TESTS
		 */
		
		//TEST: DELETE MESSAGE WITH INVALID ID
		@Order(3)
		@Test
		void deleteInvalidMessageId() throws IOException, ParseException {
			
			boolean result = Requests.sendDELETEBoolReturn(Requests.MESSAGE_URL, INVALID_MESSAGE_ID);
			assertFalse(result);
		}
		
			
		//TEST: DELETE MESSAGE USING USER ID
		@Order(4)
		@TestFactory
		Iterable<DynamicTest> deleteMessagesWithUserId() throws IOException, ParseException {
			Collection<DynamicTest> tests = new ArrayList<DynamicTest>();
			
			for (HashMap.Entry<String, String> item : this.userMap.entrySet()) {
				String userId = item.getValue();
				boolean result = Requests.sendDELETEBoolReturn(Requests.MESSAGE_URL, userId);
				
				tests.add(DynamicTest.dynamicTest("Verify that deleting messages using a userId is error handled", () -> assertFalse(result)));
			}
			return tests;
		}
		
				
		//TEST: DELETE MESSAGE WITH BLANK PARAMETER
		@Order(5)
		@Test
		void deleteWithBlankMessageId() throws IOException, ParseException {
			boolean result = Requests.sendDELETEBoolReturn(Requests.MESSAGE_URL, "");
			assertFalse(result);
		}
		
		//TEST: DELETE EXISTING MESSAGES
		@Order(6)
		@TestFactory
		Iterable<DynamicTest> deleteExistingMessages() throws IOException, ParseException {
			Collection<DynamicTest> tests = new ArrayList<DynamicTest>();
			String response = Requests.getMessage("");
			ArrayList<String> idList = JSONUtils.getAllRecordsByKey(response, "id");
			
			for (String id: idList) {
				//System.out.println(id);
				boolean result = Requests.sendDELETEBoolReturn(Requests.MESSAGE_URL, id);
				
				tests.add(DynamicTest.dynamicTest("Verify expected valid deletion of existing messages", () -> assertTrue(result)));
			}
			return tests;
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		/*
		 * FURTHER LIST TESTS:
		 * Due to test ordering, by this next test, the database will have users but all messages should be deleted.
		 * The following test will create new messages strategically to test a specific aspect of the LIST request.
		 */
		
		
		//TEST: VERIFY THAT LIST MESSAGE RETURNS THE CORRECT NUMBER OF MESSAGES
		//this test will first fill the Database with a message to and from each user
		@Order(7)
		@TestFactory
		Iterable<DynamicTest> verifyCountOfListMessagesResponse() throws IOException, ParseException {
			String fromUser, toUser;
			Collection<DynamicTest> tests = new ArrayList<DynamicTest>();
			
			//send a message from each user to each other user
			Setup.sendMessageToAndFromEachUser(userMap);
			
			//List all combinations of messages between two users. 
			for (HashMap.Entry<String, String> fromItem : this.userMap.entrySet()) {
			    String key = fromItem.getKey();
			    fromUser = fromItem.getValue();
			    
			    //create userMap clone without current element
			    HashMap<String, String> innerMap = (HashMap)this.userMap.clone();
			    innerMap.remove(key);
			    
			    for (HashMap.Entry<String, String> toItem : innerMap.entrySet()) {
				    toUser = toItem.getValue();
				    
				    //createMessage() returns null if the response code != 200
				    String response = Requests.listMessages(fromUser, toUser);
				    ArrayList<String> mList = JSONUtils.getAllRecordsByKey(response, "message");
				    int x = 1;
				    /*
				     * each user has sent one message to each other user & received one message
				     * from each other user. Hence, the list of messages between two users
				     * is expected to be == 2.
				     */
				    tests.add(DynamicTest.dynamicTest("Verify that LIST messages returns only messages between the to and from user", () -> assertTrue(mList.size()==2)));
				}
			}
			return tests;
		}
		
		//TEST: LIST MESSAGES OF USERS THAT HAVE BEEN DELETED
		//this test first deletes all users from the Database, then lists the possibly orphan messages
		@Order(8)
		@TestFactory
		Iterable<DynamicTest> listValidMessagesOfDeletedUsers() throws IOException, ParseException {
			String fromUser, toUser;
			Collection<DynamicTest> tests = new ArrayList<DynamicTest>();
			
			//clear the 'users' table of the database
			Setup.deleteAllUsers();
			
			//List the orphan messages in all combinations of two users
			for (HashMap.Entry<String, String> fromItem : this.userMap.entrySet()) {
			    fromUser = fromItem.getValue();
			    
			    //create userMap clone without current element
			    HashMap<String, String> innerMap = (HashMap)this.userMap.clone();
			    
			    for (HashMap.Entry<String, String> toItem : innerMap.entrySet()) {
				    toUser = toItem.getValue();
				    
				    //returns false if the response code != 200
				    boolean result = Requests.listMessagesBoolReturn(fromUser, toUser);
				    
			    tests.add(DynamicTest.dynamicTest(
			    		"Verify that orphaned messages of deleted users remain to be accessible through a GET request",
			    		() -> assertTrue(result)));
			    }
			}
			return tests;
		}
}
	