About
========================
In order of other project that I'm working on, I decided to provide a sample of an android application that simply saves locations. This is a normally task of many applications so, my intention was to create an easy and generic way to do that. You could keep your information in a *MySQL* database, *SQLite* database, or *KML file*. 
To enable the user to implement other ways to keep information, the main code is independent of the way you manage locations. You just need to follow some rules.
If you want to save more than a Location, fork this project and adjust the code for that. I think it will be easy. I will be glad if you notice any problems or code improvements.
The layout is also very simple, a bar with a *add button*, a *map* and a few menu options.

Using
========================
As I said, now, you could make use of this application using a *MySQL* database , *SQLite* database and a *KML* file to manage your information. To define that, you need to comment/uncomment the respective instance in *App.java* class.
In case of KML management, a KML file will be created in your device, and you will be able to use your application being offline. After you could upload the file to *Google Earth* to check your saved locations.

Implementing a new info manager
========================
To implement your own way to manage information, and work with the main code, your class will need 5 main methods:

* `List<Object> getPoints()` - returns all the points to show on map	
* `void addPoint(Object)` - adds a new point	
* `void deletePoints()` - deletes all added points
* `void open()` and `void close()` - these two methods are normally used to open/start and close/finish managers. If you think there's no need, just implement them in blank.
	
Android Support
========================
Android 2.2 (tested) or above.
