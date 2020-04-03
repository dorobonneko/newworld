package com.moe.x4jdm.model;
import java.util.Map;
import com.alibaba.fastjson.JSONArray;
import org.jsoup.Jsoup;
import java.io.IOException;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
import com.alibaba.fastjson.JSONObject;
import java.util.HashMap;

public class Indexdhdy extends Index
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
			for(Element e:doc.select("p.headerChannelList > a")){
				JSONObject item=new JSONObject();
				tab.add(item);
				item.put("title",e.text());
				item.put("href",makeUrl(e.absUrl("href")));
			}
			{
				JSONObject zongyi=new JSONObject();
				tab.add(0,zongyi);
				zongyi.put("title","综艺");
				zongyi.put("href",makeUrl(getHost()+"zongyi/"));
				JSONObject anime=new JSONObject();
				tab.add(0,anime);
				anime.put("title","动漫");
				anime.put("href",makeUrl(getHost()+"dongman/"));
			}
			for(Element title:doc.select(".modo_title")){
				JSONObject header_title=new JSONObject();
				index.add(header_title);
				header_title.put("title",title.selectFirst("h2").text());
				for(Element item:title.nextElementSibling().select("ul#resize_list > li")){
					JSONObject post=new JSONObject();
					index.add(post);
					post.put("title",item.selectFirst("label.name").text());
					post.put("desc",item.selectFirst("label.title").text());
					try{post.put("src",item.selectFirst("img").absUrl("xsrc"));}catch(Exception e){}
					try{post.put("href",item.selectFirst("a").absUrl("href"));}catch(Exception e){}
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
		JSONObject list=new JSONObject();
		try
		{
			Document doc=Jsoup.connect(url.replace("index-1.", "index.")).get();
			String page=doc.selectFirst("div.ui-vpages").ownText();
			String[] pages=page.substring(page.lastIndexOf(":")+1,page.length()-1).split("/");
			list.put("page",pages[0]);
			list.put("count",pages[1]);
			JSONArray item=new JSONArray();
			list.put("item",item);
			for(Element e:doc.select("ul#vod_list > li,ul#resize_list > li")){
				JSONObject post=new JSONObject();
				item.add(post);
				post.put("title",e.selectFirst("h2").text());
				try{post.put("desc",e.selectFirst("label.title").text());}catch(Exception ee){}
				try{post.put("src",e.selectFirst("img").absUrl("xsrc"));}catch(Exception ee){}
				try{post.put("href",e.selectFirst("a").absUrl("href"));}catch(Exception ee){}
				post.put("score",e.selectFirst(".status")==null?null:"连载");
				
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
			Element main=doc.selectFirst("#resize_vod");
			post.put("src",main.selectFirst("img.loading").absUrl("xsrc"));
			post.put("title",main.selectFirst("h1").text());
			post.put("desc",main.select("p").toString().replaceAll("p>","div>"));
			post.put("profile",doc.selectFirst("div.vod_content").toString());
			JSONArray video=new JSONArray();
			post.put("video",video);
			JSONArray play=new JSONArray();
			video.add(play);
			
			for(Element e:doc.select("ul.plau-ul-list > li > a")){
				JSONObject item=new JSONObject();
				play.add(item);
				item.put("title",e.text());
				item.put("href",e.absUrl("href"));
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
		Map<String,String> urls=new HashMap<>();
		String newurl=url.substring(0,url.lastIndexOf("/")+1)+"0-1.js";
		try
		{
			String data=Jsoup.connect(newurl).ignoreContentType(true).execute().body();
			data=data.substring(data.indexOf("'")+1,data.lastIndexOf("'"));
			JSONObject jo=JSONObject.parseObject(data);
			JSONArray jsondata=jo.getJSONArray("Data");
			for (int i=0;i < jsondata.size();i++) {
				JSONObject item=jsondata.getJSONObject(i);
				JSONArray plays=item.getJSONArray("playurls");
				for (int n=0;n < plays.size();n++) {
					JSONArray play=plays.getJSONArray(n);
					if(url.endsWith(play.getString(2))){
						urls.put(play.getString(1),play.getString(1));
						return urls;
					}
				}
			}
		}
		catch (IOException e)
		{}
		return urls;
	}

	@Override
	public String getHost()
	{
		return "http://www.dihudy.com/";
	}

	@Override
	public String makeUrl(String url)
	{
		return url.concat("index-%d.html");
	}

	@Override
	public String getGold()
	{
		return getHost()+"vod-type-id-3-wd--letter--year-0-area--order-hits-p-%d.html";
	}

	@Override
	public String search(String key)
	{
		return getHost()+"vod-search-wd-"+key+"-p-%d.html";
	}
	
}
