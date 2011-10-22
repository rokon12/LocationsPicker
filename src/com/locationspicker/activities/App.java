package com.locationspicker.activities;

import com.locationspicker.util.DBManager;
import com.locationspicker.util.KMLManager;
import com.locationspicker.util.SQLiteManager;

import android.app.Application;

public class App extends Application {
	
	/******************************************************************************
	** To change the way you keep your data, uncomment the respective code line. **
	** For now you could manage your data with a MySQL, SQLite and a KML file. 	 **
	** 																			 **
	** You can also implement your own way/class. To do that you need to	     **
	** implement the follow methods in your class and initialize it here:		 **
	** - getPoints() - returns all the points to show on map				 	 **
	** - addPoint(Object) - adds a new point									 **
	** - deletePoints() - deletes all added points								 **
	** - open() and close() - These two methods are normally used to open and 	 **
	**	 close managers. If you think there's no need, just implement	 		 **
	**	 them in blank.															 **		
	******************************************************************************/ 
	
	public KMLManager source = new KMLManager();
	//public DBManager source = new DBManager(this);
	//public SQLiteManager source = new SQLiteManager(this);
 
	@Override
    public void onCreate() {
        super.onCreate();
        source.open();
    }
}

