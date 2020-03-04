package com.moe.x4jdm.model;

import android.net.Uri;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.moe.x4jdm.video.VideoParse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Indexsakura2 extends Index
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
			Elements header=doc.select("ul.am-slides > li");
			JSONArray head=new JSONArray();
			index.add(head);
			for(Element e:header){
				JSONObject item=new JSONObject();
				head.add(item);
				item.put("title",e.selectFirst(".am-slider-desc").text());
				item.put("src",e.selectFirst("img").absUrl("src"));
				item.put("href",e.selectFirst("a.pic").absUrl("href"));
			}
			Elements tabs= doc.select("ul.am-menu-nav > li > a");
			JSONArray tab=new JSONArray();
			Iterator<Element> tabs_i=tabs.iterator();
			while(tabs_i.hasNext()){
				JSONObject item=new JSONObject();
				tab.add(item);
				Element a=tabs_i.next();
				item.put("title",a.text());
				item.put("href",makeUrl(a.absUrl("href")));
			}
			tab.remove(tab.size()-2);
			index.add(tab);

			Elements mains=doc.select(".am-titlebar");
			if (mains != null)
			{
				Iterator<Element> mains_i=mains.iterator();
				while (mains_i.hasNext())
				{
					Element main_item=mains_i.next();
					JSONObject main=new JSONObject();
					try{main.put("title", main_item.selectFirst(".am-titlebar-title").text());}catch(NullPointerException e){}
					main.put("href", makeUrl(main_item.selectFirst("a").absUrl("href")));
					//JSONArray item_json=new JSONArray();
					index.add(main);
					for (Element e:main_item.nextElementSibling().select("li"))
					{
						JSONObject post=new JSONObject();
						try{post.put("title", e.selectFirst(".am-gallery-title").text());}catch(Exception ee){}
						try{post.put("href", e.selectFirst("a").absUrl("href"));}catch(Exception ee){}
						try{post.put("src", e.selectFirst("img.lazy").absUrl("data-original"));}catch(Exception ee){}
						try{post.put("desc",e.selectFirst(".am-gallery-desc").text());}catch(Exception ee){}
						//try{post.put("score",e.selectFirst(".views").text());}catch(Exception ee){}
						index.add(post);
					}
					//main.put("item", item_json);
					//index.add(main);
				}

			}
			Elements times=doc.select(".am-tabs-bd ul.am-list");
			JSONArray time=new JSONArray();
			for(Element e:times){
				JSONArray time_item=new JSONArray();
				time.add(time_item);
				Elements lis=e.select("li");
				for(Element li:lis){
					JSONObject item=new JSONObject();
					time_item.add(item);
					item.put("title",li.selectFirst("a").text());
					item.put("href",li.selectFirst("a").absUrl("href"));
					item.put("desc",li.selectFirst("span").text());
				}
			}
			time.add(0,time.remove(time.size()-1));
			this.time=time.toJSONString();
		}
		catch (IOException e)
		{}
		return index.toJSONString();
	}

	@Override
	public String getTime()
	{
		// TODO: Implement this method
		return time;
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
			int index=url.lastIndexOf("?");
			if(index!=-1){
				Uri uri=Uri.parse(url);
				url=uri.buildUpon().clearQuery().appendQueryParameter("searchword",URLEncoder.encode(uri.getQueryParameter("searchword"),"gb2312")).appendQueryParameter("page",uri.getQueryParameter("page")).toString().replaceAll("%25","%");
				//url=url.substring(0,index+1)+URLEncoder.encode(url.substring(index+1),"GBK");
			}
			Document doc=Jsoup.connect(url.replace("index1.html","index.html").replace("_1.html",".html")).get();
			//list.put("title", doc.selectFirst("h3.section-title").text());

			Element page=doc.selectFirst("ul.am-pagination");
			if(page==null){
				list.put("page",1);
				list.put("count",1);
			}else{
				list.put("page",getPage(url));
				list.put("count",getPage(page.selectFirst("li.am-pagination-last > a").attr("href")));
			}

			Elements posts=doc.select("ul.am-gallery > li");
			JSONArray item_json=new JSONArray();
			if(posts!=null&&!posts.isEmpty())
				for (Element e:posts)
				{
					JSONObject post=new JSONObject();
					try{post.put("title", e.selectFirst(".am-gallery-title").text());}catch(Exception ee){}
					try{post.put("href", e.selectFirst("a").absUrl("href"));}catch(Exception ee){}
					try{post.put("src", e.selectFirst("img.lazy").absUrl("data-original"));}catch(Exception ee){}
					try{post.put("desc",e.selectFirst(".am-gallery-desc").text());}catch(Exception ee){}
					item_json.add(post);
				}else{
				posts=doc.select("ul.am-list > li");
				for (Element e:posts)
				{
					JSONObject post=new JSONObject();
					try{post.put("title", e.selectFirst("a").text());}catch(Exception ee){}
					try{post.put("href", e.selectFirst("a").absUrl("href"));}catch(Exception ee){}
					try{post.put("desc",e.selectFirst("a[target]").text()+"|"+ e.selectFirst(".am-list-date").text());}catch(Exception ee){}
					item_json.add(post);
				}
			}
			list.put("item", item_json);
		}catch(HttpStatusException e){
			if(e.getStatusCode()==404){
				list.put("page", Integer.parseInt(Uri.parse(url).getLastPathSegment()));
				list.put("count", list.getIntValue("page"));
			}
		}
		catch (Exception e)
		{

		}
		return list.toJSONString();
	}
	private String getPage(String url){
		url=url.substring(url.lastIndexOf("/")+1);
		if(url.startsWith("index")){
			return url.substring(5,url.length()-5);
		}
		return url.substring(url.indexOf("_")+1,url.length()-5);
	}
	@Override
	public String getPost(String url)
	{
		JSONObject post=new JSONObject();
		try
		{
			Document doc=Jsoup.connect(url).get();
			post.put("title", doc.selectFirst("div#p-info > p").ownText());
			post.put("profile", doc.selectFirst("p.txtDesc").text());
			Elements p=doc.select("div#p-info > p");
			p.remove(0);
			post.put("desc", p.toString());
			try{post.put("src", doc.selectFirst("div.am-g > .am-intro-left > img").absUrl("src"));}catch(NullPointerException e){}
			JSONArray video=new JSONArray();
			for(Element e:doc.select("ul.mvlist")){
				JSONArray items=new JSONArray();
				video.add(items);
				post.put("video", video);
				for(Element li:e.select("li > a")){
					JSONObject item=new JSONObject();
					items.add(item);

					item.put("title",li.text());
					item.put("href",li.absUrl("href"));
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
		if(!url.startsWith(getHost()))return null;
		Map<String,String> urls=new LinkedHashMap<>();
		try
		{
			String[] target=url.substring(url.lastIndexOf("/")+1,url.length()-5).split("-");
			Matcher m=Pattern.compile("=\\\"/playdata/(.*?)\\\">",Pattern.MULTILINE).matcher(Jsoup.connect(url).ignoreContentType(true).execute().body());
			if(m.find()){
				String data=Jsoup.connect(getHost()+"playdata/"+m.group(1)).ignoreContentType(true).execute().body();
				JSONArray arr=JSONArray.parseArray(data.substring(data.indexOf("[["),data.lastIndexOf("]]")+2)).getJSONArray(Integer.parseInt(target[1]));
				JSONArray videos=arr.getJSONArray(1);
				String video_url=videos.getString(Integer.parseInt(target[2]));
				String[] video=video_url.split("\\$");
				video_url=video[1];
				switch(video[2]){
					case "pptv":
						video_url=VideoParse.parse(String.format("https://m.pptv.com/show/%s.html",video_url),"pptv");
						if(video_url!=null)
							urls.put(video_url,video_url);
						break;
					case "sohu":
						video_url=VideoParse.parse(String.format("https://m.tv.sohu.com/u/vw/%s.shtml",video_url),"sohu");
						if(video_url!=null)
							urls.put(video_url,video_url);
						break;
					default:

						if(video_url.matches("[0-9]{0,4}_.*")){
							/*JSONObject jo=JSONObject.parseObject(Jsoup.connect("http://test4.diyiwl.wang/testapi777.php?time=1578841013&url="+video_url.substring(0,37)).userAgent("Mozilla (Linux,Android 10.0)").ignoreContentType(true).method(Connection.Method.GET).execute().body());
							if(jo.getIntValue("success")==1){
								urls.put(jo.getString("url"),jo.getString("url"));
							}*/
							video_url=VideoParse.parseQqSign(video_url.substring(0,37));
							if(video_url!=null)
								urls.put(video_url,video_url);
						}else{
							//判断地址是否是合法
							if(VideoParse.match(video_url)){
								url=VideoParse.parse(video_url,"");
								if(url!=null)urls.put(url,url);
							}
							//https://cache.miaomiaojia.cc/ipsign/extend/parse_url.php?version=static&vid=http%3A%2F%2Fv.pptv.com%2Fshow%2FMaPYVibxlX50AfuY.html&type=pptv&hd=2&ran=1582254120&access=d48fb23dc8d27f3f889e3551c68ba379&ctype=phone
							if(video_url.startsWith("http"))
								urls.put(video_url,VideoParse.parseQQ(video_url));
						}
						break;
				}
				/*data=Jsoup.connect(video_url).ignoreContentType(true).execute().body();
				 Matcher matcher=Pattern.compile("url:\\s'(.*?)',",Pattern.MULTILINE).matcher(data);
				 if(matcher.find()){

				 urls.put(matcher.group(1),matcher.group(1));
				 }else{
				 matcher=Pattern.compile("var\\shuiid\\s=\\s\\\"(.*?)\\\";",Pattern.MULTILINE).matcher(data);
				 if(matcher.find()){
				 urls.put(matcher.group(1),matcher.group(1));
				 }
			 }*/}
		}
		catch (Exception e)
		{}
		return urls;
	}

	@Override
	public String search(String key)
	{
		return getHost() + "search.asp?page=%d&searchword="+key;

	}

	@Override
	public String makeUrl(String url)
	{
		if(url.endsWith("index.html"))
			return url.replace("index.html","index%d.html");
		else if(url.endsWith(".html"))
			return url.replace(".html","_%d.html");
			return url+"&page=%d";

	}

	@Override
	public String getHost()
	{
		return "http://m.imomoe.in/";
	}

	@Override
	public String getGold()
	{
		// TODO: Implement this method
		return null;
	}

}
