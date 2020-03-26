package com.moe.pussy;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import android.widget.ImageView;
import com.moe.pussy.target.ImageViewTarget;
import java.util.Map;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import java.lang.ref.WeakReference;
import com.moe.pussy.handle.HandleThread;
import com.moe.pussy.target.ViewBackgroundTarget;
import android.view.View;
import com.moe.pussy.target.DrawableTarget;

public class Content implements SizeReady
{
	private Request request;
	private String key=null,tag;
	private Target target;
	private DiskCache.Cache cache=DiskCache.Cache.NONE;
	private List<Transformer> mTransformers=new ArrayList<>();
	private DrawableAnimator anim;
	private Pussy.Refresh r;
	private Drawable placeHolder;
	protected Drawable error;
	protected Loader loader;
	public Content(Request r){
		this.request=r;
	}

	@Override
	public void onSizeReady(int w, int h)
	{
		loader.onSizeReady(w,h);
	}


	void cancel()
	{
		synchronized(this){
		target=null;
			HandleThread ht=request.getPussy().request_handler.get(request.getKey());
			if (ht != null)
				ht.removeCallback(loader);
			
		}
	}
	
	public Content tag(String tag){
		this.tag=tag;
		return this;
	}
	String tag(){
		return tag;
	}
	public Content placeHolder(Drawable res){
		placeHolder=res;
		return this;
	}
	public Content error(Drawable res){
		error=res;
		return this;
	}
	public Content placeHolder(int res){
		placeHolder=request.getPussy().getContext().getResources().getDrawable(res);
		return this;
	}
	public Content error(int res){
		error=request.getPussy().getContext().getResources().getDrawable(res);
		return this;
	}
	public Pussy.Refresh getRefresh(){
		if(r==null)
			r=new Pussy.Refresh(this);
			return r;
	}
	public final DrawableAnimator getAnim(){
		return anim;
	}
	public Content anime(DrawableAnimator anim){
		this.anim=anim;
		return this;
	}
	public Content transformer(Transformer... trans){
		mTransformers.addAll(Arrays.asList(trans));
		return this;
	}
	Request getRequest(){
		return request;
	}
	Target getTarget(){
		synchronized(this){
		return  target;
		}
	}
	DiskCache.Cache getCache(){
		return cache;
	}
	public Content diskCache(DiskCache.Cache cache){
		this.cache=cache;
		return this;
	}
	boolean refresh(Target t){
		
		target=t;
		loader.begin();
		return true;
	}
	public void into(Target t){
		if(t==null)return;
		this.target=t;
		Content c=t.getContent();
		if(c!=null&&getRequest().getKey().equals(c.getRequest().getKey())){
			return;
		}
		request.getPussy().cancel(t,getRequest());
		t.onAttachContent(this);
		t.placeHolder(placeHolder);
		
		//检查是否有缓存
		loader=new Loader(this);
		loader.begin();
		}
	public void into(ImageView view){
		//view.setImageDrawable(null);
		ImageViewTarget ivt=(ImageViewTarget) view.getTag();
		if(ivt==null)
			view.setTag(ivt=new ImageViewTarget(view));
			//ivt.placeHolder(placeHolder);
			into(ivt);
	}
	public void into(View view){
		//view.setImageDrawable(null);
		ViewBackgroundTarget ivt=(ViewBackgroundTarget) view.getTag();
		if(ivt==null)
			view.setTag(ivt=new ViewBackgroundTarget(view));
		//ivt.placeHolder(placeHolder);
		into(ivt);
	}
	public DrawableTarget intoPlaceHolder(){
		DrawableTarget dt=new DrawableTarget();
		into(dt);
		return dt;
	}
	synchronized String getKey(){
		if(key==null){
			StringBuilder sb=new StringBuilder();
			sb.append(request.getKey());
			for(Transformer t:mTransformers)
			sb.append(t.getKey());
			key=Uid.fromString(sb.toString()).toString();
		}
		return key;
	}
	Transformer[] getTransformer(){
		return mTransformers.toArray(new Transformer[0]);
	}
}
