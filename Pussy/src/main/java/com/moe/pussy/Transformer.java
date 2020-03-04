package com.moe.pussy;
import android.graphics.Bitmap;

public interface Transformer
{
	public Bitmap onTransformer(Bitmap bitmap,int w,int h);
	public String getKey();
}
