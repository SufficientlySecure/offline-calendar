package at.seltner.colorpicker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.view.View;

public class GradientView extends View {
	
	private Paint paint;
	private int[] colors;
	
	public GradientView(Context context) {
		this(context, null);
	}

	public GradientView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public GradientView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (paint == null) {
			paint = new Paint();
		}
		if (colors == null)
			return;
		Shader s = new LinearGradient(0, 0, this.getMeasuredWidth(), this.getMeasuredHeight(), colors, null, TileMode.CLAMP);
		paint.setShader(s);
		canvas.drawRect(0.f, 0.f, this.getMeasuredWidth(), this.getMeasuredHeight(), paint);
	}
	
	public void setColorArray(int[] colors) {
		this.colors = colors;
		invalidate();
	}
}