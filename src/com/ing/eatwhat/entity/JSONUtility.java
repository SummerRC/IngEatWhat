package com.ing.eatwhat.entity;

import java.io.StringReader;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.util.JsonReader;
import android.widget.TextView;

public class JSONUtility {
	private String str = "";
	public String version,url;
    public JSONUtility(String JSONData){
    	parseISON(JSONData);
    }
    @SuppressLint("NewApi")
    public void parseISON(String JSONData){
	 try{
	 JsonReader jr = new JsonReader(new StringReader(JSONData));
	 jr.beginArray();
	 while(jr.hasNext()){
		 jr.beginObject();
		 while(jr.hasNext()){	
			 String Tagname = jr.nextName();
			 if(Tagname.equals("version")){
				 version = jr.nextString();
			 }else{
				 if(Tagname.equals("url")){ 
					 url = jr.nextString();		
				 }
			 } 
		 }	
		 jr.endObject();
	 }
	 jr.endArray();
	 }catch(Exception e){
		 e.printStackTrace();
	 }
 }
 
}
