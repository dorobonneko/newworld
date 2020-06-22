package com.moe.x4jdm.widget;
import com.moe.x4jdm.widget.SpanLayoutManager.Span;
import android.support.v7.widget.RecyclerView;

public class SizeLookup extends SpanLayoutManager.SpanSizeLookup
{
	private RecyclerView.Adapter adapter;
	public SizeLookup(RecyclerView.Adapter adapter){
		this.adapter=adapter;
	}
	@Override
	SpanLayoutManager.Span getSpan(int position)
	{
		switch (adapter.getItemViewType(position))
		{
			case 0:
			case 1://header
			case 2://tab
			case 3://header_title
			case 5://post_line
			case 8:
			case 7://load_more
			case 9:
			case 10:
			case 11://imagepreview
			case 12://commemnt
			case 13://listcomic
			case 100:
				return new SpanLayoutManager.Span(1,1);
			case 4:
			case 6:
				return new SpanLayoutManager.Span(3,1);
		}
		return new SpanLayoutManager.Span(3,1);
	}
	
}
