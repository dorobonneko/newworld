package com.moe.x4jdm.fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.moe.pussy.Pussy;
import com.moe.x4jdm.ListActivity;
import com.moe.x4jdm.PostViewActivity;
import com.moe.x4jdm.R;
import com.moe.x4jdm.adapter.IndexAdapter;
import com.moe.x4jdm.adapter.PostAdapter;
import com.moe.x4jdm.model.Database;
import com.moe.x4jdm.model.Index;
import com.moe.x4jdm.util.Space;
import com.moe.x4jdm.widget.GridLayoutManager;
import com.moe.x4jdm.widget.IndexGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.graphics.Rect;
import android.view.ViewTreeObserver;

public class IndexFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,View.OnClickListener,PostAdapter.OnItemClickListener
{
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private View progress;
	private RecyclerView recyclerview;
	private JSONArray data;
	private IndexAdapter mIndexAdapter;
	private Thread mThread;
	private String key;
	private int page=1;
	public void setRefreshing(boolean p0)
	{
		key=Index.getKey(getContext());
		if(mSwipeRefreshLayout!=null)
			mSwipeRefreshLayout.setRefreshing(p0);
	}
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.index_view, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
		mSwipeRefreshLayout = view.findViewById(R.id.swiperefreshlayout);
		mSwipeRefreshLayout.setEnabled(false);
		progress = mSwipeRefreshLayout.findViewById(R.id.progress);
		recyclerview = mSwipeRefreshLayout.findViewById(R.id.recyclerview);
		mSwipeRefreshLayout.setOnRefreshListener(this);
		recyclerview.setItemViewCacheSize(20);
		recyclerview.setHasFixedSize(true);
		recyclerview.addItemDecoration(new Space((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,8,getResources().getDisplayMetrics())));
		mIndexAdapter=new IndexAdapter(data=new JSONArray());
		recyclerview.setLayoutManager(new IndexGridLayoutManager(view.getContext(),mIndexAdapter));
		recyclerview.setAdapter(mIndexAdapter);
		final Toolbar bar=getActivity().findViewById(R.id.toolbar);
		bar.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener(){

				@Override
				public boolean onPreDraw()
				{
					bar.getViewTreeObserver().removeOnPreDrawListener(this);
					Rect rect=new Rect();
					bar.getGlobalVisibleRect(rect);
					//mSwipeRefreshLayout.setPadding(0,rect.top,0,0);
					recyclerview.setPadding(0,rect.bottom,0,0);
					int offset=mSwipeRefreshLayout.getProgressCircleDiameter();
					mSwipeRefreshLayout.setProgressViewOffset(false,-offset,rect.bottom+offset);
					
					return false;
				}
			});
			//recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
		//recyclerview.setAdapter(mIndexAdapter=new IndexAdapter(data=new JSONArray()));
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		key=Index.getKey(getContext());
		setHasOptionsMenu(true);
		onRefresh();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		inflater.inflate(R.menu.search,menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId()){
			case R.id.search:
				final EditText search=new EditText(getContext());
				search.setSingleLine(true);
				AlertDialog show=new AlertDialog.Builder(getActivity()).setTitle("Search").setIcon(R.drawable.magnify).setView(search).setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface p1, int p2)
						{
							String key=search.getText().toString().trim();
							if(key.startsWith("class:")){
								getContext().getSharedPreferences("web",0).edit().putString("web",key.substring(6)).commit();
								}else if(key.startsWith("clear:")){
								switch(key.substring(6)){
									case "memory":
										Pussy.$(getContext()).clearMemory();
										break;
									case "cache":
										Pussy.$(getContext()).clearCache();
										break;
									case "favorite":
										Database.getInstance(getContext()).delete(null);
										break;
								}
							} else if(key.startsWith("show:")){
								switch(key.substring(5)){
									case "memory":
										new AlertDialog.Builder(getActivity()).setMessage("单位Kb\n已使用："+(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/1000+"\n剩余："+Runtime.getRuntime().freeMemory()/1000+"\n总共："+Runtime.getRuntime().totalMemory()/1000+"\n最大："+Runtime.getRuntime().maxMemory()/1000).show();
										break;
									case "keys":
										break;
								 }
							}else{
								String url=Index.getModel(getContext()).search(key);
								if(url!=null)
								startActivity(new Intent(getContext(),ListActivity.class).putExtra("url",url));
							}
						}
					}).create();
				show.show();
				break;
		}
		return true;
	}

	@Override
	public void onRefresh()
	{
		if(mThread!=null)
			mThread.interrupt();
		(mThread=new Thread(){
			public void run()
			{
				Index.getModel(key).clearCache();
				if(interrupted())return;
				final String data=Index.getModel(key).getIndex(page);
				if(interrupted())return;
				mSwipeRefreshLayout.post(new Runnable(){

						@Override
						public void run()
						{
							load(data);
						}
					});
			}
		}).start();
	}

    private void load(String data)
	{
		key=Index.getKey(getContext());
		progress.setVisibility(View.GONE);
		mSwipeRefreshLayout.setEnabled(true);
		mSwipeRefreshLayout.setRefreshing(false);
		if (data == null)return;
		JSONArray ja=JSONArray.parseArray(data);
		int size=mIndexAdapter.getItemCount();
		this.data.clear();
		mIndexAdapter.notifyItemRangeRemoved(0,size);
		this.data.addAll(ja);
		mIndexAdapter.notifyItemRangeInserted(0,this.mIndexAdapter.getItemCount());
//		JSONObject jo=JSONObject.parseObject(data);
//		if (jo == null)return;
//		JSONArray tabs=jo.getJSONArray("tab");
//		this.tab.removeAllTabs();
//		this.tab.setOnTabSelectedListener(null);
//		if (tabs != null)
//		{
//			tab.setVisibility(View.VISIBLE);
//			for (int i=0;i < tabs.size();i++)
//			{
//				JSONObject tab=tabs.getJSONObject(i);
//				this.tab.addTab(this.tab.newTab().setText(tab.getString("title")).setTag(tab.getString("href")));
//			}
//		}
//		this.tab.setOnTabSelectedListener(this);
//		tab.setTabMode(tab.getTabCount()<=5?tab.MODE_FIXED:tab.MODE_SCROLLABLE);
//		
//		//加载头部数据
//		header_data.clear();
//		JSONArray header_array=jo.getJSONArray("head");
//		if (header_array != null)
//		{header.setVisibility(View.VISIBLE);
//			Iterator<JSONObject> iterator=(Iterator<JSONObject>) header_array.iterator();
//			while (iterator.hasNext())
//			{
//				header_data.add(iterator.next());
//			}
//		}
//		header.getAdapter().notifyDataSetChanged();
//		JSONArray mains=jo.getJSONArray("main");
//		//getContext().getSystemService(ClipboardManager.class).setText(mains.toJSONString());
//		if (mains != null)
//		{
//			Iterator<JSONObject> main_iterator=(Iterator<JSONObject>) mains.iterator();
//			content.removeAllViews();
//			while (main_iterator.hasNext())
//			{
//				createMainView(main_iterator.next(), getActivity().getLayoutInflater(), content);
//			}
//		}
//		//Toast.makeText(getContext(),header_data.toJSONString(),Toast.LENGTH_SHORT).show();
//		if (header_data.size() > 0 && !handler.hasMessages(0))
//			handler.sendEmptyMessageDelayed(0, 3000);
	}

	

	/*private void createMainView(JSONObject jo, LayoutInflater inflater, ViewGroup parent)
	{

		View main=inflater.inflate(R.layout.main_item, parent, false);
		parent.addView(main);
		
		TextView title=main.findViewById(R.id.title);
		TextView summary=main.findViewById(R.id.more);
		final RecyclerView recyclerview=main.findViewById(R.id.recyclerview);
		title.setText(jo.getString("title"));
		String href=jo.getString("href");
		if (href == null)summary.setVisibility(View.INVISIBLE);
		else
		{summary.setTag(href);summary.setOnClickListener(this);}
		if(title.getText().length()==0&&summary.getVisibility()==View.INVISIBLE)
			((View)title.getParent()).setVisibility(View.GONE);
		final JSONArray items=jo.getJSONArray("item");
		
		recyclerview.setNestedScrollingEnabled(false);
		recyclerview.addItemDecoration(new Space(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics())));
		//recyclerview.setItemAnimator(null);
		//pa.notifyDataSetChanged();
		final JSONArray data=new JSONArray();
		final PostAdapter pa=new PostAdapter(data, false);
		recyclerview.setLayoutManager(new GridLayoutManager(getContext(),pa));
		pa.setOnItemClickListener(IndexFragment.this);
		recyclerview.setAdapter(pa);
		recyclerview.getLayoutManager().setAutoMeasureEnabled(true);
		if(items!=null){
			recyclerview.postDelayed(new Runnable(){   

			@Override                                                           
			public void run()
			{
				data.addAll(items);
				pa.notifyItemRangeInserted(0,data.size());
		}
	},0);
		
		}
	}*/

	@Override
	public void onClick(View p1)
	{
		getContext().startActivity(new Intent(getContext(), ListActivity.class).putExtra("url", p1.getTag().toString()));
	}

	@Override
	public void onItemClick(PostAdapter.ViewHolder vh)
	{
		startActivity(new Intent(getContext(), PostViewActivity.class).putExtra("url", vh.getObject().getString("href")).putExtra("key",Index.getKey(getContext())));
	}




}
