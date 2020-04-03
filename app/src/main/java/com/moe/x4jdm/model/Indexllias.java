package com.moe.x4jdm.model;
import java.util.Map;
import com.alibaba.fastjson.JSONArray;
import org.jsoup.Jsoup;
import java.io.IOException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import com.alibaba.fastjson.JSONObject;
import android.net.Uri;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Indexllias extends Index
{

	@Override
	public void clearCache()
	{
	}

	@Override
	public String getIndex(int page)
	{
		JSONArray index=new JSONArray();
		try
		{
			Document doc=Jsoup.connect(getHost()).get();
			for(Element e:doc.select("div.categary").get(3).select("p.title")){
				JSONObject main=new JSONObject();
				index.add(main);
				//JSONArray item=new JSONArray();
				//main.put("item",item);
				main.put("title",e.text());
				
				for(Element i:e.nextElementSibling().select("a")){
					JSONObject i_j=new JSONObject();
					index.add(i_j);
					i_j.put("click","list");//点击是列表
					i_j.put("title",i.text());
					i_j.put("href",i.absUrl("href")+"/%d");
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
		JSONObject list=new JSONObject();
		try
		{
			Document doc=Jsoup.connect(url).get();
			list.put("page",Uri.parse(url).getLastPathSegment());
			JSONArray items=new JSONArray();
			list.put("item",items);
			for(Element e:doc.select(".main > a.item")){
				JSONObject item=new JSONObject();
				items.add(item);
				item.put("title",e.selectFirst("p").text());
				item.put("score",e.selectFirst("span").text());
				item.put("src",e.selectFirst(".imgwrap").absUrl("bgimg"));
				item.put("href",e.absUrl("href")+";"+item.getString("src"));
				
			}
			list.put("count",list.getIntValue("page")+(items.isEmpty()?0:1));
			
		}
		catch (IOException e)
		{}
		return list.toJSONString();
	}

	@Override
	public String getPost(String url)
	{
		String[] data=url.split(";");
		JSONObject post=new JSONObject();
		post.put("src",data[1]);
		try
		{
			Document doc=Jsoup.connect(data[0]).get();
			post.put("title",doc.selectFirst("header h1").text());
			JSONArray video=new JSONArray();
			post.put("video",video);
			JSONArray play=new JSONArray();
			video.add(play);
			JSONObject play_item=new JSONObject();
			play.add(play_item);
			play_item.put("title","Play");
			Element play_v=doc.selectFirst("#vedioiframe");
			if(play_v!=null){
			String src=play_v.absUrl("src");
			int index=src.indexOf("=");
			if(index!=-1)
				src=src.substring(index+1);
			play_item.put("href",src);
			}else{
				Matcher m=Pattern.compile("playUrl\\s=\\s'(.*?)';",Pattern.MULTILINE).matcher(doc.toString());
				if(m.find())
					play_item.put("href",m.group(1));
			}
		}
		catch (IOException e)
		{}
		return post.toJSONString();
	}

	@Override
	public Map<String, String> getVideoUrl(String url)
	{
		Map<String,String> urls=new LinkedHashMap<>();
		if(url!=null)
		urls.put(url,url);
		return urls;
	}

	@Override
	public String search(String key)
	{
		return getHost()+"search/"+key+"/vedio/%d";
	}

	@Override
	public String makeUrl(String url)
	{
		return null;
	}

	@Override
	public String getHost()
	{
		return "https://llias.xyz/";
	}

	@Override
	public String getGold()
	{
		return null;
	}

}
