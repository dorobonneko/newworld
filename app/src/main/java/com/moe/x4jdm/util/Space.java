package com.moe.x4jdm.util;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.v7.widget.RecyclerView.State;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;

public class Space extends RecyclerView.ItemDecoration
{
	private int size;
	public Space(float size){
		this.size=(int)size;
	}

	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
	{
		
		int v=size/2;
		outRect.set(v,v,v,v);
			
	}

	
}
