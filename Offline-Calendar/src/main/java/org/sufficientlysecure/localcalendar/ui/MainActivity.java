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

package org.sufficientlysecure.localcalendar.ui;

import java.util.List;

import org.sufficientlysecure.localcalendar.Calendar;
import org.sufficientlysecure.localcalendar.CalendarMapper;
import org.sufficientlysecure.localcalendar.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

    private class CalendarListViewAdapter extends ArrayAdapter<Calendar> {

        public CalendarListViewAdapter(Context context, int textViewResourceId,
                List<Calendar> objects) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.list_item, null);
            }

            Calendar cal = this.getItem(position);
            if (cal != null) {
                View colorView = (View) v.findViewById(R.id.list_item_view_color);
                TextView calName = (TextView) v.findViewById(R.id.list_item_text_cal_name);

                colorView.setBackgroundColor(cal.getColor());
                calName.setText(cal.getName());
            }

            return v;
        }

    }

    private ListView listView;
    private CalendarListViewAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        listView = (ListView) findViewById(R.id.privatecalendar_listview);
    }

    @Override
    protected void onResume() {
        loadCalendars();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.menu_main_add_calendar:
            showAddCalendarActivity();
            return true;
        case R.id.menu_main_about:
            showAbout();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private void showEditCalendar(int position) {
        Calendar cal = adapter.getItem(position);
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra(EditActivity.INTENT_CAL_DATA, cal);
        startActivity(intent);
    }

    private void loadCalendars() {
        List<Calendar> calendars = CalendarMapper.fetchCalendars(getContentResolver());
        adapter = new CalendarListViewAdapter(this, R.layout.list_item, calendars);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showEditCalendar(position);
            }
        });

    }

    private void showAddCalendarActivity() {
        // show edit activity with empty text field and add button
        Intent intent = new Intent(this, EditActivity.class);
        startActivity(intent);
    }

    private void showAbout() {
        SpannableString s = new SpannableString(getText(R.string.about));
        Linkify.addLinks(s, Linkify.ALL);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(s);
        AlertDialog alert = builder.create();
        alert.show();
        ((TextView) alert.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod
                .getInstance());
    }
}
