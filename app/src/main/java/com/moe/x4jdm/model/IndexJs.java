package com.moe.x4jdm.model;
import java.util.Map;
import android.content.res.AssetManager;

public class IndexJs extends Index
{

	@Override
	public boolean hasTime()
	{
		return false;
	}

	
	public IndexJs(String path){
		
	}
	public IndexJs(AssetManager am,String name){
		
	}
	@Override
	public void clearCache()
	{
	}

	@Override
	public String getIndex()
	{
		return null;
	}

	@Override
	public String getTime()
	{
		return null;
	}

	@Override
	public String getList(String url)
	{
		return null;
	}

	@Override
	public String getPost(String url)
	{
		return null;
	}

	@Override
	public Map<String, String> getVideoUrl(String url)
	{
		return null;
	}

	@Override
	public String search(String key)
	{
		return null;
	}

	@Override
	public String makeUrl(String url)
	{
		return null;
	}

	@Override
	public String getHost()
	{
		return null;
	}

	@Override
	public String getGold()
	{
		return null;
	}
	
}