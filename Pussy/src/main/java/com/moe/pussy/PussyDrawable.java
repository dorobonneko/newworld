package com.moe.pussy;
import android.graphics.drawable.Drawable;
import android.graphics.ColorFilter;
import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable.Callback;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Paint;
import java.lang.ref.WeakReference;
import android.graphics.PorterDuff;
import android.os.SystemClock;

public class PussyDrawable extends Drawable implements Animatable
{
	public WeakReference<Image> bitmap;
	//private WeakReference<Pussy.Refresh> refresh;
	private DrawableAnimator da;
	//private WeakReference<Target> t;
	public PussyDrawable(Image bitmap,DrawableAnimator da)
	{
		this.bitmap =new WeakReference<Image>(bitmap);
		this.da=da;
		if(da!=null)
			da.setCallback(this);
		//this.t=new WeakReference<Target>(t);
		//refresh=new WeakReference<>(r);
	}
	
	/*public void setAnimator(DrawableAnimator da)
	{
		this.da = da;
		if(da!=null)
		da.setCallback(this);
	}*/

	@Override
	public void start()
	{
		if(da!=null)
			da.start();
	}

	@Override
	public void stop()
	{
		if(da!=null)
			da.stop();
	}

	@Override
	public boolean isRunning()
	{
		if(da!=null)
			return da.isRunning();
		return false;
	}

	/*public Pussy.Refresh getRefresh()
	{
		return refresh.get();
	}*/
	@Override
	public void draw(Canvas p1)
	{
		p1.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.DITHER_FLAG|Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
		Image image=this.bitmap.get();
		if (image != null){
			Bitmap bitmap=image.getBitmap();
			if (da != null)
					da.draw(p1, bitmap);
				else
					p1.drawBitmap(bitmap, 0, 0, null);
			if(image.isGif())
				scheduleSelf(Updater,SystemClock.uptimeMillis()+33);
			}
	}

	@Override
	public void setAlpha(int p1)
	{
	}

	@Override
	public void setColorFilter(ColorFilter p1)
	{
	}

	@Override
	public boolean setVisible(boolean visible, boolean restart)
	{
		if(!visible)
			unscheduleSelf(Updater);
		return super.setVisible(visible, restart);
	}

	@Override
	public int getOpacity()
	{
		return 0;
	}
	/*public void recycle()
	{
		Bitmap bitmap=this.bitmap.get();
		if (bitmap != null)
			synchronized (bitmap)
			{
				bitmap.recycle();}
		recycle = true;
	}*/

	@Override
	public int getIntrinsicWidth()
	{
		Image bitmap=this.bitmap.get();
		if (bitmap != null)
			return bitmap.getWidth();
		return super.getIntrinsicWidth();
	}

	@Override
	public int getIntrinsicHeight()
	{
		Image bitmap=this.bitmap.get();
		if (bitmap != null)
			return bitmap.getHeight();
		return super.getIntrinsicHeight();
	}
	private Runnable Updater =new Runnable(){

		@Override
		public void run()
		{
			invalidateSelf();
		}
	};

}
