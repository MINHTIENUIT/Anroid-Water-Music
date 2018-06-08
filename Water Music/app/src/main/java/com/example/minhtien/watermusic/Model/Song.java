package com.example.minhtien.watermusic.Model;

public class Song{
	private String name;

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	@Override
 	public String toString(){
		return 
			"Song{" + 
			"name = '" + name + '\'' + 
			"}";
		}
}
