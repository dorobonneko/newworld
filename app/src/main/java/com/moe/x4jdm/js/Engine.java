package com.moe.x4jdm.js;
import com.sun.script.javascript.RhinoScriptEngine;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.Bindings;
import javax.script.ScriptContext;
import java.io.Reader;
import javax.script.ScriptException;
import android.content.Context;
import java.io.InputStreamReader;
import android.net.Uri;
import org.mozilla.javascript.NativeObject;
import com.moe.x4jdm.util.JavascriptUtil;
import org.mozilla.javascript.NativeArray;
import org.jsoup.Jsoup;

public class Engine implements ScriptEngine
{
	private RhinoScriptEngine scriptEngine;
	public Engine(Context context){
		scriptEngine=new RhinoScriptEngine();
		scriptEngine.put("Http",new Http());
		scriptEngine.put("runtime",Runtime.runtime);
		try
		{
			importClass(Uri.class);
			importClass(Jsoup.class);
			scriptEngine.eval(new InputStreamReader(context.getAssets().open("globa.js")));
		}catch(Exception e){}
	}
	public void importClass(Class class_) throws ScriptException{
		scriptEngine.eval(String.format("importClass(%s);",class_.getName()));
		
	}
	public void importPackage(String packagePath) throws ScriptException{
		scriptEngine.eval(String.format("importPackage(%s);",packagePath));
	}
	@Override
	public Object eval(String p1, ScriptContext p2) throws ScriptException
	{
		return scriptEngine.eval(p1,p2);
	}

	@Override
	public Object eval(Reader p1, ScriptContext p2) throws ScriptException
	{
		return scriptEngine.eval(p1,p2);
	}

	@Override
	public Object eval(String p1) throws ScriptException
	{
		return scriptEngine.eval(p1);
	}

	@Override
	public Object eval(Reader p1) throws ScriptException
	{
		return scriptEngine.eval(p1);
	}

	@Override
	public Object eval(String p1, Bindings p2) throws ScriptException
	{
		return scriptEngine.eval(p1,p2);
	}

	@Override
	public Object eval(Reader p1, Bindings p2) throws ScriptException
	{
		return scriptEngine.eval(p1,p2);
	}

	@Override
	public void put(String p1, Object p2)
	{
		scriptEngine.put(p1,p2);
	}

	@Override
	public Object get(String p1)
	{
		return scriptEngine.get(p1);
	}

	@Override
	public Bindings getBindings(int p1)
	{
		return scriptEngine.getBindings(p1);
	}

	@Override
	public void setBindings(Bindings p1, int p2)
	{
		scriptEngine.setBindings(p1,p2);
	}

	@Override
	public Bindings createBindings()
	{
		return scriptEngine.createBindings();
	}

	@Override
	public ScriptContext getContext()
	{
		return scriptEngine.getContext();
	}

	@Override
	public void setContext(ScriptContext p1)
	{
		scriptEngine.setContext(p1);
	}

	@Override
	public ScriptEngineFactory getFactory()
	{
		return scriptEngine.getFactory();
	}
	
	public Object invokeFunction(String name,Object... args) throws NoSuchMethodException, ScriptException{
		return scriptEngine.invokeFunction(name,args);
	}
	public static String toJson(Object obj){
		if(obj instanceof NativeObject)
			return JavascriptUtil.toJsonObject((NativeObject)obj).toJSONString();
		else if(obj instanceof NativeArray)
			return JavascriptUtil.toJsonArray((NativeArray)obj).toJSONString();
			return null;
	}
	/*public Object invokeFunction(String name) throws NoSuchMethodException, ScriptException{
		return scriptEngine.invokeFunction(name);
	}*/
}
