package com.moe.pussy.target;
import com.moe.pussy.Target;
import com.moe.pussy.PussyDrawable;
import android.widget.ImageView;
import com.moe.pussy.Transformer;
import android.view.ViewTreeObserver;
import android.graphics.Bitmap;
import com.moe.pussy.DrawableAnimator;
import android.graphics.drawable.Drawable;

public class ImageViewTarget extends Target implements ViewTreeObserver.OnGlobalLayoutListener
{
	private ImageView view;
	private Bitmap pd;
	private Transformer[] trans;
	public ImageViewTarget(ImageView view){
		this.view=view;
	}

	@Override
	public Bitmap onResourceReady(Bitmap bitmap, Transformer[] trans)
	{
		this.pd=bitmap;
		this.trans=trans;
		if(view.getWidth()==0||view.getHeight()==0){
			view.getViewTreeObserver().addOnGlobalLayoutListener(this);
		}else{
			Bitmap b=bitmap;
			if(b!=null)
			for(Transformer t:trans){
				b=t.onTransformer(b,view.getWidth(),view.getHeight());
			}
			final PussyDrawable pd=new PussyDrawable(b,getRefresh());
			//this.pd.setRefresh(pd.getRefresh());
			putCache(pd);
			view.post(new Runnable(){

					@Override
					public void run()
					{
						onSucccess(pd);
					}
				});
			return b;
		}
		return null;
	}
	
	@Override
	public void onSucccess(PussyDrawable pd)
	{
		pd.stop();
		view.setImageDrawable(pd);
		pd.setAnimator(getAnim());
		pd.start();
	}

	@Override
	public void error(Throwable e,Drawable d)
	{
		if(getAnim()!=null)getAnim().stop();
		view.setImageDrawable(d);
	}

	@Override
	public void placeHolder(Drawable placeHolder)
	{
		if(getAnim()!=null)getAnim().stop();
		view.setImageDrawable(placeHolder);
	}

	@Override
	public void onGlobalLayout()
	{
		view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
		new Thread(){
			public void run(){
				onResourceReady(pd,trans);
			}
		}.start();
	}
	
	
}
