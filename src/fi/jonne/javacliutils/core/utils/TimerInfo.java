package fi.jonne.javacliutils.core.utils;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import fi.jonne.javacliutils.core.Communicator;
import fi.jonne.javacliutils.core.ECommands;

public class TimerInfo extends TimerTask{
	
	// Run timer every second
	public static final long PERIOD = 1000L;
	public static final HashMap<String, Integer> TIME_MX = new HashMap<String, Integer>(){
		private static final long serialVersionUID = 1L;{
		put("h", 3600000);
		put("m", 60000);
		put("s", 1000);
	}};
	
	public String name = "timer";
	public String owner = "You";
	public String channel;
	public long timeStampStart;
	public long timeStampEnd;
	public long time;
	public long delay;
	public Timer timer;
	public int id;
	public boolean isTimerRepeating = false;
	
	/**
	 * Use this constructor when setting timers from file
	 * **/
	public TimerInfo(int id, long timerStart, long timerEnd,
			String timerName, String timerOwner, String timerChannel, boolean isRepeating){
		
		this.time = timerEnd - System.currentTimeMillis();
		
		if(this.time < 0L && isRepeating){
			this.time = timerEnd - timerStart;
		}
		
		if(this.time > 0L){			
			this.id = id;
			this.timeStampStart = timerStart;
			this.timeStampEnd = timerEnd;
			
			this.delay = timerStart - System.currentTimeMillis();
			
			if(this.delay <= 0L){
				this.delay = 0L;
			}
			
			this.name = timerName;
			this.owner = timerOwner;
			this.channel = timerChannel;
			this.isTimerRepeating = isRepeating;
			this.timer = new Timer(this.name  + "-" + String.valueOf(this.id));
			
			TimerInfoContainer.getInstance().setTimer(this);
			this.timer.scheduleAtFixedRate(this, this.delay, PERIOD);
		}
		
	}
	
	/**
	 * Use this constructor for creating public online timers if IRCIRCBot.getInstance() is connected
	 * **/
	public TimerInfo(String timerTime, String timerDelay, String timerName, String timerOwner, String timerChannel, boolean isRepeating){
		
		this.time = parseTimeFromTimerString(timerTime);
		
		if(this.time > 0L){

			this.id = TimerInfoContainer.getInstance().getNextId();
			
			if(timerName != null && timerName != ""){
				this.name = timerName;			
			}
			
			this.owner = timerOwner;
			this.channel = timerChannel;
			
			this.delay = parseTimeFromTimerString(timerDelay);
			
			this.timeStampStart = System.currentTimeMillis() + this.delay;
			this.timeStampEnd = this.timeStampStart + this.time;

			this.isTimerRepeating = isRepeating;
			this.timer = new Timer(this.name  + "-" + String.valueOf(this.id));
			
			TimerInfoContainer.getInstance().setTimer(this);
			
			if(!this.isTimerRepeating){
				Communicator.getInstance().handleOutput(this.owner + ", your timer [" + this.id + "] [" + this.name + "] has been set for " + parseTimeStringFromTime(this.time, true) + "!");				
			}else{
				Communicator.getInstance().handleOutput(this.owner + ", your timer [" + this.id + "] [" + this.name + "] has been scheduled to repeat every " + parseTimeStringFromTime(this.time, true) + " in " + parseTimeStringFromTime(this.delay, true) + "!");
			}
			
			this.timer.scheduleAtFixedRate(this, this.delay, PERIOD);
		}
	}
	
	/**
	 * Use this constructor for creating offline local timers
	 * **/
	public TimerInfo(String timerTime, String timerDelay, String timerName, boolean isRepeating){
		
		this.time = parseTimeFromTimerString(timerTime);
		
		if(this.time > 0L){
			
			this.id = TimerInfoContainer.getInstance().getNextId();
			
			if(timerName != null && timerName != ""){
				this.name = timerName;			
			}
			
			this.delay = parseTimeFromTimerString(timerDelay);
			
			this.timeStampStart = System.currentTimeMillis() + this.delay;
			this.timeStampEnd = this.timeStampStart + this.time;
			
			this.isTimerRepeating = isRepeating;
			this.timer = new Timer(this.name  + "-" + String.valueOf(this.id));

			TimerInfoContainer.getInstance().setTimer(this);
			
			if(!this.isTimerRepeating){
				Communicator.getInstance().handleOutput("Your timer [" + this.id + "] [" + this.name + "] has been set for " + parseTimeStringFromTime(this.time, true) + "!");				
			}else{
				Communicator.getInstance().handleOutput("Your timer [" + this.id + "] [" + this.name + "] has been scheduled to repeat every " + parseTimeStringFromTime(this.time,true) + " in " + parseTimeStringFromTime(this.delay, true) + "!");
			}
			
			this.timer.scheduleAtFixedRate(this, this.delay, PERIOD);
		}
	}

	private long parseTimeFromTimerString(String timerString){
		try{
			
			long parsedTimeLong = 0L;
			
			final int strLength = timerString.length();
			long hours = 0, minutes = 0, seconds = 0;
			char hms;
			boolean charFound = false;
			
			String timerTimeTemp = timerString;
			
			for(int i = 0;i < strLength; i++){
				
				hms = timerString.charAt(i);
				
				switch(hms){
				case 'h':
					String[] hourArr = timerTimeTemp.split("h");
					if(hourArr.length > 1){
						timerTimeTemp = hourArr[1];					
					}
					hours += Long.valueOf(hourArr[0]);
					charFound = true;
					break;
				case 'm':
					String[] minArr = timerTimeTemp.split("m");
					if(minArr.length > 1){
						timerTimeTemp = minArr[1];					
					}
					minutes += Long.valueOf(minArr[0]);
					charFound = true;
					break;
				case 's':
					String[] secArr = timerTimeTemp.split("s");
					if(secArr.length > 1){
						timerTimeTemp = secArr[1];						
					}
					seconds += Long.valueOf(secArr[0]);
					charFound = true;
					break;
				default:
					charFound = false;
					break;
				}
			}
			
			parsedTimeLong = (
					hours*TIME_MX.get("h")+
					minutes*TIME_MX.get("m")+
					seconds*TIME_MX.get("s")
				);
			
			if(!charFound){
				ECommands.HELP.execute(null);
			}
			
			return parsedTimeLong;
		}catch(NumberFormatException e){
			Communicator.getInstance().handleError("parseTimeFromTimerString error: " + e.getMessage());
			return 0L;
		}
	}
	
	public String parseTimeStringFromTime(long timeLong, boolean isVerboseOutput){
		String timeString = "";
		
		long timeLeft = timeLong;
		
		try{

			long hoursLeft = TimeUnit.MILLISECONDS.toHours(timeLeft);
			long minutesLeft = TimeUnit.MILLISECONDS.toMinutes(timeLeft-(hoursLeft * TIME_MX.get("h")));
			long secondsLeft = TimeUnit.MILLISECONDS.toSeconds((timeLeft - minutesLeft * TIME_MX.get("m"))-(hoursLeft * TIME_MX.get("h")));
			
			if(isVerboseOutput){
				
				
				if(hoursLeft > 0){
					
					if(hoursLeft == 1){
						timeString += hoursLeft + " hour";				
					}else{
						timeString += hoursLeft + " hours";
					}
				}
				
				if(minutesLeft > 0 && minutesLeft < 60){
					
					if(hoursLeft > 0){
						timeString += " and ";
					}
					
					if(minutesLeft == 1){
						timeString += minutesLeft + " minute";
					}else{
						timeString += minutesLeft + " minutes";
					}
				}
				
				if(secondsLeft > 0 && secondsLeft < 60){
					if(hoursLeft > 0 || minutesLeft > 0){
						timeString += " and ";
					}
					
					if(secondsLeft == 1){
						timeString += secondsLeft + " second";
					}else{
						timeString += secondsLeft + " seconds";
					}
				}
				
				return timeString;
			}else{
				return String.format("%02d:%02d:%02d", hoursLeft, minutesLeft, secondsLeft);
			}
		}catch(NumberFormatException e){
			Communicator.getInstance().handleError("parseTimeStringFromTime error: " + e.getMessage());
			return "";
		}
	}

	@Override
	public void run() {
		
		this.time -= PERIOD;
		
		// Send message if timer...
		if(this.time == 3600 * 1000 || // has 1 hour left
				this.time == 1800 * 1000 || // has 30 minutes left
				this.time == 600 * 1000 || // has 10 minutes left
				this.time == 300 * 1000 || // has 5 minutes left
				this.time == 60 * 1000 || // has 1 minute left
				this.time == 30 * 1000){ // has 30 seconds left
			
			// This is automated message, so set channel!
			Communicator.getInstance().setChannel(this.channel);
			Communicator.getInstance().handleOutput(this.owner + ", your timer [" + this.id + "] [" + this.name + "] has " + parseTimeStringFromTime(this.time, true) + " left");
		}
		else if(this.time <= 0L){
			
			String msg = this.owner + ", your timer [" + this.id + "] [" + this.name + "] has finished";
			
			if(!this.isTimerRepeating){
				TimerInfoContainer.getInstance().removeTimer(this);
			}else{
				this.time = this.timeStampEnd - this.timeStampStart;
				msg += " and set again for " + parseTimeStringFromTime(this.time, true);
			}

			// This is automated message, so set channel!
			Communicator.getInstance().setChannel(this.channel);
			Communicator.getInstance().handleOutput(msg);
		}
	}

}
