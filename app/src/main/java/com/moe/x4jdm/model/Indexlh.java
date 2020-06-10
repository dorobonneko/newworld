package com.moe.x4jdm.model;
import java.util.Map;
import com.alibaba.fastjson.JSONArray;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
import com.alibaba.fastjson.JSONObject;
import android.text.TextUtils;
import android.net.Uri;
import java.util.HashMap;

public class Indexlh extends Index
{

	@Override
	public String getIndex(int page)
	{
		JSONArray index=new JSONArray();
		try
		{
			Document doc=Jsoup.connect(getHost()).get();
			JSONArray tab=new JSONArray();
			index.add(tab);
			for(Element e:doc.select("ul.tag-textad > li > a")){
				JSONObject item=new JSONObject();
				tab.add(item);
				item.put("title",e.text());
				item.put("href",makeUrl(e.absUrl("href")));
			}
			for(Element title:doc.select(".container > .row:has(.list-name)")){
				JSONObject tit=new JSONObject();
				index.add(tit);
				tit.put("title",title.selectFirst("h2").text());
				for(Element e:title.nextElementSibling().select(".list-item")){
					JSONObject post=new JSONObject();
					index.add(post);
					post.put("title",e.selectFirst(".name").text());
					post.put("desc",e.selectFirst(".date").text());
					String url=e.selectFirst("a").attr("href");
					if(url.startsWith("/html/manhua/")){
						post.put("click","list");
						post.put("type","comic");
						url=url.replace(".html","_%d.html");
						}else{
							post.put("click","video");
						}
					post.put("viewtype","poster");
					post.put("href",getHost()+url.substring(1));
					post.put("src",e.selectFirst("img").absUrl("src"));
				}
			}
		}
		catch (IOException e)
		{}
		return index.toJSONString();
	}

	@Override
	public String makeUrl(String url)
	{
		if(url.endsWith(".html")){
			return url.replace("_1.","_%d.");
		}else if(url.endsWith("/manhua/")){
			return url+"list_1_%d.html";
		}else if(url.endsWith("/donghua/")){
			return url+"list_2_%d.html";
		}
		return super.makeUrl(url);
	}

	@Override
	public String getList(String url)
	{
		JSONObject list=new JSONObject();
		boolean manhua=!Uri.parse(url).getLastPathSegment().startsWith("list");
		try
		{
			Document doc=Jsoup.connect(manhua?url.replace("_1.html",".html"):url).get();
			JSONArray item=new JSONArray();
			list.put("item",item);
			
			Element page=doc.selectFirst(".pagination");
			if(page!=null){
				try{list.put("page",page.selectFirst("a.current").text());}catch(NullPointerException e){
					list.put("page",1);
				}
				if(!manhua){
				String href=page.select("a").last().attr("href");
				if(TextUtils.isEmpty(href))
					list.put("count",list.getIntValue("page"));
					else
					list.put("count",href.substring(href.lastIndexOf("_")+1,href.lastIndexOf(".")));
					for(Element e:doc.select(".list-item")){
						JSONObject post=new JSONObject();
						item.add(post);
						post.put("title",e.selectFirst(".name").text());
						post.put("desc",e.selectFirst(".date").text());
						href=e.selectFirst("a").attr("href");
						if(href.startsWith("/html/manhua/")){
							post.put("click","list");
							post.put("type","comic");
							href=href.replace(".html","_%d.html");
							}else{
								post.put("click","video");
							}
						post.put("viewtype","poster");
						post.put("href",getHost()+href.substring(1));
						post.put("src",e.selectFirst("img").absUrl("src"));
					}
					}else{
						String count=page.selectFirst("a.pageinfo").text();
						list.put("count",count.substring(1,count.length()-2));
						for(Element e:doc.select("#userInfo > div > img")){
							JSONObject post=new JSONObject();
							item.add(post);
							post.put("src",e.selectFirst("img").absUrl("data-original"));
							post.put("viewtype","listcomic");
						}
					}
			}else{
				/*list.put("page",1);
				list.put("count",1);*/
				throw new IOException();
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
			JSONArray video=new JSONArray();
			post.put("video",video);
			JSONArray play=new JSONArray();
			video.add(play);
			JSONObject play_item=new JSONObject();
			play.add(play_item);
			play_item.put("title", "play");
			play_item.put("href",doc.selectFirst("#userInfo").attr("data-target"));

		}
		catch (IOException e)
		{}return post.toJSONString();
	}

	@Override
	public Map<String, String> getVideoUrl(String url)
	{
		Map<String,String> urls=new HashMap<>();
		try
		{
			Document doc=Jsoup.connect(url).get();
			String video=doc.selectFirst("#userInfo").attr("data-target");
			urls.put("play",video);

		}
		catch (IOException e)
		{}return urls;
	}

	@Override
	public String getHost()
	{
		return "https://mhd299.com/";
	}
	
}
