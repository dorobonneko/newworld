package com.moe.pussy.target;
import android.view.View;
import com.moe.pussy.PussyDrawable;
import android.graphics.drawable.Drawable;
import com.moe.pussy.Listener;

public class ViewBackgroundTarget extends ViewTarget
{
	public ViewBackgroundTarget(View v){
		super(v);
	}

	@Override
	public void onSuccess(PussyDrawable pd)
	{
		if(pd!=null){
			pd.stop();
			getView().setBackground(pd);
			pd.setAnimator(getAnim());
			pd.start();
			Listener l=getListener();
			if(l!=null)l.onSuccess(pd);
			
			}else{
			error(null,null);
		}
	}

	@Override
	public void error(Throwable e,Drawable d)
	{
		if(getAnim()!=null)getAnim().stop();
		getView().setBackground(d);
		Listener l=getListener();
		if(l!=null)l.onError(d);
		
	}

	@Override
	public void placeHolder(Drawable placeHolder)
	{
		if(getAnim()!=null)getAnim().stop();
		getView().setBackground(placeHolder);
		Listener l=getListener();
		if(l!=null)l.onPlaceHolder(placeHolder);
		
	}


	
}
