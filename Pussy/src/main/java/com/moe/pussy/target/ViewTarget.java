package com.moe.pussy.target;
import android.graphics.Bitmap;
import android.view.View;
import com.moe.pussy.BitmapPool;
import com.moe.pussy.PussyDrawable;
import com.moe.pussy.Target;
import com.moe.pussy.Transformer;
import android.view.ViewTreeObserver;

public abstract class ViewTarget extends Target implements ViewTreeObserver.OnPreDrawListener
{
	private View view;
	private Bitmap pd;
	private Transformer[] trans;
	public ViewTarget(View view){
		this.view=view;
	}
	public View getView(){
		return view;
	}
	@Override
	public final Bitmap onResourceReady(Bitmap bitmap, Transformer[] trans)
	{
		this.pd=bitmap;

		this.trans=trans;
		if(view.getWidth()==0||view.getHeight()==0){
			view.post(new Runnable(){
					public void run(){
						view.getViewTreeObserver().addOnPreDrawListener(ViewTarget.this);
					}
				});
			//view.requestLayout();
		}else{
			Bitmap b=bitmap;
			if(b!=null)
				for(Transformer t:trans){
					b=t.onTransformer(BitmapPool.get(),b,view.getWidth(),view.getHeight());
				}
			final PussyDrawable pd=putCache(b);
			view.post(new Runnable(){

					@Override
					public void run()
					{
						ViewTarget.this.pd=null;
						ViewTarget.this.trans=null;
						onSucccess(pd);
					}
				});
			return null;
		}
		return null;
	}

	@Override
	public boolean onPreDraw()
	{
		view.getViewTreeObserver().removeOnPreDrawListener(this);
		new Thread(){
			public void run(){
				onResourceReady(pd,trans);
				pd=null;
				trans=null;
			}
		}.start();
		return false;
	}

	
}
