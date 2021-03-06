package com.moe.x4jdm.adapter;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.moe.pussy.Anim;
import com.moe.pussy.Pussy;
import com.moe.pussy.transformer.CropTransformer;
import com.moe.x4jdm.ListActivity;
import com.moe.x4jdm.PostViewActivity;
import com.moe.x4jdm.R;
import com.moe.x4jdm.model.Index;
import android.widget.Button;
import android.support.v4.view.ViewCompat;
import android.content.res.TypedArray;
import android.support.v4.content.res.TypedArrayUtils;
import android.graphics.drawable.ColorDrawable;
import android.view.animation.RotateAnimation;
import android.view.animation.LinearInterpolator;
import com.moe.pussy.transformer.RoundTransformer;
import com.moe.pussy.transformer.EmbossTransFormer;
import com.moe.pussy.Target;
import com.moe.pussy.transformer.SprayTransFormer;
import com.moe.x4jdm.widget.WaterFallLayout;
import android.content.Context;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import java.util.Map;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.os.Looper;
import com.moe.x4jdm.widget.ComicView;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import com.moe.pussy.utils.ProgressDrawable;
import com.moe.x4jdm.PhotoActivity;
import org.apache.commons.codec.net.Utils;

public class IndexAdapter extends RecyclerView.Adapter
{
	private JSONArray index;
	private int icon;
	private String text;
	private ObjectAnimator rotate;
	private boolean loadmore,anime;
	private View loadView;
	public IndexAdapter(JSONArray index, boolean loadmore)
	{
		this.loadmore = loadmore;
		this.index = index;
		text = "已到底";
		icon = R.drawable.check;
		setHasStableIds(true);
	}

	public IndexAdapter(JSONArray index)
	{
		this(index, true);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public void onAttachedToRecyclerView(RecyclerView recyclerView)
	{
		super.onAttachedToRecyclerView(recyclerView);
		loadView=(LayoutInflater.from(recyclerView.getContext()).inflate(R.layout.loadmore, recyclerView, false));
		rotate =ObjectAnimator.ofFloat(loadView.findViewById(R.id.icon),"Rotation",0,360);
		rotate.setDuration(500);
		rotate.setInterpolator(new LinearInterpolator());
		rotate.setRepeatMode(rotate.RESTART);
		rotate.setRepeatCount(rotate.INFINITE);
		setFoot(icon,text,anime);
	}

	/*@Override
	public void onViewRecycled(RecyclerView.ViewHolder holder)
	{
		super.onViewRecycled(holder);
		if (holder instanceof PostViewHolder)
		{
			Target t=(Target) ((PostViewHolder)holder).icon.getTag();
			Pussy.$(holder.itemView.getContext()).cancel(t, null);
		}
	}*/



	public Object getItem(int position)
	{
		return index.get(position);
	}
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup p1, int p2)
	{
		switch (p2)
		{
			case 1:
				return new HeaderViewHolder(new ViewPager(p1.getContext()));
			case 2:
				return new TabViewHolder(new TabLayout(p1.getContext()));
			case 3:
				return new HeaderTitleViewHolder(LayoutInflater.from(p1.getContext()).inflate(R.layout.header_view, p1, false));
			case 4:
				return new PostViewHolder(LayoutInflater.from(p1.getContext()).inflate(R.layout.post_item, p1, false));
			case 5:
				return new PostLineViewHolder(LayoutInflater.from(p1.getContext()).inflate(R.layout.time_item, p1, false));
			case 6:
				return new TextItemViewHolder(new Button(p1.getContext()));
				//return new PostLineViewHolder(LayoutInflater.from(p1.getContext()).inflate(R.layout.post_line_item, p1, false));
			case 7:
				return new LoadMoreViewHolder(loadView);
				//return vh==null?(vh = new LoadMoreViewHolder(LayoutInflater.from(p1.getContext()).inflate(R.layout.loadmore, p1, false))):vh;
			case 8:
				return new PostViewHolder(LayoutInflater.from(p1.getContext()).inflate(R.layout.post_line_item, p1, false));
			case 9:
				return new PostViewHolder(LayoutInflater.from(p1.getContext()).inflate(R.layout.header_item, p1, false));
			case 10:
				return new PostPosterViewHolder(LayoutInflater.from(p1.getContext()).inflate(R.layout.post_poster, p1, false));
			case 11:
				return new ImagePreviewViewHolder(LayoutInflater.from(p1.getContext()).inflate(R.layout.image_preview,p1,false));
			case 12:
				return new CommentViewHolder(LayoutInflater.from(p1.getContext()).inflate(R.layout.comment_item,p1,false));
			case 13:
				return new ListComicViewHolder(new ComicView(p1.getContext()));
			case 14:
				return new PreviewViewHolder(LayoutInflater.from(p1.getContext()).inflate(R.layout.post_preview,p1,false));
		}
		return new ViewHolder(new View(p1.getContext()));
	}
	public void setFoot(int icon, String text, boolean anim)
	{
		this.icon = icon;
		this.text = text;
		this.anime=anim;
		if (loadView != null)
		{
			ImageView iconview=((ImageView)loadView.findViewById(R.id.icon));
			iconview.setImageResource(icon);
			((TextView)loadView.findViewById(R.id.title)).setText(text);
			if (anim)
			{
				rotate.start();
			}
			else
				rotate.cancel();
		}
	}
	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder vh, int position)
	{
		if (vh instanceof TabViewHolder)
		{
			TabLayout tab= ((TabViewHolder)vh).tablayout;
			tab.removeAllTabs();
			tab.setOnTabSelectedListener(null);
			JSONArray tabs=index.getJSONArray(position);
			for (int i=0;i < tabs.size();i++)
			{
				JSONObject tab_item=tabs.getJSONObject(i);
				tab.addTab(tab.newTab().setText(tab_item.getString("title")).setTag(tab_item.getString("href")));
			}
			tab.setOnTabSelectedListener((TabViewHolder)vh);
			tab.setTabMode(tab.getTabCount() <= 5 ?tab.MODE_FIXED: tab.MODE_SCROLLABLE);
		}
		else if (vh instanceof HeaderViewHolder)
		{
			HeaderViewHolder hvh=(IndexAdapter.HeaderViewHolder) vh;
			ViewPager vp=((HeaderViewHolder)vh).viewpager;
			vp.setAdapter(new HeaderAdapter(index.getJSONArray(position)));
			if (vp.getAdapter().getCount() > 0 && !hvh.handler.hasMessages(0))
				hvh.handler.sendEmptyMessageDelayed(0, 3000);
		}
		else if (vh instanceof HeaderTitleViewHolder)
		{
			HeaderTitleViewHolder htvh=(IndexAdapter.HeaderTitleViewHolder) vh;
			JSONObject title=index.getJSONObject(position);
			htvh.itemView.setVisibility(title.isEmpty() ?View.GONE: View.VISIBLE);
			htvh.title.setText(title.getString("title"));
			htvh.more.setVisibility(TextUtils.isEmpty(title.getString("href")) ?View.INVISIBLE: View.VISIBLE);
		}
		else if (vh instanceof PostViewHolder)
		{
			PostViewHolder pvh=(IndexAdapter.PostViewHolder) vh;
			JSONObject jo=index.getJSONObject(position);
			pvh.title.setText(jo.getString("title"));
			String desc=jo.getString("desc");
			pvh.summary.setText(desc == null ?null: Html.fromHtml(desc));
			if (pvh.score != null)
				pvh.score.setText(jo.getString("score"));
			Pussy.$(pvh.icon.getContext()).load(jo.getString("src")).referer(jo.getString("referer")).execute().tag(jo.getString("title")).transformer(new CropTransformer(Gravity.CENTER), new RoundTransformer(pvh.itemView.getResources().getDisplayMetrics(), 5)).delay(150).anime(Anim.fade(500)).into(pvh.icon);
			//pvh.icon.setImageDrawable(Pussy.$(pvh.icon.getContext()).load(jo.getString("src")).execute().tag(jo.getString("title")).transformer(new CropTransformer(Gravity.CENTER),new RoundTransformer(pvh.itemView.getResources().getDisplayMetrics(),5)).anime(Anim.fade(500)).intoPlaceHolder());

			//Glide.with(pvh.itemView.getContext()).load(jo.getString("src")).centerCrop().crossFade(500).into(pvh.icon);
		}
		else if (vh instanceof PostLineViewHolder)
		{
			PostLineViewHolder plvh=(IndexAdapter.PostLineViewHolder) vh;
			JSONObject title=index.getJSONObject(position);
			plvh.title.setText(title.getString("title"));
			plvh.summary.setText(title.getString("desc"));
		}
		else if (vh instanceof TextItemViewHolder)
		{
			TextItemViewHolder plvh=(TextItemViewHolder) vh;
			JSONObject title=index.getJSONObject(position);
			plvh.title.setText(title.getString("title"));
		}
		else if (vh instanceof LoadMoreViewHolder)
		{
			LoadMoreViewHolder lmvh=(IndexAdapter.LoadMoreViewHolder) vh;
			/*lmvh.icon.setImageResource(icon);
			lmvh.title.setText(text);
			lmvh.icon.clearAnimation();*/
			//if (anime)
				//lmvh.icon.startAnimation(rotate);
				//else
				//lmvh.icon.clearAnimation();*/
		}
		else if (vh instanceof PostPosterViewHolder)
		{
			PostPosterViewHolder ppvh=(IndexAdapter.PostPosterViewHolder) vh;
			JSONObject post=index.getJSONObject(position);
			ppvh.title.setText(post.getString("title"));
			ppvh.msg.setText(post.getString("msg"));
			ppvh.desc.setText(post.getString("desc"));
			ppvh.score.setText(post.getString("score"));
			Pussy.$(ppvh.itemView.getContext()).load(post.getString("src")).userAgent("x4jdm" + Math.random()).execute().delay(150).transformer(new CropTransformer(Gravity.CENTER), new RoundTransformer(ppvh.itemView.getResources().getDisplayMetrics(), 5)).anime(Anim.fade(300)).into(ppvh.icon);
			WaterFallLayout mWaterFallLayout=ppvh.tag;
			JSONArray tag=post.getJSONArray("tag");
			if(tag==null)
				mWaterFallLayout.setVisibility(View.GONE);
				else{
					mWaterFallLayout.setVisibility(View.VISIBLE);
			int i=0;
			for (;i < mWaterFallLayout.getChildCount();i++)
			{
				TextView tv=(TextView) mWaterFallLayout.getChildAt(i);
				if (i < tag.size())
					tv.setText(tag.getJSONObject(i).getString("title"));
				else
					tv.setVisibility(View.GONE);
			}
			for (;i < tag.size();i++)
			{
				TextView tv=new TextView(mWaterFallLayout.getContext());
				tv.setOnClickListener(ppvh);
				/*TypedArray ta=tv.getContext().obtainStyledAttributes(new int[]{android.support.v7.appcompat.R.attr.selectableItemBackground});
				tv.setBackground(ta.getDrawable(0));
				ta.recycle();*/
				tv.setBackgroundResource(R.drawable.ripple_button);
				tv.setText(tag.getJSONObject(i).getString("title"));
				mWaterFallLayout.addView(tv);
			}
			}
		}else if(vh instanceof ImagePreviewViewHolder){
			ImagePreviewViewHolder ipvh=(IndexAdapter.ImagePreviewViewHolder) vh;
			JSONObject object=index.getJSONObject(position);
			ipvh.title.setText(object.getString("title"));
			ProgressDrawable pd=(ProgressDrawable) ipvh.icon.getTag(ipvh.icon.getId());
			//if(pd==null)
			ipvh.icon.setTag(ipvh.icon.getId(),pd=new ProgressDrawable(ipvh.itemView.getContext()));
			Pussy.$(ipvh.itemView.getContext()).load(object.getString("src")).userAgent("x4jdm"+Math.random()).listener(pd).execute().error(new ColorDrawable(0xffa05555)).placeHolder(pd).delay(150).into(ipvh.icon);
			
		}else if(vh instanceof CommentViewHolder){
			CommentViewHolder cvh=(IndexAdapter.CommentViewHolder) vh;
			JSONObject object=index.getJSONObject(position);
			cvh.name.setText(object.getString("name"));
			cvh.desc.setText(object.getString("desc"));
			cvh.comment.setText(Html.fromHtml(object.getString("comment")));
		}else if(vh instanceof ListComicViewHolder){
			ListComicViewHolder lcvh=(IndexAdapter.ListComicViewHolder) vh;
			JSONObject object=index.getJSONObject(position);
			Pussy.$(lcvh.itemView.getContext()).load(object.getString("src")).userAgent("x4jdm"+Math.random()).execute().delay(150).into(lcvh.comic);
			lcvh.comic.setPage(position+1);
		}else if(vh instanceof PreviewViewHolder){
			PreviewViewHolder pvh=(IndexAdapter.PreviewViewHolder) vh;
			JSONObject object=index.getJSONObject(position);
			ProgressDrawable pd=(ProgressDrawable) pvh.icon.getTag(pvh.icon.getId());
			//if(pd==null)
			pvh.icon.setTag(pvh.icon.getId(),pd=new ProgressDrawable(pvh.itemView.getContext()));
			Pussy.$(pvh.itemView.getContext()).load(object.getString("src")).listener(pd).execute().transformer(new CropTransformer(Gravity.CENTER)).error(new ColorDrawable(0xffa05555)).placeHolder(pd).delay(150).into(pvh.icon);
			
		}
	}

	@Override
	public int getItemCount()
	{
		return index.isEmpty() ?0: (index.size() + (loadmore ?1: 0));
	}

	@Override
	public int getItemViewType(int position)
	{
		if (index.isEmpty())
			return 0;
		if (loadmore && position == index.size())
			return 7;
		if (position > index.size())
		{
			return 0;
		}
		Object item= index.get(position);
		if (item == null)
			return 0;
		if (index.isValidObject(item.toString()))
		{
			JSONObject jo=index.getJSONObject(position);
			String type=jo.getString("viewtype");
			if (type != null)
			{
				switch (type)
				{
					case "post":
						return 4;
					case "poster":
						return 9;
					case "postposter":
						return 10;
					case "imagepreview":
						return 11;
					case "comment":
						return 12;
					case "listcomic":
						return 13;
					case "preview":
						return 14;
				}
			}
			String click=jo.getString("click");
			if (click != null)
			{
				switch (click)
				{
					case "list":
						if (jo.containsKey("desc"))
							return 5;
						return 6;
				}
			}
			if (jo.containsKey("key"))
				return 8;
			if (jo.containsKey("src"))
				return 4;
			if (jo.containsKey("desc"))
				return 5;
			/*if(jo.containsKey("click"))
			 return 6;*/
			return 3;
		}
		else if (index.isValidArray(item.toString()))
		{
			JSONArray items=index.getJSONArray(position);
			if (items.isEmpty())return 0;
			JSONObject jo=items.getJSONObject(0);
			if (jo.containsKey("src"))
				return 1;
			if (jo.containsKey("href"))
				return 2;
		}
		return 0;
	}
	private void performClick(final Context context, int position)
	{
		if(position==-1)return;
		final JSONObject object=(JSONObject)getItem(position);
		String click=object.getString("click");
		if (click == null)
		{
			context.startActivity(new Intent(context, PostViewActivity.class).putExtra("url", object.getString("href")).putExtra("key", object.getString("key") == null ?Index.getKey(context): object.getString("key")));
		}
		else
		{
			switch (click)
			{
				case "list":
					context.startActivity(new Intent(context, ListActivity.class).putExtra("url", object.getString("href")).putExtra("type",object.getString("type")).putExtra("key", object.getString("key") == null ?Index.getKey(context): object.getString("key")));
					break;
				case "post":
					context.startActivity(new Intent(context, PostViewActivity.class).putExtra("url", object.getString("href")).putExtra("key", object.getString("key") == null ?Index.getKey(context): object.getString("key")));
					break;
				case "video":
					final ProgressDialog pd=new ProgressDialog(context);
					pd.setMessage("正在解析");
					pd.show();
					new Thread(){
						public void run()
						{
							try{
							final Map<String,String> url=Index.getModel(context).getVideoUrl(object.getString("href"));
							if (url != null&&!url.isEmpty())
								Pussy.post(new Runnable(){

										@Override
										public void run()
										{
											BottomSheetDialog sheet=new BottomSheetDialog(context);
											RecyclerView recyclerview=null;
											sheet.setContentView(recyclerview=new RecyclerView(context),new ViewGroup.LayoutParams(-1,-2));
											recyclerview.setLayoutManager(new LinearLayoutManager(context));
											PlayAdapter pa=null;
											recyclerview.setAdapter(pa=new PlayAdapter());
											pa.addAll(url);
											sheet.show();
										}
									});
									}catch(Exception e){}
							pd.dismiss();
						}
					}.start();
					break;
				case "photo":
					String url=object.getString("source");
					if(url==null)
						url=object.getString("src");
					context.startActivity(new Intent(context,PhotoActivity.class).setData(Uri.parse(url)));
					
					break;
			}
		}
	}
	class PreviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
		ImageView icon;
		PreviewViewHolder(View v){
			super(v);
			icon=(ImageView) v;
			v.setOnClickListener(this);
		}

		@Override
		public void onClick(View p1)
		{
			JSONObject data=index.getJSONObject(getAdapterPosition());
			String url=data.getString("source");
			if(url==null)
				url=data.getString("src");
			p1.getContext().startActivity(new Intent(p1.getContext(),PhotoActivity.class).setData(Uri.parse(url)));
		}

		
	}
	class ListComicViewHolder extends RecyclerView.ViewHolder{
		ComicView comic;
		ListComicViewHolder(View v){
			super(v);
			comic=(ComicView) v;
			v.setLayoutParams(new ViewGroup.LayoutParams(-1,-2));
			comic.setAdjustViewBounds(true);
		}
	}
	public class CommentViewHolder extends RecyclerView.ViewHolder
	{
		TextView name,desc,comment;
		CommentViewHolder(View v)
		{
			super(v);
			name = v.findViewById(R.id.name);
			desc = v.findViewById(R.id.desc);
			comment = v.findViewById(R.id.comment);
		}
	}
	public class ImagePreviewViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener,View.OnClickListener
	{
		ImageView icon;
		TextView title;
		ImagePreviewViewHolder(View v)
		{
			super(v);
			title = v.findViewById(R.id.title);
			icon = v.findViewById(R.id.icon);
			icon.setOnLongClickListener(this);
			icon.setOnClickListener(this);
		}

		@Override
		public void onClick(View p1)
		{
			JSONObject data=index.getJSONObject(getAdapterPosition());
			String url=data.getString("source");
			if(url==null)
				url=data.getString("src");
			p1.getContext().startActivity(new Intent(p1.getContext(),PhotoActivity.class).setData(Uri.parse(url)));
		}


		@Override
		public boolean onLongClick(final View view)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
			builder.setItems(R.array.search_image, new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface p1, int p2)
					{
						String[] urls=view.getResources().getStringArray(R.array.search_url);
						String url=String.format(urls[p2], index.getJSONObject(getAdapterPosition()).getString("src"));
						Intent intent=new Intent(Intent.ACTION_VIEW);
						intent.setData(Uri.parse(url));
						//intent.setDataAndType(Uri.parse(url), p2 < urls.length - 1 ?"text/html": "image/*");
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						try
						{itemView.getContext().startActivity(intent);}
						catch (Exception e)
						{}

					}
				}).create().show();
			return true;
		}
	}
	public class PostPosterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
	{
		TextView msg,title,score,desc;
		WaterFallLayout tag;
		ImageView icon;
		PostPosterViewHolder(View v)
		{
			super(v);
			v.setOnClickListener(this);
			msg = v.findViewById(R.id.msg);
			title = v.findViewById(R.id.title);
			score = v.findViewById(R.id.score);
			desc = v.findViewById(R.id.desc);
			icon = v.findViewById(R.id.icon);
			tag = v.findViewById(R.id.tag);
		}
		@Override
		public void onClick(View p1)
		{
			if(p1==itemView)
			performClick(p1.getContext(), getAdapterPosition());
			else{
				JSONArray tag=((JSONObject)getItem(getAdapterPosition())).getJSONArray("tag");
				JSONObject object=tag.getJSONObject(this.tag.indexOfChild(p1));
				p1.getContext().startActivity(new Intent(p1.getContext(), ListActivity.class).putExtra("url", object.getString("href")).putExtra("key", object.getString("key") == null ?Index.getKey(p1.getContext()): object.getString("key")));
				
			}
		}


	}
	public class ViewHolder extends RecyclerView.ViewHolder
	{
		ViewHolder(View v)
		{
			super(v);
		}
	}
	public class LoadMoreViewHolder extends RecyclerView.ViewHolder
	{
		TextView title;
		ImageView icon;
		public LoadMoreViewHolder(View v)
		{
			super(v);
			title = v.findViewById(R.id.title);
			icon = v.findViewById(R.id.icon);
		}
	}
	public class TextItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
	{
		private TextView title;
		public TextItemViewHolder(View v)
		{
			super(v);
			title = (TextView) v;
			TypedArray ta=itemView.getContext().obtainStyledAttributes(new int[]{android.support.v7.appcompat.R.attr.selectableItemBackground});
			ViewCompat.setBackground(v, ta.getDrawable(0));
			ta.recycle();
			title.setGravity(Gravity.CENTER);
			v.setOnClickListener(this);
		}
		@Override
		public void onClick(View p1)
		{
			performClick(p1.getContext(), getAdapterPosition());
		}
	}
	public class HeaderTitleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
	{
		private TextView title,more;
		public HeaderTitleViewHolder(View v)
		{
			super(v);
			title = v.findViewById(R.id.title);
			more = v.findViewById(R.id.more);
			more.setOnClickListener(this);
		}

		@Override
		public void onClick(View p1)
		{
			JSONObject jo=index.getJSONObject(getAdapterPosition());
			String href=jo.getString("href");
			if (href != null)
				p1.getContext().startActivity(new Intent(itemView.getContext(), ListActivity.class).putExtra("url", href));

		}


	}
	public class PostLineViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
	{
		TextView title,summary;
		public PostLineViewHolder(View v)
		{
			super(v);
			title = v.findViewById(R.id.title);
			summary = v.findViewById(R.id.summary);
			v.setOnClickListener(this);
		}

		@Override
		public void onClick(View p1)
		{
			performClick(p1.getContext(), getAdapterPosition());
		}
	}
	public class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
	{
		TextView title,summary;
		public ImageView icon;
		public TextView score;

		public PostViewHolder(View v)
		{
			super(v);
			title = v.findViewById(R.id.title);
			summary = v.findViewById(R.id.summary);
			icon = v.findViewById(R.id.icon);
			score = v.findViewById(R.id.score);
			v.setOnClickListener(this);
			icon.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}

		@Override
		public void onClick(View p1)
		{
			performClick(p1.getContext(), getAdapterPosition());
		}
	}
	public class HeaderViewHolder extends RecyclerView.ViewHolder implements ViewPager.OnPageChangeListener
	{
		ViewPager viewpager;
		public HeaderViewHolder(View v)
		{
			super(v);
			viewpager = (ViewPager) v;
			v.setLayoutParams(new ViewGroup.LayoutParams(-1, (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, v.getResources().getDisplayMetrics())));
			viewpager.setOnPageChangeListener(this);
		}




		@Override
		public void onPageSelected(int p1)
		{


		}

		@Override
		public void onPageScrollStateChanged(int p1)
		{
			if (p1 == ViewPager.SCROLL_STATE_DRAGGING)
			{
				//拖动
				handler.removeMessages(0);
			}
			else if (p1 == ViewPager.SCROLL_STATE_IDLE)
			{
				int position=viewpager.getCurrentItem();
				if (position == 0)
				{
					viewpager.setCurrentItem(viewpager.getAdapter().getCount() - 2, false);

				}
				else if (position == viewpager.getAdapter().getCount() - 1)
				{
					// 当视图在最后一个是,将页面号设置为图片的第一张。
					viewpager.setCurrentItem(1, false);
				}

				//松手
				handler.sendEmptyMessageDelayed(0, 3000);
			}
			else
			{


			}
		}
		private Handler handler=new Handler(){

			@Override
			public void handleMessage(Message msg)
			{
				/*if (viewpager.getCurrentItem() == 0) {
				 viewpager.setCurrentItem(viewpager.getAdapter().getCount() - 2, false);

				 } else if (viewpager.getCurrentItem() == viewpager.getAdapter().getCount() - 1) {
				 // 当视图在最后一个是,将页面号设置为图片的第一张。
				 viewpager.setCurrentItem(1, false);
				 }*/
				int current=viewpager.getCurrentItem();
				//if (current >= viewpager.getAdapter().getCount() - 1)
				//current = -1;
				viewpager.setCurrentItem(++current, true);
				//handler.sendEmptyMessageDelayed(0,3000);
			}

		};

		@Override
		public void onPageScrolled(int p1, float p2, int p3)
		{
		}




	}
	public class TabViewHolder extends RecyclerView.ViewHolder implements TabLayout.OnTabSelectedListener
	{
		TabLayout tablayout;
		public TabViewHolder(View v)
		{
			super(v);
			tablayout = (TabLayout) v;
			tablayout.setTabTextColors(tablayout.getTabTextColors().getDefaultColor(), tablayout.getTabTextColors().getDefaultColor());
			tablayout.setSelectedTabIndicatorColor(0);
			tablayout.setLayoutParams(new ViewGroup.LayoutParams(-1, -2));
			//tablayout.setOnTabSelectedListener(this);
		}
		@Override
		public void onTabSelected(TabLayout.Tab p1)
		{
			itemView.getContext().startActivity(new Intent(itemView.getContext(), ListActivity.class).putExtra("url", p1.getTag().toString()));

		}

		@Override
		public void onTabUnselected(TabLayout.Tab p1)
		{
		}

		@Override
		public void onTabReselected(TabLayout.Tab p1)
		{
			onTabSelected(p1);
		}

	}
}
