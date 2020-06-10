package com.moe.x4jdm.model;
import java.util.Map;
import com.alibaba.fastjson.JSONArray;
import org.jsoup.Jsoup;
import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import java.util.HashMap;

public class indexsg extends Index
{

	@Override
	public String getIndex(int page)
	{
		JSONArray index=new JSONArray();
		try
		{
			JSONObject obj=JSONObject.parseObject(Jsoup.connect(getHost() + "api/videosort").ignoreContentType(true).userAgent("Mozilla (Linux;Android 10)").execute().body());
			index.addAll(obj.getJSONArray("rescont"));
			for (int i=0;i < index.size();i++) {
				JSONObject item=index.getJSONObject(i);
				item.put("href",getHost()+"api/videosort/"+item.getIntValue("id")+"?page=%d");
				item.put("src",item.remove("icopath"));
				item.put("title",item.remove("name"));
				item.put("viewtype","post");
				item.put("click","list");
			}
		}
		catch (IOException e)
		{}
		return index.toJSONString();
	}

	@Override
	public String getList(String url)
	{
		JSONObject list=new JSONObject();
		JSONArray index=new JSONArray();
		list.put("item",index);
		try
		{
			JSONObject obj=JSONObject.parseObject(Jsoup.connect(url).ignoreContentType(true).userAgent("Mozilla (Linux;Android 10)").execute().body()).getJSONObject("rescont");
			list.put("page",obj.getIntValue("current_page"));
			index.addAll(obj.getJSONArray("data"));
			
			list.put("count",obj.getIntValue("last_page"));
			for (int i=0;i < index.size();i++) {
				JSONObject item=index.getJSONObject(i);
				item.put("href",item.remove("id"));
				item.put("src",item.remove("coverpath"));
				item.put("desc",item.remove("authername"));
				item.put("score",item.remove("pageviews"));
				item.put("viewtype","postposter");
				item.put("click","video");
			}
		}
		catch (IOException e)
		{}
		return list.toJSONString();
	}

	@Override
	public String getPost(String url)
	{
		return null;
	}

	@Override
	public Map<String, String> getVideoUrl(String url)
	{
		Map<String,String> urls=new HashMap<>();
		try
		{
			JSONObject obj=JSONObject.parseObject(Jsoup.connect(getHost() + "api/videoplay/" + url + "?uuid=" + getUuid()).ignoreContentType(true).userAgent("Mozilla (Linux;Android 10)").execute().body()).getJSONObject("rescont");
			String video=obj.getString("videopath");
			urls.put(video,video);
		}
		catch (Exception e)
		{}

		return urls;
	}

	@Override
	public String getHost()
	{
		return "http://itrafficnet.com/";
	}
	public String getUuid(){
		return "724b9c9fdd5e7b6f";
	}

	@Override
	public String search(String key)
	{//videotopic/#id
		return getHost()+"api/videosort/search?page=%d";
	}
	
}
