package perrytestsuite;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.simple.parser.ParseException;

public class Requests {

	private static final String USER_AGENT = "Mozilla/5.0";

	public static final String USER_URL = "https://perrys-summer-vacation.herokuapp.com/api/users";
	
	public static final String MESSAGE_URL = "https://perrys-summer-vacation.herokuapp.com/api/messages";
	
	
	//Send a GET
	private static String sendGET(String url, String payload) throws IOException {
		//null will throw error due to concatenation
		if (payload == null) {
			payload = "";
		}
		
		URL obj = new URL(url+payload);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", USER_AGENT);
		int responseCode = con.getResponseCode();
		//System.out.println("GET Response Code :: " + responseCode);
		if (responseCode == HttpURLConnection.HTTP_OK) { // success
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			System.out.println("GET request successful\n");
			return response.toString();
		} else {
			System.out.println("GET request failed\n");
			return null;
		}

	}
	
	//send a GET; return true if successful
	public static boolean sendGETBoolReturn(String url, String payload) throws IOException {
		String response = sendGET(url, "/"+payload);
		if (response == null) {
			return false;
		} else {
			return true;
		}
	}
	
	//Overloaded: Send a get with blank payload
	public static String sendGET(String url) throws IOException {
		return sendGET(url, "");
	}
	
	//GET a message
	public static String getMessage(String messageId) throws IOException {	
		return sendGET(MESSAGE_URL,"/"+messageId);
	}
	
	//List messages between two users
	public static String listMessages(String fromUser, String toUser) throws IOException {
		String payload = "?from="+fromUser+"&to="+toUser;
		
		return sendGET(MESSAGE_URL, payload);
	}
	
	//List messages between two users :: returns true if successful
	public static boolean listMessagesBoolReturn(String fromUser, String toUser) throws IOException {
		String response = listMessages(fromUser, toUser);
		if (response == null) {
			return false;
		}
		else {
			return true;
		}
	}
	
	//Send a Delete request :: returns true if successful
	public static boolean sendDELETEBoolReturn(String url, String param) throws IOException, ParseException {
		String response = sendDELETE(url, param);
		if (response == null) {
			return false;
		} else {
			return true;
		}
	}
	
	//Send a DELETE Request :: returns a string response (empty String if no response) if successful.
	//returns null if unsuccessful
	public static String sendDELETE(String url, String param) throws IOException, ParseException {
		URL obj = new URL(url+"/"+param);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("DELETE");
		
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded" );
        con.setDoOutput(true);
        con.setDoInput(true);

        con.connect();
        

		int responseCode = con.getResponseCode();
		System.out.println("DELETE Response Code :: " + responseCode);

		if (responseCode == HttpURLConnection.HTTP_OK) { //success
				BufferedReader in = new BufferedReader(new InputStreamReader(
						con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
	
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
	
				//System.out.println("DELETE request successful");
				return response.toString();
		}  else if (responseCode == HttpURLConnection.HTTP_UNAVAILABLE) {//Users API returns 503 when successful (bug)
				
				//System.out.println("DELETE request likely successful\n");
				//return blank (not null) if delete is successful but no response content
				return "";
			
		} else if(responseCode == HttpURLConnection.HTTP_NO_CONTENT) {//Message API returns 204 when successful
				//System.out.println("DELETE request successful\n");
				//return blank (not null) if delete is successful but no response content
				return "";
		}else {
				//System.out.println("DELETE request failed\n");
				//returns null if failed
				return null;
		}
	} 
	
	//Send a post :: return String response if successful / null if unsuccessful
	public static String sendPOST(String url, String payload) throws IOException, ParseException {
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("POST");
		//con.setRequestProperty("User-Agent", USER_AGENT);
		
		con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        con.setDoOutput(true);
        con.setDoInput(true);

		// For POST only - START
		//con.setDoOutput(true);
		OutputStream os = con.getOutputStream();
		os.write(payload.getBytes());
		os.flush();
		os.close();
		// For POST only - END
		
		//System.out.println("con.getContent() :: " + con.getContent());
		//System.out.println("con.getResponseMessage() :: " + con.getResponseMessage());

		int responseCode = con.getResponseCode();
		//System.out.println("POST Response Code :: " + responseCode);

		if (responseCode == HttpURLConnection.HTTP_OK) { //success
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			//return id of newly created user
			//System.out.println("POST request successful\n");
			return response.toString();
		} else {
			//System.out.println("POST request failed\n");
			return null;
		}
	} 
	
	
	//Send a POST, creating a user of specified payload. returns the new userId
	public static String createUser(String payload) throws IOException, ParseException {
		return parseUserId(sendPOST(USER_URL, payload));
	} 
	
	
	//Send a POST, creating message of specified payload
	public static String createMessage(String payload) throws IOException, ParseException {
		return sendPOST(MESSAGE_URL, payload);
	}
	 
	//creates a new message through a POST request :: returns true if response code == 200
	public static boolean createMessageBoolReturn(String payload) throws IOException, ParseException {
		String response = createMessage(payload);
		if (response == null) {
			return false;
		} else {
			return true;
		}
	}
	
	//Accepts a full system response from the API; then it parses and returns the userid
	private static String parseUserId(String response) throws ParseException {
		return JSONUtils.returnValueFromJSON(response, "id");
	}

}
