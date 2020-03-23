package com.moe.x4jdm.fragment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.support.design.widget.TabLayout;
import com.moe.x4jdm.R;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.view.ViewPager;
import com.moe.x4jdm.model.Indexx4jdm;
import com.alibaba.fastjson.JSONArray;
import com.moe.x4jdm.adapter.TimeViewPagerAdapter;
import java.util.Iterator;
import com.moe.x4jdm.util.Copy;
import java.util.Calendar;
import com.moe.x4jdm.model.Index;
import android.support.v7.widget.Toolbar;
import android.view.ViewTreeObserver;
import android.graphics.Rect;

public class TimeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,ViewPager.OnPageChangeListener
{
    private TabLayout mTabLayout;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private ViewPager mViewPager;
	private JSONArray time_data;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.time_view,container,false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
		mSwipeRefreshLayout=view.findViewById(R.id.swiperefreshlayout);
        mTabLayout=view.findViewById(R.id.tablayout);
		mViewPager=view.findViewById(R.id.viewpager);
		mTabLayout.setupWithViewPager(mViewPager,true);
		mSwipeRefreshLayout.setOnRefreshListener(this);
		mViewPager.setAdapter(new TimeViewPagerAdapter(time_data=new JSONArray()));
		mTabLayout.setTabMode(mTabLayout.MODE_FIXED);
		mViewPager.addOnPageChangeListener(this);
		final Toolbar bar=getActivity().findViewById(R.id.toolbar);
		bar.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener(){

				@Override
				public boolean onPreDraw()
				{
					bar.getViewTreeObserver().removeOnPreDrawListener(this);
					Rect rect=new Rect();
					bar.getGlobalVisibleRect(rect);
					mSwipeRefreshLayout.setPadding(0,rect.bottom,0,0);
					//view.setPadding(0,rect.bottom,0,0);
					int offset=mSwipeRefreshLayout.getProgressCircleDiameter();
					mSwipeRefreshLayout.setProgressViewOffset(false,rect.bottom-offset,rect.bottom+offset);
					
					return false;
				}
			});
    }

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		onRefresh();
	}

	@Override
	public void onRefresh()
	{
		new Thread(){
			public void run(){
				final String data=Index.getModel(getContext()).getTime();
				getView().post(new Runnable(){

						@Override
						public void run()
						{
							mSwipeRefreshLayout.setRefreshing(false);
							load(data);
						}
					});
			}
		}.start();
	}
private void load(String data){
	Copy.copy(getContext(),data);
	time_data.clear();
	try{
	Iterator<JSONArray> iterator=(Iterator<JSONArray>) JSONArray.parseArray(data).iterator();
	while(iterator.hasNext()){
		time_data.add(iterator.next());
	}
	}catch(Exception e){}
	mViewPager.getAdapter().notifyDataSetChanged();
	Calendar calendar=Calendar.getInstance();
	TabLayout.Tab tab=mTabLayout.getTabAt(calendar.get(calendar.DAY_OF_WEEK)-1);
	if(tab!=null)tab.select();
}

@Override
public void onPageScrolled(int p1, float p2, int p3)
{
}

@Override
public void onPageSelected(int p1)
{
}

@Override
public void onPageScrollStateChanged(int p1)
{
	if(p1==ViewPager.SCROLL_STATE_DRAGGING)
		mSwipeRefreshLayout.setEnabled(false);
		else if(p1==ViewPager.SCROLL_STATE_IDLE)
		mSwipeRefreshLayout.setEnabled(true);
}



    
}
