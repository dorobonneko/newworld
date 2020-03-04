package com.moe.pussy;
import java.io.InputStream;
import java.io.IOException;

public interface Handler
{
	public boolean canHandle(Request request);
	public Response onHandle(Request request);
	public class Response{
		public InputStream get() throws IOException{return null;}
		public void close(){}
		public PussyDrawable getDrawable(){return null;}
	}
}
