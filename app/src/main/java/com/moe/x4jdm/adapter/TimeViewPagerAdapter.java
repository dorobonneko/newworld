package com.moe.x4jdm.adapter;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import com.alibaba.fastjson.JSONArray;
import com.moe.x4jdm.util.Space;
import com.moe.x4jdm.widget.GridLayoutManager;
import java.util.ArrayList;
import java.util.List;

public class TimeViewPagerAdapter extends PagerAdapter
{
	private JSONArray ja;
	private static List<String> week;
	static{
		week=new ArrayList<>();
		week.add("星期日");
		week.add("星期一");
		week.add("星期二");
		week.add("星期三");
		week.add("星期四");
		week.add("星期五");
		week.add("星期六");
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
		PostAdapter pa=new PostAdapter(ja.getJSONArray(position),false);
		view.setLayoutManager(new GridLayoutManager(container.getContext(),pa));
		view.setAdapter(pa);
		container.addView(view);
		return view;
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
