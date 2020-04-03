package com.moe.x4jdm.widget;
import android.support.v7.widget.GridLayoutManager;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

public class GridLayoutManager extends GridLayoutManager
{
	private GridLayoutManager(Context c,final RecyclerView.Adapter a){
		super(c,3);
		//setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
		setSpanSizeLookup(new SpanSizeLookup(){

				@Override
				public int getSpanSize(int p1)
				{
					switch(a.getItemViewType(p1)){
						case 1:
						case 3:
						case 4:
						return 3;
						default:
						return 1;
					}
				}
			});
	}
}
