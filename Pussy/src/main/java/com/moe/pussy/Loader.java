package com.moe.pussy;
import java.io.File;
import java.io.FileInputStream;
import android.graphics.Bitmap;
import java.io.FileOutputStream;
import com.moe.pussy.handle.HandleThread;
import java.io.InputStream;
import java.io.IOException;
import com.moe.pussy.RequestHandler.Response;
import java.io.FileNotFoundException;

public class Loader implements Runnable,HandleThread.Callback,BitmapLoader.Callback
{
	private Content content;
	private boolean cancel;
	private Pussy pussy;
	private Request request;
	//loader绑定一个target，多个loader可绑定一个handler
	public Loader(Content content)
	{
		this.content = content;
		request = content.getRequest();
		pussy = request.getPussy();
	}

	@Override
	public void run()
	{
		try
		{
			DiskCache dc=pussy.getDiskCache();
			//查询内存缓存
			if (cancel)return;
			File cache_file=dc.getCache(content.getKey());
			if (cache_file.exists())
			{
				//解码
				pussy.fileThreadPool.execute(new BitmapLoader(pussy.decoder,content.getKey(),cache_file,this));
				
			}
			cache_file = dc.getCache(request.getKey());
			if (cache_file.exists())
			{
				//原始缓存
				pussy.fileThreadPool.execute(new BitmapLoader(pussy.decoder,request.getKey(),cache_file,this));
			}
			else
			{
				if (cancel)return;
				//加载数据
				HandleThread ht=pussy.getHandleThread(request.getKey());
				if (ht == null)
					pussy.putHandleThread(request.getKey(), ht = new HandleThread(request, pussy.netThreadPool));
				ht.addCallback(this);
			}

			

			//解码加载
		}
		catch (final Exception e)
		{
			pussy.post(new Runnable(){
					public void run()
					{
						Target t=content.getTarget();
						if (t != null)t.error(e, content.error);
					}
				});
		}
	}

	@Override
	public void onResponse(RequestHandler.Response response)
	{
		if (cancel)return;
		if (response.getBitmap() != null)
		{
			//来自软件内部，直接显示
			Bitmap p = content.getTarget().onResourceReady(response.getBitmap(), content.getTransformer());
			if (p == null)return;//不做后续处理

			if (cancel)return;
			//putMemory(content.getKey(), p);
			success(p, null);

		}
		else
		if (response.get() == null)
			success(null, null);
		else
		{
			File input=response.get();

			//加入缓存
			if (cancel)return;
			pussy.fileThreadPool.execute(new BitmapLoader(pussy.decoder,request.getKey(),input,this));
			
		}
	}





	private void success(Bitmap bitmap, Throwable e)
	{
		if (bitmap == null)
		{
			pussy.post(new Runnable(){
					public void run()
					{
						Target t=content.getTarget();
						if (t != null)t.error(new NullPointerException("decoder error"), content.error);
					}
				});
		}
		else
		{
			pussy.getDiskCache().invalidate(content.getKey());
			Resource res=new Resource(content.getKey(), bitmap);
			res.acquire();
			pussy.getActiveResource().add(res);
			//content.getRequest().getPussy().getMemoryCache().put(content.getKey(), bitmap);
			final PussyDrawable pd=new PussyDrawable(bitmap, content.getRefresh());
			pussy.post(new Runnable(){
					public void run()
					{
						Target t=content.getTarget();
						if (t != null)t.onSucccess(pd);
					}
				});}
	}

	@Override
	public void onSuccess(String key, Bitmap bitmap, File file)
	{
		if(cancel){
			BitmapPool.recycle(bitmap);
			return;
		}
		if(key.equals(content.getKey())){
			if(bitmap==null){
				file.delete();
				File cache_file = pussy.getDiskCache().getCache(request.getKey());
				if (cache_file.exists())
					//原始缓存
					pussy.fileThreadPool.execute(new BitmapLoader(pussy.decoder,content.getKey(),cache_file,this));
				}else{
					success(bitmap,null);
				}
		}else{
			if(bitmap==null){
				file.delete();
				HandleThread ht=pussy.getHandleThread(request.getKey());
				if (ht == null)
					pussy.putHandleThread(request.getKey(), ht = new HandleThread(request, pussy.netThreadPool));
				ht.addCallback(this);
			}else{
				if(content.getTarget()!=null){
					bitmap=content.getTarget().onResourceReady(bitmap,content.getTransformer());
					if(bitmap==null)return;
					try
					{
						if (content.getCache() == DiskCache.Cache.MASK)
							bitmap.compress(Bitmap.CompressFormat.WEBP, 99, new FileOutputStream(pussy.getDiskCache().getCache(content.getKey())));
					}
					catch (FileNotFoundException e)
					{}
					success(bitmap,null);
				}else{
					BitmapPool.recycle(bitmap);
				}
			}
		}
	}





	public void cancel()
	{
		HandleThread ht=pussy.getHandleThread(request.getKey());
		if (ht != null)
			ht.removeCallback(this);
		cancel = true;

	}
	public void reset()
	{
		cancel = false;
	}
}