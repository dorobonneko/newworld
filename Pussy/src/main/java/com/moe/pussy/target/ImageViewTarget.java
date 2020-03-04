package com.moe.pussy.target;
import com.moe.pussy.Target;
import com.moe.pussy.PussyDrawable;
import android.widget.ImageView;
import com.moe.pussy.Transformer;
import android.view.ViewTreeObserver;
import android.graphics.Bitmap;
import com.moe.pussy.DrawableAnimator;

public class ImageViewTarget extends Target implements ViewTreeObserver.OnGlobalLayoutListener
{
	private ImageView view;
	private PussyDrawable pd;
	private Transformer[] trans;
	public ImageViewTarget(ImageView view){
		this.view=view;
	}

	@Override
	public PussyDrawable onResourceReady(PussyDrawable pd, Transformer[] trans)
	{
		this.pd=pd;
		this.trans=trans;
		if(view.getWidth()==0||view.getHeight()==0){
			view.getViewTreeObserver().addOnGlobalLayoutListener(this);
		}else{
			Bitmap b=pd.getBitmap();
			if(b!=null)
			for(Transformer t:trans){
				b=t.onTransformer(b,view.getWidth(),view.getHeight());
			}
			this.pd=new PussyDrawable(b,pd.getRefresh());
			//this.pd.setRefresh(pd.getRefresh());
			putCache(this.pd);
			view.post(new Runnable(){

					@Override
					public void run()
					{
						onSucccess(ImageViewTarget.this.pd);
					}
				});
			return this.pd;
		}
		return null;
	}
	
	@Override
	public void onSucccess(PussyDrawable pd)
	{
		pd.stop();
		pd.setAnimator(getAnim());
		view.setImageDrawable(pd);
		pd.start();
	}

	@Override
	public void onFailed(Throwable e)
	{
		if(getAnim()!=null)getAnim().stop();
		view.setImageDrawable(null);
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
