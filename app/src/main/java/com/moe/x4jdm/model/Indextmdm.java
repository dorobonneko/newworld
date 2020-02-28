package com.moe.x4jdm.model;

import android.text.TextUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.moe.x4jdm.video.VideoParse;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import android.util.Base64;
import org.jsoup.Connection;

public class Indextmdm extends Index
{
	private String time;
	@Override
	public void clearCache()
	{
		time=null;
	}

	@Override
	public String getIndex()
	{
		JSONArray index=new JSONArray();
		try
		{
			Document doc=Jsoup.connect(getHost()).get();

			Elements headers=doc.select("ul#slider > li > a");
			if(headers!=null){
				JSONArray header=new JSONArray();
				index.add(header);
				Iterator<Element> headers_i=headers.iterator();
				while(headers_i.hasNext()){
					Element header_item=headers_i.next();
					JSONObject head=new JSONObject();
					header.add(head);
					head.put("href",header_item.absUrl("href"));
					head.put("src",header_item.selectFirst("img").absUrl("src"));
					head.put("title",header_item.text());
				}
			}
			Elements a_tab=doc.select(".sort > a");
			if(a_tab!=null){
				JSONArray tabs=new JSONArray();
				index.add(tabs);
				Iterator<Element> a_tab_i=a_tab.iterator();
				while(a_tab_i.hasNext()){
					Element a=a_tab_i.next();
					JSONObject tab=new JSONObject();
					tabs.add(tab);
					tab.put("title",a.text());
					tab.put("href",makeUrl(a.absUrl("href")));
				}
			}
			Elements mains=doc.select("div.listtit");
			if(mains!=null){
				//JSONArray main=new JSONArray();
				//index.put("main",main);
				Iterator<Element> mains_i=mains.iterator();
				while(mains_i.hasNext()){
					Element list=mains_i.next();
					JSONObject main_item=new JSONObject();
					index.add(main_item);
					main_item.put("title",list.selectFirst("a.listtitle").text());
					main_item.put("href",makeUrl(list.selectFirst("a.listtitle").absUrl("href")));
					Elements items=new Elements(list.nextElementSiblings().subList(0,2)).select("li.item");
					if(items!=null){
						JSONArray item=new JSONArray();
						main_item.put("item",item);
						Iterator<Element> items_i=items.iterator();
						while(items_i.hasNext()){
							Element post_item=items_i.next();
							JSONObject post=new JSONObject();
							item.add(post);
							post.put("title",post_item.selectFirst("a.itemtext").text());
							post.put("href",post_item.selectFirst("a.itemtext").absUrl("href"));
							post.put("desc",post_item.selectFirst("div.itemimgtext").text());
							String style=post_item.selectFirst("div.imgblock").attr("style");
							post.put("src",style.substring(22,style.length()-2));
						}
					}
				}
			}
			JSONArray time=new JSONArray();
			Elements uls=doc.select("div.tlist > ul");
			if(uls!=null){
				Iterator<Element> uls_i=uls.iterator();
				while(uls_i.hasNext()){
					Elements ul=uls_i.next().select("li");
					JSONArray item=new JSONArray();
					time.add(item);
					Iterator<Element> ul_i=ul.iterator();
					while(ul_i.hasNext()){
						Element a=ul_i.next();
						JSONObject a_item=new JSONObject();
						item.add(a_item);
						a_item.put("title",a.child(1).text());
						a_item.put("href",a.child(1).absUrl("href"));
						a_item.put("desc",a.child(0).text());
					}
				}
				time.add(0,time.remove(time.size()-1));
			}
			this.time=time.toJSONString();
		}
		catch (IOException e)
		{}
		return index.toJSONString();
	}

	@Override
	public String getTime()
	{
		if(time==null)
			getIndex();
		return time;
	}

	@Override
	public String getList(String url)
	{
		JSONObject list=new JSONObject();
		try
		{
			if(url.endsWith(".html"))
				list.put("page",Integer.parseInt(url.substring(url.lastIndexOf("/")+1,url.length()-5)));
			else{
				try{list.put("page",Integer.parseInt(url.substring(url.lastIndexOf("/")+1,url.length())));}catch(Exception e){
					list.put("page",1);
				}
			}
			if(url.endsWith("/1.html"))url=url.substring(0,url.length()-6);
			Document doc=Jsoup.connect(url).get();
			Elements li=doc.select("li.item");
			if(li==null||li.isEmpty()){
				list.put("count",list.getIntValue("page"));
			}else{
				list.put("count",list.getIntValue("page")+(url.contains("?")?0:1));
				
				JSONArray item=new JSONArray();
				list.put("item",item);
				Iterator<Element> items_i=li.iterator();
				while(items_i.hasNext()){
					Element post_item=items_i.next();
					JSONObject post=new JSONObject();
					item.add(post);
					post.put("title",post_item.selectFirst("a.itemtext").text());
					post.put("href",post_item.selectFirst("a.itemtext").absUrl("href"));
					post.put("desc",post_item.selectFirst("div.itemimgtext").text());
					String style=post_item.selectFirst("div.imgblock").attr("style");
					String src=style.substring(22,style.length()-2);
					if(!src.startsWith("https://img.tmdm.tv/"))
						src=src.replace("/pic_200.jpg","");
					
					post.put("src",src);
				}
			}
		}
		catch (IOException e)
		{}
		return list.toJSONString();
	}

	@Override
	public String getPost(String url)
	{
		JSONObject post=new JSONObject();
		try
		{
			Document doc=Jsoup.connect(url).get();
			post.put("title",doc.selectFirst("div.show > h1").text());
			post.put("src",doc.selectFirst("div.show > img").absUrl("src"));
			Elements p=doc.select("div.show > p");
			p.remove(p.size()-1);
			post.put("desc",p.toString());
			post.put("profile",doc.selectFirst("div.info").text());
			Elements play=doc.select("div#playlists > ul > li > a");
			if(play!=null&&!play.isEmpty()){
				JSONArray video=new JSONArray();
				JSONArray plays=new JSONArray();
				video.add(plays);
				post.put("video",video);
				Iterator<Element> play_i=play.iterator();
				while(play_i.hasNext()){
					Element play_item=play_i.next();
					JSONObject item=new JSONObject();
					plays.add(item);
					item.put("title",play_item.text());
					item.put("href",play_item.absUrl("href"));
				}
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
		try
		{
			Document doc=Jsoup.connect(url).get();
			Element a=doc.selectFirst("div#playbox");
			if(a!=null){
				String vid=a.attr("data-vid");
				doc=Jsoup.connect("https://player.tmdm.tv/disp.php?vid="+vid).get();
				Element script=doc.selectFirst("body > :last-child");
				Matcher m=Pattern.compile("url:\\s\\\"(.*?)\\\",",Pattern.MULTILINE).matcher(script.toString());
				if(m.find()){
					urls.put(m.group(1),VideoParse.parseQQ(m.group(1)));
				}
			}
			
		}
		catch (IOException e)
		{}
		return urls;
	}

	@Override
	public String search(String key)
	{
		return getHost().concat("search/").concat(key).concat("/%d");
	}
	
	@Override
	public String makeUrl(String url)
	{
		return url+"%d.html";
	}

	@Override
	public String getHost()
	{
		// TODO: Implement this method
		return "http://m.tmdm.tv/";
	}

	@Override
	public String getGold()
	{
		// TODO: Implement this method
		return getHost()+"new?page=%d";
	}
}
