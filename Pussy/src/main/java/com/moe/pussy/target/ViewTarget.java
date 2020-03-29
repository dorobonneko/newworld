package com.moe.pussy.target;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewTreeObserver;
import com.moe.pussy.DrawableAnimator;
import com.moe.pussy.Pussy;
import com.moe.pussy.Target;
import com.moe.pussy.Transformer;
import com.moe.pussy.Content;
import java.lang.ref.WeakReference;

public abstract class ViewTarget  implements Target,ViewTreeObserver.OnPreDrawListener,View.OnAttachStateChangeListener
{
	private  Content content;
	private WeakReference<View> view;
	public ViewTarget(View view)
	{
		this.view =new WeakReference<View>( view);
		view.addOnAttachStateChangeListener(this);
	}

	@Override
	public void onViewAttachedToWindow(View p1)
	{
		content.getRefresh().refresh(this);
	}

	@Override
	public void onViewDetachedFromWindow(View p1)
	{
		content.getRefresh().cancel();
	}



	@Override
	public void onAttachContent(Content c)
	{
		content=c;
	}

	
	public View getView()
	{
		return view.get();
	}
	@Override
	public final void onResourceReady(Bitmap bitmap, Transformer[] trans)
	{
		
		if (getView().getWidth() == 0 || getView().getHeight() == 0)
		{
			getView().post(new Runnable(){
					public void run()
					{
						getView().getViewTreeObserver().addOnPreDrawListener(ViewTarget.this);
					}
				});
			//view.requestLayout();
		}
		else
		{
			onSizeReady(getView().getWidth(),getView().getHeight());
		}
	}

	@Override
	public boolean onPreDraw()
	{
		getView().getViewTreeObserver().removeOnPreDrawListener(this);
		new Thread(){
			public void run()
			{
				onSizeReady(getView().getWidth(),getView().getHeight());
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
