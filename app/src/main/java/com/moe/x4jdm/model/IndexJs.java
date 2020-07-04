package com.moe.x4jdm.model;
import java.util.Map;
import android.content.res.AssetManager;
import com.sun.script.javascript.RhinoScriptEngine;
import javax.script.ScriptException;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.IOException;
import android.content.ContentResolver;
import android.net.Uri;
import com.moe.x4jdm.js.Http;
import com.moe.x4jdm.js.Runtime;
import org.mozilla.javascript.NativeJSON;
import com.moe.x4jdm.util.JavascriptUtil;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import android.content.Context;
import com.moe.x4jdm.js.Engine;
import javax.script.ScriptEngine;
import org.mozilla.javascript.NativeBoolean;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.Jsoup;
import android.support.v7.app.AlertDialog;
import android.os.Looper;
import android.os.Handler;
import android.widget.Toast;

public class IndexJs extends Index
{
	private String name;
	private Engine engine;
	private Uri uri;
	private Context context;
	public IndexJs(Context context,String name){
		this.context=context;
		this.name=name;
	}
	public IndexJs(Context context,Uri uri){
		this.context=context;
		this.uri=uri;
	}
	private void showError(final String error){
		if(Looper.getMainLooper()!=Looper.myLooper())
			new Handler(Looper.getMainLooper()).post(new Runnable(){

					@Override
					public void run()
					{
						new AlertDialog.Builder(context).setMessage(error).show();
					}
				});
				else
		new AlertDialog.Builder(context).setMessage(error).show();
	}
	private void checkInit(){
		if(engine==null){
			engine=new Engine(context);
			try{
				if(name!=null)
				{
					engine.eval(new InputStreamReader(context.getAssets().open(name)));
				}else if(uri!=null){
					engine.eval(new InputStreamReader(context.getContentResolver().openInputStream(uri)));
				}
			}
			
			catch (Exception e)
			{showError(e.getMessage());}
		}
	}
	@Override
	public String getIndex(int page)
	{
		checkInit();
		try
		{
			return JavascriptUtil.toJsonArray((NativeArray)engine.invokeFunction("getIndex")).toJSONString();
		}
		catch (Exception e)
		{showError(e.getMessage());}
		return null;
	}

	@Override
	public Map<String, String> getVideoUrl(String url)
	{
		return null;
	}

	@Override
	public String getList(String url)
	{
		checkInit();
		try
		{
			return JavascriptUtil.toJsonObject((NativeObject)engine.invokeFunction("getList",url)).toJSONString();
		}
		catch (Exception e)
		{showError(e.getMessage());}
		return null;
	}

	@Override
	public String getPost(String url)
	{
		checkInit();
		try
		{
			return engine.invokeFunction("getPost", url).toString();
		}
		catch (Exception e)
		{showError(e.getMessage());}
		
		return null;
	}

	@Override
	public String getGold()
	{
		checkInit();
		try
		{
			return engine.invokeFunction("getGold").toString();
		}
		catch(NoSuchMethodException e){}
		catch (Exception e)
		{showError(e.getMessage());}

		return null;
	}

	@Override
	public String getFilter()
	{
		checkInit();
		try
		{
			return engine.invokeFunction("getFilter").toString();
		}catch(NoSuchMethodException e){}
		catch (Exception e)
		{showError(e.getMessage());}

		return null;
	}

	@Override
	public String getTime()
	{
		checkInit();
		try
		{
			return engine.invokeFunction("getTime").toString();
		}
		catch (Exception e)
		{showError(e.getMessage());}

		return null;
	}

	@Override
	public boolean hasTime()
	{
		checkInit();
	try
	{
	return engine.invokeFunction("hasTime");
	}
	catch (Exception e)
	{}
	return false;
	}

	@Override
	public String search(String key)
	{
		checkInit();
		try
		{
			return engine.invokeFunction("search",new Object[]{key}).toString();
		}catch(NoSuchMethodException e){}
		catch (Exception e)
		{ showError(e.getMessage());}
		return null;
	}





	
}
