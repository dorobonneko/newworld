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

public class IndexAdapter extends RecyclerView.Adapter
{
	private JSONArray index;
	public IndexAdapter(JSONArray index)
	{
		this.index = index;
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
		}
		return null;
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
			htvh.itemView.setVisibility(title.isEmpty()?View.GONE:View.VISIBLE);
			htvh.title.setText(title.getString("title"));
			htvh.more.setVisibility(TextUtils.isEmpty(title.getString("href"))?View.INVISIBLE:View.VISIBLE);
		}else if(vh instanceof PostViewHolder){
				PostViewHolder pvh=(IndexAdapter.PostViewHolder) vh;
				JSONObject jo=index.getJSONObject(position);
				pvh.title.setText(jo.getString("title"));
				String desc=jo.getString("desc");
				pvh.summary.setText(desc==null?null:Html.fromHtml(desc));
				pvh.score.setText(jo.getString("score"));
				Pussy.$(pvh.icon.getContext()).load(jo.getString("src")).execute().transformer(new CropTransformer(Gravity.CENTER)).anime(Anim.fade(500)).into(pvh.icon);

			}else if(vh instanceof PostLineViewHolder){
				PostLineViewHolder plvh=(IndexAdapter.PostLineViewHolder) vh;
				JSONObject title=index.getJSONObject(position);
				plvh.title.setText(title.getString("title"));
				plvh.summary.setText(title.getString("desc"));
			}else if(vh instanceof TextItemViewHolder){
				TextItemViewHolder plvh=(TextItemViewHolder) vh;
				JSONObject title=index.getJSONObject(position);
				plvh.title.setText(title.getString("title"));
				}
	}

	@Override
	public int getItemCount()
	{
		return index.size();
	}

	@Override
	public int getItemViewType(int position)
	{
		if(index.isEmpty())
			return 0;
		Object item= index.get(position);
		if(item==null)
			return 0;
		if (index.isValidObject(item.toString())){
			JSONObject jo=index.getJSONObject(position);
			if(jo.containsKey("src"))
				return 4;
			if(jo.containsKey("desc"))
				return 5;
			if(jo.containsKey("list"))
				return 6;
			return 3;
		}
		else if (index.isValidArray( item.toString()))
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
	public class TextItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
		private TextView title;
		public TextItemViewHolder(View v){
			super(v);
			title=(TextView) v;
			TypedArray ta=itemView.getContext().obtainStyledAttributes(new int[]{android.support.v7.appcompat.R.attr.selectableItemBackground});
			ViewCompat.setBackground(v,ta.getDrawable(0));
			ta.recycle();
			title.setGravity(Gravity.CENTER);
			v.setOnClickListener(this);
		}
		@Override
		public void onClick(View p1)
		{
			if(getObject().getBooleanValue("list"))
				p1.getContext().startActivity(new Intent(p1.getContext(), ListActivity.class).putExtra("url", getObject().getString("href")).putExtra("key",getObject().getString("key")==null?Index.getKey(p1.getContext()):getObject().getString("key")));
			else
				p1.getContext().startActivity(new Intent(p1.getContext(), PostViewActivity.class).putExtra("url", getObject().getString("href")).putExtra("key",getObject().getString("key")==null?Index.getKey(p1.getContext()):getObject().getString("key")));
		}

		public JSONObject getObject()
		{
			return index.getJSONObject(getAdapterPosition());
		}
	}
	public class HeaderTitleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
		private TextView title,more;
		public HeaderTitleViewHolder(View v){
			super(v);
			title=v.findViewById(R.id.title);
			more=v.findViewById(R.id.more);
			more.setOnClickListener(this);
		}

		@Override
		public void onClick(View p1)
		{
			JSONObject jo=index.getJSONObject(getAdapterPosition());
			String href=jo.getString("href");
			if(href!=null)
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
			if(getObject().getBooleanValue("list"))
				p1.getContext().startActivity(new Intent(p1.getContext(), ListActivity.class).putExtra("url", getObject().getString("href")).putExtra("key",getObject().getString("key")==null?Index.getKey(p1.getContext()):getObject().getString("key")));
			else
				p1.getContext().startActivity(new Intent(p1.getContext(), PostViewActivity.class).putExtra("url", getObject().getString("href")).putExtra("key",getObject().getString("key")==null?Index.getKey(p1.getContext()):getObject().getString("key")));
		}

		public JSONObject getObject()
		{
			return index.getJSONObject(getAdapterPosition());
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
		}

		@Override
		public void onClick(View p1)
		{
			if(getObject().getBooleanValue("list"))
				p1.getContext().startActivity(new Intent(p1.getContext(), ListActivity.class).putExtra("url", getObject().getString("href")).putExtra("key",getObject().getString("key")==null?Index.getKey(p1.getContext()):getObject().getString("key")));
			else
				p1.getContext().startActivity(new Intent(p1.getContext(), PostViewActivity.class).putExtra("url", getObject().getString("href")).putExtra("key",getObject().getString("key")==null?Index.getKey(p1.getContext()):getObject().getString("key")));
		}

		public JSONObject getObject()
		{
			return index.getJSONObject(getAdapterPosition());
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
			viewpager.addOnPageChangeListener(this);
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
				//松手
				handler.sendEmptyMessageDelayed(0, 3000);
			}
		}
		private Handler handler=new Handler(){

			@Override
			public void handleMessage(Message msg)
			{
				int current=viewpager.getCurrentItem();
				if (current >= viewpager.getAdapter().getCount() - 1)
					current = -1;
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
