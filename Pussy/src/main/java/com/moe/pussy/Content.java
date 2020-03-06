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

public class Content
{
	private Request request;
	private String key=null;
	private Target target;
	private DiskCache.Cache cache=DiskCache.Cache.MASK;
	private List<Transformer> mTransformers=new ArrayList<>();
	private DrawableAnimator anim;
	private Pussy.Refresh r;
	Drawable placeHolder,error;
	public Content(Request r){
		this.request=r;
	}

	void clearTarget()
	{
		target=null;
	}
	MemoryCache getMemory(){
		return request.getPussy().mMemoryCache;
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
	public DrawableAnimator getAnim(){
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
		return  target;
	}
	DiskCache.Cache getCache(){
		return cache;
	}
	public Content diskCache(DiskCache.Cache cache){
		this.cache=cache;
		return this;
	}
	public void into(Target t){
		t.placeHolder(placeHolder);
		this.target=t;
		t.onAttachContent(this);
		request.getPussy().cancel(t);
		//检查是否有缓存
		Bitmap pd=getMemory().get(getKey());
		if(pd!=null){
			request.getPussy().getDiskCache().invalidate(getKey());
			t.onSucccess(new PussyDrawable(pd,getRefresh()));
			return;
			}
		Loader l=new Loader(this);
		request.getPussy().loader_queue.put(t,l);
		request.getPussy().mThreadPoolExecutor.execute(l);
	}
	public void into(ImageView view){
		//view.setImageDrawable(null);
		ImageViewTarget ivt=(ImageViewTarget) view.getTag();
		if(ivt==null)
			view.setTag(ivt=new ImageViewTarget(view));
			//ivt.placeHolder(placeHolder);
			into(ivt);
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
