package client; 

/**
 * TODO description
 */
public   class  SpamFilterAll  implements SpamFilterIF {
	
	public String spamFilter  (String message){
		 String[] spamWords = {"Porn", "porn", "Lottery", "lottery", "Click Here", "click here","100% free"};
		 for (int i=0; i<spamWords.length; i++)
	     {
			 message = message.replaceAll(spamWords[i], "");
	     }
		 return message;
	 }


}
