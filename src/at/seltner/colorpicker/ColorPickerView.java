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
package at.seltner.colorpicker;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import at.seltner.privatecalendar.R;

public class ColorPickerView extends LinearLayout {

    private OnColorChoosenListener onColorChoosenListener;
    private OnColorCancelListener onColorCancelListener;

    private int initialColor;

    private SeekBar seekBarHue;
    private SeekBar seekBarSaturation;
    private SeekBar seekBarLightness;

    private View newColorView;
    private View oldColorView;

    private float hue;
    private float saturation;
    private float lightness;

    public ColorPickerView(Context context, int initialColor, OnColorChoosenListener choosen,
            OnColorCancelListener cancel) {
        super(context);
        this.initialColor = initialColor;
        onColorChoosenListener = choosen;
        onColorCancelListener = cancel;

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.colorpicker, null);

        seekBarHue = (SeekBar) view.findViewById(R.id.seekBarHue);
        seekBarSaturation = (SeekBar) view.findViewById(R.id.seekBarSaturation);
        seekBarLightness = (SeekBar) view.findViewById(R.id.seekBarLightness);

        oldColorView = view.findViewById(R.id.oldColor);
        newColorView = view.findViewById(R.id.newColor);

        // and so on for the rest of the buttons

        addView(view);

        int[] hueColors = new int[] { 0xFFFF0000, 0xFFFFFF00, 0xFF00FF00, 0xFF00FFFF, 0xFF0000FF,
                0xFFFF00FF, 0xFFFF0000 };
        GradientDrawable d = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
                hueColors);
        seekBarHue.setBackgroundDrawable(d);

        oldColorView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onColorCancelListener.colorCancel(ColorPickerView.this.initialColor);
            }
        });

        newColorView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onColorChoosenListener.colorChoosen(getSelectedColor());
            }
        });

        seekBarHue.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setHue(progress);
                updateColorView();
            }
        });
        seekBarSaturation.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setSaturation(progress);
                updateColorView();
            }
        });
        seekBarLightness.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setLightness(progress);
                updateColorView();
            }
        });

        setInitialColor(initialColor);
    }

    private void updateColorView() {
        int color = Color.HSVToColor(new float[] { hue, saturation, lightness });
        newColorView.setBackgroundColor(color);
    }

    private void setInitialColor(int color) {
        oldColorView.setBackgroundColor(color);
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hue = hsv[0];
        seekBarHue.setProgress((int) hue);
        saturation = hsv[1];
        seekBarSaturation.setProgress((int) (saturation * 100));
        lightness = hsv[2];
        seekBarLightness.setProgress((int) (lightness * 100));
        updateColorView();
        drawSaturationGradient();
        drawLightnessGradient();
    }

    private void setHue(float hue) {
        this.hue = (float) hue;
        drawSaturationGradient();
        drawLightnessGradient();
    }

    private void setSaturation(int saturation) {
        this.saturation = ((float) saturation) / 100;
        drawLightnessGradient();
    }

    private void drawSaturationGradient() {
        int[] colors = new int[2];
        colors[0] = Color.HSVToColor(new float[] { hue, 0, lightness });
        colors[1] = Color.HSVToColor(new float[] { hue, 1, lightness });
        GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors);
        seekBarSaturation.setBackgroundDrawable(gd);
    }

    private void setLightness(int lightness) {
        this.lightness = ((float) lightness) / 100;
        drawSaturationGradient();
    }

    private void drawLightnessGradient() {
        int[] colors = new int[2];
        colors[0] = Color.HSVToColor(new float[] { hue, saturation, 0 });
        colors[1] = Color.HSVToColor(new float[] { hue, saturation, 1 });
        GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors);
        seekBarLightness.setBackgroundDrawable(gd);
    }

    private int getSelectedColor() {
        return Color.HSVToColor(new float[] { hue, saturation, lightness });
    }
}