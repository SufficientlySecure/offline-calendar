/*
 * Copyright (C) 2013-2016 Dominik Schürmann <dominik@dominikschuermann.de>
 * Copyright (C) 2012 Harald Seltner <h.seltner@gmx.at>
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
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SVBar;

import org.sufficientlysecure.localcalendar.CalendarController;
import org.sufficientlysecure.localcalendar.R;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class EditActivity extends AppCompatActivity {
    boolean edit = false;

    private TextInputLayout displayNameEditTextLyout;
    private EditText displayNameEditText;
    ColorPicker colorPicker;

    Toolbar mToolbar;

    LinearLayout toolbar2;
    ImageButton deleteButton;
    ImageButton importExportButton;
    ImageButton newEventButton;

    long mCalendarId;

    public static final String ICAL_LOAD_CALENDAR = "org.sufficientlysecure.ical.LOAD_CALENDAR";
    public static final String ICAL_EXTRA_CALENDAR_ID = "calendarId";

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean("useDarkTheme", false)) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.LightTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_activity);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        setFullScreenDialogDoneClose(R.string.edit_activity_save, new OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        }, new OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });

        toolbar2 = (LinearLayout) findViewById(R.id.toolbar2);
        displayNameEditText = (EditText) findViewById(R.id.edit_activity_text_cal_name);
        displayNameEditTextLyout = (TextInputLayout) findViewById(R.id.edit_activity_text_cal_name_layout);
        colorPicker = (ColorPicker) findViewById(R.id.edit_activity_color_picker);
        SVBar svBar = (SVBar) findViewById(R.id.edit_activity_svbar);

        colorPicker.addSVBar(svBar);

        // check if add new or edit existing
        Intent intent = getIntent();
        Uri calendarUri = intent.getData();
        if (calendarUri != null) {
            edit = true;
        }

        if (edit) {
            // edit calendar
            setTitle(R.string.edit_activity_name_edit);

            Cursor cur = getContentResolver().query(calendarUri, CalendarController.PROJECTION, null, null, null);
            mCalendarId = ContentUris.parseId(calendarUri);
            try {
                if (cur.moveToFirst()) {
                    String displayName = cur.getString(CalendarController.PROJECTION_DISPLAY_NAME_INDEX);
                    int color = cur.getInt(CalendarController.PROJECTION_COLOR_INDEX);

                    // display for editing
                    displayNameEditText.setText(displayName);
                    setColor(color);
                }
            } finally {
                if (cur != null && !cur.isClosed()) {
                    cur.close();
                }
            }
        } else {
            // new calendar
            setTitle(R.string.edit_activity_name_new);

            setColor(getResources().getColor(R.color.emphasis));
            // on calendar creation, set both center colors to new color
            colorPicker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {
                @Override
                public void onColorChanged(int color) {
                    colorPicker.setOldCenterColor(color);
                }
            });
        }

        newEventButton = (ImageButton) findViewById(R.id.edit_activity_new_event);
        importExportButton = (ImageButton) findViewById(R.id.edit_activity_import_export);
        deleteButton = (ImageButton) findViewById(R.id.edit_activity_delete);

        if (prefs.getBoolean("useDarkTheme", false)) {
            newEventButton.setImageResource(R.drawable.ic_event_white_24dp);
            importExportButton.setImageResource(R.drawable.ic_swap_vert_white_24dp);
            deleteButton.setImageResource(R.drawable.ic_delete_white_24dp);
        } else {
            newEventButton.setImageResource(R.drawable.ic_event_black_24dp);
            importExportButton.setImageResource(R.drawable.ic_swap_vert_black_24dp);
            deleteButton.setImageResource(R.drawable.ic_delete_black_24dp);
        }

        if (!edit) {
            toolbar2.setVisibility(View.GONE);
        }

        newEventButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addEvent();
            }
        });
        importExportButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                importExport();
            }
        });
        deleteButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                delete();
            }
        });

        // remove error when characters are entered
        displayNameEditText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                displayNameEditText.setError(null);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
    }

    /**
     * Inflate custom design to look like a full screen dialog, as specified in Material Design Guidelines
     * see http://www.google.com/design/spec/components/dialogs.html#dialogs-full-screen-dialogs
     */
    public void setFullScreenDialogDoneClose(int doneText, View.OnClickListener doneOnClickListener,
                                             View.OnClickListener cancelOnClickListener) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean("useDarkTheme", false)) {
            mToolbar.setNavigationIcon(R.drawable.ic_clear_white_24dp);
        } else {
            mToolbar.setNavigationIcon(R.drawable.ic_clear_black_24dp);
        }

        // Inflate the custom action bar view
        final LayoutInflater inflater = (LayoutInflater) getSupportActionBar().getThemedContext()
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        final View customActionBarView = inflater.inflate(R.layout.full_screen_dialog, null);

        TextView firstTextView = ((TextView) customActionBarView.findViewById(R.id.full_screen_dialog_done_text));
        firstTextView.setText(doneText);
        customActionBarView.findViewById(R.id.full_screen_dialog_done).setOnClickListener(
                doneOnClickListener);

        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setCustomView(customActionBarView, new ActionBar.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT,
                Gravity.END));
        mToolbar.setNavigationOnClickListener(cancelOnClickListener);
    }

    private void importExport() {
        Intent icalIntent = new Intent(ICAL_LOAD_CALENDAR);
        icalIntent.putExtra(ICAL_EXTRA_CALENDAR_ID, mCalendarId);
        try {
            startActivity(icalIntent);
        } catch (ActivityNotFoundException e) {
            ActivityNotFoundDialogFragment notFoundDialog = ActivityNotFoundDialogFragment
                    .newInstance(R.string.no_ical_title, R.string.no_ical_message,
                            "market://details?id=org.sufficientlysecure.ical", "iCal Import/Export");

            notFoundDialog.show(getSupportFragmentManager(), "notFoundDialog");
        }
    }

    private void addEvent() {
        try {
            /*
             * NOTE:
             * Setting CalendarContractWrapper.Events.CALENDAR_ID only works with
             * AOSP calendar app > 4.3, not Google's calendar app!
             */
            Intent intent = new Intent(Intent.ACTION_INSERT);
            intent.setPackage("com.android.calendar");
            intent.setData(CalendarContract.Events.CONTENT_URI);
            intent.putExtra(CalendarContract.Events.CALENDAR_ID, mCalendarId);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            try {
                // open calendar at today
                Calendar cal = GregorianCalendar.getInstance();

                Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
                builder.appendPath("time");
                ContentUris.appendId(builder, cal.getTimeInMillis());
                Intent intent = new Intent(Intent.ACTION_VIEW)
                        .setData(builder.build());
                startActivity(intent);
            } catch (ActivityNotFoundException ex) {
                Toast.makeText(this, "Not supported. Please open calendar manually!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void save() {
        if (displayNameEditText.getText().length() == 0) {
            displayNameEditTextLyout.setError(getString(R.string.edit_activity_error_empty_name));
        } else {
            displayNameEditTextLyout.setError(null);
            if (edit)
                updateCalendar();
            else
                addCalendar(EditActivity.this);
        }
    }

    private void delete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.edit_activity_really_delete_title)
                .setMessage(R.string.edit_activity_really_delete)
                .setCancelable(false)
                .setPositiveButton(R.string.edit_activity_delete_dialog_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteCalendar();
                    }
                }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void setColor(int color) {
        colorPicker.setColor(color);
        colorPicker.setNewCenterColor(color);
        colorPicker.setOldCenterColor(color);
    }

    private void showMessageAndFinish(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void addCalendar(Context context) {
        try {
            CalendarController.addCalendar(context, displayNameEditText.getText().toString(), colorPicker.getColor(), getContentResolver());
            finish();
        } catch (IllegalArgumentException e) {
            showMessageAndFinish(getString(R.string.edit_activity_error_add));
        }
    }

    private void updateCalendar() {
        CalendarController.updateCalendar(mCalendarId, displayNameEditText.getText()
                .toString(), colorPicker.getColor(), getContentResolver());
        finish();
    }

    private void deleteCalendar() {
        if (CalendarController.deleteCalendar(mCalendarId, getContentResolver())) {
            finish();
        } else {
            showMessageAndFinish(getString(R.string.edit_activity_error_delete));
        }
    }

}
