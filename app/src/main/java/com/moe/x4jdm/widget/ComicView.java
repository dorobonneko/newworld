package com.moe.x4jdm.widget;
import android.widget.ImageView;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.TypedValue;
import android.util.AttributeSet;

public class ComicView extends ImageView
{
	private int page;
	private Paint p;
	public ComicView(Context context){
		this(context,null);
		}
	public ComicView(Context context, AttributeSet attrs){
		this(context,attrs,0);
	}
	public ComicView(Context context,AttributeSet attrs,int defStype){
		super(context,attrs,defStype,0);
		p=new Paint();
		p.setColor(Color.WHITE);
		p.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,50,getResources().getDisplayMetrics()));
		p.setTextAlign(Paint.Align.CENTER);
		
	}
	public void setPage(int page){
		this.page=page;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		int width=MeasureSpec.getSize(widthMeasureSpec);
		Drawable d=getDrawable();
		if(d!=null){
			int height=(int)(width/(float)d.getIntrinsicWidth()*d.getIntrinsicHeight());
			setMeasuredDimension(width,height);
		}else
		setMeasuredDimension(width,width);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		if(getDrawable()==null){
			canvas.drawColor(Color.GRAY);
			if(page!=0){
			String page=String.valueOf(this.page);
			
			canvas.drawText(page,0,page.length(),canvas.getWidth()/2,(canvas.getHeight()-p.descent()+p.ascent())/2,p);
			}
		}
		else
		super.onDraw(canvas);
	}
	
	
}
