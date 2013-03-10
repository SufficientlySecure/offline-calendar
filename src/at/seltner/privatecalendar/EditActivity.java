/**
 *  Copyright (C) 2012  Dominik Schürmann <dominik@dominikschuermann.de>
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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import at.seltner.colorpicker.ColorPickerDialog;
import at.seltner.colorpicker.OnColorCancelListener;
import at.seltner.colorpicker.OnColorChoosenListener;

public class EditActivity extends Activity {

    public static final String INTENT_CAL_DATA = "cal_data";

    private final static int DEFAULT_COLOR = Color.rgb(100, 100, 200);

    private int selectedColor;
    private boolean edit;
    private Calendar originalCalendar;

    private EditText displayText;
    private View colorView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit);

        displayText = (EditText) findViewById(R.id.edit_activity_text_cal_name);
        colorView = (View) findViewById(R.id.edit_activity_view_color);

        // check if add new or edit existing
        Intent intent = getIntent();
        edit = intent.hasExtra(INTENT_CAL_DATA);
        if (edit) {
            // fetch the existing calendar data and display for editing
            originalCalendar = (Calendar) intent.getSerializableExtra(INTENT_CAL_DATA);
            setSelectedColor(originalCalendar.getColor());
            displayText.setText(originalCalendar.getName());
        } else {
            setSelectedColor(DEFAULT_COLOR);
        }
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
        case R.id.menu_edit_delete:
            if (edit)
                confirmAndDeleteCalendar();
            else
                finish();
            return true;
        case R.id.menu_edit_save:
            if (edit)
                updateCalendar();
            else
                addCalendar();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private void showMessageAndFinish(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditActivity.this.finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void setSelectedColor(int color) {
        selectedColor = color;
        colorView.setBackgroundColor(color);
    }

    private void addCalendar() {
        Calendar calendar = new Calendar(displayText.getText().toString(), selectedColor);
        try {
            CalendarMapper.addCalendar(calendar, getContentResolver());
            showMessageAndFinish(getText(R.string.edit_activity_message_added).toString());
        } catch (IllegalArgumentException e) {
            showMessageAndFinish(getText(R.string.edit_activity_error_add) + e.getMessage());
        }
    }

    private void updateCalendar() {
        CalendarMapper.updateCalendar(originalCalendar, new Calendar(displayText.getText()
                .toString(), selectedColor), getContentResolver());
        showMessageAndFinish(getText(R.string.edit_activity_message_saved).toString());
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
            showMessageAndFinish(getText(R.string.edit_activity_message_deleted).toString());
        else
            showMessageAndFinish(getText(R.string.edit_activity_error_delete).toString());
    }

    public void handleClickPickColor(View view) {

        OnColorCancelListener cancel = new OnColorCancelListener() {
            @Override
            public void colorCancel(int initialColor) {
                // nothing to do
            }
        };

        OnColorChoosenListener choosen = new OnColorChoosenListener() {
            @Override
            public void colorChoosen(int color) {
                setSelectedColor(color);
            }
        };

        String title = getText(R.string.pick_color).toString();
        ColorPickerDialog cpd = new ColorPickerDialog(this, selectedColor, title, choosen, cancel);
        cpd.show();

        // AndroidExampleColorPickerDialog cpd = new AndroidExampleColorPickerDialog(this,
        // new OnColorChangedListener() {
        //
        // @Override
        // public void colorChanged(int color) {
        // setSelectedColor(color);
        // }
        // }, selectedColor);
        // cpd.show();

        // AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, selectedColor,
        // new OnAmbilWarnaListener() {
        // @Override
        // public void onOk(AmbilWarnaDialog dialog, int color) {
        // setSelectedColor(color);
        // }
        //
        // @Override
        // public void onCancel(AmbilWarnaDialog dialog) {
        // // cancel was selected by the user
        // }
        // });
        //
        // dialog.show();
    }
}