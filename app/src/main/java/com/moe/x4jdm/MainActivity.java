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

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,SharedPreferences.OnSharedPreferenceChangeListener{

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle abdt;
    private NavigationView mNavigationView;
    private String cursor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        abdt=new ActionBarDrawerToggle(this,mDrawerLayout,toolbar,R.string.app_name,R.string.app_name);
        mNavigationView=findViewById(R.id.navigation_view);
        mNavigationView.setNavigationItemSelectedListener(this);
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
				String qrcode="c1x04252qlecmighcngswb99hq";
				try
				{startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("alipayqr://platformapi/startapp?saId=10000007&qrcode=" +qrcode)));
				}
				catch (Exception e)
				{
					Toast.makeText(getApplicationContext(), "未安装支付宝！", Toast.LENGTH_SHORT).show();
				}
				break;
        }
        mDrawerLayout.closeDrawer(Gravity.START,true);
        return true;
    }
    private void show(String cursor){
        if(cursor.equals(this.cursor))return;
        Fragment f=getSupportFragmentManager().findFragmentByTag(cursor);
        if(f==null)
        switch(cursor){
            case "index":
                f=new IndexFragment();
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
			mNavigationView.getMenu().getItem(0).setChecked(true);
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
				
				break;
		}
	}

	
}
