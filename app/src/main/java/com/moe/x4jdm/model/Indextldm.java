package com.moe.x4jdm.model;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.select.Elements;
import com.alibaba.fastjson.JSONArray;
import java.util.Iterator;
import org.jsoup.nodes.Element;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Map;
import java.util.LinkedHashMap;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;
import android.net.Uri;

public class Indextldm extends Index
{
	private static final String host="http://www.tldm.net/";
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
			Document doc= Jsoup.connect(host).get();
			Elements tabs=doc.select("#naviin li");
			if(tabs!=null){
				JSONArray tab=new JSONArray();
				tabs.remove(0);
				Iterator<Element> tabs_i=tabs.iterator();
				while(tabs_i.hasNext()){
					Element e=tabs_i.next().child(0);
					String href=e.absUrl("href");
					if(!e.attr("href").startsWith("http")){
						JSONObject tab_item=new JSONObject();
						tab_item.put("href",href+"index%d.html");
						tab_item.put("title",e.text());
						tab.add(tab_item);
					}
				}
			index.add(tab);
			}
			Elements mains=doc.select(".page_content > .box720,.page_content > .home-top-new > dl.nobg");
			if(mains!=null){
				mains.remove(0);
				//JSONArray main=new JSONArray();
				//index.put("main",main);
				for(Element e:mains){
					JSONObject jo=new JSONObject();
					index.add(jo);
					try{jo.put("title",e.selectFirst("em.dhp,em.topdhp").text());}catch(Exception ee){}
					//JSONArray items=new JSONArray();
					//jo.put("item",items);
					Iterator<Element> main_i=e.select("ul > li > a,dl.fix > dd > a,dl.nobg > dd > a").iterator();
					while(main_i.hasNext()){
						Element a=main_i.next();
						JSONObject item=new JSONObject();
						item.put("href",a.absUrl("href"));
						try{
							item.put("title",a.selectFirst("span").text());
						}catch(NullPointerException ee){
							item.put("title",a.ownText());
						}
						try{item.put("desc",a.selectFirst("em").text());}catch(NullPointerException ee){
							item.put("desc",a.parent().ownText());
						}
						try{
							item.put("src",a.selectFirst("img").absUrl("src"));
							item.put("title",a.selectFirst("img").attr("alt"));
							}catch(Exception ee){}
						index.add(item);
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
		// TODO: Implement this method
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
		if(url.endsWith("index1.html"))
			url=url.substring(0,url.length()-11);
		try
		{
			int index=url.indexOf("searchword=");
			if(index!=-1)
			{
			url =url.substring(0,index+11)+ URLEncoder.encode(url.substring(index+11), "GBK");
			}
		}
		catch (UnsupportedEncodingException e)
		{}
		JSONObject list=new JSONObject();
		try
		{
			Document doc=Jsoup.connect(url).ignoreContentType(true).postDataCharset("GBK").timeout(15000).get();
			list.put("title",doc.selectFirst("#broodtitle").text());
			String f=doc.selectFirst(".pages > span > span").text();
			String[] page=f.substring(f.indexOf(":")+1,f.length()-1).split("/");
			list.put("page",Integer.parseInt(page[0]));
			
			list.put("count",Integer.parseInt(page[1]));
			Elements lis=doc.select(".movie-chrList > ul > li");
			if(lis!=null){
				JSONArray items=new JSONArray();
				Iterator<Element> lis_i=lis.iterator();
				while(lis_i.hasNext()){
					Element e=lis_i.next();
					JSONObject item=new JSONObject();
					item.put("href",e.selectFirst("a").absUrl("href"));
					item.put("src",e.selectFirst("img").absUrl("src"));
					item.put("title",e.selectFirst("img").attr("alt"));
					item.put("desc",e.selectFirst("abbr").text());
					items.add(item);
				}
				list.put("item",items);
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
			post.put("title",doc.selectFirst(".mtext h1").text());
			post.put("desc",doc.selectFirst(".mtext em").text());
			post.put("profile",doc.selectFirst("div.m-intro").toString());
			post.put("src",doc.selectFirst(".mimg > img").absUrl("src"));
			Elements play=doc.select(".playurl > .bfdz");
			if(play!=null){
				JSONArray video=new JSONArray();
				Iterator<Element> play_i=play.iterator();
					while(play_i.hasNext()){
						Elements as=play_i.next().select("li > a");
						if(as!=null){
						JSONArray plays=new JSONArray();
						Iterator<Element> as_i=as.iterator();
						while(as_i.hasNext()){
							JSONObject play_item=new JSONObject();
							Element a=as_i.next();
							play_item.put("title",a.text());
							play_item.put("href",makeUrl(a.attr("href")));
							plays.add(play_item);
						}
						
						video.add(plays);
						}
					}
				
				
				post.put("video",video);
			}
		}
		catch (IOException e)
		{}
		return post.toJSONString();
	}

	@Override
	public Map<String,String> getVideoUrl(String url)
	{
		Map<String,String> urls=new LinkedHashMap<>();
		try
		{
			String[] target=url.substring(url.lastIndexOf("?")+1).split("-");
			Document doc=Jsoup.connect(url).get();
			String href=doc.selectFirst("script[src^=/playdata]").absUrl("src");
			String data=Jsoup.connect(href).ignoreContentType(true).execute().body();
			JSONArray arr=JSONArray.parseArray(data.substring(data.indexOf("[["),data.lastIndexOf("]]")+2)).getJSONArray(Integer.parseInt(target[1]));
			JSONArray videos=arr.getJSONArray(1);
			String video_url=videos.getString(Integer.parseInt(target[2]));
			video_url=video_url.substring(video_url.indexOf("$")+1,video_url.lastIndexOf("$"));
			data=Jsoup.connect(video_url).ignoreContentType(true).execute().body();
			Matcher matcher=Pattern.compile("url:\\s'(.*?)',",Pattern.MULTILINE).matcher(data);
			if(matcher.find()){
				
				urls.put(matcher.group(1),matcher.group(1));
			}else{
				matcher=Pattern.compile("var\\shuiid\\s=\\s\\\"(.*?)\\\";",Pattern.MULTILINE).matcher(data);
				if(matcher.find()){
					urls.put(matcher.group(1),matcher.group(1));
				}
			}
		}
		catch (Exception e)
		{}
		return urls;
	}

	@Override
	public String search(String key)
	{
		return getHost() + "search.asp?page=%d&searchtype=-1&searchword=" +key;
		
	}

	@Override
	public String makeUrl(String url)
	{
		if(url.startsWith("//")){
			return "http:"+url;
		}
		if(url.startsWith("/"))
		return getHost()+url.substring(1);
		return url;
	}

	@Override
	public String getHost()
	{
		// TODO: Implement this method
		return host;
	}

	@Override
	public String getGold()
	{
		// TODO: Implement this method
		return null;
	}
	
}
