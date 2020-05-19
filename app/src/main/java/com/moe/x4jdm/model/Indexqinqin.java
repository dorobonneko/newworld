package com.moe.x4jdm.model;
import java.util.Map;
import com.alibaba.fastjson.JSONArray;
import org.jsoup.Jsoup;
import java.io.IOException;
import com.alibaba.fastjson.JSONObject;
import java.util.Iterator;
import android.net.Uri;

public class Indexqinqin extends Index
{

	@Override
	public String getIndex(int page)
	{
		JSONArray index=new JSONArray();
		try
		{
			JSONObject main=JSONObject.parseObject(Jsoup.connect(getHost() + "app2/block/home").ignoreContentType(true).execute().body());
			JSONArray items=main.getJSONArray("items");
			for (int i=0;i < items.size();i++) {
				JSONObject item=items.getJSONObject(i);
				switch(item.getString("style")){
					/*case "carousel":{
						JSONArray headers=item.getJSONArray("items");
						index.add(0,headers);
						Iterator<Object> iterator=headers.iterator();
						while(iterator.hasNext()){
							JSONObject header=(JSONObject) iterator.next();
							if(header.getString("link").startsWith("browser://")){iterator.remove();continue;}
							header.put("src",getImageHost()+ header.getString("image"));
							header.put("href",header.getString("link").substring(6));
						}
						}break;*/
					case "block-col-3":
						if(item.getString("title").equals("小说推荐"))continue;
						JSONObject title=new JSONObject();
						index.add(title);
						title.put("title",item.getString("title"));
						if(item.getString("type").equals("comicList")){
							Iterator<Object> iterator=item.getJSONArray("comics").iterator();
							while(iterator.hasNext()){
								JSONObject post=(JSONObject) iterator.next();
								index.add(post);
								post.put("src",getImageHost()+ post.getString("cover"));
								post.put("href",post.getString("id"));
								post.put("desc",post.remove("last_chapter_name"));
							}
						}else{
						Iterator<Object> iterator=item.getJSONArray("items").iterator();
						while(iterator.hasNext()){
							JSONObject post=(JSONObject) iterator.next();
							index.add(post);
							post.put("src",getImageHost()+ post.getString("image"));
							post.put("href",Uri.parse( post.getString("link")).getLastPathSegment());
							}
							}
						break;
				}
			}
		}
		catch (IOException e)
		{}
		return index.toJSONString();
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
	public String getHost()
	{
		return "http://84.sotor.wzfclub.com/";
	}
	public String getImageHost(){
		return "http://gkd155.yuexue0.com:45078/";
	}
}
