package fi.jonne.javacliutils.core;

import java.util.Map;

import fi.jonne.javacliutils.core.utils.Calculator;
import fi.jonne.javacliutils.core.utils.IRCBot;
import fi.jonne.javacliutils.core.utils.TimerInfo;
import fi.jonne.javacliutils.core.utils.TimerInfoContainer;
import fi.jonne.javacliutils.core.utils.UfoName;

/**
 * This enum contains all core commands and their implementations
 * **/
public enum ECommands implements ICommands {
	CALCULATE {
		public boolean isCommand(String[] args){
			if(args[0].toLowerCase().startsWith("calc") && args.length == 2){
				return true;
			}
			return false;
		}
		public String getInputStringFromArgs(String[] args){
			String inputString = "";
			
			for(int i = 1; i < args.length; i++){
				inputString += args[i]+" ";
			}
			
			return inputString.trim();
		}
		public boolean isAuthorized(String sender){
			return true;
		}
		public void execute(String[] args) {
			Communicator.getInstance()
			.handleOutput(Calculator.getInstance()
					.calculate(getInputStringFromArgs(args)));
		}
	},
	UFONAME {
		public boolean isCommand(String[] args){
			if(args[0].toLowerCase().startsWith("ufo") && args.length >= 2){
				return true;
			}
			return false;
		}
		public String getInputStringFromArgs(String[] args){
			String inputString = "";
			
			for(int i = 1; i < args.length; i++){
				inputString += args[i]+" ";
			}
			
			return inputString.trim();
		}
		public boolean isAuthorized(String sender){
			return true;
		}
		public void execute(String[] args) {
			Communicator.getInstance()
			.handleOutput(new UfoName(getInputStringFromArgs(args)).name);
		}
	},
	EXIT {
		public boolean isCommand(String[] args){
			if(args[0].toLowerCase().startsWith("exit")){
				return true;
			}
			return false;
		}
		public String getInputStringFromArgs(String[] args){
			String inputString = "";
			
			for(int i = 1; i < args.length; i++){
				inputString += args[i]+" ";
			}
			
			return inputString.trim();
		}
		public boolean isAuthorized(String sender){
			for(EAuth auth : EAuth.values()){
				if(auth.toString().equalsIgnoreCase(sender)){
					return true;
				}
			}
			return false;
		}
		public void execute(String[] args) {
			if(IRCBot.getInstance().isConnected()){					
				IRCBot.getInstance().quitServer("Disconnecting...");
			}else{
				System.exit(0);
			}
		}
	},
	IRC {
		public boolean isCommand(String[] args){
			if(args[0].toLowerCase().startsWith("irc") && args.length == 4){
				return true;
			}
			return false;
		}
		public String getInputStringFromArgs(String[] args){
			String inputString = "";
			
			for(int i = 1; i < args.length; i++){
				inputString += args[i]+" ";
			}
			
			return inputString.trim();
		}
		public boolean isAuthorized(String sender){
			for(EAuth auth : EAuth.values()){
				if(auth.toString().equalsIgnoreCase(sender)){
					return true;
				}
			}
			return false;
		}
		public void execute(String[] args) {
			if(!IRCBot.getInstance().isConnected()){
				
				IRCBot.getInstance().setBotName(args[1]);
				
				try{
					IRCBot.getInstance().setVerbose(true);
					IRCBot.getInstance().setEncoding("UTF-8");
					
					Communicator.getInstance()
					.handleOutput("Connecting to " + args[2] + "...");
					
					IRCBot.getInstance().connect(args[2]);
					
					Communicator.getInstance()
					.handleOutput("[OK]");
					Communicator.getInstance()
					.handleOutput("Joining channel " + args[3] + "...");
					IRCBot.getInstance().joinChannel(args[3]);
					Communicator.getInstance()
					.handleOutput("[OK]");
					
					if(IRCBot.getInstance().isConnected()){
						IRCBot.getInstance().setVerbose(false);
					}
					
				}catch(Exception e){
					Communicator.getInstance().handleError("IRC ERROR: " + e.getMessage());
				}
			}
		}
	},
	TIMER {
		public boolean isCommand(String[] args){
			if(args[0].toLowerCase().startsWith("timer") && args.length >= 1){
				return true;
			}
			return false;
		}
		public String getInputStringFromArgs(String[] args){
			String inputString = "";
			
			for(int i = 2; i < args.length; i++){
				inputString += args[i]+" ";
			}
			
			return inputString.trim();
		}
		public boolean isAuthorized(String sender){
			return true;
		}
		public void execute(String[] args) {
			if(args.length > 1){
				
				TimerInfo timer;
				
				if(!IRCBot.getInstance().isConnected()){
					timer = new TimerInfo(args[1], "0s", getInputStringFromArgs(args), false);						
				}else{
					timer = new TimerInfo(args[1], "0s", getInputStringFromArgs(args),
							Communicator.getInstance().getSender(),
							Communicator.getInstance().getChannel(), false);
				}
				
				//Check if timer thread started correctly
				if(timer.isTimerRunning){
					TimerInfoContainer.getInstance().setTimer(timer);
				}
				
			}else if(args.length == 1){
				
				if(TimerInfoContainer.getInstance().getTimers().size() < 1){
					Communicator.getInstance().handleOutput("No timers set. Use ?timer [(int)time (char)h/m/s] [timer name] to set a timer");
				}else{						
					String timers = "";
					
					for(Map.Entry<Integer, TimerInfo> timer : TimerInfoContainer.getInstance().getTimers().entrySet()){
						
						timers += ">>Timer [" + timer.getValue().id + "] [" + timer.getValue().name + "] for " + timer.getValue().owner + " has " + timer.getValue().parseTimeStringFromTime(timer.getValue().time) + " remaining!<<";
						
					}
					Communicator.getInstance().handleOutput(timers);
				}
			}
		}
	},
	RMTIMER {
		public boolean isCommand(String[] args){
			if(args[0].toLowerCase().startsWith("rmtimer") && args.length == 2){
				return true;
			}
			return false;
		}
		public String getInputStringFromArgs(String[] args){
			String inputString = "";
			
			for(int i = 1; i < args.length; i++){
				inputString += args[i]+" ";
			}
			
			return inputString.trim();
		}
		public boolean isAuthorized(String sender){
			return true;
		}
		public void execute(String[] args) {
			
			int id = Integer.valueOf(getInputStringFromArgs(args));
			
			if(TimerInfoContainer.getInstance().isTimerExist(id)){
				TimerInfo timer = TimerInfoContainer.getInstance().getTimer(id);
				timer.isTimerRunning = false;
			}else{
				Communicator.getInstance().handleOutput("No timer " + String.valueOf(id) + " found. Use ?rmtimer [(int)id] to remove a timer");
			}
		}
	},
	RTIMER {
		public boolean isCommand(String[] args){
			if(args[0].toLowerCase().startsWith("rtimer") && args.length >= 4){
				return true;
			}
			return false;
		}
		public String getInputStringFromArgs(String[] args){
			String inputString = "";
			
			for(int i = 3; i < args.length; i++){
				inputString += args[i]+" ";
			}
			
			return inputString.trim();
		}
		public boolean isAuthorized(String sender){
			return true;
		}
		public void execute(String[] args) {
			
			TimerInfo timer;
			
			if(!IRCBot.getInstance().isConnected()){
				timer = new TimerInfo(args[1], args[2], getInputStringFromArgs(args), true);						
			}else{
				timer = new TimerInfo(args[1], args[2], getInputStringFromArgs(args),
						Communicator.getInstance().getSender(),
						Communicator.getInstance().getChannel(), true);
			}
			
			//Check if timer thread started correctly
			if(timer.isTimerRunning){
				TimerInfoContainer.getInstance().setTimer(timer);
			}
		}
	},
	HELP {
		public boolean isCommand(String[] args){
			if(args[0].toLowerCase().startsWith("help")){
				return true;
			}
			return false;
		}
		public String getInputStringFromArgs(String[] args){
			String inputString = "";
			
			for(int i = 2; i < args.length; i++){
				inputString += args[i]+" ";
			}
			
			return inputString.trim();
		}
		public boolean isAuthorized(String sender){
			return true;
		}
		public void execute(String[] args) {
			Communicator.getInstance().handleOutput("https://github.com/jnsknn/java-cli-utils/blob/master/README.md");
		}
	};
}