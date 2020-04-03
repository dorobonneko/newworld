package com.moe.x4jdm.model;

import android.net.Uri;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.moe.x4jdm.util.EscapeUnescape;
import com.moe.x4jdm.video.VideoParse;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import android.text.TextUtils;
import java.net.URLDecoder;

public class IndexNieta extends Index
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

			Elements headers=doc.select("ul.carousel > li > a");
			if(headers!=null){
				JSONArray header=new JSONArray();
				index.add(header);
				Iterator<Element> headers_i=headers.iterator();
				while(headers_i.hasNext()){
					Element header_item=headers_i.next();
					JSONObject head=new JSONObject();
					header.add(head);
					head.put("href",header_item.absUrl("href"));
					String src=header_item.selectFirst("div").attr("style");
					head.put("src",src.substring(src.indexOf("(")+1,src.indexOf(")")));
					String title=header_item.attr("title");
					int i=title.lastIndexOf(" ");
					if(i!=-1)
						try{head.put("desc",title.substring(i+1));}catch(NullPointerException e){}
					head.put("title",title.substring(0,i));
					
				}
			}
			/*Elements a_tab=doc.select(".container .myui-panel-box > ul > li > a");
			if(a_tab!=null){
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
			}*/
			Elements mains=doc.select("div.slader-r,div.column-lft:not([id])");
			if(mains!=null){
				//JSONArray main=new JSONArray();
				//index.put("main",main);
				Iterator<Element> mains_i=mains.iterator();
				while(mains_i.hasNext()){
					Element list=mains_i.next();
					JSONObject main_item=new JSONObject();
					index.add(main_item);
					try{main_item.put("title",list.selectFirst("h3.sort_nav_tit > span").text());}catch(Exception e){}
					try{main_item.put("href",makeUrl(list.selectFirst("a.aMore").absUrl("href")));}catch(Exception e){}
					Elements items=list.select("ul.sort_lst > li,div.groom-module.home-card");
					if(items!=null){
						//JSONArray item=new JSONArray();
						//main_item.put("item",item);
						Iterator<Element> items_i=items.iterator();
						while(items_i.hasNext()){
							Element post_item=items_i.next();
							JSONObject post=new JSONObject();
							index.add(post);
							String title=post_item.selectFirst("a").attr("title");
							int i=title.lastIndexOf(" ");
							if(i!=-1)
							try{post.put("desc",title.substring(i+1));}catch(NullPointerException e){}
							post.put("title",title.substring(0,i));
							String src=post_item.selectFirst("img").absUrl("data-echo");
							if(TextUtils.isEmpty(src))
								src=post_item.selectFirst("img").absUrl("src");
							post.put("src",src);
							post.put("href",post_item.selectFirst("a").absUrl("href"));
							//try{post.put("score",post_item.selectFirst("div.play").text());}catch(NullPointerException e){}
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
	public boolean hasTime()
	{
		return true;
	}


	@Override
	public String getTime()
	{
		JSONArray time=new JSONArray();
		try
		{
			Document doc=Jsoup.connect(getHost() + "label/week.html").get();
			Elements uls=doc.select("ul.tab-content");
			if (uls != null)
			{
				uls.remove(0);
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
						post.put("src",post_item.selectFirst("img").absUrl("src"));
						try{post.put("desc",post_item.selectFirst("p.num").text());}catch(NullPointerException e){}
						post.put("href",post_item.selectFirst("a").absUrl("href"));
						//try{post.put("score",post_item.selectFirst("span.pic-tag-top").text());}catch(NullPointerException e){}

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
	public String getList(String url)
	{
		JSONObject list=new JSONObject();
		try
		{
			Document doc=Jsoup.connect(url).get();
			Element li=doc.selectFirst("div.pagebox > a");
			if(li!=null){
				Matcher m=Pattern.compile(":([0-9]*?)/([0-9]*?)页").matcher(li.ownText());
				if(m.find()){
				list.put("page",m.group(1));
				list.put("count",m.group(2));
				}}
			JSONArray item=new JSONArray();
			list.put("item",item);
			Elements items=doc.select("div.listbox > ul > li,div.searchbox > ul > li");
			if(items!=null){
				Iterator<Element> items_i=items.iterator();
				while(items_i.hasNext()){
					Element post_item=items_i.next();
					JSONObject post=new JSONObject();
					item.add(post);
					post.put("title",post_item.selectFirst("a").attr("title"));
					post.put("src",post_item.selectFirst("img").absUrl("data-echo"));
					try{post.put("desc",post_item.selectFirst("span.listbox-remarks,span.so-imgTag_rb").text());}catch(NullPointerException e){
						//post.put("desc",post_item.selectFirst("span,so-imgTag_rb").text());
					}
					post.put("href",post_item.selectFirst("a").absUrl("href"));
					try{post.put("score",post_item.selectFirst("span.score").text());}catch(NullPointerException e){}
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
			post.put("title",doc.selectFirst("div.drama-tit > h3").text());
			post.put("src",doc.selectFirst("div.thumb > img").absUrl("src"));
			Elements p=doc.select("div.drama-data > div");
			p=new Elements(p.subList(0,3));
			post.put("desc",p.toString());
			post.put("profile",doc.select("label.intro").text());
			JSONArray video=new JSONArray();
			post.put("video",video);
			for(Element e:doc.select("ul.rec-list")){
				JSONArray plays=new JSONArray();
				video.add(plays);
				for(Element play:e.select("li > a")){
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
			Document doc=Jsoup.connect(url).get();
			Element script=doc.selectFirst("div#player > script");
			if(script==null)return null;
			Matcher m=Pattern.compile("player_data=\\{(.*?)\\}",Pattern.MULTILINE).matcher(script.toString());
			if(m.find()){
				JSONObject jo=JSONObject.parseObject("{"+m.group(1)+"}");
				String link=URLDecoder.decode(jo.getString("url"));
				if(link!=null)
					urls.put(link,VideoParse.parseQQ(link));
			}
		}
		catch (IOException e)
		{}
		return urls;
	}

	@Override
	public String search(String key)
	{
		return getHost()+"vodsearch/wd/"+key+"/page/%d.html";
	}

	@Override
	public String makeUrl(String url)
	{
		return url.replace(".html","/page/%d.html");
	}

	@Override
	public String getHost()
	{
		// TODO: Implement this method
		return "http://www.nieta.co/";
	}

	@Override
	public String getGold()
	{
		// TODO: Implement this method
		return null;
	}
}
