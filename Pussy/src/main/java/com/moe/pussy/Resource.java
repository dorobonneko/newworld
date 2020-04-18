package com.moe.pussy;
import android.graphics.Bitmap;

public class Resource
{
	String key;
	Image image;
	int count;
	private OnResourceListener listener;
	protected Resource(String key,Image bitmap){
		this.key=key;
		this.image=bitmap;
	}
	public void acquire(){
		count++;
	}
	public void release(){
		if(--count==0)
			listener.onResourceRelease(this);
	}
	public void setOnResourceListener(OnResourceListener l){
		listener=l;
	}
	public interface OnResourceListener{
		void onResourceRelease(Resource res);
	}
}
