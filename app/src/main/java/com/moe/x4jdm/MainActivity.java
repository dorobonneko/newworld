package com.moe.x4jdm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.moe.x4jdm.fragment.FavoriteFragment;
import com.moe.x4jdm.fragment.IndexFragment;
import com.moe.x4jdm.fragment.ListFragment;
import com.moe.x4jdm.fragment.TimeFragment;
import com.moe.x4jdm.model.Index;
import com.moe.x4jdm.fragment.FilterFragment;
import com.moe.pussy.Pussy;
import android.widget.ImageView;
import android.view.View;
import com.moe.pussy.transformer.CircleTransFormation;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import com.moe.pussy.target.ViewBackgroundTarget;
import com.moe.pussy.transformer.CropTransformer;
import android.util.TypedValue;
import android.app.ActionBar;
import com.moe.pussy.transformer.SprayTransFormer;
import android.os.Handler;
import android.os.Message;
import android.graphics.Bitmap;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,SharedPreferences.OnSharedPreferenceChangeListener,DrawerLayout.DrawerListener,View.OnClickListener{

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle abdt;
    private NavigationView mNavigationView;
    private String cursor;
	private int count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
		View v= getWindow().getDecorView();
		v.setSystemUiVisibility(v.getSystemUiVisibility()|v.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
		
        super.onCreate(savedInstanceState);
		getSharedPreferences("web",0).registerOnSharedPreferenceChangeListener(this);
        if(savedInstanceState!=null){
            cursor=savedInstanceState.getString("cursor");
        }
		setContentView(R.layout.activity_main);
		Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerLayout=findViewById(R.id.drawerlayout);
        mDrawerLayout.addDrawerListener(this);
		abdt=new ActionBarDrawerToggle(this,mDrawerLayout,toolbar,R.string.app_name,R.string.app_name);
        mNavigationView=findViewById(R.id.navigation_view);
        mNavigationView.setNavigationItemSelectedListener(this);
		ImageView icon=mNavigationView.getHeaderView(0).findViewById(R.id.icon);
		Pussy.$(this).load(R.drawable.logo).transformer(new CircleTransFormation((int)Math.ceil(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,2,getResources().getDisplayMetrics())))).into(icon);
		ViewBackgroundTarget vbt=new ViewBackgroundTarget((View)icon.getParent());
		Pussy.$(this).load(R.drawable.timg).transformer(new CropTransformer(Gravity.CENTER),new SprayTransFormer(50)).into(vbt);
		//vbt.getView().setBackgroundResource(R.raw.background);
		icon.setOnClickListener(this);
        if(cursor==null){
            show("index");
        }
		onSharedPreferenceChanged(getSharedPreferences("web",0),"web");
		//throw new NullPointerException();
    }

	
	
    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        abdt.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        abdt.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem p1)
    {
        switch(p1.getItemId()){
            case R.id.index:
                show("index");
                break;
            case R.id.time:
                show("time");
                break;
            case R.id.order:
                show("order");
                break;
			case R.id.favorite:
				show("favorite");
				break;
			case R.id.filter:
				show("filter");
				break;
			case R.id.redpacket:
				count++;
				mHandler.removeMessages(0);
				mHandler.sendEmptyMessageDelayed(0,150);
				
				break;
        }
         return true;
    }
    private void show(String cursor){
		mDrawerLayout.closeDrawer(Gravity.START,true);
        if(cursor.equals(this.cursor))
			return;
		switch(cursor){
            case "index":
                mNavigationView.getMenu().findItem(R.id.index).setChecked(true);
				break;
            case "time":
				mNavigationView.getMenu().findItem(R.id.time).setChecked(true);
				
                break;
			case "favorite":
				mNavigationView.getMenu().findItem(R.id.favorite).setChecked(true);
				
				break;
			case "filter":
				mNavigationView.getMenu().findItem(R.id.filter).setChecked(true);
				
				break;
            case "order":
				mNavigationView.getMenu().findItem(R.id.order).setChecked(true);
				
				break;
				}
        Fragment f=getSupportFragmentManager().findFragmentByTag(cursor);
        if(f==null)
        switch(cursor){
            case "index":
                f=new IndexFragment();
				mNavigationView.getMenu().findItem(R.id.index).setChecked(true);
				 break;
            case "time":
                f=new TimeFragment();
                break;
			case "favorite":
				f=new FavoriteFragment();
				break;
			case "filter":
				f=new FilterFragment();
				break;
            case "order":
                f=new ListFragment();
				Bundle b=new Bundle();
				b.putString("url",Index.getModel(getApplicationContext()).getGold());
				b.putInt("page",1);
				b.putString("key",Index.getKey(getApplicationContext()));
				f.setArguments(b);
                break;
			
        }
      FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
      if(this.cursor!=null)ft.hide(getSupportFragmentManager().findFragmentByTag(this.cursor));
      if(f.isAdded()){
          ft.show(f);
		  this.cursor=cursor;
      }else{
          ft.add(R.id.content,f,this.cursor=cursor);
      }
	  ft.setTransition(ft.TRANSIT_FRAGMENT_FADE);
      ft.commit();
    }

	@Override
	public void finish()
	{
		if(!"index".equals(cursor)){
			show("index");
			}else
		moveTaskToBack(true);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences p1, String p2)
	{
		switch(p2){
			case "web":
				String web=p1.getString(p2,"x4j");
				Index index=Index.getModel(web);
				Menu menu=mNavigationView.getMenu();
				menu.findItem(R.id.order).setVisible(index.getGold()!=null);
				menu.findItem(R.id.time).setVisible(index.hasTime());
				menu.findItem(R.id.filter).setVisible(index.getFilter()!=null);
				Fragment f=getSupportFragmentManager().findFragmentByTag("order");
				if(f!=null)
					getSupportFragmentManager().beginTransaction().remove(f).commitAllowingStateLoss();
				f=getSupportFragmentManager().findFragmentByTag("time");
				if(f!=null)
					getSupportFragmentManager().beginTransaction().remove(f).commitAllowingStateLoss();
				f=getSupportFragmentManager().findFragmentByTag("filter");
				if(f!=null)
					getSupportFragmentManager().beginTransaction().remove(f).commitAllowingStateLoss();
				IndexFragment index_f=(IndexFragment) getSupportFragmentManager().findFragmentByTag("index");
				if(index_f!=null){
				index_f.setRefreshing(true);
				index_f.onRefresh();
				}
				//Pussy.$(getContext()).clearMemory();
				
				break;
		}
	}

	@Override
	public void onDrawerOpened(View p1)
	{
		View v= getWindow().getDecorView();
		v.setSystemUiVisibility(v.getSystemUiVisibility()&(~v.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR));
		
	}

	@Override
	public void onDrawerStateChanged(int p1)
	{
	}

	@Override
	public void onDrawerSlide(View p1, float p2)
	{
	}

	@Override
	public void onDrawerClosed(View p1)
	{
		View v= getWindow().getDecorView();
		v.setSystemUiVisibility(v.getSystemUiVisibility()|v.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
		
		
	}

	@Override
	public void onClick(View p1)
	{
		if("index".equals(cursor)){
			String[] keys=getResources().getStringArray(R.array.site);
			new AlertDialog.Builder(this).setItems(keys, new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface p1, int p2)
					{
						String[] data=getResources().getStringArray(R.array.site_key);
						getSharedPreferences("web",0).edit().putString("web",data[p2]).commit();
						mDrawerLayout.closeDrawer(Gravity.START);
					}
				}).show();
		}else if("favorite".equals(cursor)){
			final String[] keys=new String[]{"adh","hh","xyg","msiv","hhr","ppc","ahv","llias","jyk","hb","3atv"};
			new AlertDialog.Builder(this).setItems(keys, new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface p1, int p2)
					{
						getSharedPreferences("web",0).edit().putString("web",keys[p2]).commit();
						mDrawerLayout.closeDrawer(Gravity.START);
						show("index");
						
					}
				}).show();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putString("cursor",cursor);
	}

	private Handler mHandler=new Handler(){

		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what){
				case 0:
					if(count==1){
						String qrcode="HTTPS://QR.ALIPAY.COM/FKX04316NDQAI5DTRD9P20";
						try
						{startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("alipayqr://platformapi/startapp?saId=10000007&qrcode=" +qrcode)));
						}
						catch (Exception e)
						{
							Toast.makeText(getApplicationContext(), "未安装支付宝！", Toast.LENGTH_SHORT).show();
						}
					}else if(count==2){
						final String[] keys=new String[]{"moeero"};
						new AlertDialog.Builder(MainActivity.this).setItems(keys, new DialogInterface.OnClickListener(){

								@Override
								public void onClick(DialogInterface p1, int p2)
								{
									getSharedPreferences("web",0).edit().putString("web",keys[p2]).commit();
									mDrawerLayout.closeDrawer(Gravity.START);
									show("index");

								}
							}).show();
					}
					count=0;
					break;
			}
		}

		
	
};

	
}
