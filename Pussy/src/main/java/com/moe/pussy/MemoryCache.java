package com.moe.pussy;
import android.util.LruCache;
import android.graphics.Bitmap;
import java.util.Map;
import java.lang.ref.WeakReference;
import java.util.HashMap;

public class MemoryCache extends LruCache<String,Bitmap>
{
	//private Map<String,WeakReference> cache=new HashMap<>();
	public MemoryCache(){
		super((int)Runtime.getRuntime().maxMemory()/8);
	}
	
	@Override
	protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue)
	{
		if(evicted){
			synchronized(oldValue){
				BitmapPool.recycle(oldValue);
			}
		}else{
			if(newValue!=null){
				synchronized(oldValue){
					BitmapPool.recycle(oldValue);
				}
			}
		}
	}

	@Override
	protected int sizeOf(String key, Bitmap value)
	{
		return value.getByteCount();
	}



	
}
