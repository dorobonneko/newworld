package com.moe.x4jdm.adapter;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import java.util.Map;
import android.support.v4.util.ArrayMap;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.net.Uri;
import android.content.res.TypedArray;

public class PlayAdapter extends RecyclerView.Adapter
{
	private ArrayMap<String,String> data=new ArrayMap<>();
	public void addAll(Map<String,String> maps){
		int size=getItemCount();
		data.clear();
		notifyItemRangeRemoved(getItemCount(),size-getItemCount());
		size=getItemCount();
		data.putAll(maps);
		notifyItemRangeInserted(size,getItemCount()-size);
		
	}
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup p1, int p2)
	{
		return new ViewHolder(new Button(p1.getContext()));
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder p1, int p2)
	{
		ViewHolder vh=(PlayAdapter.ViewHolder) p1;
		String key=data.keyAt(p2);
		vh.btn.setText(key);
		vh.btn.setTag(key);
	}

	@Override
	public int getItemCount()
	{
		return data.size();
	}
	class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
		private Button btn;
		ViewHolder(View v){
			super(v);
			v.setLayoutParams(new ViewGroup.LayoutParams(-1,-2));
			TypedArray ta=v.getContext().obtainStyledAttributes(new int[]{android.support.v7.appcompat.R.attr.selectableItemBackground});
			v.setBackground(ta.getDrawable(0));
			ta.recycle();
			btn=(Button) v;
			btn.setAllCaps(false);
			v.setOnClickListener(this);
		}

		@Override
		public void onClick(View p1)
		{
			p1.getContext().startActivity(new Intent(Intent.ACTION_VIEW).setDataAndType(Uri.parse(data.get(btn.getTag())),"video/*"));
		}

		
	}
}
