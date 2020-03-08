package com.moe.pussy.handle;
import com.moe.pussy.RequestHandler;
import com.moe.pussy.RequestHandler.Response;
import com.moe.pussy.Request;
import android.net.Uri;
import android.content.Context;
import android.content.res.Resources;
import com.moe.pussy.PussyDrawable;
import java.io.InputStream;
import android.graphics.drawable.Drawable;
import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.moe.pussy.BitmapPool;

public class ResourceRequestHandler implements RequestHandler
{
	private Resources res;
	public ResourceRequestHandler(Context context){
		res=context.getResources();
	}
	@Override
	public boolean canHandle(Request request)
	{
		Uri uri=Uri.parse(request.getUrl());
		switch(uri.getScheme()){
			case "drawable":
				return true;
		}
		return false;
	}

	@Override
	public RequestHandler.Response onHandle(Request request)
	{
		Uri uri=Uri.parse(request.getUrl());
		BitmapFactory.Options options=new BitmapFactory.Options();

		options.inPreferredConfig=Bitmap.Config.ARGB_8888;
		options.inJustDecodeBounds=true;
		BitmapFactory.decodeResource(res,Integer.parseInt(uri.getHost()),options);
		options.inJustDecodeBounds=false;
		options.inBitmap=BitmapPool.getBitmap(options.outWidth,options.outHeight,options.outConfig);
		options.inMutable=true;
		return new ResResponse(BitmapFactory.decodeResource(res,Integer.parseInt(uri.getHost()),options));
	}
	class ResResponse extends Response{
		private Bitmap pd;
		public ResResponse(Bitmap pd){
			this.pd=pd;
		}

		@Override
		public Bitmap getBitmap()
		{
			return pd;
		}

		



		
	}
	
}
