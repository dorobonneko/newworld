package com.moe.pussy.target;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewTreeObserver;
import com.moe.pussy.DrawableAnimator;
import com.moe.pussy.Pussy;
import com.moe.pussy.Target;
import com.moe.pussy.Transformer;
import com.moe.pussy.Content;

public abstract class ViewTarget  implements Target,ViewTreeObserver.OnPreDrawListener
{
	private Content content;
	private View view;
	public ViewTarget(View view)
	{
		this.view = view;
	}

	@Override
	public void onAttachContent(Content c)
	{
		content=c;
	}

	
	public View getView()
	{
		return view;
	}
	@Override
	public final void onResourceReady(Bitmap bitmap, Transformer[] trans)
	{
		
		if (view.getWidth() == 0 || view.getHeight() == 0)
		{
			view.post(new Runnable(){
					public void run()
					{
						view.getViewTreeObserver().addOnPreDrawListener(ViewTarget.this);
					}
				});
			//view.requestLayout();
		}
		else
		{
			onSizeReady(view.getWidth(),view.getHeight());
		}
	}

	@Override
	public boolean onPreDraw()
	{
		view.getViewTreeObserver().removeOnPreDrawListener(this);
		new Thread(){
			public void run()
			{
				onSizeReady(view.getWidth(),view.getHeight());
			}
		}.start();
		return false;
	}

	protected DrawableAnimator getAnim(){
		if(content!=null)
			return content.getAnim();
		return null;
	}
	protected Pussy.Refresh getRefresh(){
		return content.getRefresh();
	}

	@Override
	public Content getContent()
	{
		return content;
	}

	@Override
	public final void onSizeReady(int w, int h)
	{
		content.onSizeReady(w,h);
	}


	
}
