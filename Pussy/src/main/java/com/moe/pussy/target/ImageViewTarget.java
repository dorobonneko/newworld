package com.moe.pussy.target;
import com.moe.pussy.Target;
import com.moe.pussy.PussyDrawable;
import android.widget.ImageView;
import com.moe.pussy.Transformer;
import android.view.ViewTreeObserver;
import android.graphics.Bitmap;
import com.moe.pussy.DrawableAnimator;
import android.graphics.drawable.Drawable;
import com.moe.pussy.BitmapPool;
import java.util.ArrayList;

public class ImageViewTarget extends ViewTarget implements ViewTreeObserver.OnPreDrawListener
{
	public ImageViewTarget(ImageView view){
		super(view);
	}

	
	
	@Override
	public void onSucccess(PussyDrawable pd)
	{
		if(pd!=null){
		pd.stop();
		((ImageView)getView()).setImageDrawable(pd);
		pd.setAnimator(getAnim());
		pd.start();}else{
			error(null,null);
		}
	}

	@Override
	public void error(Throwable e,Drawable d)
	{
		if(getAnim()!=null)getAnim().stop();
		((ImageView)getView()).setImageDrawable(d);
	}

	@Override
	public void placeHolder(Drawable placeHolder)
	{
		if(getAnim()!=null)getAnim().stop();
		((ImageView)getView()).setImageDrawable(placeHolder);
	}
	
	
}
