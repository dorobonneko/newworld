package com.moe.x4jdm.adapter;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import com.alibaba.fastjson.JSONArray;
import com.moe.x4jdm.util.Space;
import java.util.ArrayList;
import java.util.List;
import com.moe.x4jdm.widget.IndexGridLayoutManager;

public class TimeViewPagerAdapter extends PagerAdapter
{
	private JSONArray ja;
	private static List<String> week;
	private int lastPosition;
	static{
		week=new ArrayList<>();
		week.add("日");
		week.add("月");
		week.add("水");
		week.add("火");
		week.add("木");
		week.add("金");
		week.add("土");
	}
	private List<RecyclerView> cache=new ArrayList<>();
	public TimeViewPagerAdapter(JSONArray ja){
		this.ja=ja;
	}
	@Override
	public int getCount()
	{
		return ja.size();
	}

	@Override
	public boolean isViewFromObject(View p1, Object p2)
	{
		return p1==p2;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position)
	{
		RecyclerView view=null;
		if(cache.size()==0){
			view=new RecyclerView(container.getContext());
			view.addItemDecoration(new Space((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,8,view.getResources().getDisplayMetrics())));
			
			view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
		}else{
			view=cache.remove(0);
		}
		IndexAdapter pa=new IndexAdapter(ja.getJSONArray(position),false);
		view.setLayoutManager(new IndexGridLayoutManager(container.getContext(),pa));
		view.setAdapter(pa);
		container.addView(view);
		return view;
	}

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object)
	{
		//super.setPrimaryItem(container, position, object);
		if(lastPosition!=position){
			lastPosition=position;
		
		}
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object)
	{
		container.removeView((View)object);
		cache.add((RecyclerView)object);
	}

	@Override
	public CharSequence getPageTitle(int position)
	{
		return week.get(position);
	}
	
}
