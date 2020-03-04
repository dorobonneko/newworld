package com.moe.x4jdm.model;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import java.io.IOException;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
import com.alibaba.fastjson.JSONArray;
import java.util.Iterator;
import java.util.Map;
import java.util.LinkedHashMap;
import android.util.Base64;
import org.jsoup.Connection;
import java.net.URLDecoder;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import android.net.Uri;

class Indexx4jdm extends Index
{
    private static Indexx4jdm mIndexModel;
	private String time;
	private String index;
	public static final String Host="https://m.x4jdm.tv/";
	public final static String GOLD="https://m.x4jdm.tv/list/ribendongman_____gold_%d.html";
    private Indexx4jdm()
	{}
    public static Indexx4jdm getInstance()
	{
        if (mIndexModel == null)
            synchronized (Indexx4jdm.class)
			{
                if (mIndexModel == null)
                    mIndexModel = new Indexx4jdm();
            }
        return mIndexModel;
    }
	public void refresh()
	{	try
		{
			Document doc=Jsoup.connect(getHost()).get();
			Element header=doc.selectFirst(".focusList");
			JSONArray index=new JSONArray();
			if (header != null)
			{
				Elements items=header.children();
				items.remove(items.size() - 1);
				if (items.size() > 0)
				{
					JSONArray ja=new JSONArray();
					for (int i=0;i < items.size();i++)
					{
						Element item=items.get(i);
						JSONObject jo=new JSONObject();
						Element a=item.child(0);
						jo.put("href", a.attr("abs:href"));
						jo.put("src", a.selectFirst("img").attr("abs:data-src"));
						jo.put("title", a.selectFirst(".sTxt").text());
						ja.add(jo);
					}
					index.add( ja);
				}
			}
			Element tab=doc.selectFirst(".headerChannelList");
			if (tab != null)
			{
				Elements items=tab.children();
				if (items.size() > 0)
				{
					items.remove(items.size() - 1);
					items.remove(items.size() - 1);
					items.remove(0);

					JSONArray ja=new JSONArray();
					for (int i=0;i < items.size();i++)
					{
						Element item=items.get(i);
						JSONObject jo=new JSONObject();
						jo.put("href",getHref(item.attr("abs:href")));
						jo.put("title", item.text());
						ja.add(jo);
					}
					index.add(ja);
				}
			}
			Elements mains=doc.select(".modo_title");
			if (mains != null && mains.size() > 0)
			{
				//JSONArray ja=new JSONArray();
				Iterator<Element> iterator=mains.iterator();
				while (iterator.hasNext())
				{
					Element main=iterator.next();
					JSONObject main_item=new JSONObject();
					Element a=main.selectFirst("a");
					main_item.put("title", a.attr("title"));
					main_item.put("href", getHref(a.attr("abs:href")));
					index.add(main_item);
					//JSONArray items=new JSONArray();
					Elements childs=main.nextElementSibling().selectFirst("ul").children();
					Iterator<Element> child_iterator=childs.iterator();
					while (child_iterator.hasNext())
					{
						Element child=child_iterator.next().child(0);
						JSONObject item=new JSONObject();
						item.put("href", child.attr("abs:href"));
						item.put("title", child.attr("title"));
						item.put("desc", child.selectFirst(".title").text());
						item.put("src", child.selectFirst("img").attr("abs:data-original"));
						index.add(item);
					}
					//main_item.put("item", items);
					
				}

				//index.put("main", ja);
			}
			Indexx4jdm.this.index = index.toJSONString();
			//主体解析完毕，解析时间表
			JSONArray time=new JSONArray();
			Elements list=doc.select(".list-txt");
			Iterator<Element> ul_iterator=list.iterator();
			while (ul_iterator.hasNext())
			{
				Element ul=ul_iterator.next();
				JSONArray time_tab=new JSONArray();
				Elements lis=ul.children();
				Iterator<Element> li_iterator=lis.iterator();
				while (li_iterator.hasNext())
				{
					Element li=li_iterator.next();
					JSONObject item=new JSONObject();
					item.put("title", li.child(0).attr("title"));
					item.put("href", li.child(0).attr("abs:href"));
					item.put("desc", li.child(1).text());
					time_tab.add(item);
				}
				switch (ul.attr("id"))
				{
					case "con_zy_7":
						time.add(0, time_tab);
						break;
					default:
						time.add(time_tab);
						break;
				}

			}
			Indexx4jdm.this.time = time.toJSONString();


		}
		catch (IOException e)
		{}
	}

	@Override
	public void clearCache()
	{
		index=null;
	}
	public String getTime()
	{
		if(time==null)
			refresh();
		return time;
	}

	@Override
	public boolean hasTime()
	{
		return true;
	}

	
	public String getIndex()
	{
		if(index==null)
			refresh();
		return index;
	}
	public String search(String key)
	{
		return getHost()+"search/".concat(key)+"-%d.html";
		
	}
	/*public String getGold(int page){
	 return getList(String.format("https://m.x4jdm.tv/list/ribendongman_____gold_%d.html",page));
	 }*/
	public String getList(String href)
	{
		JSONObject gold=new JSONObject();
		
	
		try
		{
			Document doc=Jsoup.connect(href).get();
			gold.put("title", doc.title());
			try{
			String[] num=doc.selectFirst(".num").text().split("/");
				gold.put("page", Integer.parseInt(num[0]));
				gold.put("count", Integer.parseInt(num[1]));
				
			}catch(Exception e){
				
				gold.put("page",Integer.parseInt(href.substring(href.lastIndexOf("-")+1,href.length()-5)));
				gold.put("count", gold.getIntValue("page")+1);
				
			}
			try{
			Elements lis=doc.select("#vod_list > li");
			if(lis==null||lis.isEmpty())
				lis=doc.select("ul.new_tab_img > li");
			JSONArray items=new JSONArray();
			Iterator<Element> iterator=lis.iterator();
			while (iterator.hasNext())
			{
				JSONObject item=new JSONObject();
				Element li=iterator.next();
				Element a=li.child(0);
				item.put("title", a.attr("title"));
				item.put("href", a.attr("abs:href"));
				item.put("src", li.selectFirst("div > img").attr("abs:data-original"));
				try{item.put("desc", li.selectFirst(".title").text());}catch(NullPointerException n){}
				try{item.put("score", li.selectFirst(".score").text());}catch(NullPointerException n){}
				//item.put("staff", li.selectFirst("p").text());
				items.add(item);
			}

			gold.put("item", items);
			}catch(Exception e){
				gold.put("count", gold.getIntValue("page"));
			}
		}
		catch (Exception e)
		{}
		return gold.toJSONString();

	}
	public String getHref(String href){
		if(href.endsWith("/"))
			return href.concat("index-%d.html");
			else
			return href.concat("/index-%d.html");
	}
	public String getPost(String url){
		JSONObject jo=new JSONObject();
		try
		{
			Document doc=Jsoup.connect(url).get();
			Element body=doc.selectFirst(".vod-body");
			jo.put("src",body.selectFirst(".loading").attr("abs:data-original"));
			jo.put("title",body.selectFirst(".loading").attr("alt"));
			/*jo.put("staff");
			jo.put("type");
			jo.put("director");
			jo.put("area");*/
			Elements vod=doc.select(".vod-n-l > p");
			jo.put("desc",vod.toString());
			jo.put("profile",doc.selectFirst(".vod_content").text());
			JSONArray video=new JSONArray();
			Elements plau=doc.select(".plau-ul-list");
			if(plau!=null){
				Iterator<Element> uls=plau.iterator();
				while(uls.hasNext()){
					Element ul=uls.next();
					JSONArray plays=new JSONArray();
					Iterator<Element> iterator=ul.children().iterator();
					while(iterator.hasNext()){
						
					Element a=iterator.next().child(0);
					JSONObject item=new JSONObject();
					item.put("title",a.attr("title"));
					item.put("href",a.attr("abs:href"));
					plays.add(item);
					}
					video.add(plays);
					
				}
			}
			jo.put("video",video);
		}
		catch (IOException e)
		{}
		return jo.toJSONString();
	}
	public Map<String,String> getVideoUrl(String href){
		Map<String,String> urls=new LinkedHashMap<>();
		try
		{
			Document doc=Jsoup.connect(href).get();
			Element iframe=doc.selectFirst("iframe");
			if(iframe==null){
				return urls;
			}else{
			String url=iframe.attr("abs:src");
			int index=url.indexOf("vid=");
			if(index!=-1)
			{
				url=url.substring(index+4);
				index=url.lastIndexOf("~");
				if(url.startsWith("http")){
				if(index!=-1)
				url=url.substring(0,index);
				if(url.startsWith("https://m3u8.1yy0.com/")){
					Pattern p=Pattern.compile("var\\smain\\s=\\s\\\"(.*?)\\\";",Pattern.MULTILINE);
					Matcher matcher=p.matcher(Jsoup.connect(url).execute().body());
					if(matcher.find()){
						urls.put("https://m3u8.1yy0.com"+matcher.group(1),"https://m3u8.1yy0.com"+matcher.group(1));
					}
				}else{
					urls.put(url,url);
				}
				}else{
					//用解析引擎
					String type=url.substring(index+1);
					String vid=url.substring(0,index);
					String body=String.format("url=%s&referer=&ref=0&time=%d&type=%s&other=a2traw==&ref=0&ios=",vid,System.currentTimeMillis()/1000,type);
					JSONObject data=JSONObject.parseObject(Jsoup.connect("https://pp.vvvvdy.com/pp/api.php").method(Connection.Method.POST).requestBody(body).ignoreContentType(true).execute().body());
					if(data.getIntValue("code")==200){
						String video_url=new String(Base64.decode(data.getString("url"),Base64.DEFAULT));
						video_url=URLDecoder.decode(video_url.substring(video_url.indexOf("http")));
						urls.put(video_url,video_url);
					}
				}
			}
			}
		}
		catch (Exception e)
		{}
		return urls;
	}

	@Override
	public String makeUrl(String url)
	{
		if(url.startsWith("//"))
			return "https:"+url;
		if(url.startsWith("/"))
			return Host+url.substring(1);
		return Host+url;
	}

	@Override
	public String getHost()
	{
		return Host;
	}

	@Override
	public String getGold()
	{
		return GOLD;
	}



	 
}
