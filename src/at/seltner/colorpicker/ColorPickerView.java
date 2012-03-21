package at.seltner.colorpicker;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import at.seltner.privatecalendar.R;

public class ColorPickerView extends LinearLayout {
	
	private SeekBar seekBarHue;
	private SeekBar seekBarSaturation;
	private SeekBar seekBarLightness;
	
	private View newColorView;
	private View oldColorView;
	private GradientView hueGradient;
	private GradientView saturationGradient;
	private GradientView lightnessGradient;

	private float hue;
	private float saturation;
	private float lightness;

	public ColorPickerView(Context context, int initialColor) {
		super(context);

		LayoutInflater inflater = 
	        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View view = inflater.inflate(R.layout.colorpicker, null);

	    seekBarHue = (SeekBar) view.findViewById(R.id.seekBarHue);
	    seekBarSaturation = (SeekBar) view.findViewById(R.id.seekBarSaturation);
	    seekBarLightness = (SeekBar) view.findViewById(R.id.seekBarLightness);
	    hueGradient = (GradientView) view.findViewById(R.id.hueGradient);
	    saturationGradient = (GradientView) view.findViewById(R.id.saturationGradient);
	    lightnessGradient = (GradientView) view.findViewById(R.id.lightnessGradient);
	    oldColorView = view.findViewById(R.id.oldColor);
	    newColorView = view.findViewById(R.id.newColor);

	    // and so on for the rest of the buttons

	    addView(view);

	    int[] hueColors = new int[] { 0xFFFF0000, 0xFFFFFF00, 0xFF00FF00, 0xFF00FFFF, 0xFF0000FF, 0xFFFF00FF, 0xFFFF0000 };
	    hueGradient.setColorArray(hueColors);
	    
	    GradientDrawable d = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, hueColors);
	    //d.setStroke(2, Color.BLACK);
	    seekBarHue.setBackgroundDrawable(d);

		
		seekBarHue.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {				
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				setHue(progress);
				updateColorView();
			}
		});
		seekBarSaturation.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {				
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				setSaturation(progress);
				updateColorView();
			}
		});
		seekBarLightness.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {				
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				setLightness(progress);
				updateColorView();
			}
		});
		
		setInitialColor(initialColor);
	}
	
	private void updateColorView() {
		int color = Color.HSVToColor(new float[] {hue, saturation, lightness});
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
		this.saturation = ((float)saturation) / 100;
		drawLightnessGradient();
	}
	
	private void drawSaturationGradient() {
		int[] colors = new int[2];
		colors[0] = Color.HSVToColor(new float[] {hue, 0, lightness});
		colors[1] = Color.HSVToColor(new float[] {hue, 1, lightness});
		saturationGradient.setColorArray(colors);
	}
	
	private void setLightness(int lightness) {
		this.lightness = ((float)lightness) / 100;
		drawSaturationGradient();
	}
	
	private void drawLightnessGradient() {
		int[] colors = new int[2];
		colors[0] = Color.HSVToColor(new float[] {hue, saturation, 0});
		colors[1] = Color.HSVToColor(new float[] {hue, saturation, 1});
		lightnessGradient.setColorArray(colors);		
	}
}