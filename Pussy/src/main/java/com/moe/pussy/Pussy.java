package com.moe.pussy;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ComponentCallbacks;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.View;
import com.moe.pussy.handle.HandleThread;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLSocketFactory;
import android.app.Application;
import java.util.ArrayList;
import java.util.List;

public class Pussy
{
	private static Map<Context,Pussy> context_pussy=new ConcurrentHashMap<>();
	private static Map<Fragment,Pussy> fragment_pussy=new ConcurrentHashMap<>();
	private static Map<View,Pussy> view_pussy=new ConcurrentHashMap<>();
	protected String userAgent="Pussy_1.0";
	private SSLSocketFactory SSLSocketFactory;
	private ComponentCallbacks mComponentCallbacks;
	private Application.ActivityLifecycleCallbacks mActivityLifecycle;
	private WeakReference<Context> mContext;
	protected DiskCache mDiskCache;

	//Map<Target,Loader> loader_queue=new HashMap<>();
	protected static Map<String,HandleThread> request_handler=new ConcurrentHashMap<>();
	protected ThreadPoolExecutor mThreadPoolExecutor;
	protected Decoder decoder;
	private Dispatcher mDispatcher;
	private static android.os.Handler mainHandler;
	private MemoryCache mMemoryCache;
	private ActiveResource mActiveResource;
	protected ThreadPoolExecutor netThreadPool,fileThreadPool;
	protected static BitmapPool mBitmapPool;
	static{
		//初始化数据
		mainHandler = new android.os.Handler(Looper.getMainLooper());
		mBitmapPool = new BitmapPool(Runtime.getRuntime().maxMemory() / 8);
	}
	private Pussy()
	{
		userAgent = PussyConfig.userAgent;
		SSLSocketFactory = PussyConfig.mSSLSocketFactory;
		decoder = PussyConfig.mDecoder;
		mMemoryCache = new MemoryCache(mBitmapPool);
		mActiveResource = new ActiveResource(this);
		final ThreadGroup group=new ThreadGroup("image load");
		ThreadFactory tf=new ThreadFactory(){

			@Override
			public Thread newThread(Runnable p1)
			{
				Thread t=new Thread(group, p1);
				t.setPriority(Thread.MAX_PRIORITY);
				t.setDaemon(true);
				return t;
			}

		};
		fileThreadPool = new ThreadPoolExecutor(64, 128, 1, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(), tf);//先进后出
		netThreadPool = new ThreadPoolExecutor(128, 512, 10, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(), tf);//先进后出
		mThreadPoolExecutor = new ThreadPoolExecutor(32, 128, 3, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(), tf);//先进后出

	}
	private void init(Context context)
	{
		this.mContext = new WeakReference<Context>(context.getApplicationContext());
		mDispatcher = Dispatcher.getDefault(context.getApplicationContext());
		mDiskCache = DiskCache.get(this);
		if (mComponentCallbacks == null)
		{
			context.registerComponentCallbacks(mComponentCallbacks = new ComponentCallbacks3());
		}
		if (mActivityLifecycle == null)
			((Application)context.getApplicationContext()).registerActivityLifecycleCallbacks(mActivityLifecycle = new ActivityLifecycle()); 
	}
	public ActiveResource getActiveResource()
	{
		return mActiveResource;
	}
	public MemoryCache getMemoryCache()
	{
		return mMemoryCache;
	}
	public static Pussy $(Context context)
	{
		Pussy.checkThread(true);
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
		Pussy.checkThread(true);
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
	public static Pussy $(View v)
	{
		Pussy.checkThread(true);
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
	public BitmapPool getBitmapPool()
	{
		return mBitmapPool;
	}
	public Dispatcher getDispatcher()
	{
		return mDispatcher;
	}
	public static void checkThread(boolean main)
	{
		if (main && !Thread.currentThread().getName().equalsIgnoreCase("main"))
		{
			throw new RuntimeException("not main thread");
		}
		else if (!main && Thread.currentThread().getName().equalsIgnoreCase("main"))
		{
			throw new RuntimeException("not third thread");
		}
	}
	public static void post(Runnable run)
	{
		mainHandler.post(run);
	}
	public void userAgent(String useragent)
	{
		this.userAgent = useragent;
	}
	public void sslSocketFactory(SSLSocketFactory ssf)
	{
		SSLSocketFactory = ssf;
	}
	public Request load(String url)
	{
		return new Request(this, url);
	}

	public Content load(int res)
	{

		return  new Request(this, res).execute();
	}
	public DiskCache getDiskCache()
	{
		return mDiskCache;
	}
	public SSLSocketFactory getSSLSocketFactory()
	{
		return SSLSocketFactory;
	}


	public void cancel(Target t, Request request)
	{
		if (t == null)return;
		Content content=t.getContent();
		if (content != null)
		{
			content.cancel();
			if (request != null&&request.getKey()!=null && !request.getKey().equals(content.getRequest().getKey()))
			{
				HandleThread ht=request_handler.remove(content.getRequest().getKey());
				if (ht != null)
					ht.cancel();
			}
			Resource res=getActiveResource().get(content.getKey());
			if (res != null)
				res.release();
			//t.onResourceReady(null,null);

		}

	}

	public Context getContext()
	{
		return mContext.get();
	}
	public void trimMemory()
	{
		mMemoryCache.trimToSize(2 * 1024 * 1024);
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
		mActiveResource.clear();
		clearMemory();
		trimCache();
		mThreadPoolExecutor.shutdownNow();
		netThreadPool.shutdownNow();
		fileThreadPool.shutdownNow();
		mContext.get().unregisterComponentCallbacks(mComponentCallbacks);
		((Application)mContext.get().getApplicationContext()).unregisterActivityLifecycleCallbacks(mActivityLifecycle);


	}
	int[] getScreenSize()
	{
		DisplayMetrics dm=mContext.get().getResources().getDisplayMetrics();
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
			clearMemory();
		}

		@Override
		public void onTrimMemory(int p1)
		{
			if (p1 == Application.TRIM_MEMORY_BACKGROUND)
				trimMemory();
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
			Pussy p=context_pussy.remove(p1);
			if (p != null)
				p.release();
		}
	}
	static	class FragmentLifecycle extends FragmentManager.FragmentLifecycleCallbacks
	{

		@Override
		public void onFragmentDestroyed(FragmentManager fm, Fragment f)
		{
			fm.unregisterFragmentLifecycleCallbacks(this);
			Pussy p=fragment_pussy.remove(f);
			if (p != null)
				p.release();
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
			p1.removeOnAttachStateChangeListener(this);
			Pussy p=view_pussy.remove(p1);
			if (p != null)
				p.release();
		}
	}
	public static class Refresh
	{
		private Content l;
		public Refresh(Content l)
		{
			this.l = l;
		}

		public void cancel()
		{
			l.getRequest().getPussy().cancel(l.getTarget(), l.getRequest());
		}

		public boolean isCancel()
		{
			return l.getTarget() == null;
		}
		public boolean refresh(Target t)
		{
			return l.refresh(t);
		}
	}
}
