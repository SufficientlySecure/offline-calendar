package at.seltner.colorpicker;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

public class ColorPickerDialog extends Dialog {

	private int initialColor;
	private ColorSelectListener listener;
	
	public interface ColorSelectListener {
        void colorSelected(int color);
    }
	


	public ColorPickerDialog(Context context, ColorSelectListener listener, int initialColor) {
		super(context);
		this.initialColor = initialColor;
	}
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(new ColorPickerView(getContext(), initialColor));
        setTitle("Pick a Color"); //TODO: strings
    }

}
