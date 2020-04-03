package com.moe.pussy;
import java.util.Map;
import java.util.HashMap;
import android.graphics.Bitmap;
import java.util.ArrayList;
import android.graphics.Canvas;
import java.util.List;
import android.os.Build;
import android.util.ArrayMap;
import android.util.SparseArray;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Collections;

public class BitmapPool
{
	private  Map<Integer,List<Bitmap>> map=Collections.synchronizedMap(new LinkedHashMap<Integer,List<Bitmap>>());
	private  long maxSize,currentSize;
	public BitmapPool(long maxSize){
		this.maxSize=maxSize;
	}

	public Bitmap getBitmap(int w, int h, Bitmap.Config config)
	{
		int size=getBitmapByteSize(w,h,config);
		List<Bitmap> list=map.remove(size);
		if(list!=null&&!list.isEmpty()){
			map.put(size,list);
			Bitmap bitmap=list.remove(0);
			bitmap.reconfigure(w,h,config);
			currentSize-=size;
			return bitmap;
		}
		return Bitmap.createBitmap(w,h,config);
	}
	public void recycle(Bitmap bitmap)
	{
		if(bitmap==null)return;
		if(!bitmap.isMutable()){bitmap.recycle();return;}
		int size=getBitmapByteSize(bitmap);
		List<Bitmap> list=map.get(size);
		if(list==null){
			map.put(size,list=new ArrayList<>());
		}
		list.add(bitmap);
		currentSize+=size;
		trimToSize(maxSize);
	}
	public void trimToSize(long size){
		if(currentSize>size){
			Iterator<List<Bitmap>> iterator_map=map.values().iterator();
			while(iterator_map.hasNext()){
				List<Bitmap> list=iterator_map.next();
				Iterator<Bitmap> iterator=list.iterator();
				while(iterator.hasNext()){
					Bitmap bitmap=iterator.next();
					iterator.remove();
					currentSize-=getBitmapByteSize(bitmap);
					synchronized(bitmap){
					bitmap.recycle();
					}
					if(currentSize<size)return;
				}
				if(list.isEmpty())
					iterator_map.remove();
			}
		}
	}
	public boolean isRecycled(Bitmap bitmap)
	{
		List<Bitmap> list=map.get(bitmap.getWidth() * bitmap.getHeight() + bitmap.getConfig().name());
		if (list == null)return false;
		return list.contains(bitmap);
	}
	public static int getBitmapByteSize( Bitmap bitmap) {
		// The return value of getAllocationByteCount silently changes for recycled bitmaps from the
		// internal buffer size to row bytes * height. To avoid random inconsistencies in caches, we
		// instead assert here.
		if (bitmap.isRecycled()) {
			throw new IllegalStateException(
				"Cannot obtain size for recycled Bitmap: "
				+ bitmap
				+ "["
				+ bitmap.getWidth()
				+ "x"
				+ bitmap.getHeight()
				+ "] "
				+ bitmap.getConfig());
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			// Workaround for KitKat initial release NPE in Bitmap, fixed in MR1. See issue #148.
			try {
				return bitmap.getAllocationByteCount();
			} catch (
			NullPointerException e) {
				// Do nothing.
			}
		}
		return bitmap.getHeight() * bitmap.getRowBytes();
	}

	/**
	 * Returns the in memory size of {@link android.graphics.Bitmap} with the given width, height, and
	 * {@link android.graphics.Bitmap.Config}.
	 */
	public static int getBitmapByteSize(int width, int height, Bitmap.Config config) {
		return width * height * getBytesPerPixel(config);
	}

	private static int getBytesPerPixel(Bitmap.Config config) {
		// A bitmap by decoding a GIF has null "config" in certain environments.
		if (config == null) {
			config = Bitmap.Config.ARGB_8888;
		}

		int bytesPerPixel;
		switch (config) {
			case ALPHA_8:
				bytesPerPixel = 1;
				break;
			case RGB_565:
			case ARGB_4444:
				bytesPerPixel = 2;
				break;
			case RGBA_F16:
				bytesPerPixel = 8;
				break;
			case ARGB_8888:
			default:
				bytesPerPixel = 4;
				break;
		}
		return bytesPerPixel;
	}
	
}
