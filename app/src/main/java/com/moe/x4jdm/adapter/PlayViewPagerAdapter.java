package com.moe.x4jdm.adapter;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import com.alibaba.fastjson.JSONArray;
import android.support.v7.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import android.support.v7.widget.GridLayoutManager;
import com.alibaba.fastjson.JSONObject;
import com.moe.x4jdm.adapter.PlayItemAdapter.ViewHolder;

public class PlayViewPagerAdapter extends PagerAdapter
{
	private JSONArray ja;
	private OnClickListener l;
	private List<RecyclerView> cache=new ArrayList<>();
	public PlayViewPagerAdapter(JSONArray ja){
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
		RecyclerView view;
		if(cache.size()==0){
			view=new RecyclerView(container.getContext());
			view.setLayoutParams(new ViewGroup.LayoutParams(-2,-2));
		}else{
			view=cache.remove(0);
		}
		view.setLayoutManager(new GridLayoutManager(container.getContext(),2));
		JSONArray playitems=ja.getJSONArray(position);
		view.setAdapter(new PlayItemAdapter(playitems));
		((PlayItemAdapter)view.getAdapter()).setOnItemClickListener(new PlayItemAdapter.OnItemClickListener(){

				@Override
				public void onItemClick(PlayItemAdapter.ViewHolder vh)
				{
					if(l!=null)l.onClick(vh.getObject());
				}
			});
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
		return String.valueOf((char)(position+97));
	}
	public void setOnClickListener(OnClickListener l){
		this.l=l;
	}
	public interface OnClickListener{
		void onClick(JSONObject jo);
	}
}
