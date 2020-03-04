package com.moe.pussy.handle;
import com.moe.pussy.Request;
import com.moe.pussy.Handler;

public class HandleThread extends Thread
{
	private Handler.Response response;
	private Request request;
	private int count;
	private Object locked=new Object();
	public HandleThread(Request request){
		this.request=request;
		start();
	}

	@Override
	public void run()
	{
		for(Handler h:request.getPussy().getHandlers()){
			if(h.canHandle(request)){
				response=h.onHandle(request);
				break;
				}
		}
		if(response==null)
		response=new Handler.Response();
	}
	
	public Handler.Response get(){
		synchronized(locked){
		count++;
		}
		synchronized(this){
		while(response==null);
		return response;
		}
	}
	public void close(){
		synchronized(locked){
		count--;
		if(count<=0){
			//request.getPussy().removeHandleThread(request.getKey());
			//request.cancel();
			}
		}
	}
}
