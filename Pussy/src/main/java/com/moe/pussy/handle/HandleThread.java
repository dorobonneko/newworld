package com.moe.pussy.handle;
import com.moe.pussy.Request;
import com.moe.pussy.RequestHandler;
import java.util.List;
import java.util.ArrayList;

public class HandleThread implements Runnable
{
	private RequestHandler.Response response;
	private Request request;
	private List<Callback> calls=new ArrayList<>();
	private boolean success;
	public HandleThread(Request request){
		this.request=request;
	}
	public void addCallback(Callback call){
		if(!success)
		calls.add(call);
		else
			call.onResponse(response);
	}
	public void removeCallback(Callback call){
		calls.remove(call);
	}
	@Override
	public void run()
	{
		RequestHandler h=request.getPussy().getDispatcher().getHandler(request);
		if(h!=null)
		response=h.onHandle(request);
		success=true;
		for(Callback call:calls){
			call.onResponse(response);
		}
	}
	
	public interface Callback{
		void onResponse(RequestHandler.Response response);
	}
}
