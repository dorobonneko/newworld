package com.moe.pussy.decode;
import com.moe.pussy.Decoder;
import java.io.InputStream;
import com.moe.pussy.PussyDrawable;
import android.graphics.BitmapFactory;

public class BitmapDecoder implements Decoder
{

	@Override
	public PussyDrawable decode(InputStream input)
	{
		return new PussyDrawable(BitmapFactory.decodeStream(input),null);
	}
	
}
