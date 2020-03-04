package com.moe.pussy;
import java.io.File;
import java.io.FileInputStream;
import android.graphics.Bitmap;
import java.io.FileOutputStream;
import com.moe.pussy.handle.HandleThread;
import java.io.InputStream;
import java.io.IOException;

public class Loader implements Runnable
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
		pussy=request.getPussy();
	}
	
	@Override
	public void run()
	{
		try
		{
			if (cancel)return;
			MemoryCache mc=pussy.mMemoryCache;
			DiskCache dc=pussy.mDiskCache;
			
			{
			final Bitmap pd=mc.get(content.getKey());
			if (pd != null)
			{
				if(pd.isRecycled()){
					mc.remove(content.getKey());
				}else{
				pussy.mDiskCache.invalidate(content.getKey());
				pussy.post(new Runnable(){
						public void run()
						{
							content.getTarget().onSucccess(new PussyDrawable(pd,content.getRefresh()));
						}
					});
				return;
				}
			}
			}
			//查询内存缓存
			if (cancel)return;
			DiskCache.Cache cache=content.getCache();
			switch (cache)
			{
				case MASK:
				if (cancel)return;
					File cache_file=dc.getCache(content.getKey());
					if (cache_file.exists())
					{
						//解码
						final PussyDrawable p=pussy.decoder.decode(new FileInputStream(cache_file));
						p.setRefresh(content.getRefresh());
						mc.put(content.getKey(), p.getBitmap());
						pussy.mDiskCache.invalidate(content.getKey());
						pussy.post(new Runnable(){
								public void run()
								{
									content.getTarget().onSucccess(p);
								}
							});
						return;
					}
					cache_file = dc.getCache(request.getKey());
					if (cache_file.exists())
					{
						//原始缓存
						PussyDrawable p=pussy.decoder.decode(new FileInputStream(cache_file));
						p = content.getTarget().onResourceReady(p, content.getTransformer());
						//缓存转变过的图片
						if(p==null)return;
						mc.put(content.getKey(), p.getBitmap());
						final PussyDrawable pd=p;
						p.setRefresh(content.getRefresh());
						pussy.post(new Runnable(){
								public void run()
								{
									content.getTarget().onSucccess(pd);
								}
							});
						return;
					}
					break;
			}
			//查询磁盘缓存（如果有，重新解码加载）
			if(cancel)return;
				//加载数据
				HandleThread ht=pussy.getHandleThread(request.getKey());
				if(ht!=null){
					toString();
				}
				if(ht==null)
					pussy.putHandleThread(request.getKey(),ht=new HandleThread(request));
				final Handler.Response response=ht.get();//线程阻塞
				
			
			//handler加载
			if(response.getDrawable()!=null){
				//来自软件内部，直接显示
				//mc.put(content.getKey(),response.getDrawable());
				pussy.post(new Runnable(){
					public void run(){
						content.getTarget().onSucccess(response.getDrawable());
						}
						});
				return;
			}
			if(response.get()==null)
				throw new IOException("empty stream");
			InputStream input=response.get();
			
			//加入缓存
			if(cancel){input.close(); return;}
			PussyDrawable p=pussy.decoder.decode(input);
			p = content.getTarget().onResourceReady(p, content.getTransformer());
			if(p==null)return;
			mc.put(content.getKey(), p.getBitmap());
			if(cancel)return;
			final PussyDrawable pd=p;
			p.setRefresh(content.getRefresh());
			pussy.post(new Runnable(){
					public void run()
					{
						content.getTarget().onSucccess(pd);
					}
				});
			//解码加载
		}
		catch (final Exception e)
		{
			pussy.post(new Runnable(){
					public void run()
					{
						content.getTarget().onFailed(e);
					}
				});
		}
	}



	public void cancel()
	{
		cancel = true;
		/*HandleThread ht=pussy.getHandleThread(request.getKey());
		if(ht!=null)
			ht.close();*/
	}
	public void reset()
	{
		cancel = false;
	}
}
