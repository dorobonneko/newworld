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
			catch (IOException e)
			{}
			catch (ScriptException e)
			{}
		}
	}
	@Override
	public String getHost()
	{
		checkInit();
		try
		{
			return engine.invokeFunction("getHost").toString();
		}
		catch (NoSuchMethodException e)
		{}
		catch (ScriptException e)
		{}
		return null;
	}

	@Override
	public String getIndex(int page)
	{
		checkInit();
		try
		{
			return JavascriptUtil.toJsonArray((NativeArray)engine.invokeFunction("getIndex")).toJSONString();
		}
		catch (NoSuchMethodException e)
		{}
		catch (Exception e)
		{}
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
		catch (NoSuchMethodException e)
		{}
		catch (Exception e)
		{}
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
		{}
		
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
		catch (Exception e)
		{}

		return null;
	}





	
}
