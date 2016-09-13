package fi.jonne.javacliutils.core.utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.sound.sampled.LineUnavailableException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import fi.jonne.javacliutils.core.Communicator;
import fi.jonne.javacliutils.settings.Settings;

/**
 * This class handles all TimerInfo objects
 * **/
public class TimerInfoContainer {
	
	private static TimerInfoContainer instance;
	private static HashMap<Integer, TimerInfo> timerInfos;
	private static int nextId = 0;
	
	public TimerInfoContainer(){}
	
	public static TimerInfoContainer getInstance(){
		if(instance == null){
			instance = new TimerInfoContainer();
			timerInfos = new HashMap<Integer, TimerInfo>();
		}
		return instance;
	}
	
	public void setTimer(TimerInfo timerInfo){
		timerInfos.put(timerInfo.id, timerInfo);
		saveTimers();
	}
	
	public TimerInfo getTimer(int id){
		return timerInfos.get(id);
	}
	
	public int getNextId(){
		nextId += 1;
		return nextId;
	}
	
	public void setNextId(int id){
		nextId = id;
	}
	
	public HashMap<Integer, TimerInfo> getTimers(){
		return timerInfos;
	}
	
	public void removeTimer(TimerInfo timer){
		timerInfos.remove(timer.id);
		timer.timer.cancel();
		saveTimers();
	}
	
	public boolean isTimerExist(int id){
		if(timerInfos.get(id) != null){
			return true;
		}else{
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")
	public void saveTimers(){
		JSONObject objRoot = new JSONObject();
		
		JSONArray objArr = new JSONArray();
		
		for(Map.Entry<Integer, TimerInfo> timer : timerInfos.entrySet()){

			JSONObject obj = new JSONObject();
			
			obj.put("id", String.valueOf(timer.getValue().id));
			obj.put("timeStampStart", String.valueOf(timer.getValue().timeStampStart));
			obj.put("timeStampEnd", String.valueOf(timer.getValue().timeStampEnd));
			obj.put("name", String.valueOf(timer.getValue().name));
			obj.put("owner", String.valueOf(timer.getValue().owner));
			obj.put("channel", String.valueOf(timer.getValue().channel));
			obj.put("isTimerRepeating", String.valueOf(timer.getValue().isTimerRepeating));
			
			objArr.add(obj);
		}
		
		objRoot.put("timerInfos", objArr);
		
		try{
			FileWriter file = new FileWriter("timerinfos.json");
			file.write(objRoot.toJSONString());
			file.flush();
			file.close();
		}catch(IOException e){
			Communicator.getInstance().handleError(Settings.LOCAL_CHANNEL, Settings.currentLocalSender, "saveTimers error: " + e.getMessage());
		}
	}
	
	@SuppressWarnings("unchecked")
	public void initializeTimers(){
		Communicator.getInstance().handleOutput(Settings.LOCAL_CHANNEL, Settings.currentLocalSender, "Initializing timers...");
		JSONParser parser = new JSONParser();
		
		try {

			Object obj = parser.parse(new FileReader("timerinfos.json"));

			JSONObject jsonObject = (JSONObject) obj;

			JSONArray timerObjects = (JSONArray) jsonObject.get("timerInfos");
			Iterator<JSONObject> iterator = timerObjects.iterator();
			while (iterator.hasNext()) {
				JSONObject objNext = iterator.next();
				
				Integer id = Integer.valueOf((String) objNext.get("id"));
				Long timeStampStart = Long.valueOf((String) objNext.get("timeStampStart"));
				Long timeStampEnd = Long.valueOf((String) objNext.get("timeStampEnd"));
				String name = (String) objNext.get("name");
				String owner = (String) objNext.get("owner");
				String channel = (String) objNext.get("channel");
				Boolean isTimerRepeating = Boolean.valueOf((String) objNext.get("isTimerRepeating"));
				
				new TimerInfo(id, timeStampStart, timeStampEnd, name, owner, channel, isTimerRepeating);
				
				nextId = id;
			}
			Communicator.getInstance().handleOutput(Settings.LOCAL_CHANNEL, Settings.currentLocalSender, "[OK]");
		}catch (FileNotFoundException e) {
			Communicator.getInstance().handleError(Settings.LOCAL_CHANNEL, Settings.currentLocalSender, "initializeTimers error: " + e.getMessage());
			Communicator.getInstance().handleError(Settings.LOCAL_CHANNEL, Settings.currentLocalSender, "Writing timerinfos.json file to JavaCLIUtils root folder...");
			saveTimers();
			Communicator.getInstance().handleOutput(Settings.LOCAL_CHANNEL, Settings.currentLocalSender, "[OK]");
		}catch (IOException e) {
			Communicator.getInstance().handleError(Settings.LOCAL_CHANNEL, Settings.currentLocalSender, "initializeTimers IO error: " + e.getMessage());
		}catch (ParseException e) {
			Communicator.getInstance().handleError(Settings.LOCAL_CHANNEL, Settings.currentLocalSender, "initializeTimers parse error: " + e.getMessage());
			Communicator.getInstance().handleError(Settings.LOCAL_CHANNEL, Settings.currentLocalSender, "Writing new timerinfos.json file to JavaCLIUtils root folder...");
			saveTimers();
			Communicator.getInstance().handleOutput(Settings.LOCAL_CHANNEL, Settings.currentLocalSender, "[OK]");
		}
	}
}
