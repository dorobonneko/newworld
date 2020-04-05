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
	public Bitmap decode(BitmapPool mBitmapPool,File input,int w,int h)
	{
		BitmapFactory.Options options=new BitmapFactory.Options();
		options.inDither=true;
		options.inPreferredConfig=Bitmap.Config.ARGB_8888;
		options.inJustDecodeBounds=true;
		BitmapFactory.decodeFile(input.getAbsolutePath(),options);
		if(options.outWidth<=0||options.outHeight<=0)return null;
		options.inJustDecodeBounds=false;
		options.inSampleSize=computeSampleSize(options,-1,w*h);
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
	if (height > reqHeight || width > reqWidth) {
		int halfHeight = height / 2;
		int halfWidth = width / 2;
		try{
			while ((halfHeight / inSampleSize) >= reqHeight&& (halfWidth / inSampleSize) >= reqWidth) {
				if(inSampleSize==0)
					throw new NullPointerException();
				int now =inSampleSize*2;
				inSampleSize=now;
				if(now==0)
					throw new NullPointerException("inSampleSize"+inSampleSize);
				
			}
		}catch(ArithmeticException e){
			throw new ArithmeticException("inSampleSize"+inSampleSize+"/halfWidth"+halfWidth+"/halfHeight"+halfHeight);
		}
	}
	return inSampleSize;
	}
	public static int computeSampleSize(BitmapFactory.Options options,

										int minSideLength, int maxNumOfPixels) {

		int initialSize = computeInitialSampleSize(options, minSideLength,

												   maxNumOfPixels);



		int roundedSize;

		if (initialSize <= 8) {

			roundedSize = 1;

			while (roundedSize < initialSize) {

				roundedSize <<= 1;

			}

		} else {

			roundedSize = (initialSize + 7) / 8 * 8;

		}



		return roundedSize;

	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,

												int minSideLength, int maxNumOfPixels) {

		double w = options.outWidth;

		double h = options.outHeight;



		int lowerBound = (maxNumOfPixels == -1) ? 1 :

            (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));

		int upperBound = (minSideLength == -1) ? 128 :

            (int) Math.min(Math.floor(w / minSideLength),

						   Math.floor(h / minSideLength));



		if (upperBound < lowerBound) {

			// return the larger one when there is no overlapping zone.

			return lowerBound;

		}



		if ((maxNumOfPixels == -1) &&

            (minSideLength == -1)) {

			return 1;

		} else if (minSideLength == -1) {

			return lowerBound;

		} else {

			return upperBound;

		}

	}

}
