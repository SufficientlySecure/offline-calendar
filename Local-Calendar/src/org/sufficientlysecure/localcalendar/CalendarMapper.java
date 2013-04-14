/**
 *  Copyright (C) 2013  Dominik Schürmann <dominik@dominikschuermann.de>
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

package org.sufficientlysecure.localcalendar;

import java.util.ArrayList;
import java.util.List;

import org.sufficientlysecure.localcalendar.util.AccountHelper;
import org.sufficientlysecure.localcalendar.util.Constants;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.util.Log;

@SuppressLint("NewApi")
public class CalendarMapper {
    private static final boolean BEFORE_JELLYBEAN = android.os.Build.VERSION.SDK_INT < 16;

    public static final String ACCOUNT_NAME = "Local Calendar";
    /*
     * Use ACCOUNT_TYPE_LOCAL only on Android >= 4.1
     * 
     * see http://code.google.com/p/android/issues/detail?id=27474
     */
    private static final String ACCOUNT_TYPE = BEFORE_JELLYBEAN ? "org.sufficientlysecure.localcalendar.account"
            : CalendarContract.ACCOUNT_TYPE_LOCAL;
    public static final String CONTENT_AUTHORITY = "com.android.calendar";
    public static final Account ACCOUNT = new Account(ACCOUNT_NAME, ACCOUNT_TYPE);

    private static final String INT_NAME_PREFIX = "local_";

    // Projection array. Creating indices for this array instead of doing
    // dynamic lookups improves performance.
    private static final String[] EVENT_PROJECTION = new String[] { Calendars._ID, // 0
            Calendars.CALENDAR_DISPLAY_NAME, // 1
            Calendars.CALENDAR_COLOR // 2
    };

    // The indices for the projection array above.
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 1;
    private static final int PROJECTION_COLOR_INDEX = 2;

    private static Uri buildCalUri() {
        return CalendarContract.Calendars.CONTENT_URI.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(Calendars.ACCOUNT_NAME, ACCOUNT_NAME)
                .appendQueryParameter(Calendars.ACCOUNT_TYPE, ACCOUNT_TYPE).build();
    }

    private static ContentValues buildContentValues(Calendar calendar) {
        String dispName = calendar.getName();
        String intName = INT_NAME_PREFIX + dispName;
        final ContentValues cv = new ContentValues();
        cv.put(Calendars.ACCOUNT_NAME, ACCOUNT_NAME);
        cv.put(Calendars.ACCOUNT_TYPE, ACCOUNT_TYPE);
        cv.put(Calendars.NAME, intName);
        cv.put(Calendars.CALENDAR_DISPLAY_NAME, dispName);
        cv.put(Calendars.CALENDAR_COLOR, calendar.getColor());
        cv.put(Calendars.CALENDAR_ACCESS_LEVEL, Calendars.CAL_ACCESS_OWNER);
        cv.put(Calendars.OWNER_ACCOUNT, ACCOUNT_NAME);
        cv.put(Calendars.VISIBLE, 1);
        cv.put(Calendars.SYNC_EVENTS, 1);
        return cv;
    }

    public static List<Calendar> fetchCalendars(ContentResolver cr) {
        ArrayList<Calendar> calendars = new ArrayList<Calendar>();

        // Run query
        Cursor cur = null;
        Uri uri = Calendars.CONTENT_URI;
        String selection = "((" + Calendars.ACCOUNT_NAME + " = ?) AND (" + Calendars.ACCOUNT_TYPE
                + " = ?))";
        String[] selectionArgs = new String[] { ACCOUNT_NAME, ACCOUNT_TYPE };
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

    public static void addCalendar(Context context, final Calendar calendar,
            final ContentResolver cr) {
        if (calendar == null)
            throw new IllegalArgumentException();

        /*
         * On Android < 4.1 create an Account for our calendars. Using only local calendars cause
         * these bugs:
         * 
         * - On Android < 4.1: Selecting "Calendars to sync" in the calendar app it crashes with
         * NullPointerException. see http://code.google.com/p/android/issues/detail?id=27474
         * 
         * - On Android <= 2.3: Opening the calendar app will ask to create an account first even
         * when local calendars are present
         */
        if (BEFORE_JELLYBEAN) {
            AccountHelper accHelper = new AccountHelper(context);
            Bundle result = accHelper.addAccount();

            if (result != null) {
                if (result.containsKey(AccountManager.KEY_ACCOUNT_NAME)) {
                    Log.d(Constants.TAG, "Account was added!");

                    // wait until account is added asynchronously
                    try {
                        Thread.sleep(2000);
                        Log.d(Constants.TAG, "after wait...");
                    } catch (InterruptedException e) {
                        Log.e(Constants.TAG, "InterruptedException", e);
                    }
                } else {
                    Log.e(Constants.TAG,
                            "Account was not added! result did not contain KEY_ACCOUNT_NAME!");
                }
            } else {
                Log.e(Constants.TAG, "Account was not added! result was null!");
            }

        }

        // Add calendar
        final ContentValues cv = buildContentValues(calendar);
        Uri calUri = buildCalUri();
        cr.insert(calUri, cv);
    }

    /**
     * @return true iff exactly one row is deleted
     */
    public static boolean deleteCalendar(Calendar calendar, ContentResolver cr) {
        if (calendar == null)
            throw new IllegalArgumentException();

        Uri calUri = ContentUris.withAppendedId(buildCalUri(), calendar.getId());
        return cr.delete(calUri, null, null) == 1;
    }

    public static void updateCalendar(Calendar oldCal, Calendar newCal, ContentResolver cr) {
        Uri calUri = ContentUris.withAppendedId(buildCalUri(), oldCal.getId());
        ContentValues cv = buildContentValues(newCal);
        cr.update(calUri, cv, null, null);
    }
}
