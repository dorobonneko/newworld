package com.moe.pussy;
import android.graphics.Bitmap;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;

public abstract class Target
{
	private Content content;
	public PussyDrawable onResourceReady(PussyDrawable pd,Transformer... trans){
		if(trans.length>0){
			Bitmap bitmap=pd.getBitmap();
			for(Transformer t:trans){
				bitmap=t.onTransformer(bitmap,0,0);
			}
			pd=new PussyDrawable(bitmap,pd.getRefresh());
		}
		putCache(pd);
		return pd;
	}
	public abstract void onSucccess(PussyDrawable pd);
	public abstract void onFailed(Throwable e);
	public void putCache(PussyDrawable pd){
		if(pd.getBitmap()==null)return;
		content.getMemory().put(content.getKey(),pd.getBitmap());
		if(content.getCache()==DiskCache.Cache.MASK){
			//持久化
			Bitmap bitmap=pd.getBitmap();
			if (bitmap != null)
			{
				try
				{
					bitmap.compress(Bitmap.CompressFormat.WEBP, 100, new FileOutputStream(content.getRequest().getPussy().mDiskCache.getCache(content.getKey())));
				}
				catch (FileNotFoundException e)
				{}
			}
		}
	}
	protected void onAttachContent(Content c){
		this.content=c;
	}
	protected DrawableAnimator getAnim(){
		if(content!=null)
		return content.getAnim();
		return null;
	}
	
}
