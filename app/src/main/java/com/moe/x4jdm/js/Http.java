package com.moe.x4jdm.js;
import org.jsoup.Jsoup;
import java.io.IOException;
import org.jsoup.Connection;
import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.NativeFunction;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.annotations.JSFunction;
import org.mozilla.javascript.InterpretedFunction;
import org.mozilla.javascript.Context;
import org.jsoup.nodes.Document;
import org.mozilla.javascript.NativeArray;
import java.util.Iterator;
import java.util.Map;
import com.alibaba.fastjson.JSONObject;

public class Http
{
	@JSFunction
	public Object sync(NativeObject net)
	{
		try
		{
			Connection conn=Jsoup.connect(net.get("url").toString());
			if (net.has("method", net))
				conn.method(Connection.Method.valueOf(net.get("method").toString().toUpperCase()));
			conn.ignoreContentType(true);
			if (net.has("data", net))
				conn.requestBody(net.get("data").toString());
			if(net.has("header",net)){
				NativeObject header=(NativeObject) net.get("header");
				Iterator<Map.Entry<Object,Object>> i=header.entrySet().iterator();
				while(i.hasNext()){
					Map.Entry<Object,Object> obj=i.next();
					conn.header(obj.getKey().toString(),obj.getValue().toString());
				}
			}
			if(net.has("type",net)){
				switch(net.get("type").toString()){
					case "json":
						
						return JSONObject.parse(conn.execute().body());
					case "string":
						return conn.execute().body();
				}
			}
			return conn.execute().parse();
			}
		catch (Exception e)
		{
			}
		return null;
	}
	@JSFunction
	public void async(NativeObject net)
	{
		InterpretedFunction res=(InterpretedFunction) net.get("response");
		try
		{
			Connection conn=Jsoup.connect(net.get("url").toString());
			if (net.has("method", res))
				conn.method(Connection.Method.valueOf(net.get("method").toString()));
			conn.ignoreContentType(true);
			if (net.has("data", res))
				conn.requestBody(net.get("data").toString());
			Connection.Response response=conn.execute();
			if (res != null)
				res.call(Context.getCurrentContext(), res, res, new Object[]{response.statusCode(),response.body()});
		}
		catch (Exception e)
		{
			if (res != null)
				res.call(Context.getCurrentContext(), res, res, new Object[]{1000,e});
		}
	}
}
