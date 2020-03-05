package com.moe.x4jdm.adapter;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.moe.x4jdm.PostViewActivity;
import com.moe.x4jdm.R;
import android.text.TextUtils;
import com.moe.x4jdm.model.Index;
import android.text.Html;
import com.moe.pussy.Anim;
import com.moe.x4jdm.ListActivity;
import android.view.animation.RotateAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import com.moe.pussy.Pussy;
import com.moe.pussy.transformer.CropTransformer;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ItemViewHolder>
{
	private JSONArray ja;
	private int icon;
	private String text;
	private ViewHolder vh;
	private boolean foot;
	private OnItemClickListener l;
	private Animation rotate;
	public PostAdapter(JSONArray ja, boolean foot)
	{
		this.ja = ja;
		this.foot = foot;
		rotate=new RotateAnimation(0,360,RotateAnimation.RELATIVE_TO_SELF,0.5f,RotateAnimation.RELATIVE_TO_SELF,0.5f);
		rotate.setRepeatCount(-1);
		rotate.setDuration(500);
		rotate.setInterpolator(new LinearInterpolator());
	}
	@Override
	public PostAdapter.ItemViewHolder onCreateViewHolder(ViewGroup p1, int p2)
	{
		switch (p2)
		{
			case 2:
				return new ViewHolder(LayoutInflater.from(p1.getContext()).inflate(R.layout.post_item, p1, false));
			case 1:
				 vh = new ViewHolder(LayoutInflater.from(p1.getContext()).inflate(R.layout.loadmore, p1, false));
				 
				 return vh;
			case 3:
			case 5:
				return new ItemViewHolder(LayoutInflater.from(p1.getContext()).inflate(R.layout.time_item, p1, false));
			case 4:
			return new ViewHolder(LayoutInflater.from(p1.getContext()).inflate(R.layout.post_line_item,p1,false));
			
		}
		return null;
	}
	public void setFoot(int icon, String text,boolean anim)
	{
		this.icon = icon;
		this.text = text;
		if (vh != null)
		{
			vh.icon.setImageResource(icon);
			vh.title.setText(text);
			if(anim)
				vh.icon.startAnimation(rotate);
				else
				vh.icon.clearAnimation();
		}
	}
	@Override
	public void  onBindViewHolder(ItemViewHolder vh, int p2)
	{
		if (p2 < ja.size())
		{

			JSONObject jo=ja.getJSONObject(p2);
			vh.title.setText(jo.getString("title"));
			String desc=jo.getString("desc");
			vh.summary.setText(desc==null?null:Html.fromHtml(desc));
			if (vh instanceof ViewHolder)
			{
				ViewHolder holder=(PostAdapter.ViewHolder) vh;
				if(holder.score!=null)
				holder.score.setText(jo.getString("score"));
				if(holder.icon!=null)
				//Pussy.$(holder.icon.getContext()).load(jo.getString("src")).transForm(new CropTransForm(Gravity.CENTER)).Anim(Anim.fade(500)).into(holder.icon);
				Pussy.$(holder.icon.getContext()).load(jo.getString("src")).execute().transformer(new CropTransformer(Gravity.CENTER)).anime(Anim.fade(500)).into(holder.icon);
				
			}
		}
		else
		{

			((ViewHolder)vh).icon.setImageResource(icon);
			vh.title.setText(text);
		}
	}

	@Override
	public int getItemCount()
	{
		return foot ?ja.size() + 1: ja.size();
	}

	@Override
	public int getItemViewType(int position)
	{
		return foot ?(position == ja.size() ?1: getItemType(position)): getItemType(position);
	}

	public int getItemType(int position)
	{
		JSONObject object=ja.getJSONObject(position);
		if(TextUtils.isEmpty(object.getString("key"))){
			if(!TextUtils.isEmpty(object.getString("src"))){
				//带图片
				return 2;
			}else{
				if(TextUtils.isEmpty(ja.getJSONObject(position).getString("desc"))){
					//不带描述
					if(object.containsKey("list"))
						return 5;
					return 3;
				}else{
					//纯文字带描述
					return 3;
				}
			}
		}else{
			//长型post
			return 4;
		}
	}


	public class ViewHolder extends ItemViewHolder
	{
		public ImageView icon;
		public TextView score;
		ViewHolder(View v)
		{
			super(v);
			icon = v.findViewById(R.id.icon);
			score = v.findViewById(R.id.score);

		}




	}
	public void setOnItemClickListener(OnItemClickListener l)
	{
		this.l = l;
	}
	public interface OnItemClickListener
	{
		void onItemClick(ViewHolder vh);
	}
	class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
	{
		TextView title,summary;
		ItemViewHolder(View v)
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
			return ja.getJSONObject(getAdapterPosition());
		}
	}
}
