package com.moe.x4jdm.model;
import java.util.Map;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.Connection;
import java.io.IOException;
import com.alibaba.fastjson.JSONArray;
import java.util.LinkedHashMap;
import android.net.Uri;
import com.moe.x4jdm.video.VideoParse;

public class Indexjn extends Index
{

	@Override
	public void clearCache()
	{
	}
/*21,热血
20，科幻
31，百合
16，青春
15，推理
14，奇幻
13，恐怖
12，后宫
11，耽美
3，搞笑
17，泡面
*/
	@Override
	public String getIndex(int page)
	{
		JSONArray index=new JSONArray();
		try
		{
			JSONObject data=JSONObject.parseObject(Jsoup.connect("http://59.110.16.198//app.php/XyyVideo/video_recommend").ignoreContentType(true).method(Connection.Method.POST).requestBody("version_number=31").execute().body());
			if(data.getIntValue("status")==1){
				//head
				JSONArray header=data.getJSONArray("data");
				index.add(header);
				for(int i=0;i<header.size();i++){
					JSONObject head=header.getJSONObject(i);
					head.put("href",head.getIntValue("id"));
					head.put("src",head.getString("recommend_image"));
				}
			}
			index.add(JSONArray.parseArray("[{\"href\":\"21,%d\",\"title\":\"热血\"},{\"href\":\"20,%d\",\"title\":\"科幻\"},{\"href\":\"31,%d\",\"title\":\"百合\"},{\"href\":\"16,%d\",\"title\":\"青春\"},{\"href\":\"15,%d\",\"title\":\"推理\"},{\"href\":\"14,%d\",\"title\":\"奇幻\"},{\"href\":\"13,%d\",\"title\":\"恐怖\"},{\"href\":\"12,%d\",\"title\":\"后宫\"},{\"href\":\"11,%d\",\"title\":\"耽美\"},{\"href\":\"3,%d\",\"title\":\"搞笑\"},{\"href\":\"17,%d\",\"title\":\"泡面番\"}]"));
			
			data=JSONObject.parseObject(Jsoup.connect("http://59.110.16.198//app.php/XyyVideo/video_class_list").ignoreContentType(true).method(Connection.Method.POST).requestBody("version_number=31").execute().body());
			if(data.getIntValue("status")==1){
				JSONArray arr=data.getJSONArray("data");
				//index.put("main",arr);
				for(int i=0;i<arr.size();i++){
					JSONObject item=arr.getJSONObject(i);
					JSONObject title=new JSONObject();
					title.put("title",item.remove("video_class_name"));
					index.add(title);
					
					//item.put("href",item.remove("id")+",%d");
					//item.put("item",item.remove("video"));
					JSONArray video=item.getJSONArray("video");
					for(int n=0;n<video.size();n++){
						JSONObject video_item=video.getJSONObject(n);
						video_item.put("title",video_item.remove("video_subject"));
						video_item.put("src",video_item.remove("video_image"));
						video_item.put("href",video_item.remove("vid"));
						index.add(video_item);
					}
				}
			}
		}
		catch (Exception e)
		{}
		return index.toJSONString();
	}

	@Override
	public String getTime()
	{
		JSONArray time=new JSONArray();
		try
		{
			for(int i=1;i<7;i++){
			JSONObject data=JSONObject.parseObject(Jsoup.connect(getHost()+"/app.php/XyyVideo/date_video").method(Connection.Method.POST).requestBody(String.format("date_number=%d",i)).ignoreContentType(true).execute().body());
			if(data.getIntValue("status")==1){
				time.add(data.getJSONArray("data"));
				JSONArray object=data.getJSONArray("data");
				for(int n=0;n<object.size();n++){
					JSONObject item=object.getJSONObject(n);
					item.put("title",item.remove("video_subject"));
					item.put("href",item.remove("id"));
				}
			}else{
				time.add(new JSONArray());
			}
			}
			JSONObject data=JSONObject.parseObject(Jsoup.connect(getHost()+"/app.php/XyyVideo/date_video").method(Connection.Method.POST).requestBody(String.format("date_number=%d",7)).ignoreContentType(true).execute().body());
			if(data.getIntValue("status")==1){
				time.add(0,data.getJSONArray("data"));
				JSONArray object=data.getJSONArray("data");
				for(int n=0;n<object.size();n++){
					JSONObject item=object.getJSONObject(n);
					item.put("title",item.remove("video_subject"));
					item.put("href",item.remove("id"));
				}
			}else{
				time.add(0,new JSONArray());
			}
		}
		catch (IOException e)
		{}
		return time.toJSONString();
	}

	@Override
	public boolean hasTime()
	{
		return true;
	}


	@Override
	public String getList(String url)
	{
		JSONObject list=new JSONObject();
		if(!url.startsWith(getHost())){
		try
		{
			JSONObject data=JSONObject.parseObject(Jsoup.connect(getHost()+"/app.php/XyyVideo/video_type_search_list").ignoreContentType(true).method(Connection.Method.POST).requestBody(String.format("type_id=%s&cid=3&page=%s&version_number=72", url.split(","))).execute().body());
			list.put("page",url.split(",")[1]);
			list.put("count",list.getIntValue("page"));
			if(data.getIntValue("status")==1){
				JSONArray video=data.getJSONArray("data");
				list.put("item",video);
				for(int n=0;n<video.size();n++){
					JSONObject video_item=video.getJSONObject(n);
					video_item.put("title",video_item.remove("video_subject"));
					video_item.put("src",video_item.remove("video_image"));
					video_item.put("href",video_item.remove("vid"));
				}
				if(video.size()>0)
					list.put("count",list.getIntValue("page")+1);
			}
		}
		catch (IOException e)
		{}
		}else{
			try
			{
				JSONObject data=JSONObject.parseObject(Jsoup.connect(url).method(Connection.Method.POST).ignoreContentType(true).requestBody(url.substring(url.indexOf("?"))).execute().body());
				list.put("page",Uri.parse(url).getQueryParameter("page"));
					list.put("count",list.getIntValue("page"));
					if(data.getIntValue("status")==1){
						JSONArray video=data.getJSONArray("data");
						list.put("item",video);
						for(int n=0;n<video.size();n++){
							JSONObject video_item=video.getJSONObject(n);
							video_item.put("title",video_item.remove("video_subject"));
							video_item.put("src",video_item.remove("video_image"));
							video_item.put("href",video_item.remove("id"));
						}
						if(video.size()>0)
							list.put("count",list.getIntValue("page")+1);
					}
				
			}
			catch (IOException e)
			{}
		}
		return list.toJSONString();
	}

	@Override
	public String getPost(String url)
	{
		JSONObject post=new JSONObject();
		try
		{
			JSONObject data=JSONObject.parseObject(Jsoup.connect(getHost() + "/app.php/XyyVideo/video_info").ignoreContentType(true).method(Connection.Method.POST).requestBody(String.format("uid=0&vid=%s", url)).execute().body());
			if(data.getIntValue("status")==1){
				JSONArray video=data.getJSONArray("video_lists");
				JSONArray play=new JSONArray();
				post.put("video",play);
				play.add(video);
				for(int i=0;i<video.size();i++){
					JSONObject item=video.getJSONObject(i);
					item.put("title",item.remove("video_number"));
					item.put("href",item.remove("vod_id"));
				}
			}
			data=data.getJSONObject("data");
			post.put("profile",data.getString("video_describe"));
			post.put("src",data.getString("video_image"));
			post.put("title",data.getString("video_subject"));
		}
		catch (IOException e)
		{}
		return post.toJSONString();
	}

	@Override
	public Map<String, String> getVideoUrl(String url)
	{
		Map<String,String> urls=new LinkedHashMap<>();
		if(VideoParse.match(url)){
			url=VideoParse.parse(url,"");
		}
		if(url!=null)
		urls.put(url,url);
		return urls;
	}

	@Override
	public String search(String key)
	{
		return getHost()+"/app.php/XyySearch/get_search_data?keyword="+key+"&uid=0&page=%d&type=1";
	}

	@Override
	public String makeUrl(String url)
	{
		return null;
	}

	@Override
	public String getHost()
	{
		return "http://59.110.16.198/";
	}

	@Override
	public String getGold()
	{
		return "27,%d";
	}
	
}
