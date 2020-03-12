package com.moe.x4jdm.fragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.moe.x4jdm.R;
import com.moe.x4jdm.adapter.PostAdapter;
import com.moe.x4jdm.model.Index;
import com.moe.x4jdm.util.Space;
import com.moe.x4jdm.widget.GridLayoutManager;
import com.moe.x4jdm.model.Database;
import com.moe.x4jdm.adapter.IndexAdapter;
import com.moe.x4jdm.widget.IndexGridLayoutManager;

public class FavoriteFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener
{

	private SwipeRefreshLayout mSwipeRefreshLayout;
	private RecyclerView mRecyclerView;
	private JSONArray post_data;
	private IndexAdapter pa;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.list_view,container,false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		mSwipeRefreshLayout=view.findViewById(R.id.swiperefreshlayout);
		mSwipeRefreshLayout.setOnRefreshListener(this);
		mRecyclerView=view.findViewById(R.id.recyclerview);

		pa=new IndexAdapter(post_data=new JSONArray(),false);
		mRecyclerView.setLayoutManager(new IndexGridLayoutManager(getContext(),pa));
		//((SimpleItemAnimator)mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
		mRecyclerView.addItemDecoration(new Space((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,8,getResources().getDisplayMetrics())));
		mRecyclerView.setAdapter(pa);
		//mRecyclerView.addOnScrollListener(new Scroll());
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onActivityCreated(savedInstanceState);
		mSwipeRefreshLayout.setRefreshing(true);
		onRefresh();
	}

	@Override
	public void onRefresh()
	{
		new Thread(){
			public void run(){
				final JSONArray data=JSONArray.parseArray(Database.getInstance(getContext()).query());
				mSwipeRefreshLayout.post(new Runnable(){

						@Override
						public void run()
						{
							post_data.clear();
							post_data.addAll(data);
							pa.notifyDataSetChanged();
							mSwipeRefreshLayout.setRefreshing(false);
						}
					});
			}
		}.start();
	}

	@Override
	public void onResume()
	{
		super.onResume();
		onRefresh();
	}
	
	
}
