import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
 
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.ArrayList;
import java.util.List;
 
public class JSONUtils 
{
	
	//Parse a JSON file and return the records
    public static ArrayList<String> returnJsonRecords(String filepath) 
    {
        //JSON parser object to parse read file
        JSONParser jsonParser = new JSONParser();
         
        try (FileReader reader = new FileReader(filepath))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
 
            JSONArray recordList = (JSONArray) obj;
            
            ArrayList<String> list = new ArrayList<String>();
            for (int i=0; i<recordList.size(); i++) {
                list.add( recordList.get(i).toString() );
            }
            return list;
            
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    
    
    //Parse a String of JSON items, returning the list of values specified by the key as a list
    public static ArrayList<String> getAllRecordsByKey(String response, String key) throws ParseException{
    	ArrayList<String> recordList = new ArrayList<String>();
    	JSONParser parser = new JSONParser();
    	ArrayList<JSONObject> allRecords = new ArrayList<JSONObject>();
    	
    	try {
			allRecords = (ArrayList<JSONObject>) parser.parse(response);
    	}
    	catch (ClassCastException e) {
    		allRecords.add( (JSONObject) parser.parse(response));
    	}
    	for (JSONObject obj: allRecords) {
    		recordList.add((String) obj.get(key));
    	}

    	return recordList;
    }
    
    //Parse a value from a JSON formatted string by key
    public static String returnValueFromJSON(String response, String key) throws ParseException {
		JSONParser parser = new JSONParser();
		JSONObject json = (JSONObject) parser.parse(response);
		return (String) json.get(key);
	}
 
    //Return a JSON formatted message object to send as a payload when creating a message (using POST request)
    public static String createMessageObject(String fromUser, String toUser, String messageContent) {
    	JSONObject from_obj= new JSONObject();
    	from_obj.put("id", fromUser);

    	JSONObject to_obj = new JSONObject();
    	to_obj.put("id", toUser);


    	JSONObject parent = new JSONObject();
    	parent.put("from", from_obj);
    	parent.put("to", to_obj);
    	parent.put("message", messageContent);
    	
    	return parent.toString();
    	
    }
	
	
}
	
	
	
	