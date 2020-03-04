package com.moe.pussy.handle;
import com.moe.pussy.Handler;
import com.moe.pussy.Handler.Response;
import com.moe.pussy.Request;
import android.net.Uri;
import android.content.Context;
import android.content.res.Resources;
import com.moe.pussy.PussyDrawable;
import java.io.InputStream;
import android.graphics.drawable.Drawable;
import android.graphics.Canvas;

public class ResourceHandler implements Handler
{
	private Resources res;
	public ResourceHandler(Context context){
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
	public Handler.Response onHandle(Request request)
	{
		Uri uri=Uri.parse(request.getUrl());
		Drawable d=res.getDrawable(Integer.parseInt(uri.getHost()));
		return new ResResponse(new ResDrawable(d));
	}
	class ResResponse extends Response{
		private PussyDrawable pd;
		public ResResponse(PussyDrawable pd){
			this.pd=pd;
		}

		@Override
		public PussyDrawable getDrawable()
		{
			return pd;
		}



		
	}
	class ResDrawable extends PussyDrawable{
		private Drawable d;
		public ResDrawable(Drawable d){
			this.d=d;
		}

		@Override
		public void draw(Canvas p1)
		{
			d.draw(p1);
		}

		@Override
		public int getIntrinsicWidth()
		{
			return d.getIntrinsicWidth();
		}

		@Override
		public int getIntrinsicHeight()
		{
			return d.getIntrinsicHeight();
		}

		@Override
		public void setBounds(int left, int top, int right, int bottom)
		{
			d.setBounds(left, top, right, bottom);
		}

		@Override
		public void setAlpha(int p1)
		{
			d.setAlpha(p1);
		}

		@Override
		public int getOpacity()
		{
			return d.getOpacity();
		}

		
	}
}
