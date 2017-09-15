package com.yongyida.robot.settings.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.yongyida.robot.settings.R;


/**
 * 圆形图片
 * 
 */
public class CircleImageView extends ImageView {
	private int m_radius = 0;
	private int mWidth;
	private int mHeight;

	public CircleImageView(Context context) {
		super(context);
	}

	public CircleImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(getResources().getDrawable(R.drawable.addperson).getIntrinsicWidth(), getResources().getDrawable(R.drawable.addperson).getIntrinsicHeight());
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		mWidth = getWidth();
		mHeight = getHeight();
		m_radius = (mWidth <= mHeight ? mWidth : mHeight) /2;
	}

	@Override
	public void draw(Canvas canvas) {
		BitmapDrawable drawable = (BitmapDrawable)getDrawable();
		if (drawable == null) {
			Log.d("CircleImageView", "BitmapDrawable null");
			return;
		}
		Paint paint = new Paint();
		Bitmap bitmap = drawable.getBitmap();
		BitmapShader shader = new BitmapShader(bitmap,
				BitmapShader.TileMode.CLAMP,
				BitmapShader.TileMode.CLAMP);

		float scale = 1.0f;

		// 拿到bitmap宽或高的小值
		int bSize = Math.min(bitmap.getWidth(), bitmap.getHeight());
		scale = mWidth * 1.0f / bSize;
		Matrix matrix = new Matrix();
		// shader的变换矩阵，我们这里主要用于放大或者缩小
		matrix.setScale(scale, scale);
		// 设置变换矩阵
		shader.setLocalMatrix(matrix);
		// 设置shader
		paint.setAntiAlias(true);
		paint.setShader(shader);
		drawCircleBorder(canvas, m_radius, m_radius, m_radius, Color.LTGRAY);
		canvas.drawCircle(m_radius, m_radius, m_radius - 1, paint);
	}
	
	private void drawCircleBorder(Canvas canvas, int cx, int cy, int radius, int color) {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(color);
		paint.setDither(true);
		paint.setFilterBitmap(true);
		canvas.drawCircle(cx, cy, radius, paint);
	}
}
