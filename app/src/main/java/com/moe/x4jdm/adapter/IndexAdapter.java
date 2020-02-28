package com.moe.x4jdm.adapter;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.moe.x4jdm.R;
import android.support.v4.view.ViewPager;
import android.support.design.widget.TabLayout;
import android.view.ViewGroup;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONArray;
import android.view.LayoutInflater;
import com.moe.x4jdm.ListActivity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import com.moe.x4jdm.widget.GridLayoutManager;
import com.moe.x4jdm.util.Space;
import com.moe.x4jdm.adapter.PostAdapter.ViewHolder;
import com.moe.x4jdm.PostViewActivity;
import com.moe.x4jdm.model.Index;

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
				return new MainViewHolder(LayoutInflater.from(p1.getContext()).inflate(R.layout.main_item, p1, false));
		}
		return null;
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder vh, int p2)
	{
		if (vh instanceof TabViewHolder)
		{
			TabLayout tab= ((TabViewHolder)vh).tablayout;
			tab.removeAllTabs();
			tab.setOnTabSelectedListener(null);
			JSONArray tabs=index.getJSONArray(vh.getAdapterPosition());
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
			vp.setAdapter(new HeaderAdapter(index.getJSONArray(vh.getAdapterPosition())));
			if (vp.getAdapter().getCount() > 0 && !hvh.handler.hasMessages(0))
				hvh.handler.sendEmptyMessageDelayed(0, 3000);
		}
		else if (vh instanceof MainViewHolder)
		{
			MainViewHolder mvh=(IndexAdapter.MainViewHolder) vh;
			JSONObject jo=index.getJSONObject(vh.getAdapterPosition());
			mvh.title.setText(jo.getString("title"));
			String href=jo.getString("href");
			if (href == null)mvh.href.setVisibility(View.INVISIBLE);
			else
			{
				mvh.href.setTag(href);
			}
			if (mvh.title.getText().length() == 0 && mvh.href.getVisibility() == View.INVISIBLE)
				mvh.head.setVisibility(View.GONE);
			final JSONArray items=jo.getJSONArray("item");
			//recyclerview.setItemAnimator(null);
			//pa.notifyDataSetChanged();
			final PostAdapter pa=new PostAdapter(items, false);
			mvh.recyclerview.setLayoutManager(new GridLayoutManager(vh.itemView.getContext(), pa));
			pa.setOnItemClickListener(mvh);
			mvh.recyclerview.getLayoutManager().setAutoMeasureEnabled(true);
			mvh.recyclerview.setAdapter(pa);
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
		Object item= index.get(position);
		if(item==null)
			return 0;
		if (index.isValidObject(item.toString()))
			return 3;
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

	public class MainViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,PostAdapter.OnItemClickListener
	{
		View head;
		TextView title,href;
		RecyclerView recyclerview;
		public MainViewHolder(View v)
		{
			super(v);
			title = v.findViewById(R.id.title);
			href = v.findViewById(R.id.more);
			head = (View) title.getParent();
			recyclerview = v.findViewById(R.id.recyclerview);
			href.setOnClickListener(this);
			recyclerview.addItemDecoration(new Space(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, itemView.getResources().getDisplayMetrics())));
			recyclerview.setNestedScrollingEnabled(false);
			recyclerview.setHasFixedSize(true);
		}

		@Override
		public void onItemClick(PostAdapter.ViewHolder vh)
		{
			vh.itemView.getContext().startActivity(new Intent(itemView.getContext(), PostViewActivity.class).putExtra("url", vh.getObject().getString("href")).putExtra("key",Index.getKey(itemView.getContext())));
			
		}

		@Override
		public void onClick(View p1)
		{
			p1.getContext().startActivity(new Intent(p1.getContext(), ListActivity.class).putExtra("url", p1.getTag().toString()));
			
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
