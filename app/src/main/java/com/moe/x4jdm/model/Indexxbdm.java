package com.moe.x4jdm.model;
import android.net.Uri;
import android.util.Base64;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.moe.x4jdm.util.EscapeUnescape;
import com.moe.x4jdm.video.VideoParse;

public class Indexxbdm extends Index
{

	@Override
	public String getIndex()
	{
		JSONArray index=new JSONArray();
		try
		{
			Document doc=Jsoup.connect(getHost()).get();
			
			Elements headers=doc.select(".carousel-inner > .item > a");
			if(!headers.isEmpty()){
				JSONArray header=new JSONArray();
				index.add(header);
				Iterator<Element> headers_i=headers.iterator();
				while(headers_i.hasNext()){
					Element header_item=headers_i.next();
					JSONObject head=new JSONObject();
					header.add(head);
					head.put("href",header_item.absUrl("href"));
					head.put("src",header_item.selectFirst("img").absUrl("src"));
					//head.put("title",header_item.text());
				}
			}
			Elements a_tab=doc.select(".container .myui-panel-box > ul > li > a");
			if(!a_tab.isEmpty()){
				JSONArray tabs=new JSONArray();
				index.add(tabs);
				Iterator<Element> a_tab_i=a_tab.iterator();
				while(a_tab_i.hasNext()){
					Element a=a_tab_i.next();
					JSONObject tab=new JSONObject();
					tabs.add(tab);
					tab.put("title",a.ownText());
					tab.put("href",makeUrl(a.absUrl("href")));
				}
				tabs.remove(tabs.size()-1);
			}
			Elements mains=doc.select(".myui-panel-box:has(h3.title)");
			if(!mains.isEmpty()){
				//JSONArray main=new JSONArray();
				//index.put("main",main);
				Iterator<Element> mains_i=mains.iterator();
				while(mains_i.hasNext()){
					Element list=mains_i.next();
					JSONObject main_item=new JSONObject();
					index.add(main_item);
					main_item.put("title",list.selectFirst("h3.title").text());
					try{main_item.put("href",makeUrl(list.selectFirst("a.more").absUrl("href")));}catch(Exception e){}
					Elements items=list.select("ul.myui-vodlist > li");
					if(items!=null){
						//JSONArray item=new JSONArray();
						//main_item.put("item",item);
						Iterator<Element> items_i=items.iterator();
						while(items_i.hasNext()){
							Element post_item=items_i.next();
							JSONObject post=new JSONObject();
							index.add(post);
							post.put("title",post_item.selectFirst("a").attr("title"));
							post.put("src",post_item.selectFirst("a").absUrl("data-original"));
							try{post.put("desc",post_item.selectFirst("span.pic-text").text());}catch(NullPointerException e){}
							post.put("href",post_item.selectFirst("a").absUrl("href"));
							try{post.put("score",post_item.selectFirst("span.pic-tag-top").text());}catch(NullPointerException e){}
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
		JSONArray time=new JSONArray();
		try
		{
			Document doc=Jsoup.connect(getHost() + "week.html").get();
			Elements uls=doc.select("div.tab-pane > ul");
			if (uls != null)
			{
				Iterator<Element> uls_i=uls.iterator();
				while (uls_i.hasNext())
				{
					Elements ul=uls_i.next().select("li");
					JSONArray item=new JSONArray();
					time.add(item);
					Iterator<Element> ul_i=ul.iterator();
					while (ul_i.hasNext())
					{
						Element post_item=ul_i.next();
						JSONObject post=new JSONObject();
						item.add(post);
						post.put("title",post_item.selectFirst("a").attr("title"));
						post.put("src",post_item.selectFirst("a").absUrl("data-original"));
						try{post.put("desc",post_item.selectFirst("span.pic-text").text());}catch(NullPointerException e){}
						post.put("href",post_item.selectFirst("a").absUrl("href"));
						try{post.put("score",post_item.selectFirst("span.pic-tag-top").text());}catch(NullPointerException e){}
						
					}
				}
				time.add(0, time.remove(time.size() - 1));
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
		try
		{
			Document doc=Jsoup.connect(url.replace("_1.",".")).get();
			Elements li=doc.select("ul.myui-page > li > a");
			list.put("page",doc.selectFirst("ul.myui-page > li > a.btn-warm").text());
			String page=li.last().attr("href");
			if(page.startsWith("?"))
				list.put("count",Uri.parse(page).getQueryParameter("page"));
			else
			list.put("count",page.substring(page.lastIndexOf("_")+1,page.length()-5));
				JSONArray item=new JSONArray();
				list.put("item",item);
				Elements items=doc.select("ul.myui-vodlist > li,ul.myui-vodlist__media > li");
				if(items!=null){
					Iterator<Element> items_i=items.iterator();
					while(items_i.hasNext()){
						Element post_item=items_i.next();
						JSONObject post=new JSONObject();
						item.add(post);
						post.put("title",post_item.selectFirst("a").attr("title"));
						post.put("src",post_item.selectFirst("a").absUrl("data-original"));
						try{post.put("desc",post_item.selectFirst("span.pic-text").text());}catch(NullPointerException e){}
						post.put("href",post_item.selectFirst("a").absUrl("href"));
						try{post.put("score",post_item.selectFirst("span.pic-tag-top").text());}catch(NullPointerException e){}
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
			post.put("title",doc.selectFirst("div.myui-content__thumb > a").attr("title"));
			post.put("src",doc.selectFirst("div.myui-content__thumb > a > img").absUrl("data-original"));
			Elements p=doc.select("div.myui-content__detail > p.data:not(.hidden-xs)");
			post.put("desc",p.toString());
			post.put("profile",doc.selectFirst("span.sketch + span.data").text());
				JSONArray video=new JSONArray();
			post.put("video",video);
			for(Element e:doc.select("div.tab-pane,div.downlist")){
				JSONArray plays=new JSONArray();
				video.add(plays);
				for(Element play:e.select("ul > li > a")){
					JSONObject item=new JSONObject();
					plays.add(item);
					item.put("title",play.text());
					item.put("href",play.absUrl("href"));
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
		if(!url.startsWith(getHost()))return null;
		Map<String,String> urls=new LinkedHashMap<>();
		try
		{
			String[] target=url.substring(url.indexOf("?")+1,url.length()-5).split("-");
			Document doc=Jsoup.connect(url).get();
			Element script=doc.selectFirst("div.embed-responsive > script");
			if(script==null)
				return null;
			Matcher m=Pattern.compile("VideoUrl=unescape\\(\"(.*?)\"\\)",Pattern.MULTILINE).matcher(script.toString());
			if(m.find()){
				String videourl=EscapeUnescape.unescape(m.group(1));
				if(videourl!=null){
					String[] b=videourl.split("\\$");
					doc=Jsoup.connect("https://tsdlrh.manhuafenxiao.com/GV/?u="+b[3]+"&h=&id="+target[0]).get();
					Element last=doc.selectFirst("body > :last-child");
					if(last.tagName().equalsIgnoreCase("script")){
					m=Pattern.compile("url:'(.*?)',",Pattern.MULTILINE).matcher(doc.select("script").last().toString());
					if(m.find()){
						videourl=m.group(1);
						}}else if(last.tagName().equalsIgnoreCase("iframe")){
							videourl=last.absUrl("src");
							int index=videourl.indexOf("=");
							if(index!=-1)
								videourl=videourl.substring(index+1);
						}
					if(videourl!=null)
						urls.put(videourl,VideoParse.parseQQ(videourl));
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
		return getHost()+"search.php?page=%d&searchword="+key+"&searchtype=";
	}

	@Override
	public String makeUrl(String url)
	{
		return url.replace(".html","_%d.html");
	}

	@Override
	public String getHost()
	{
		// TODO: Implement this method
		return "https://dm.xbdm.net/";
	}

	@Override
	public String getGold()
	{
		// TODO: Implement this method
		return getHost()+"search.php?page=%d&searchtype=5&order=hit&tid=0&area=&year=&letter=&yuyan=&state=&money=&ver=&jq=";
	}
	
}
