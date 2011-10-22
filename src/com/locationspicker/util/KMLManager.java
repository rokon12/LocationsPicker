package com.locationspicker.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlSerializer;

import android.location.Location;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;

/*****
 * 
 * @author Diogo Bernardino
 * This class manage the information between the application and a KML file.
 *
 *****/

public class KMLManager {
	
	File kmlFile;
	List<Location> points;
	
    public KMLManager(){
    	points = new LinkedList<Location>();
		kmlFile = new File(Environment.getExternalStorageDirectory()+"/locations_picker.kml");
		
        try{
        	if(!kmlFile.exists())
        		kmlFile.createNewFile();
        }catch(IOException e){
        	Log.e("IOException", "exception in createNewFile() method");
        	e.printStackTrace();
        }
    }
    
    public void open(){}

	public List<Location> getPoints(){
		points = new LinkedList<Location>();
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(kmlFile);
			doc.getDocumentElement().normalize();
			NodeList nodeList = doc.getElementsByTagName("Placemark");
			for (int i = 0; i < nodeList.getLength(); i++){
				Node node = nodeList.item(i);
				Element placemarkElem = (Element) node;
				NodeList list = placemarkElem.getElementsByTagName("Point");
				Element pointElement = (Element) list.item(0);
				list = pointElement.getElementsByTagName("coordinates");

				String l = list.item(0).getTextContent();
				String[] al = l.split(",");
				Location location = Util.toLocation(Double.parseDouble(al[1]),Double.parseDouble(al[0]));
				points.add(location);
			}
			return points;
		} catch (Exception e) {
        	Log.e("Exception", "exception in getPoint()");
			e.printStackTrace();
		}
		return points;
    }
    
    public void addPoint(Location location){
    	points.add(location);
    }
    
    public void close(){
        FileOutputStream fileos = null;       	
        try{
        	fileos = new FileOutputStream(kmlFile);
        }catch(FileNotFoundException e){
        	Log.e("FileNotFoundException", "Can't create FileOutputStream");
        	e.printStackTrace();
        }
        XmlSerializer serializer = Xml.newSerializer();
        try {
			serializer.setOutput(fileos, "UTF-8");
			serializer.startDocument(null, Boolean.valueOf(true)); 
			serializer.startTag(null, "kml");
			serializer.attribute(null, "xmlns", "http://www.opengis.net/kml/2.2");
			serializer.startTag(null, "Document");
			for(int i = 0; i < points.size(); i++){
				serializer.startTag(null, "Placemark");
					serializer.startTag(null, "name");
						serializer.text("Location"+i);
					serializer.endTag(null, "name");
					serializer.startTag(null, "Point");
						serializer.startTag(null, "coordinates");
							serializer.text(Double.toString(points.get(i).getLongitude())+","+Double.toString(points.get(i).getLatitude())+",0");
						serializer.endTag(null, "coordinates");
					serializer.endTag(null, "Point");
				serializer.endTag(null, "Placemark");
			}
			serializer.endTag(null, "Document");
			serializer.endTag(null, "kml");
			serializer.endDocument();
			serializer.flush();
			fileos.close();
		} catch (Exception e) {
			Log.e("Exception","Error occurred while saving xml file");
			e.printStackTrace();
		}
    }

	public void deletePoints() {
    	points = new LinkedList<Location>();
	}
    
}
