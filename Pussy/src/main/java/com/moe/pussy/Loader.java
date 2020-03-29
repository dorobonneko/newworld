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
import android.widget.Toast;
import java.lang.ref.WeakReference;

public class Loader implements Runnable,HandleThread.Callback,BitmapLoader.Callback,SizeReady
{
	private WeakReference<Content> content;
	private Bitmap source;
	private Object locked=new Object();
	//loader绑定一个target，多个loader可绑定一个handler
	public Loader(Content content)
	{
		this.content =new WeakReference<>(content);
		//request = content.getRequest();
		//pussy = request.getPussy();
	}
	private boolean isCancel()
	{
		return content.get().getTarget() == null;
	}
	Pussy getPussy(){
		return content.get().getRequest().getPussy();
	}
	Request getRequest(){
		return content.get().getRequest();
	}
	Target getTarget(){
		return content.get().getTarget();
	}
	public void begin()
	{
		Resource res=content.get().getRequest().getPussy().getActiveResource().get(content.get().getKey());
		if (res != null)
		{
			res.acquire();
			getPussy().getDiskCache().invalidate(content.get().getKey());
			Target target=getTarget();
			if (target != null)
				target.onSucccess(new PussyDrawable(res.bitmap,target, content.get().getRefresh()));
			return;
		}
		Bitmap bitmap=getPussy().getMemoryCache().remove(content.get().getKey());
		if (bitmap != null)
		{
			res = new Resource(content.get().getKey(), bitmap);
			res.acquire();
			getPussy().getActiveResource().add(res);
			getPussy().getDiskCache().invalidate(content.get().getKey());
			Target target=getTarget();
			if (target != null)
				target.onSucccess(new PussyDrawable(bitmap,target, content.get().getRefresh()));
			return;
		}
		getPussy().mThreadPoolExecutor.execute(this);
	}

	@Override
	public void onSizeReady(int w, int h)
	{
		synchronized(locked){
		if (isCancel() || source == null)return;
		Target t=getTarget();
		if (t == null)return;
		for (Transformer transformer:content.get().getTransformer())
		{
			source = transformer.onTransformer(BitmapPool.get(), source, w, h);
		}
		if(source==null){
			success(null,new NullPointerException("possible bitmap transformer error"));
			return;}
		try
		{
			if (content.get().getCache() == DiskCache.Cache.MASK)
				source.compress(Bitmap.CompressFormat.WEBP, 99, new FileOutputStream(getPussy().getDiskCache().getCache(content.get().getKey())));
		}
		catch (FileNotFoundException e)
		{}
		success(source,null);
		source=null;
		}
	}


	@Override
	public void run()
	{
		try
		{
			DiskCache dc=getPussy().getDiskCache();
			//查询内存缓存
			File cache_file=dc.getCache(content.get().getKey());
			if (cache_file != null)
			{
				if (isCancel())return;
				//解码
				getPussy().fileThreadPool.execute(new BitmapLoader(getPussy().decoder, content.get().getKey(), cache_file, this));

			}
			else
			{
				cache_file = dc.getCache(getRequest().getKey());
				if (cache_file != null)
				{
					//原始缓存
					if (isCancel())return;
					getPussy().fileThreadPool.execute(new BitmapLoader(getPussy().decoder, getRequest().getKey(), cache_file, this));
				}
				else
				{
					//加载数据
					if (isCancel())return;
					synchronized(Uid.getLock(getRequest().getKey())){
					HandleThread ht=getPussy().request_handler.get(getRequest().getKey());
					if (ht == null)
						getPussy().request_handler.put(getRequest().getKey(), ht = new HandleThread(getRequest(), getPussy().netThreadPool));
					ht.addCallback(this);
					}
				}

			}

			//解码加载
		}
		catch (final Exception e)
		{
			//Toast.makeText(pussy.getContext(), content.tag(), Toast.LENGTH_SHORT).show();
			if (isCancel())return;
			getPussy().post(new Runnable(){
					public void run()
					{
						Target t=getTarget();
						if (t != null)t.error(e, content.get().error);
					}
				});
		}
	}

	@Override
	public void onResponse(RequestHandler.Response response)
	{
		if (response.getBitmap() != null)
		{
			if (isCancel())
				BitmapPool.recycle(response.getBitmap());
			else
			{
				//来自软件内部，直接显示
				source = response.getBitmap();
				getTarget().onResourceReady(response.getBitmap(), content.get().getTransformer());
			}
		}else if (response.get() == null)
			success(null, null);
		else
		{
			File input=response.get();

			//加入缓存
			getPussy().fileThreadPool.execute(new BitmapLoader(getPussy().decoder, getRequest().getKey(), input, this));

		}
	}





	private void success(final Bitmap bitmap, Throwable e)
	{
		if (isCancel())
		{
			if(bitmap!=null)
			BitmapPool.recycle(bitmap);
			return;
		}
		if (bitmap == null)
		{
			//Toast.makeText(pussy.getContext(), content.tag(), Toast.LENGTH_SHORT).show();
			getPussy().post(new Runnable(){
					public void run()
					{
						Target t=getTarget();
						if (t != null)t.error(new NullPointerException("decoder error"), content.get().error);
					}
				});
		}
		else
		{
			getPussy().getDiskCache().invalidate(content.get().getKey());
			Resource res=new Resource(content.get().getKey(), bitmap);
			res.acquire();
			getPussy().getActiveResource().add(res);
			//content.getRequest().getPussy().getMemoryCache().put(content.getKey(), bitmap);
			getPussy().post(new Runnable(){
					public void run()
					{
						Target t=getTarget();
						if (t != null)t.onSucccess(new PussyDrawable(bitmap,getTarget(), content.get().getRefresh()));
					}
				});}
	}

	@Override
	public void onSuccess(String key, Bitmap bitmap, File file)
	{
		if (isCancel())
		{
			BitmapPool.recycle(bitmap);
			return;
		}
		source = bitmap;
		if (key.equals(content.get().getKey()))
		{
			if (bitmap == null)
			{
				file.delete();
				File cache_file = getPussy().getDiskCache().getCache(getRequest().getKey());
				if (cache_file.exists())
				//原始缓存
					getPussy().fileThreadPool.execute(new BitmapLoader(getPussy().decoder, content.get().getKey(), cache_file, this));
			}
			else
			{
				success(bitmap, null);
			}
		}
		else
		{
			if (bitmap == null)
			{
				file.delete();
				synchronized(Uid.getLock(getRequest().getKey())){
				HandleThread ht=getPussy().request_handler.get(getRequest().getKey());
				if (ht == null)
					getPussy().request_handler.put(getRequest().getKey(), ht = new HandleThread(getRequest(), getPussy().netThreadPool));
				ht.addCallback(this);
				}
			}
			else
			{
				if (getTarget() != null)
				{
					getTarget().onResourceReady(bitmap, content.get().getTransformer());
//					if (bitmap == null)return;
//					try
//					{
//						if (content.getCache() == DiskCache.Cache.MASK)
//							bitmap.compress(Bitmap.CompressFormat.WEBP, 99, new FileOutputStream(pussy.getDiskCache().getCache(content.getKey())));
//					}
//					catch (FileNotFoundException e)
//					{}
//					success(bitmap, null);
				}
				else
				{
					BitmapPool.recycle(bitmap);
				}
			}
		}
	}




}
