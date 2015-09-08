/*
 * Copyright (C) 2013 Dominik Schürmann <dominik@dominikschuermann.de>
 * Copyright (C) 2014 Harald Seltner <h.seltner@gmx.at>
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
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SVBar;

import org.sufficientlysecure.localcalendar.CalendarController;
import org.sufficientlysecure.localcalendar.R;
import org.sufficientlysecure.localcalendar.util.ActionBarHelper;

public class EditActivity extends FragmentActivity {
    boolean edit = false;

    private EditText displayNameEditText;
    ColorPicker colorPicker;
    private SVBar svBar;

    LinearLayout editButtons;
    Button deleteButton;
    Button importExportButton;
    Button cancelButton;
    Button saveButton;

    long mCalendarId;

    public static final String ICAL_LOAD_CALENDAR = "org.sufficientlysecure.ical.LOAD_CALENDAR";
    public static final String ICAL_EXTRA_CALENDAR_ID = "calendarId";

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit);

        displayNameEditText = (EditText) findViewById(R.id.edit_activity_text_cal_name);
        colorPicker = (ColorPicker) findViewById(R.id.edit_activity_color_picker);
        svBar = (SVBar) findViewById(R.id.edit_activity_svbar);

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

        // Based on Android version use ButtonBar on bottom or use custom Actionbar layout
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            if (edit) {
                ActionBarHelper.setDoneView(getActionBar(), new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        save();
                    }
                });
            } else {
                ActionBarHelper.setDoneCancelView(getActionBar(), new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                save();
                            }
                        }, new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                finish();
                            }
                        }
                );
            }
        } else {
            // Android < 3.0
            editButtons = (LinearLayout) findViewById(R.id.edit_activity_edit_buttons);
            deleteButton = (Button) findViewById(R.id.edit_activity_delete);
            importExportButton = (Button) findViewById(R.id.edit_activity_import_export);
            cancelButton = (Button) findViewById(R.id.edit_activity_cancel);
            saveButton = (Button) findViewById(R.id.edit_activity_save);

            if (edit) {
                editButtons.setVisibility(View.VISIBLE);
            }

            deleteButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    delete();
                }
            });
            importExportButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    importExport();
                }
            });
            cancelButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            saveButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    save();
                }
            });
        }

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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Show menu only in edit mode
        return edit;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_edit_cancel:
                finish();
                return true;
            case R.id.menu_edit_delete:
                delete();
                return true;
            case R.id.menu_edit_import_export:
                importExport();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

    private void save() {
        if (displayNameEditText.getText().length() == 0) {
            displayNameEditText.requestFocus();
            displayNameEditText.setError(getString(R.string.edit_activity_error_empty_name));
        } else {
            displayNameEditText.setError(null);
            if (edit)
                updateCalendar();
            else
                addCalendar(EditActivity.this);
        }
    }

    private void delete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.edit_activity_really_delete)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
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