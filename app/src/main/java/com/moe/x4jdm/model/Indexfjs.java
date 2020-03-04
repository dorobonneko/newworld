package com.moe.x4jdm.model;
import org.jsoup.Jsoup;
import java.io.IOException;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.alibaba.fastjson.JSONArray;
import java.util.Iterator;
import android.net.Uri;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.jsoup.Connection;
import android.content.UriMatcher;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;
import com.moe.x4jdm.video.VideoParse;

public class Indexfjs extends Index
{

	@Override
	public boolean hasTime()
	{
		return true;
	}

	private static final String host="http://m.feijisu7.com/";/*"http://xunjuba.com/";*/
	private static final String PLAY_MATCH="^playarr(|_1|_2)\\[([0-9]*)\\]=\"(.*),.*?,.*?\"$";
	private static final Pattern PLAY=Pattern.compile(PLAY_MATCH);
	
	@Override
	public void clearCache()
	{
	}

	@Override
	public String getIndex()
	{
		JSONArray index=new JSONArray();
		try
		{
			Document doc=null;/*Jsoup.connect(host + "acg/").get();
			 Elements tabs=doc.select(".subNavheader p > a");
			 if(tabs!=null){
			 tabs.remove(0);
			 tabs.remove(0);
			 JSONArray tab=new JSONArray();
			 Iterator<Element> a_iterator=tabs.iterator();
			 while(a_iterator.hasNext()){
			 JSONObject tab_item=new JSONObject();
			 Element a=a_iterator.next();
			 tab_item.put("href",a.absUrl("href"));
			 tab_item.put("title",a.text());
			 tab.add(tab_item);
			 }
			 index.put("tab",tab);
			 }*/
			doc = Jsoup.connect("http://gda.mtyee.com/getsortdata.php?action=acg&page=1&year=0&class=0&area=all&id=").get();
			doc.setBaseUri(host);
			Elements main=doc.select(".mod_a");
			if (main != null)
			{
				//JSONArray mains=new JSONArray();
				Iterator<Element> main_iterator=main.iterator();
				while (main_iterator.hasNext())
				{
					Element main_item=main_iterator.next();
					JSONObject main_json=new JSONObject();
					main_json.put("title", main_item.selectFirst(".sMark").text());
					Element more=main_item.selectFirst(".aMore");
					if (more != null)
						main_json.put("href", href(more.absUrl("href")));
					index.add(main_json);
					Elements li=main_item.select("ul > ul > li > a");
					if (li != null)
					{
						//JSONArray items=new JSONArray();
						Iterator<Element> li_iterator=li.iterator();
						while (li_iterator.hasNext())
						{
							Element a=li_iterator.next();
							JSONObject item=new JSONObject();
							item.put("title", a.selectFirst(".sTit").text());
							item.put("desc", a.selectFirst(".sDes").text());
							item.put("src", a.selectFirst("img").absUrl("src"));
							item.put("href", a.absUrl("href"));
							index.add(item);
						}
						//main_json.put("item", items);
					}

					
				}
				//index.put("main", mains);
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
			Document doc=Jsoup.connect(host).get();
			Elements list=doc.select(".list-txt");
			if(list==null||list.isEmpty())
				list=doc.select(".am-list");
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
					case "con_dm_7":
						time.add(0, time_tab);
						break;
					default:
						time.add(time_tab);
						break;
				}

			}
		}
		catch (IOException e)
		{}
		return time.toJSONString();
	}

	@Override
	public String getList(String url)
	{
		JSONObject posts=new JSONObject();
		if(url.startsWith("http://v.mtyee.com/m/sssv.php")){
			posts.put("page",1);
			posts.put("count",1);
			try
			{
				JSONArray data=JSONArray.parseArray(Jsoup.connect(url).header("Origin", getHost()).ignoreContentType(true).execute().body());
				posts.put("item",data);
				for (int i=0;i < data.size();i++) {
					JSONObject data_item=data.getJSONObject(i);
					data_item.put("href",data_item.remove("url"));
					data_item.put("src",data_item.remove("thumb"));
					data_item.put("score",data_item.remove("time"));
				}
			}
			catch (IOException e)
			{}
			return posts.toJSONString();
		}
		try
		{
			Document doc=Jsoup.connect(url).header("Origin",getHost()).get();
			doc.setBaseUri(host);
			Elements main=doc.select(".mod_a");
			posts.put("page", Integer.parseInt(Uri.parse(url).getQueryParameter("page")));
			if (main != null)
			{
				posts.put("count", posts.getIntValue("page") + 1);
				JSONArray mains=new JSONArray();
				Iterator<Element> main_iterator=main.iterator();
				while (main_iterator.hasNext())
				{
					Element main_item=main_iterator.next();
					Elements li=main_item.select("ul > li > a");
					if (li != null)
					{
						Iterator<Element> li_iterator=li.iterator();
						while (li_iterator.hasNext())
						{
							Element a=li_iterator.next();
							JSONObject item=new JSONObject();
							item.put("title", a.selectFirst(".sTit").text());
							item.put("desc", a.selectFirst(".sDes").text());
							item.put("src", a.selectFirst("img").absUrl("src"));
							item.put("href", a.absUrl("href"));
							mains.add(item);
						}
					}
					else
					{
						//数据加载完毕
						posts.put("count", posts.getIntValue("page") - 1);
					}


				}
				posts.put("item", mains);
			}
		}
		catch (IOException e)
		{}
		return posts.toJSONString();
	}

	@Override
	public String getPost(String url)
	{
		JSONObject post=new JSONObject();
		try
		{
			Document doc=Jsoup.connect(url).get();
			Element poster=doc.selectFirst("#posterPica > img");
			if(poster!=null){
			post.put("title", poster.attr("alt"));
			Elements desc=doc.select(".pBottom > .sDes");
			if (desc != null)
			{
				for (int i=0;i < desc.size();i++)
				{
					desc.get(i).tagName("p");
					Iterator<Element> iterator=desc.get(i).children().iterator();
					while(iterator.hasNext()){
						Element e=iterator.next();
						if(e.tagName().equalsIgnoreCase("a"))
							e.after("&nbsp;&nbsp;");
					}
				}
				post.put("desc", desc.toString());
			}
			post.put("src", poster.absUrl("src"));
			post.put("profile", doc.selectFirst(".pSummary").text());
			Elements posts=doc.select(".dramaNumList > li");
			if(post!=null){
				JSONArray video=new JSONArray();
				JSONArray plays=new JSONArray();
				video.add(plays);
				
			Iterator<Element> lis=posts.iterator();
			while(lis.hasNext()){
				Element li=lis.next().child(0);
				JSONObject play=new JSONObject();
				play.put("title",li.text());
				play.put("href",li.absUrl("href"));
				
				plays.add(play);
			}
			post.put("video",video);
			}
			}else{
				//换解析算法
				Element content= doc.selectFirst(".content");
				Element pic=content.selectFirst(".pic > img");
				post.put("src",pic.absUrl("src"));
				post.put("title",pic.attr("alt"));
				Elements desc=content.select(".info > dl > :not(dt)");
				Iterator<Element> desc_i=desc.iterator();
				while(desc_i.hasNext()){
					Element e=desc_i.next();
					if(e.tagName().equalsIgnoreCase("dd"))
						e.tagName("p");
					Iterator<Element> iterator=e.children().iterator();
					while(iterator.hasNext()){
						e=iterator.next();
						if(e.tagName().equalsIgnoreCase("a"))
							e.after("&nbsp;&nbsp;");
					}
				}
				post.put("desc",desc.toString());
				post.put("profile",content.selectFirst(".des2").text());
				Elements posts=doc.select(".urlli > div > ul > li");
				if(post!=null){
					JSONArray video=new JSONArray();
					JSONArray plays=new JSONArray();
					video.add(plays);

					Iterator<Element> lis=posts.iterator();
					while(lis.hasNext()){
						Element li=lis.next().child(0);
						JSONObject play=new JSONObject();
						play.put("title",li.text());
						play.put("href",li.absUrl("href"));

						plays.add(play);
					}
					post.put("video",video);
					}
			}
		}
		catch (IOException e)
		{}
		return post.toJSONString();
	}

	@Override
	public Map<String,String> getVideoUrl(String url)
	{
		Map<String,String> urls_map=new LinkedHashMap<>();
		try
		{
			List<String> path=Uri.parse(url).getPathSegments();
			String[] url_=new String[]{"http://t.mtyee.com/ps/s"+path.get(1)+".js","http://t.mtyee.com/ty/zd/s"+path.get(1)+".js","http://t.mtyee.com/ty/yj/s"+path.get(1)+".js","http://t.mtyee.com/ne2/s"+path.get(1)+".js"};
			int index=Integer.parseInt(path.get(2).substring(0,path.get(2).indexOf(".")));
			for(String url_s:url_){
				try{
				String url_item=getUrl(url_s,index);
				if(url_item!=null)
					urls_map.put(url_item,url_item);
					}catch(Exception e){}
			}
			
		}
		catch (Exception e)
		{}
		return urls_map;
	}
	private String getUrl(String url,int index) throws IOException{
		String js=Jsoup.connect(url).ignoreContentType(true).execute().body();
		String[] arr=js.split(";");
		for(String line:arr){
			Matcher matcher=PLAY.matcher(line);
			if(matcher.find()){
				
				if(index==Integer.parseInt(matcher.group(2))){
					String href=matcher.group(3);
					if(href.matches("[0-9]{0,4}_.*")){
						/*JSONObject data=JSONObject.parseObject(Jsoup.connect("http://test4.diyiwl.wang/testapi777.php?time=1578841013&url="+href.substring(0,37)).userAgent("Mozilla (Linux,Android 10.0)").ignoreContentType(true).method(Connection.Method.GET).execute().body());
						if(data.getIntValue("success")==1){
							return data.getString("url");
						}*/
						return VideoParse.parseQqSign(href.substring(0,37));
						
					}else{
						//判断地址是否是合法
						if(VideoParse.match(href)){
							return VideoParse.parse(href,"");
							
						}
						return href;
					}
					//break;
				}
			}
		}
		return null;
	}
	@Override
	public String search(String key)
	{
		return "http://v.mtyee.com/m/sssv.php?&page=%d&top=20&q="+key;
	}

	@Override
	public String makeUrl(String url)
	{
		return null;
	}

	@Override
	public String getHost()
	{
		return host;
	}

	@Override
	public String getGold()
	{
		return "http://gda.mtyee.com/getsortdata_all_js.php?action=acg&page=%d&year=0&area=all&id=&class=0";
	}
	private String href(String href)
	{
		Uri uri=Uri.parse(href);
		String area=uri.getLastPathSegment();
		return "http://gda.mtyee.com/getsortdata_all_js.php?action=acg&page=%d&year=0&class=0&area=" + area + "&id=";
	}

}
