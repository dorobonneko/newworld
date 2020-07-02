package com.moe.x4jdm.js;
import org.mozilla.javascript.annotations.JSConstructor;
import org.mozilla.javascript.annotations.JSFunction;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class Runtime
{
	public static Runtime runtime;
	private Context context;
	@JSConstructor
	public Runtime(){}
	public Runtime(Context context){
		this.context=context;
	}
	@JSFunction
	public void toast(final String text){
		new Handler(Looper.getMainLooper()).post(new Runnable(){

				@Override
				public void run()
				{
					Toast.makeText(context,text,Toast.LENGTH_SHORT).show();
				}
			});
	}
}
