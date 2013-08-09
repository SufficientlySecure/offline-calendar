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

import com.larswerkman.colorpicker.OpacityBar;
import com.larswerkman.colorpicker.SVBar;
import org.sufficientlysecure.localcalendar.Calendar;
import org.sufficientlysecure.localcalendar.CalendarMapper;
import org.sufficientlysecure.localcalendar.R;

import com.larswerkman.colorpicker.ColorPicker;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class EditActivity extends Activity {

    public static final String INTENT_CAL_DATA = "cal_data";

    private final static int DEFAULT_COLOR = Color.RED;

    private boolean edit;
    private Calendar originalCalendar;

    private EditText displayText;
    ColorPicker colorPicker;
    private SVBar svBar;

    Button cancelButton;
    Button deleteButton;
    Button saveButton;

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit);

        if (android.os.Build.VERSION.SDK_INT >= 11) {
            ActionBar actionBar = this.getActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        displayText = (EditText) findViewById(R.id.edit_activity_text_cal_name);
        colorPicker = (ColorPicker) findViewById(R.id.edit_activity_color_picker);
        svBar = (SVBar) findViewById(R.id.edit_activity_svbar);

        cancelButton = (Button) findViewById(R.id.edit_activity_cancel);
        deleteButton = (Button) findViewById(R.id.edit_activity_delete);
        saveButton = (Button) findViewById(R.id.edit_activity_save);

        colorPicker.addSVBar(svBar);

        // check if add new or edit existing
        Intent intent = getIntent();
        edit = intent.hasExtra(INTENT_CAL_DATA);
        if (edit) {
            // fetch the existing calendar data and display for editing
            originalCalendar = (Calendar) intent.getSerializableExtra(INTENT_CAL_DATA);
            colorPicker.setColor(originalCalendar.getColor());
            colorPicker.setNewCenterColor(originalCalendar.getColor());
            colorPicker.setOldCenterColor(originalCalendar.getColor());
            displayText.setText(originalCalendar.getName());
        } else {
            colorPicker.setColor(DEFAULT_COLOR);
            colorPicker.setNewCenterColor(DEFAULT_COLOR);
            colorPicker.setOldCenterColor(DEFAULT_COLOR);
            // on calendar creation, set both center colors to new color
            colorPicker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {
                @Override
                public void onColorChanged(int color) {
                    colorPicker.setOldCenterColor(color);
                }
            });
        }

        cancelButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (edit) {
            deleteButton.setVisibility(View.VISIBLE);
        }
        deleteButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (edit)
                    confirmAndDeleteCalendar();
                else
                    finish();

            }
        });

        saveButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (edit)
                    updateCalendar();
                else
                    addCalendar(EditActivity.this);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            // app icon in Action Bar clicked; go home
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private void showMessageAndFinish(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditActivity.this.finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void addCalendar(Context context) {
        Calendar calendar = new Calendar(displayText.getText().toString(), colorPicker.getColor());

        try {
            CalendarMapper.addCalendar(context, calendar, getContentResolver());
            EditActivity.this.finish();
        } catch (IllegalArgumentException e) {
            showMessageAndFinish(getText(R.string.edit_activity_error_add) + e.getMessage());
        }
    }

    private void updateCalendar() {
        CalendarMapper.updateCalendar(originalCalendar, new Calendar(displayText.getText()
                .toString(), colorPicker.getColor()), getContentResolver());
        EditActivity.this.finish();
    }

    private void confirmAndDeleteCalendar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getText(R.string.edit_activity_really_delete).toString())
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

    private void deleteCalendar() {
        if (CalendarMapper.deleteCalendar(originalCalendar, getContentResolver()))
            EditActivity.this.finish();
        else
            showMessageAndFinish(getText(R.string.edit_activity_error_delete).toString());
    }

}