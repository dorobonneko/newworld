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

public class Loader implements Runnable,HandleThread.Callback
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
			out:
				{
					if (cancel)return;
					File cache_file=dc.getCache(content.getKey());
					if (cache_file.exists())
					{
						//解码
						Bitmap[] p=pussy.decoder.decode(cache_file);
						if (p[0] != null)
						{
							if (cancel)return;
							success(p[0],null);
							return;
						}
						else
						{
							cache_file.delete();
						}
					}
					cache_file = dc.getCache(request.getKey());
					if (cache_file.exists())
					{
						//原始缓存
						Bitmap[] p=pussy.decoder.decode(cache_file);
						for (int i=0;i < p.length;i++)
						{
							p[i] = content.getTarget().onResourceReady(p[i], content.getTransformer());
						}
						//缓存转变过的图片
						if (p.length == 0 || p[0] == null)
						{
						/*	//无法解码
							cache_file.delete();
							//查询磁盘缓存（如果有，重新解码加载）
							if (cancel)return;
							//加载数据
							HandleThread ht=pussy.getHandleThread(request.getKey());
							if (ht == null)
								pussy.putHandleThread(request.getKey(), ht = new HandleThread(request));
							ht.addCallback(this);
							//handler加载*/
						}
						else
						{
							if(content.getCache()==DiskCache.Cache.MASK)
							p[0].compress(Bitmap.CompressFormat.WEBP,99,new FileOutputStream(dc.getCache(content.getKey())));
							if (cancel)return;
							success(p[0],null);
							return;
						}
					}else{
						if (cancel)return;
						//加载数据
						HandleThread ht=pussy.getHandleThread(request.getKey());
						if (ht == null)
							pussy.putHandleThread(request.getKey(), ht = new HandleThread(request));
						ht.addCallback(this);
					}

				}

			//解码加载
		}
		catch (final Exception e)
		{
			pussy.post(new Runnable(){
					public void run()
					{
						Target t=content.getTarget();
						if(t!=null)t.error(e, content.error);
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
			success(p,null);

		}
		else
		if (response.get() == null)
			success(null,null);
		else
		{
			File input=response.get();

			//加入缓存
			if (cancel)return;
			Bitmap[] p=pussy.decoder.decode(input);
			for (int i=0;i < p.length;i++)
			{
				p[i] = content.getTarget().onResourceReady(p[i], content.getTransformer());
			}
			if (p.length == 0 || p[0] == null)
			{
				//success(null,null);
			}
			else
			{
				try
				{
					if (content.getCache() == DiskCache.Cache.MASK)
						p[0].compress(Bitmap.CompressFormat.WEBP, 99, new FileOutputStream(pussy.getDiskCache().getCache(content.getKey())));
				}
				catch (FileNotFoundException e)
				{}
				//putMemory(content.getKey(), p[0]);
				if (cancel)return;
				success(p[0],null);
			}
		}
	}




	
	private void success(Bitmap bitmap,Throwable e)
	{
		if (bitmap == null)
		{
			pussy.post(new Runnable(){
					public void run()
					{
						Target t=content.getTarget();
						if(t!=null)t.error(new NullPointerException("decoder error"), content.error);
					}
				});
		}
		else
		{
			pussy.getDiskCache().invalidate(content.getKey());
			Resource res=new Resource(content.getKey(),bitmap);
			res.acquire();
			pussy.getActiveResource().add(res);
			//content.getRequest().getPussy().getMemoryCache().put(content.getKey(), bitmap);
			final PussyDrawable pd=new PussyDrawable(bitmap, content.getRefresh());
			pussy.post(new Runnable(){
					public void run()
					{
						Target t=content.getTarget();
						if(t!=null)t.onSucccess(pd);
					}
				});}
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
