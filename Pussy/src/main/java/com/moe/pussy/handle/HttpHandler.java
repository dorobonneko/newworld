package com.moe.pussy.handle;
import com.moe.pussy.Handler;
import com.moe.pussy.Handler.Response;
import com.moe.pussy.Request;
import android.net.Uri;
import java.io.InputStream;
import com.moe.pussy.PussyDrawable;
import java.net.URLConnection;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import java.util.Iterator;
import java.util.Map;
import java.io.File;
import com.moe.pussy.DiskCache;
import java.io.FileInputStream;

public class HttpHandler implements Handler
{

	@Override
	public boolean canHandle(Request request)
	{
		switch(Uri.parse(request.getUrl()).getScheme()){
			case "http":
			case "https":
				return true;
		}
		return false;
	}

	@Override
	public Handler.Response onHandle(Request request)
	{
		HttpResponse hrs=new HttpResponse();
		if(request.isCancel())
			return hrs;
		try
		{
			HttpURLConnection huc=(HttpURLConnection) new URL(request.getUrl()).openConnection();
			if(huc instanceof HttpsURLConnection){
				SSLSocketFactory ssf=request.getPussy().getSSLSocketFactory();
				if(ssf!=null)
					((HttpsURLConnection)huc).setSSLSocketFactory(ssf);
			}
			Iterator<Map.Entry<String,String>> header_i=request.getHeader().entrySet().iterator();
			while(header_i.hasNext()){
				Map.Entry<String,String> entry=header_i.next();
				huc.setRequestProperty(entry.getKey(),entry.getValue());
			}
			File tmp=request.getPussy().getDiskCache().getTmp(request.getKey());
			huc.setRequestProperty("Range", "bytes=".concat(tmp.exists()?String.valueOf(tmp.length()):"0").concat("-"));
			huc.setFollowRedirects(true);
			if(request.isCancel())return hrs;
			int code=huc.getResponseCode();
			if(code==301||code==302){
				String location=huc.getHeaderField("Location");
				if(location!=null){
					request.setLocation(location);
					huc.disconnect();
					return onHandle(request);
				}
			}
			DiskCache dc=request.getPussy().getDiskCache();
			if(tmp.length()>0&&code==200)
				huc.getInputStream().skip(tmp.length());
			InputStream input= dc.getInputStream(tmp,huc.getInputStream());
			while(input.read()!=-1&&!request.isCancel());
			input.close();
			huc.disconnect();
			if(request.isCancel())
				return hrs;
				tmp.renameTo(dc.getCache(request.getKey()));
				hrs.set(dc.getCache(request.getKey()));
			}
		catch (IOException e)
		{}
		return hrs;
	}
	class HttpResponse extends Response
	{
		private File in;
		public void set(File in){
			this.in= in;
		}
		@Override
		public InputStream get() throws IOException
		{
			if(in!=null)
			return new FileInputStream(in);
			return null;
		}

		

		
	}
}
