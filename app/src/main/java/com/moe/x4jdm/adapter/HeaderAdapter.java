package com.moe.x4jdm.adapter;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.alibaba.fastjson.JSONArray;
import com.moe.x4jdm.R;
import java.util.List;
import java.util.ArrayList;
import android.widget.ImageView;
import android.widget.TextView;
import com.alibaba.fastjson.JSONObject;
import android.view.Gravity;
import android.content.Intent;
import com.moe.x4jdm.PostViewActivity;
import com.moe.x4jdm.model.Index;
import com.moe.pussy.Pussy;
import com.moe.pussy.transformer.CropTransformer;
import com.moe.x4jdm.ListActivity;

public class HeaderAdapter extends PagerAdapter
{
	private JSONArray ja;
	private List<View> cache=new ArrayList<>();
	public HeaderAdapter(JSONArray ja){
		this.ja=ja;
		ja.add(0,ja.get(ja.size()-1));
		ja.add(ja.size(),ja.get(1));
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
		View v=null;
		if(cache.size()==0){
		v=LayoutInflater.from(container.getContext()).inflate(R.layout.header_item,container,false);
		}else{
			v=cache.remove(0);
		}
		ViewHolder vh=(HeaderAdapter.ViewHolder) v.getTag();
		if(vh==null)v.setTag(vh=new ViewHolder(v));
		JSONObject jo=ja.getJSONObject(position);
		vh.title.setText(jo.getString("title"));
		vh.summary.setText(jo.getString("desc"));
		Pussy.$(container.getContext()).load(jo.getString("src")).execute().transformer(new CropTransformer(Gravity.CENTER)).into(vh.icon);
		container.addView(v);
		vh.position=position;
		return v;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object)
	{
		container.removeView((View)object);
		cache.add((View)object);
	}

	class ViewHolder implements View.OnClickListener{
		ImageView icon;
		TextView title,summary;
		int position;
		ViewHolder(View v){
			icon=v.findViewById(R.id.icon);
			title=v.findViewById(R.id.title);
			summary=v.findViewById(R.id.summary);
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
		public JSONObject getObject(){
			return ja.getJSONObject(position);
		}

		
	}
}
