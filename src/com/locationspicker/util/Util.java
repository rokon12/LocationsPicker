package com.locationspicker.util;

import android.location.Location;
import com.google.android.maps.GeoPoint;

/*****
 * 
 * @author Diogo Bernardino
 * This class has a few methods that help me to reuse frequent code.
 *
 */

public class Util {
	
	public static final GeoPoint toGeoPoint(Location location){
		return new GeoPoint((int) (location.getLatitude() * 1e6), (int) (location.getLongitude() * 1e6));
	}
	
	public static final Location toLocation(GeoPoint location){
		Location loc = new Location("");
		loc.setLatitude(location.getLatitudeE6() / 1e6);
		loc.setLongitude(location.getLongitudeE6() / 1e6);
		return loc;
	}
	
	public static final Location toLocation(double lat, double longue){
		Location loc = new Location("");
		loc.setLatitude(lat);
		loc.setLongitude(longue);
		return loc;
	}
	
}
