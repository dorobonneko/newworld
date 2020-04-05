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
import java.util.concurrent.RejectedExecutionException;

public class Loader implements Runnable,HandleThread.Callback,SizeReady
{
	private WeakReference<Content> content;
	//loader绑定一个target，多个loader可绑定一个handler
	private Bitmap resource;
	private int w,h;
	public Loader(Content content)
	{
		this.content = new WeakReference<>(content);
		//request = content.getRequest();
		//pussy = request.getPussy();
	}
	private boolean isCancel()
	{
		return content.get()==null||content.get().getTarget() == null;
	}
	Pussy getPussy()
	{
		return content.get().getRequest().getPussy();
	}
	Request getRequest()
	{
		return content.get().getRequest();
	}
	Target getTarget()
	{
		return content.get().getTarget();
	}
	public void begin()
	{
		Pussy.checkThread(true);
		Resource res=content.get().getRequest().getPussy().getActiveResource().get(content.get().getKey());
			if (res != null)
			{
				res.acquire();
				getPussy().getDiskCache().invalidate(content.get().getKey());
				Target target=getTarget();
				if (target != null)
					target.onSuccess(new PussyDrawable(res.bitmap, target, content.get().getRefresh()));
			}
			else
			{
				Bitmap bitmap=getPussy().getMemoryCache().remove(content.get().getKey());
				if (bitmap != null)
				{
					res = new Resource(content.get().getKey(), bitmap);
					res.acquire();
					getPussy().getActiveResource().add(res);
					getPussy().getDiskCache().invalidate(content.get().getKey());
					Target target=getTarget();
					if (target != null)
						target.onSuccess(new PussyDrawable(bitmap, target, content.get().getRefresh()));
				}
				else{
					try
					{
						Pussy.checkThread(true);
						DiskCache dc=getPussy().getDiskCache();
						//查询内存缓存
						File cache_file=dc.getCache(content.get().getKey());
						if (cache_file != null)
						{
							if (isCancel())return;
							//解码
							Bitmap bitamp=getPussy().decoder.decode(getPussy().getBitmapPool(), cache_file, 0, 0);
							success(bitamp, null);
							//getTarget().onResourceReady(cache_file);
							//getPussy().fileThreadPool.execute(new BitmapLoader(getPussy().getBitmapPool(),getPussy().decoder, content.get().getKey(), cache_file, this));

						}
						else
						{
							cache_file = dc.getCache(getRequest().getKey());
							if (cache_file != null)
							{
								//原始缓存
								if (isCancel())return;
								getTarget().onResourceReady(cache_file);
								//getPussy().fileThreadPool.execute(new BitmapLoader(getPussy().getBitmapPool(),getPussy().decoder, getRequest().getKey(), cache_file, this));
							}
							else
							{
								//加载数据
								if (isCancel())return;
								HandleThread ht=getPussy().request_handler.get(getRequest().getKey());
								if (ht == null)
									getPussy().request_handler.put(getRequest().getKey(), ht = new HandleThread(getRequest(), getPussy().mThreadPoolExecutor));
								ht.addCallback(this);
							}

						}

						//解码加载
					}finally{}
					/*catch (final NullPointerException e)
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
					}*/
				}
				
			}
	}

	@Override
	public void onSizeReady(int w, int h)
	{this.w=w;this.h=h;
		Pussy.checkThread(true);
		if (isCancel())return;
		try{getPussy().mThreadPoolExecutor.execute(this);}catch(RejectedExecutionException e){}
	}


	@Override
	public void run()
	{
		if(isCancel())return;
		synchronized (getTarget())
		{
			Bitmap source=null;
			File cache=getPussy().getDiskCache().getCache(getRequest().getKey());
			if (cache != null)
				source = getPussy().decoder.decode(getPussy().getBitmapPool(), cache, w, h);
			else
				source = resource;
				if(source==null){
					if(cache!=null)
				cache.delete();
				success(null, new NullPointerException("possible bitmap decoder error"));
			return;}
			for (Transformer transformer:content.get().getTransformer())
			{
				source = transformer.onTransformer(getPussy().mBitmapPool, source, w, h);
			}
			success(source,null);
			try
			{
				if (content.get().getCache() == DiskCache.Cache.MASK)
					source.compress(Bitmap.CompressFormat.WEBP, 99, new FileOutputStream(getPussy().getDiskCache().getCache(content.get().getKey())));
			}
			catch (FileNotFoundException e)
			{}
			source = null;
			resource = null;
		}
		
	}

	@Override
	public void onResponse(RequestHandler.Response response)
	{
		Pussy.checkThread(true);
		if (response.getBitmap() != null)
		{
			if (isCancel())
				getPussy().getBitmapPool().recycle(response.getBitmap());
			else
			{
				resource=response.getBitmap();
				//resource=getPussy().getBitmapPool().getBitmap(response.getBitmap().getWidth(),response.getBitmap().getHeight(),response.getBitmap().getConfig());
				//int[] pix=new int[response.getBitmap().getWidth()*response.getBitmap().getHeight()];
				//response.getBitmap().getPixels(pix,0,resource.getWidth(),0,0,resource.getWidth(),resource.getHeight());
				//resource.setPixels(pix,0,resource.getWidth(),0,0,resource.getWidth(),resource.getHeight());
				getTarget().onResourceReady(null);
			}
		}
		else if (response.get() == null)
			success(null, null);
		else
		{
			File input=response.get();

			//加入缓存
			if (!isCancel())
			{
				getTarget().onResourceReady(input);
			}
		}
	}





	private void success(final Bitmap bitmap, final Throwable e)
	{
		//Pussy.checkThread(false);
		if (isCancel())
		{
			if (bitmap != null)
				getPussy().getMemoryCache().put(content.get().getKey(), bitmap);
			return;
		}
		if (bitmap == null)
		{
			getPussy().post(new Runnable(){
					public void run()
					{
						Target t=getTarget();
						if (t != null)t.error(e, content.get().error);
					}
				});
		}
		else
		{
			//content.getRequest().getPussy().getMemoryCache().put(content.getKey(), bitmap);
			getPussy().post(new Runnable(){
					public void run()
					{
						Target t=getTarget();
						if (t != null)
						{
							getPussy().getDiskCache().invalidate(content.get().getKey());
							Resource res=new Resource(content.get().getKey(), bitmap);
							res.acquire();
							getPussy().getActiveResource().add(res);
							t.onSuccess(new PussyDrawable(bitmap, getTarget(), content.get().getRefresh()));
						}
					}
				});}
	}

	/*@Override
	 public void onSuccess(String key, Bitmap bitmap, File file)
	 {
	 Pussy.checkThread(false);
	 if (isCancel())
	 {
	 getPussy().getBitmapPool().recycle(bitmap);
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
	 getPussy().fileThreadPool.execute(new BitmapLoader(getPussy().getBitmapPool(),getPussy().decoder, content.get().getKey(), cache_file, this));
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
	 HandleThread ht=getPussy().request_handler.get(getRequest().getKey());
	 if (ht == null)
	 getPussy().request_handler.put(getRequest().getKey(), ht = new HandleThread(getRequest(), getPussy().netThreadPool));
	 ht.addCallback(this);
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
	 getPussy().getBitmapPool().recycle(bitmap);
	 }
	 }
	 }
	 }

	 */


}
