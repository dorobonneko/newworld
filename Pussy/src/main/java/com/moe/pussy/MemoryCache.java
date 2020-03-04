package com.moe.pussy;
import android.util.LruCache;
import android.graphics.Bitmap;

public class MemoryCache extends LruCache<String,Bitmap>
{
	public MemoryCache(){
		super((int)Runtime.getRuntime().maxMemory()/2);
	}

	@Override
	protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue)
	{
		if(evicted&&oldValue!=null){
			synchronized(oldValue){
				oldValue.recycle();
			}
		}
	}

	@Override
	protected int sizeOf(String key, Bitmap value)
	{
		return value.getByteCount();
	}



	
}
