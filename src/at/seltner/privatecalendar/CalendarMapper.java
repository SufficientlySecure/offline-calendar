/**
 *  Private Calendar allows you to add private calendars to Android's
 *  Calendar Storage.
 *  Copyright (C) 2012  Harald Seltner <h.seltner@gmx.at>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.seltner.privatecalendar;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;

public class CalendarMapper {

	private static final String ACCOUNT_NAME = "private";
	private static final String INT_NAME_PREFIX = "priv";

	// Projection array. Creating indices for this array instead of doing
	// dynamic lookups improves performance.
	private static final String[] EVENT_PROJECTION = new String[] {
			Calendars._ID, // 0
			Calendars.CALENDAR_DISPLAY_NAME, // 1
			Calendars.CALENDAR_COLOR // 2
	};

	// The indices for the projection array above.
	private static final int PROJECTION_ID_INDEX = 0;
	private static final int PROJECTION_DISPLAY_NAME_INDEX = 1;
	private static final int PROJECTION_COLOR_INDEX = 2;

	private static Uri buildCalUri() {
		return CalendarContract.Calendars.CONTENT_URI
				.buildUpon()
				.appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
				.appendQueryParameter(Calendars.ACCOUNT_NAME, ACCOUNT_NAME)
				.appendQueryParameter(Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL)
				.build();
	}

	private static ContentValues buildContentValues(Calendar calendar) {
		String dispName = calendar.getName();
		String intName = INT_NAME_PREFIX + dispName;
		final ContentValues cv = new ContentValues();
		cv.put(Calendars.ACCOUNT_NAME, ACCOUNT_NAME);
		cv.put(Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
		cv.put(Calendars.NAME, intName);
		cv.put(Calendars.CALENDAR_DISPLAY_NAME, dispName);
		cv.put(Calendars.CALENDAR_COLOR, calendar.getColor());
		cv.put(Calendars.CALENDAR_ACCESS_LEVEL, Calendars.CAL_ACCESS_OWNER);
		cv.put(Calendars.OWNER_ACCOUNT, ACCOUNT_NAME);
//		cv.put(Calendars.VISIBLE, 1);
		cv.put(Calendars.SYNC_EVENTS, 1);
		return cv;
	}

	public static List<Calendar> fetchCalendars(ContentResolver cr) {
		ArrayList<Calendar> calendars = new ArrayList<Calendar>();
		
		// Run query
		Cursor cur = null;
		Uri uri = Calendars.CONTENT_URI;   
		String selection = "((" + Calendars.ACCOUNT_NAME + " = ?) AND (" 
		                        + Calendars.ACCOUNT_TYPE + " = ?))";
		String[] selectionArgs = new String[] {ACCOUNT_NAME, CalendarContract.ACCOUNT_TYPE_LOCAL}; 
		// Submit the query and get a Cursor object back. 
		cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);
		
		// Use the cursor to step through the returned records
		while (cur.moveToNext()) {
		    long id = 0;
		    String name = null;
		    int color;
		    
		    // Get the field values
		    id = cur.getLong(PROJECTION_ID_INDEX);
		    name = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
		    color = cur.getInt(PROJECTION_COLOR_INDEX);

		    calendars.add(new Calendar(id, name, color));
		}

		return calendars;
	}

	public static void addCalendar(Calendar calendar, ContentResolver cr) {
		if (calendar == null)
			throw new IllegalArgumentException();

		final ContentValues cv = buildContentValues(calendar);

		Uri calUri = buildCalUri();
		cr.insert(calUri, cv);
		// return result.toString();
	}

	/**
	 * @return true iff exactly one row is deleted
	 */
	public static boolean deleteCalendar(Calendar calendar, ContentResolver cr) {
		if (calendar == null)
			throw new IllegalArgumentException();
		
		Uri calUri = ContentUris
				.withAppendedId(buildCalUri(), calendar.getId());
		return cr.delete(calUri, null, null) == 1;
	}
	
	public static void updateCalendar(Calendar oldCal, Calendar newCal, ContentResolver cr) {
		Uri calUri = ContentUris
				.withAppendedId(buildCalUri(), oldCal.getId());
		ContentValues cv = buildContentValues(newCal);
		cr.update(calUri, cv, null, null);
	}
}
