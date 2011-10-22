package com.locationspicker.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

/*****
 * 
 * @author Diogo Bernardino
 * This class manage the information between the application and a MySQL database.
 *
 *****/

public class DBManager {

	private static String IP = "HOSTIP:PORT"; //example: http://34.34.34.34:80
	private static String WEBSERVICE_FILEPATH = "FILEPATH"; //example: /locationspicker/webservice.php
	
    private InputStream is;
	private HttpClient httpClient;
	private HttpPost httpPost;
	private ArrayList<NameValuePair> nameValuePairs;
	private Handler mHandler;
	
	public DBManager(final Context context){
		nameValuePairs = new ArrayList<NameValuePair>();
	       
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
					case 0: Toast.makeText(context, "No locations received", Toast.LENGTH_SHORT).show(); break;
				}
			}
		};
		
		try{
			httpClient = new DefaultHttpClient();
			httpPost = new HttpPost(IP+WEBSERVICE_FILEPATH);
		}catch(Exception e){
			Message msg = Message.obtain();
			msg.setTarget(mHandler);
			msg.what = 0;
			msg.sendToTarget();
			Log.e("log_server", "Host Fail ");
			e.printStackTrace();
		}
	}
	
	public void open(){}
	
	public void addPoint(Location location) {
		nameValuePairs.add(new BasicNameValuePair("type","set"));
        nameValuePairs.add(new BasicNameValuePair("lat", Double.toString(location.getLatitude())));
        nameValuePairs.add(new BasicNameValuePair("lng", Double.toString(location.getLongitude())));
        try{
        	httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        	HttpResponse response = httpClient.execute(httpPost); 
        	HttpEntity entity = response.getEntity();
        	is = entity.getContent();
        }catch(Exception e){
        	Log.e("log_server", "addPoint(Location) Failed ");
        	e.printStackTrace();
        }
        nameValuePairs.clear();
	}

	public List<Location> getPoints(){
        StringBuilder sb = new StringBuilder();
        nameValuePairs.add(new BasicNameValuePair("type","get"));

        //Connect to server
        try{
        	//Select
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpClient.execute(httpPost); 
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
            nameValuePairs.clear();
            Log.e("log_server", "Select Succeded ");

            //Response
        	BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
            String line = null;
            while ((line = reader.readLine()) != null)
            	sb.append(line + "\n");
            is.close();

            //Parse JSON
            JSONArray array = new JSONArray(sb.toString());
            List<Location> list = new LinkedList<Location>();
    		for(int i = 0; i < array.length(); i++)
    			list.add(Util.toLocation(array.getJSONObject(i).getDouble("lat"), array.getJSONObject(i).getDouble("lng")));
    		return list;
        	
        }catch(Exception e){
        	nameValuePairs.clear();
		    Message msg = Message.obtain();
		    msg.setTarget(mHandler);
		    msg.what = 0;
		    msg.sendToTarget();
		    Log.e("log_server", "getPoints() Failed "+e.toString());
		    e.printStackTrace();
        	return null;  	
        }
	}

	public void close() {}

	public void deletePoints() {
		nameValuePairs.add(new BasicNameValuePair("type","del"));
        try{
        	httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        	HttpResponse response = httpClient.execute(httpPost); 
        	HttpEntity entity = response.getEntity();
        	is = entity.getContent();
        }catch(Exception e){
        	Log.e("log_server", "deletePoints() Failed ");
        	e.printStackTrace();
        }
        nameValuePairs.clear();
	}
	
}
