package com.moe.pussy;
import android.content.Context;
import android.app.Fragment;
import android.view.View;
import javax.net.ssl.SSLSocketFactory;
import java.util.Map;
import java.util.HashMap;
import android.content.ComponentCallbacks2;
import android.content.res.Configuration;
import android.content.ComponentCallbacks;
import android.app.Application;
import android.os.Bundle;
import android.app.Activity;
import android.app.FragmentManager;
import java.util.Iterator;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.List;
import java.util.ArrayList;
import com.moe.pussy.handle.HandleThread;
import android.os.Looper;
import android.util.DisplayMetrics;
import com.moe.pussy.decode.BitmapDecoder;
import com.moe.pussy.handle.HttpRequestHandler;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ConcurrentHashMap;

public class Pussy
{
	private static Map<Context,Pussy> context_pussy=new HashMap<>();
	private static Map<Fragment,Pussy> fragment_pussy=new HashMap<>();
	private static Map<View,Pussy> view_pussy=new HashMap<>();
	static String userAgent="Pussy_1.0";
	private static SSLSocketFactory SSLSocketFactory;
	static long diskCacheSize=128 * 1024 * 1024;
	private ComponentCallbacks mComponentCallbacks;
	private Application.ActivityLifecycleCallbacks mActivityLifecycle;
	private Context mContext;
	DiskCache mDiskCache;

	Map<Target,Loader> loader_queue=new HashMap<>();
	private Map<String,HandleThread> request_handler=new ConcurrentHashMap<>();
	ThreadPoolExecutor mThreadPoolExecutor;
	static Decoder decoder;
	private Dispatcher mDispatcher;
	private static android.os.Handler mainHandler;
	private MemoryCache mMemoryCache;
	private ActiveResource mActiveResource;
	protected ThreadPoolExecutor netThreadPool,fileThreadPool;
	static{
		//初始化数据
		mainHandler = new android.os.Handler(Looper.getMainLooper());
		decoder = new BitmapDecoder();
	}
	private Pussy()
	{
		mMemoryCache=new MemoryCache();
		mActiveResource=new ActiveResource(this);
		final ThreadGroup group=new ThreadGroup("image load");
		ThreadFactory tf=new ThreadFactory(){

				@Override
				public Thread newThread(Runnable p1)
				{
					Thread t=new Thread(group,p1);
					t.setPriority(Thread.MAX_PRIORITY);
					t.setDaemon(true);
					return t;
				}

			};
		fileThreadPool = new ThreadPoolExecutor(32, 128, 1, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(),tf);//先进后出
		netThreadPool = new ThreadPoolExecutor(32, 64, 10, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(),tf);//先进后出
		mThreadPoolExecutor = new ThreadPoolExecutor(32, 128, 3, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(),tf);//先进后出
		
		}
	private void init(Context context)
	{
		this.mContext = context;
		mDispatcher =Dispatcher.getDefault(context);
		mDiskCache = DiskCache.get(this);
		if (mComponentCallbacks == null)
		{
			context.registerComponentCallbacks(mComponentCallbacks = new ComponentCallbacks3());
		}
		if (mActivityLifecycle == null)
			((Application)context.getApplicationContext()).registerActivityLifecycleCallbacks(mActivityLifecycle = new ActivityLifecycle()); 
	}
	public ActiveResource getActiveResource(){
		return mActiveResource;
	}
	public MemoryCache getMemoryCache(){
		return mMemoryCache;
	}
	public static Pussy $(Context context)
	{
		synchronized (context_pussy)
		{
			Pussy p=context_pussy.get(context);
			if (p == null)
			{
				context_pussy.put(context, p = new Pussy());
				p.init(context);
			}
			return p;
		}
	}
	public static Pussy $(Fragment fragment)
	{
		synchronized (fragment_pussy)
		{
			Pussy p=fragment_pussy.get(fragment);
			if (p == null)
			{
				fragment_pussy.put(fragment, p = new Pussy());
				p.init(fragment.getContext());
				fragment.getFragmentManager().registerFragmentLifecycleCallbacks(new Pussy.FragmentLifecycle(), false);
			}
			return p;
		}
	}
	public synchronized static Pussy $(View v)
	{
		synchronized (view_pussy)
		{
			Pussy p=view_pussy.get(v);
			if (p == null)
			{
				view_pussy.put(v, p = new Pussy());
				p.init(v.getContext());
				v.addOnAttachStateChangeListener(new ViewLifecycle());
			}
			return p;
		}
	}
	public Dispatcher getDispatcher(){
		return mDispatcher;
	}
	public static void post(Runnable run)
	{
		mainHandler.post(run);
	}
	public static void userAgent(String useragent)
	{
		Pussy.userAgent = useragent;
	}
	public static void sslSocketFactory(SSLSocketFactory ssf)
	{
		Pussy.SSLSocketFactory = ssf;
	}
	public static void diskCacheSize(long size)
	{
		Pussy.diskCacheSize = size;
	}
	public Request load(String url)
	{
		return new Request(this, url);
	}

	public Request load(int res)
	{
		return new Request(this, res);
	}
	public DiskCache getDiskCache()
	{
		return mDiskCache;
	}
	public SSLSocketFactory getSSLSocketFactory()
	{
		return SSLSocketFactory;
	}
	
	public HandleThread getHandleThread(String key)
	{
		return request_handler.get(key);
	}
	public void removeHandleThread(String key)
	{
		request_handler.remove(key);
	}
	public void putHandleThread(String key, HandleThread ht)
	{
		request_handler.put(key, ht);
		netThreadPool.execute(ht);
	}
	public void cancel(Target t)
	{
		if(t==null)return;
		Content content=t.getContent();
		if(content!=null){
		Resource res=getActiveResource().get(content.getKey());
		if(res!=null)
			res.release();
		}
		synchronized (loader_queue)
		{
			Loader l=loader_queue.remove(t);
			if (l != null)
			{
				//移出线程池
				mThreadPoolExecutor.remove(l);
				l.cancel();
			}
		}
	}
	
	//解码器
	public static void decoder(Decoder d)
	{
		decoder = d;
	}
	public Context getContext()
	{
		return mContext;
	}
	public void trimMemory()
	{
		mMemoryCache.trimToSize(10 * 1024 * 1024);
	}
	public void trimCache()
	{
		mDiskCache.trimToSize();
	}
	public void clearMemory()
	{
		mMemoryCache.trimToSize(0);
	}
	public void clearCache()
	{
		mDiskCache.clearCache();
	}
	public void release()
	{
		clearMemory();
		trimCache();
		mThreadPoolExecutor.shutdown();
		synchronized (loader_queue)
		{
			Iterator<Map.Entry<Target,Loader>> i=loader_queue.entrySet().iterator();
			while (i.hasNext())
			{
				Map.Entry<Target,Loader> entry=i.next();
				i.remove();
				Content content=entry.getKey().getContent();
				if(content!=null){
					Resource res=getActiveResource().get(content.getKey());
					if(res!=null)
						res.release();
				}
				entry.getValue().cancel();
			}
		}
	}
	int[] getScreenSize()
	{
		DisplayMetrics dm=mContext.getResources().getDisplayMetrics();
		return new int[]{dm.widthPixels,dm.heightPixels};
	}
	class ComponentCallbacks3 implements ComponentCallbacks2
	{

		@Override
		public void onConfigurationChanged(Configuration p1)
		{
		}

		@Override
		public void onLowMemory()
		{
			synchronized (view_pussy)
			{
				Iterator<Pussy> p_i=view_pussy.values().iterator();
				while (p_i.hasNext())
				{
					Pussy p=p_i.next();
					if (p != null)
						p.clearMemory();
				}
			}
			synchronized (fragment_pussy)
			{
				Iterator<Pussy> p_i=fragment_pussy.values().iterator();
				while (p_i.hasNext())
				{
					Pussy p=p_i.next();
					if (p != null)
						p.clearMemory();
				}
			}
			synchronized (context_pussy)
			{
				Iterator<Pussy> p_i=context_pussy.values().iterator();
				while (p_i.hasNext())
				{
					Pussy p=p_i.next();
					if (p != null)
						p.clearMemory();
				}
			}
		}

		@Override
		public void onTrimMemory(int p1)
		{
			synchronized (view_pussy)
			{
				Iterator<Pussy> p_i=view_pussy.values().iterator();
				while (p_i.hasNext())
				{
					Pussy p=p_i.next();
					if (p != null)
						p.trimMemory();
				}
			}
			synchronized (fragment_pussy)
			{
				Iterator<Pussy> p_i=fragment_pussy.values().iterator();
				while (p_i.hasNext())
				{
					Pussy p=p_i.next();
					if (p != null)
						p.trimMemory();
				}
			}
			synchronized (context_pussy)
			{
				Iterator<Pussy> p_i=context_pussy.values().iterator();
				while (p_i.hasNext())
				{
					Pussy p=p_i.next();
					if (p != null)
						p.trimMemory();
				}
			}
		}
	}
	class ActivityLifecycle implements Application.ActivityLifecycleCallbacks
	{

		@Override
		public void onActivityCreated(Activity p1, Bundle p2)
		{
		}

		@Override
		public void onActivityStarted(Activity p1)
		{
		}

		@Override
		public void onActivityResumed(Activity p1)
		{
		}

		@Override
		public void onActivityPaused(Activity p1)
		{
		}

		@Override
		public void onActivityStopped(Activity p1)
		{
		}

		@Override
		public void onActivitySaveInstanceState(Activity p1, Bundle p2)
		{
		}

		@Override
		public void onActivityDestroyed(Activity p1)
		{
			synchronized (context_pussy)
			{
				Pussy p=context_pussy.remove(p1);
				if (p != null)
					p.release();
			}
		}
	}
	static class FragmentLifecycle extends FragmentManager.FragmentLifecycleCallbacks
	{

		@Override
		public void onFragmentDestroyed(FragmentManager fm, Fragment f)
		{
			synchronized (fragment_pussy)
			{
				fm.unregisterFragmentLifecycleCallbacks(this);
				Pussy p=fragment_pussy.remove(f);
				if (p != null)
					p.release();
			}
		}

	}
	static class ViewLifecycle implements View.OnAttachStateChangeListener
	{

		@Override
		public void onViewAttachedToWindow(View p1)
		{
		}

		@Override
		public void onViewDetachedFromWindow(View p1)
		{
			synchronized (view_pussy)
			{
				p1.removeOnAttachStateChangeListener(this);
				Pussy p=view_pussy.remove(p1);
				if (p != null)
					p.release();
			}
		}
	}
	public static class Refresh
	{
		private Content l;
		public Refresh(Content l)
		{
			this.l = l;
		}
		public void refresh()
		{
			l.into(l.getTarget());
		}
	}
}
