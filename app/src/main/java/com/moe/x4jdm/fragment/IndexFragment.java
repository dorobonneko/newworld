package com.moe.x4jdm.fragment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import com.moe.x4jdm.R;
import android.support.v4.widget.SwipeRefreshLayout;
import com.moe.x4jdm.model.Indexx4jdm;
import com.alibaba.fastjson.JSONObject;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.TabLayout;
import android.widget.LinearLayout;
import com.alibaba.fastjson.JSONArray;
import android.support.design.widget.TabLayout.Tab;
import android.widget.TabHost;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v4.view.ViewPager;
import android.os.Handler;
import android.os.Message;
import com.moe.x4jdm.adapter.HeaderAdapter;
import java.util.Iterator;
import android.widget.Toast;
import android.widget.ImageView;
import android.widget.TextView;
import com.moe.x4jdm.adapter.PostAdapter;
import android.content.ClipboardManager;
import android.util.TypedValue;
import com.moe.x4jdm.util.Space;
import android.content.Intent;
import com.moe.x4jdm.ListActivity;
import com.moe.x4jdm.adapter.PostAdapter.ViewHolder;
import com.moe.x4jdm.PostViewActivity;
import com.moe.x4jdm.model.Index;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import com.moe.x4jdm.widget.GridLayoutManager;
import com.moe.x4jdm.adapter.IndexAdapter;
import com.moe.tinyimage.Pussy;
import com.moe.x4jdm.model.Database;
import com.moe.x4jdm.widget.IndexGridLayoutManager;

public class IndexFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,View.OnClickListener,PostAdapter.OnItemClickListener
{
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private View progress;
	private RecyclerView recyclerview;
	private JSONArray data;
	private IndexAdapter mIndexAdapter;
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
		recyclerview.setItemViewCacheSize(5);
		recyclerview.setHasFixedSize(true);
		recyclerview.addItemDecoration(new Space((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,8,getResources().getDisplayMetrics())));
		recyclerview.setAdapter(mIndexAdapter=new IndexAdapter(data=new JSONArray()));
		
		recyclerview.setLayoutManager(new IndexGridLayoutManager(view.getContext(),mIndexAdapter));
		//recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
		//recyclerview.setAdapter(mIndexAdapter=new IndexAdapter(data=new JSONArray()));
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
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
								Fragment f=getFragmentManager().findFragmentByTag("order");
								if(f!=null)
								getFragmentManager().beginTransaction().remove(f).commit();
								f=getFragmentManager().findFragmentByTag("time");
								if(f!=null)
								getFragmentManager().beginTransaction().remove(f).commit();
								mSwipeRefreshLayout.setRefreshing(true);
								onRefresh();
								Pussy.$(getContext()).clearMemory();
							}else if(key.startsWith("clear:")){
								switch(key.substring(6)){
									case "memory":
										Pussy.$(getContext()).clearMemory();
										break;
									case "cache":
										Pussy.$(getContext()).clearDisk();
										break;
									case "favorite":
										Database.getInstance(getContext()).delete(null);
										break;
								}
							} else{
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
		new Thread(){
			public void run()
			{
				Index.getModel(getContext()).clearCache();
				final String data=Index.getModel(getContext()).getIndex();
				mSwipeRefreshLayout.post(new Runnable(){

						@Override
						public void run()
						{
							load(data);
						}
					});
			}
		}.start();
	}

    private void load(String data)
	{
		progress.setVisibility(View.GONE);
		mSwipeRefreshLayout.setEnabled(true);
		mSwipeRefreshLayout.setRefreshing(false);
		if (data == null)return;
		JSONArray ja=JSONArray.parseArray(data);
		int size=this.data.size();
		this.data.clear();
		mIndexAdapter.notifyItemRangeRemoved(0,size);
		this.data.addAll(ja);
		mIndexAdapter.notifyItemRangeInserted(0,this.data.size());
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

	

	private void createMainView(JSONObject jo, LayoutInflater inflater, ViewGroup parent)
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
	}

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
