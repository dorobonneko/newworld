package com.moe.x4jdm.model;
import java.util.Map;

public class None extends Index
{

	@Override
	public String getIndex(int page)
	{
		return null;
	}

	@Override
	public String getTime()
	{
		return null;
	}

	@Override
	public boolean hasTime()
	{
		return false;
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
