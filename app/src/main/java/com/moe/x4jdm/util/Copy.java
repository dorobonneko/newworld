package com.moe.x4jdm.util;
import android.content.Context;
import android.content.ClipboardManager;

public class Copy
{
	public static void copy(Context context,String text){
		context.getSystemService(ClipboardManager.class).setText(text);
	}
}
