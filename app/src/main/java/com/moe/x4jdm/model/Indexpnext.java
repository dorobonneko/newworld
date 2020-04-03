package com.moe.x4jdm.model;
import java.util.Map;
import com.alibaba.fastjson.JSONArray;
import org.jsoup.Jsoup;
import java.io.IOException;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
import com.alibaba.fastjson.JSONObject;
import android.net.Uri;
import java.net.URLEncoder;
import android.text.TextUtils;
import com.moe.x4jdm.video.VideoParse;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Collections;

public class Indexpnext extends Index
{
	private String time;
	private String getKey(String str){
		
		long userPKey=System.currentTimeMillis();
		int userPMain=17;//(int) Math.ceil(100*Math.random());
		int userPBase=(int) Math.floor(userPKey/userPMain/102*.8);
		String data=str.substring(str.indexOf("&")+1)+"userPKey"+userPKey+"userPMain"+userPMain+"userPBase"+userPBase;
		data=filter(data);
		String userUser="";
		for(int n=0;n<data.length();n++)
		{
			if(n%5==0&&n<=80)
				userUser+=data.charAt(n);
		}
		return String.format("&userPKey=%d&userPMain=%d&userPBase=%d&userUser=%s",userPKey,userPMain,userPBase,userUser);
		}
	private String filter(String str){
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<str.length();i++){
			char c=str.charAt(i);
			if((c>='a'&&c<='z')||(c>='A'&&c<='Z')||(c>='0'&&c<='9')){
				sb.append(c);
			}
		}
		return sb.toString();
	}
	@Override
	public String getFilter()
	{
		return "[{\"key\":\"classId\",\"desc\":\"类型\",\"value\":[{\"value\":\"0\",\"title\":\"全部\"},{\"value\":\"23\",\"title\":\"历史\"},{\"value\":\"22\",\"title\":\"战争\"},{\"value\":\"21\",\"title\":\"魔法\"},{\"value\":\"17\",\"title\":\"爱情\"},{\"value\":\"16\",\"title\":\"治愈\"},{\"value\":\"15\",\"title\":\"日常\"},{\"value\":\"14\",\"title\":\"动作\"},{\"value\":\"13\",\"title\":\"机战\"},{\"value\":\"12\",\"title\":\"科幻\"},{\"value\":\"11\",\"title\":\"惊悚\"},{\"value\":\"10\",\"title\":\"恐怖\"},{\"value\":\"9\",\"title\":\"奇幻\"},{\"value\":\"8\",\"title\":\"冒险\"},{\"value\":\"7\",\"title\":\"后宫\"},{\"value\":\"6\",\"title\":\"恋爱\"},{\"value\":\"5\",\"title\":\"校园\"},{\"value\":\"4\",\"title\":\"日本\"},{\"value\":\"3\",\"title\":\"热血\"},{\"value\":\"2\",\"title\":\"搞笑\"},{\"value\":\"1\",\"title\":\"战斗\"}]},{\"key\":\"year\",\"desc\":\"年代\",\"value\":[{\"value\":\"0\",\"title\":\"全部\"},{\"value\":2019,\"title\":2019},{\"value\":2018,\"title\":2018},{\"value\":2017,\"title\":2017},{\"value\":2016,\"title\":2016},{\"value\":2015,\"title\":2015},{\"value\":2014,\"title\":2014},{\"value\":2013,\"title\":2013},{\"value\":2012,\"title\":2012},{\"value\":2011,\"title\":2011},{\"value\":2010,\"title\":\"更早\"}]}]";
	}

	@Override
	public String makeFilter(String filter)
	{
		return getHost()+"/apiH5.php?r=class/classlist"+filter+"&page=%d";
	}
	
	@Override
	public void clearCache()
	{
		time=null;
		}
	
	
	@Override
	public String getTime()
	{
		
		return time;
	}

	@Override
	public boolean hasTime()
	{
		return true;
	}
	
	@Override
	public String getIndex(int page)
	{
		JSONArray index=new JSONArray();
		try
		{
			JSONObject data=JSONObject.parseObject(Jsoup.connect(getHost()+"/apiH5.php?r=index/getbannerlist"+getKey("&")).ignoreContentType(true).referrer(getHost()).execute().body());
			if(data.getIntValue("code")==0){
				data=data.getJSONObject("specialData");
				if(data==null)throw new IOException();
				//轮播
				JSONArray banner=data.getJSONArray("banner");
				for(int i=0;i<banner.size();i++){
					JSONObject item=banner.getJSONObject(i);
					item.put("href",makeUrl(item.remove("h5Url").toString()));
					//item.put("score",item.getBooleanValue("isNew")?"new":"");
					item.put("src",item.remove("picUrl"));
					//item.put("desc",item.remove("newTag"));
					//item.put("title",item.remove("name"));
					item.put("click","outUrl".equals(item.getString("type"))?"list":"post");
				}
				index.add(banner);
				//番源新增
				JSONArray newUp=data.getJSONArray("newUp");
				JSONObject new_=new JSONObject();
				new_.put("title","番源新增");
				index.add(new_);
				format(newUp,index);
				//热番看看看
				JSONArray lookVideo=data.getJSONArray("lookVideo");
				new_=new JSONObject();
				new_.put("title","热番看看看");
				index.add(new_);
				format(lookVideo,index);
				//上季佳品
				JSONArray oldVideo=data.getJSONArray("oldVideo");
				new_=new JSONObject();
				new_.put("title","上季佳品");
				index.add(new_);
				format(oldVideo,index);
				//时间表
				JSONObject newUpdataVideo=data.getJSONObject("newUpdataVideo");
				JSONArray time=new JSONArray();
				for(int i=1;i<7;i++){
					JSONArray week=new JSONArray();
					time.add(week);
					format(newUpdataVideo.getJSONObject(String.valueOf(i)).getJSONArray("list"),week);
				}
				JSONArray week=new JSONArray();
				time.add(0,week);
				format(newUpdataVideo.getJSONObject(String.valueOf(7)).getJSONArray("list"),week);
				this.time=time.toJSONString();
			}
		}
		catch (IOException e)
		{}
		return index.toJSONString();
	}
	private void format(JSONArray source,JSONArray des){
		for(int i=0;i<source.size();i++){
			JSONObject item=source.getJSONObject(i);
			des.add(item);
			item.put("href",item.remove("id"));
			item.put("score",item.getBooleanValue("isNew")?"new":"");
			item.put("src",item.remove("pic"));
			item.put("desc",item.remove("newTag"));
			item.put("title",item.remove("name"));
		}
	}
	@Override
	public String getList(String url)
	{
		JSONObject list=new JSONObject();
		try
		{
			Uri uri=Uri.parse(url);
			String page=uri.getQueryParameter("page");
			list.put("page",page==null?1:page);
			JSONObject data=JSONObject.parseObject(Jsoup.connect(url+getKey(url)).ignoreContentType(true).referrer(getHost()).execute().body());
			JSONObject param=data.getJSONObject("params");
			JSONArray posts=null;
			if(param!=null){
				JSONObject video=data.getJSONObject("videoList");
				posts=video.getJSONArray("list");
				int num=video.getIntValue("num");
				int size=param.getIntValue("size");
				list.put("count",(int)Math.ceil(num/(float)size));
			}else{
				posts=data.getJSONArray("videoList");
				if(posts==null)
					posts=data.getJSONArray("list");
				list.put("count",list.get("page"));
			}
			
			JSONArray items=new JSONArray();
			list.put("item",items);
			format(posts,items);
			
		}
		catch (IOException e)
		{}

		return list.toJSONString();
	}

	@Override
	public String makeUrl(String url)
	{
		String[] data=url.split("\\?");
		return getHost()+"/apiH5.php?r=video/videocollection&"+data[1];
	}

	@Override
	public String search(String key)
	{
		return getHost()+"/apiH5.php?r=video/videosearch&searchText="+key;
	}

	@Override
	public String getPost(String url)
	{
		JSONObject post=new JSONObject();
		try
		{
			url=getHost() + "/apiH5.php?r=video/videoinfo&videoId=" + url + "&sourceId=0&userId=";
			JSONObject data=JSONObject.parseObject(Jsoup.connect(url+getKey(url)).ignoreContentType(true).referrer(getHost()).execute().body());
			data=data.getJSONObject("videoInfo");
			JSONObject videoInfo=data.getJSONObject("videoInfo");
			JSONArray videoList=data.getJSONArray("videoList");
			post.put("title",videoInfo.getString("name"));
			post.put("src",videoInfo.getString("pic"));
			post.put("profile",videoInfo.getString("content"));
			post.put("desc","年代："+videoInfo.getString("year")+"<br/>"+videoInfo.getString("act")+"<br/>"+videoInfo.getString("newTag"));
			post.put("video",videoList);
			for(int i=0;i<videoList.size();i++){
				JSONArray videos=videoList.getJSONArray(i);
				for(int n=0;n<videos.size();n++){
					JSONObject play=videos.getJSONObject(n);
					play.put("title",play.remove("name"));
					play.put("href",play.remove("id"));
				}
			}
			String baiduPanUrl=videoInfo.getString("baiduPanUrl");
			if(!TextUtils.isEmpty(baiduPanUrl)){
			JSONArray plays=new JSONArray();
			videoList.add(plays);
			JSONObject item=new JSONObject();
			plays.add(item);
			item.put("title",videoInfo.getString("baiduPanCode"));
			item.put("href",baiduPanUrl);
			}
		}
		catch (IOException e)
		{}

		return post.toJSONString();
	}

	@Override
	public Map<String, String> getVideoUrl(String video_id)
	{
		if(video_id.startsWith("http"))return null;
		Map<String,String> map=new HashMap<>();
		try
		{
			String url=getHost() + "/apiH5.php?r=video/videosource&sourceId="+video_id;
			JSONObject data=JSONObject.parseObject(Jsoup.connect(url +getKey(url)).ignoreContentType(true).referrer(getHost()).execute().body());
			data=data.getJSONObject("videoSource");
			url=data.getString("url");
			if(url.equals(data.getString("baseUrl"))){
				String query="&url="+url;
			data=JSONObject.parseObject(Jsoup.connect(getHost()+"/apiH5.php?r=video/"+data.getString("type")+"curl&url="+URLEncoder.encode(url)+getKey(query)).ignoreContentType(true).referrer(getHost()).execute().body());
			map.put(data.getString("url"),data.getString("url"));
			}else{
				map.put(data.getString("url"),data.getString("url"));
			}
		}
		catch (IOException e)
		{}
		return map;
	}

	@Override
	public String getHost()
	{
		return "http://app.pnext.top";
	}
	
}
