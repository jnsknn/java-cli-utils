# CLI Utils

_Archived_

Java Maven project for learning purposes where I try and do different things with Java. The main program contains sub programs which can be executed with right commands and arguments.

# Commands

Arguments are separated with space.

- ?calculate [arg] calculates result from given string using JavaScript engine
- ?ufo [arg...arg] creates weird name based on given strings
- ?irc [arg(BotName) arg(server) arg(#Channel)] connects to irc
- ?timer [arg((int)time (char)h/m/s) arg(Timer name)...arg(Timer name)] sets timer. Timers are saved to timerinfos.json when adding or removing timers
- ?ptimer same as timer but it sends reminders in tell messages if used when IRCBot is online
- ?rtimer [arg((int)time (char)h/m/s) arg((int)delay (char)h/m/s) arg(Timer name)...arg(Timer name)] sets repeating timer
- ?timer with no arguments lists all set timers
- ?rmtimer [arg((int)id)] removes timer
- ?exit disconnects irc if bot is connected to a server. Quits the main program if bot is offline
- ?help posts link to this file
- ?playlist shows youtube playlist from posted youtube links
- ?clrplaylist clears playlist

# Examples

- Input: `?calculate 1+1*(123-5.5)/43`

	- Output: 1+1*(123-5.5)/43 = 3.7325581395348837

- Input: `?ufo Jonne Niskanen`
	
	- Output: Fynni Semropow

- Input: `?irc Jbot irc.elisa.fi #CHANNELNAME`

	- Output: Connecting to irc.elisa.fi...

You can also speak to channel when bot is connected by typing: #CHANNELNAME hello

- Input: `?timer 2h20m30s go to work`
	
	- Output: Your timer [go to work] has been set for 2 hours and 20 minutes and 30 seconds!
	- Output: Your timer [go to work] has 1 hour left
	- Output: Your timer [go to work] has 30 minutes left
	- Output: Your timer [go to work] has 5 minutes left
	- Output: Your timer [go to work] has 1 minute left
	- Output: Your timer [go to work] has 30 seconds left
	- Output: Your timer [go to work] has finished!

You can make timer repeating by using command ?rtimer instead of regular ?timer

- Input: `?rtimer 10m 1m stand up!`

	- Output: Your timer [1] [stand up!] has been scheduled to repeat every 10 minutes in 1 minute!

- Input: `?timer`

	- Output: >>Timer [1] [go to work] for You has 1 hour and 58 minutes and 16 seconds remaining!<<>>Timer [2] [food!] for You has 8 minutes and 34 seconds remaining!<<>>Timer [3] [check oven] for You has 59 minutes and 55 seconds remaining!<<
		
- Input: `?rmtimer 1`

	- Output: You, your timer go to work has been removed!

- Input: `?help`

	- Output: https://github.com/jnsknn/java-cli-utils/blob/master/README.md
	
You can make playlist by posting youtube links into chat
	
- Input: `Check this video https://www.youtube.com/watch?v=oSOd8lnGfcE this is a cool video too https://www.youtube.com/watch?v=XmLSeuJE_Ds this is awesome video https://www.youtube.com/watch?v=N4pj7RByIeA`

- Input: `?playlist`

	- Output: https://www.youtube.com/watch_videos?video_ids=oSOd8lnGfcE,XmLSeuJE_Ds,N4pj7RByIeA
	
-Input: `?clrplaylist`

	- Output: Playlist cleared!
