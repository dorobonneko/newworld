package com.moe.pussy.decode;
import com.moe.pussy.Decoder;
import java.io.InputStream;
import com.moe.pussy.PussyDrawable;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import java.io.File;
import com.moe.pussy.BitmapPool;

public class BitmapDecoder implements Decoder
{
	//byte[] buff=new byte[32*1024];
	@Override
	public Bitmap decode(BitmapPool mBitmapPool,File input)
	{
		BitmapFactory.Options options=new BitmapFactory.Options();
		options.inDither=true;
		options.inPreferredConfig=Bitmap.Config.ARGB_8888;
		options.inJustDecodeBounds=true;
		BitmapFactory.decodeFile(input.getAbsolutePath(),options);
		if(options.outWidth<=0||options.outHeight<=0)return null;
		options.inJustDecodeBounds=false;
		options.inBitmap=mBitmapPool.getBitmap(options.outWidth,options.outHeight,options.inPreferredConfig);
		options.inMutable=true;
		Bitmap bitmap=BitmapFactory.decodeFile(input.getAbsolutePath(),options);
		if(options.outMimeType==null||options.outWidth!=bitmap.getWidth()||options.outHeight!=bitmap.getHeight()){
				mBitmapPool.recycle(bitmap);
				bitmap=null;
			}else if(bitmap.getWidth()<=0||bitmap.getHeight()<=0){
				mBitmapPool.recycle(bitmap);
				bitmap=null;
		}
		
			return bitmap;
	}
	public static int inSampleSize(int width,int height,int reqWidth,int reqHeight){
	int inSampleSize = 1;

	//如果当前图片的高或者宽大于所需的高或宽，
	// 就进行inSampleSize的2倍增加处理，直到图片宽高符合所需要求。
	if (height > reqHeight || width > reqWidth) {
		int halfHeight = height / 2;
		int halfWidth = width / 2;
		while ((halfHeight / inSampleSize >= reqHeight)
			   && (halfWidth / inSampleSize) >= reqWidth) {
			inSampleSize *= 2;
		}
	}
	return inSampleSize;
	}
}
