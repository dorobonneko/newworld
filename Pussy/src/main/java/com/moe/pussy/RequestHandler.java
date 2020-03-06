package com.moe.pussy;
import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import android.graphics.Bitmap;

public interface RequestHandler
{
	public boolean canHandle(Request request);
	public Response onHandle(Request request);
	public class Response{
		public File get() {return null;}
		public void close(){}
		public Bitmap getBitmap(){return null;}
	}
}
