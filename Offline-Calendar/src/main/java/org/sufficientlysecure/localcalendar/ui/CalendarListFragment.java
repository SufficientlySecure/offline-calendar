/*
 * Copyright (C) 2013 Dominik Schürmann <dominik@dominikschuermann.de>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.sufficientlysecure.localcalendar.ui;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.ListFragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.app.LoaderManager;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import org.sufficientlysecure.localcalendar.CalendarController;
import org.sufficientlysecure.localcalendar.R;

@SuppressLint("NewApi")
public class CalendarListFragment extends ListFragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private CalendarListViewAdapter mAdapter;

    private class CalendarListViewAdapter extends CursorAdapter {

        private LayoutInflater mInflater;

        public CalendarListViewAdapter(Context context, Cursor c, boolean autoRequery) {
            super(context, c, autoRequery);

            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            return mInflater.inflate(R.layout.list_item, null);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            String displayName = cursor.getString(CalendarController.PROJECTION_DISPLAY_NAME_INDEX);
            int color = cursor.getInt(CalendarController.PROJECTION_COLOR_INDEX);

            TextView displayNameView = (TextView) view.findViewById(R.id.list_item_text_cal_name);
            View colorView = (View) view.findViewById(R.id.list_item_view_color);

            if (displayName != null) {
                displayNameView.setText(displayName);
            }
            colorView.setBackgroundColor(color);
        }
    }

    /**
     * Define Adapter and Loader on create of Activity
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getListView().setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // edit calendar
                Intent intent = new Intent(getActivity(), EditActivity.class);
                intent.setData(ContentUris.withAppendedId(CalendarContract.Calendars.CONTENT_URI, id));
                startActivity(intent);
            }
        });

        // Give some text to display if there is no data.
        setEmptyText(getString(R.string.main_activity_empty_list));

        mAdapter = new CalendarListViewAdapter(getActivity(), null, true);

        setListAdapter(mAdapter);

        // Start out with a progress indicator.
        setListShown(false);

        // Prepare the loader. Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created. This
        // sample only has one Loader, so we don't care about the ID.
        Uri baseUri = CalendarContract.Calendars.CONTENT_URI;

        String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
                + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?))";
        String[] selectionArgs = new String[]{CalendarController.ACCOUNT_NAME, CalendarController.ACCOUNT_TYPE};
        String sortOrder = CalendarContract.Calendars.CALENDAR_DISPLAY_NAME + " asc";

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(getActivity(), baseUri, CalendarController.PROJECTION, selection, selectionArgs, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in. (The framework will take care of closing the
        // old cursor once we return.)
        mAdapter.swapCursor(data);

        // The list should now be shown.
        if (isResumed()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed. We need to make sure we are no
        // longer using it.
        mAdapter.swapCursor(null);
    }
}
