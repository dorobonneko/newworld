package com.moe.x4jdm;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.moe.x4jdm.fragment.ListFragment;
import android.view.View;
import com.moe.x4jdm.app.Application;
import com.moe.pussy.Pussy;

public class ListActivity extends AppCompatActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility()|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
		if(savedInstanceState==null){
			ListFragment list=new ListFragment();
			list.setArguments(getIntent().getExtras());
		getSupportFragmentManager().beginTransaction().add(android.R.id.content,list).commit();
		}
		}
	
}
