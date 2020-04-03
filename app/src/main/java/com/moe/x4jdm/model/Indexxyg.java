package com.moe.x4jdm.model;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import java.util.Iterator;
	import java.util.Map;
	import org.jsoup.Jsoup;
	import org.jsoup.nodes.Document;
	import org.jsoup.nodes.Element;
	import org.jsoup.select.Elements;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.net.URLDecoder;

public class Indexxyg extends Index
{


	@Override
	public String getIndex(int page)
	{
		JSONArray index=new JSONArray();
		try
		{
			Document doc=Jsoup.connect(getHost()).get();
			Elements a_tab=doc.select("ul.nav_menu > li > a");
			if(a_tab!=null){
				a_tab.remove(0);
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
			
			Elements mains=doc.select("div.box.movie_list");
			if(mains!=null){
				//JSONArray main=new JSONArray();
				//index.put("main",main);
				Iterator<Element> mains_i=mains.iterator();
				while(mains_i.hasNext()){
					Element list=mains_i.next();
					JSONObject main_item=new JSONObject();
					index.add(main_item);
					try{main_item.put("title",list.selectFirst("h2").ownText());}catch(Exception e){}
					try{main_item.put("href",makeUrl(list.selectFirst("div.title a").absUrl("href")));}catch(Exception e){}
					Elements items=list.select("div.vodlist");
					if(items!=null){
						//JSONArray item=new JSONArray();
						//main_item.put("item",item);
						Iterator<Element> items_i=items.iterator();
						while(items_i.hasNext()){
							Element post_item=items_i.next();
							JSONObject post=new JSONObject();
							index.add(post);
							post.put("title",post_item.selectFirst("h3").text());
							post.put("href",post_item.selectFirst("a").absUrl("href"));
							//post.put("desc",post_item.selectFirst("div.itemimgtext").text());
							String style=post_item.selectFirst("a").attr("style");
							post.put("src",style.substring(22,style.indexOf(")")-1));
						}
					}
				}
			}
			
		}
		catch (IOException e)
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
		if(url.endsWith("-1.html")){
			url=url.substring(0,url.length()-7).concat(".html");
		}
		JSONObject list=new JSONObject();
		try
		{
			
			Document doc=Jsoup.connect(url).get();
			String[] num=doc.selectFirst("span.num").text().split("/");
			list.put("page",Integer.parseInt(num[0]));
			list.put("count",Integer.parseInt(num[1]));
			Elements li=doc.select("div.movie_list > div.vodlist");
			JSONArray item=new JSONArray();
				list.put("item",item);
				Iterator<Element> items_i=li.iterator();
				while(items_i.hasNext()){
					Element post_item=items_i.next();
					JSONObject post=new JSONObject();
					item.add(post);
					post.put("title",post_item.selectFirst("h3").text());
					post.put("href",post_item.selectFirst("a").absUrl("href"));
					//post.put("desc",post_item.selectFirst("div.itemimgtext").text());
					String style=post_item.selectFirst("a").attr("style");
					post.put("src",style.substring(22,style.indexOf(")")-1));
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
			post.put("title",doc.select("span.cat_pos_l a").last().text());
			Matcher matcher=Pattern.compile("var\\spostimg\\s=\\s'(.*?)';",Pattern.MULTILINE).matcher(doc.toString());
			if(matcher.find())
			post.put("src",matcher.group(1));
			JSONArray video=new JSONArray();
				JSONArray plays=new JSONArray();
				video.add(plays);
				post.put("video",video);
				JSONObject play=new JSONObject();
				plays.add(play);
				play.put("title","Play");
				String video_url=doc.selectFirst(".stui-player__video > script").absUrl("src");
				String data=Jsoup.connect(video_url).ignoreContentType(true).execute().body();
				matcher=Pattern.compile("mac_url=unescape\\('(.*?)'\\);",Pattern.MULTILINE).matcher(data);
				if(matcher.find()){
					video_url=matcher.group(1);
					video_url=video_url.substring(video_url.indexOf("http"));
				play.put("href",URLDecoder.decode(video_url));
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
		urls.put(url,url);
		return urls;
	}

	@Override
	public String search(String key)
	{
		return null;
	}

	@Override
	public String makeUrl(String url)
	{
		int index=url.lastIndexOf("/");
		String page=url.substring(index);
		url=url.substring(0,index+1);
		if(!page.contains("-")){
			index=page.indexOf(".");
			page=page.substring(0,index)+"-%d"+page.substring(index);
		}
		return url+page;
	}

	@Override
	public String getHost()
	{
		return "https://xygxyg.icu/";//"https://寻欲宫.com/";
	}

	@Override
	public String getGold()
	{
		return null;
	}
	
}
