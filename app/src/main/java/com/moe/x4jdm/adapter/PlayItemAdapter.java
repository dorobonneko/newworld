package com.moe.x4jdm.adapter;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.content.res.TypedArray;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class PlayItemAdapter extends RecyclerView.Adapter<PlayItemAdapter.ViewHolder>
{
	private JSONArray ja;
	private OnItemClickListener l;
	public PlayItemAdapter(JSONArray ja){
		this.ja=ja;
	}
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup p1, int p2)
	{
		// TODO: Implement this method
		return new ViewHolder(new Button(p1.getContext()));
	}

	@Override
	public void onBindViewHolder(ViewHolder p1, int p2)
	{
		p1.btn.setText(ja.getJSONObject(p2).getString("title"));
	}

	@Override
	public int getItemCount()
	{
		// TODO: Implement this method
		return ja.size();
	}
	
	class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
		Button btn;
		ViewHolder(View v){
			super(v);
			btn=(Button) v;
			btn.setAllCaps(false);
			TypedArray ta=v.getContext().obtainStyledAttributes(new int[]{android.support.v7.appcompat.R.attr.selectableItemBackground});
			btn.setBackground(ta.getDrawable(0));
			ta.recycle();
			v.setOnClickListener(this);
		}

		@Override
		public void onClick(View p1)
		{
			if(l!=null)l.onItemClick(this);
		}
		public JSONObject getObject(){
			return ja.getJSONObject(getAdapterPosition());
		}
	}
	public void setOnItemClickListener(OnItemClickListener l){
		this.l=l;
	}
	public interface OnItemClickListener{
		void onItemClick(ViewHolder vh)
	}
}
