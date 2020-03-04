package com.moe.pussy.transformer;
import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
import android.graphics.BitmapFactory.Options;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.renderscript.Element;
import android.renderscript.Allocation;
import android.content.Context;
import com.moe.pussy.Transformer;

public class BlurTransformer implements Transformer
{
	private int level;
	private Context c;
	public BlurTransformer(Context c,int level){
		this.level=level;
		this.c=c.getApplicationContext();
	}
	
	@Override
	public Bitmap onTransformer(Bitmap source, int w, int h)
	{
		Bitmap out_bitmap=Bitmap.createBitmap(source.getWidth(),source.getHeight(),Bitmap.Config.ARGB_8888);
		RenderScript rs=RenderScript.create(c);
		ScriptIntrinsicBlur sib=ScriptIntrinsicBlur.create(rs,Element.U8_4(rs));
		Allocation in=Allocation.createFromBitmap(rs,source);
		Allocation out=Allocation.createFromBitmap(rs,out_bitmap);
		sib.setRadius(level);
		sib.setInput(in);
		sib.forEach(out);
		out.copyTo(out_bitmap);
		rs.destroy();
		if(out_bitmap!=source)
			source.recycle();
		return out_bitmap;
	}

	
	@Override
	public String getKey()
	{
		return "pussy&blur".concat(String.valueOf(level));
	}
	
}
