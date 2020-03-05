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
	public PussyDrawable decode(File input)
	{
		BitmapFactory.Options options=new BitmapFactory.Options();
		
		options.inPreferredConfig=Bitmap.Config.RGB_565;
		options.inJustDecodeBounds=true;
		BitmapFactory.decodeFile(input.getAbsolutePath(),options);
		options.inJustDecodeBounds=false;
		options.inBitmap=BitmapPool.getBitmap(options.outWidth,options.outHeight,options.outConfig);
		options.inMutable=true;
		return new PussyDrawable(BitmapFactory.decodeFile(input.getAbsolutePath(),options),null);
	}
	
}
