package com.moe.pussy;
import android.util.LruCache;
import android.graphics.Bitmap;
import java.util.Map;
import java.lang.ref.WeakReference;
import java.util.HashMap;

public class MemoryCache extends LruCache<String,Bitmap>
{
	//private Map<String,WeakReference> cache=new HashMap<>();
	private BitmapPool bp;
	public MemoryCache(BitmapPool bp){
		super((int)Runtime.getRuntime().maxMemory()/4);
		this.bp=bp;
	}
	
	@Override
	protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue)
	{
		if(evicted){
			bp.recycle(oldValue);
		}else{
			if(newValue!=null){
				bp.recycle(oldValue);
			}
		}
	}

	@Override
	protected int sizeOf(String key, Bitmap value)
	{
		return value.getByteCount();
	}



	
}
