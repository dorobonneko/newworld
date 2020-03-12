package com.moe.x4jdm.adapter;
import com.alibaba.fastjson.JSONArray;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import com.moe.x4jdm.R;
import android.support.design.widget.TabLayout;
import android.widget.TextView;
import com.alibaba.fastjson.JSONObject;
import android.support.design.widget.TabLayout.Tab;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import android.util.TypedValue;

public class FilterAdapter extends RecyclerView.Adapter
{
	private JSONArray filter;
	private Map<String,String> key=new HashMap<>();
	private OnChangeListener l;
	public FilterAdapter(JSONArray filter){
		this.filter=filter;
	}
	public String getKey(){
		Iterator<Map.Entry<String,String>> i=key.entrySet().iterator();
		StringBuilder sb=new StringBuilder();
		while(i.hasNext()){
			Map.Entry<String,String> entry=i.next();
			sb.append("&").append(entry.getKey()).append("=").append(entry.getValue());
		}
		return sb.toString();
	}
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup p1, int p2)
	{
		switch(p2){
			case 100:
				return new ViewHolder(LayoutInflater.from(p1.getContext()).inflate(R.layout.filter_line_item_view,p1,false));
		}
		return null;
		}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder vh, int position)
	{
		if(vh instanceof ViewHolder){
			ViewHolder vvh=(FilterAdapter.ViewHolder) vh;
			JSONObject jo=filter.getJSONObject(position);
			vvh.title.setText(jo.getString("desc"));
			JSONArray value=jo.getJSONArray("value");
			vvh.tl.removeAllTabs();
			for(int i=0;i<value.size();i++){
			vvh.tl.addTab(vvh.tl.newTab().setText(value.getJSONObject(i).getString("title")));
			}
			}
			}

	@Override
	public int getItemCount()
	{
		return filter.size();
	}

	@Override
	public int getItemViewType(int position)
	{
		return 100;
	}

	public Object getItem(int position)
	{
		return filter.getJSONObject(position);
	}
	
	class ViewHolder extends RecyclerView.ViewHolder implements TabLayout.OnTabSelectedListener{
		TabLayout tl;
		TextView title;
		ViewHolder(View v){
			super(v);
			tl=(TabLayout) v.findViewById(R.id.tablayout);
			title=v.findViewById(R.id.title);
			tl.setTabMode(tl.MODE_SCROLLABLE);
			tl.setOnTabSelectedListener(this);
			tl.setNestedScrollingEnabled(false);
			v.setNestedScrollingEnabled(false);
			}

		@Override
		public void onTabSelected(TabLayout.Tab p1)
		{
			JSONObject object=(JSONObject) getItem(getAdapterPosition());
			String key=object.getString("key");
			String value=object.getJSONArray("value").getJSONObject(p1.getPosition()).getString("value");
			FilterAdapter.this.key.put(key,value);
			if(l!=null)
				l.onChange();
		}

		@Override
		public void onTabReselected(TabLayout.Tab p1)
		{
			onTabSelected(p1);
		}

		@Override
		public void onTabUnselected(TabLayout.Tab p1)
		{
		}



		
	}
	public void setOnChangeListener(OnChangeListener l){
		this.l=l;
	}
	public interface OnChangeListener{
		void onChange();
	}
}
