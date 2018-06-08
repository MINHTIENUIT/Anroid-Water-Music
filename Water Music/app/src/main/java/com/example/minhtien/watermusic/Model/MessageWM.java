package com.example.minhtien.watermusic.Model;

public class MessageWM{
	private String type;
	private String message;

	public void setType(String type){
		this.type = type;
	}

	public String getType(){
		return type;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}

	@Override
 	public String toString(){
		return 
			"MessageWM{" + 
			"type = '" + type + '\'' + 
			",message = '" + message + '\'' + 
			"}";
		}
}
