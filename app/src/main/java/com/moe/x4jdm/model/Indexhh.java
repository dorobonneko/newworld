package com.moe.x4jdm.model;
import android.net.Uri;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Indexhh extends Index
{

	


	@Override
	public String getFilter()
	{
		return null;
	}


	@Override
	public void clearCache()
	{
	}

	@Override
	public String getIndex()
	{
		JSONArray index=new JSONArray();
		JSONArray tabs=new JSONArray();
		String[] target=new String[]{"newest","fapped","viewed","discussed","name","year"};
		for(String tab:target){
			JSONObject tab_item=new JSONObject();
			tab_item.put("title",tab);
			tab_item.put("href",makeUrl(getHost())+"?action=action_pagination&genre=all&page=%d&typex="+tab+"&search=all&pagetype=home");
			tabs.add(tab_item);
		}
		index.add(tabs);
		try
		{
			Document doc=Jsoup.connect(makeUrl(getHost())).requestBody("action=action_cookies_get").post();
			Elements a=doc.select(".liked_post > .like_thumbnail");
			if(a!=null&&!a.isEmpty()){
				//JSONArray mains=new JSONArray();
				//index.put("main",mains);
				//JSONObject main=new JSONObject();
				//index.add(main);
				//JSONArray items=new JSONArray();
				//main.put("item",items);
				Iterator<Element> a_i=a.iterator();
				while(a_i.hasNext()){
					Element a_item=a_i.next();
					JSONObject item=new JSONObject();
					index.add(item);
					item.put("href",a_item.absUrl("href"));
					item.put("title",a_item.parent().selectFirst(".like_title").text());
					item.put("src",a_item.child(0).absUrl("src"));
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
		Uri uri=Uri.parse(url);
		JSONObject list=new JSONObject();
		try
		{
			try{
			list.put("page",Integer.parseInt(uri.getQueryParameter("page")));
			}catch(Exception e){
				list.put("page",1);
			}
			Document doc=Jsoup.connect(uri.toString()).requestBody(uri.getEncodedQuery()).post();
			Elements page=doc.select(".page-item");
			if(page==null||page.isEmpty())
				list.put("count",list.getIntValue("page"));
				else
			list.put("count",Integer.parseInt(page.get(page.size()-2).text()));
			
			Elements div=doc.select(".brick.zoe-grid.brick-big");
			JSONArray items=new JSONArray();
			list.put("item",items);
			Iterator<Element> div_i=div.iterator();
			while(div_i.hasNext()){
				Element div_item=div_i.next();
				JSONObject item=new JSONObject();
				items.add(item);
				item.put("title",div_item.selectFirst("a.brick-title").text());
				item.put("href",div_item.selectFirst("a.brick-title").absUrl("href"));
				item.put("src",div_item.selectFirst("img").absUrl("src"));
				item.put("desc",div_item.selectFirst(".social-info").child(0).text());
				
			}
		}
		catch (IOException e)
		{}
		return list.toJSONString();
	}

	@Override
	public String getPost(String url)
	{
		if(url.contains("\\?"))return null;
		JSONObject post=new JSONObject();
		try
		{
			Document doc=Jsoup.connect(url).get();
			post.put("title",doc.selectFirst("h1.entry-title.page-title.long-title").text());
			post.put("profile",doc.selectFirst("div.category_description_text").child(0).text());
			post.put("src",doc.selectFirst("meta[property='og:image']").absUrl("content"));
			post.put("desc",doc.selectFirst("table.category_extra").toString());
			String series=doc.selectFirst("[rel='category tag']").absUrl("href");
			JSONObject list=JSONObject.parseObject(getList(series));
			JSONArray ja=new JSONArray();
			JSONArray video=new JSONArray();
			video.add(ja);
			post.put("video",video);
			ja.addAll(list.getJSONArray("item"));
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
			String key=Uri.parse(doc.selectFirst("p.video-container > iframe").absUrl("src")).getLastPathSegment();
			doc=Jsoup.connect("https://ksplayer.com/download/" + key + "/").get();
			Elements a=doc.select(".download_links > a");
			Iterator<Element> a_i=a.iterator();
			while(a_i.hasNext()){
				Element link=a_i.next();
				urls.put(link.child(0).text(),link.absUrl("href"));
			}
		}
		catch (IOException e)
		{}
		return urls;
	}

	@Override
	public String search(String key)
	{
		return makeUrl(getHost())+"?action=action_pagination&genre=all&page=%d&typex=newest&search="+key+"&pagetype=search";
	}

	@Override
	public String makeUrl(String url)
	{
		return url+"wp-admin/admin-ajax.php";
	}

	@Override
	public String getHost()
	{
		return "https://hentaihaven.gg/";
	}

	@Override
	public String getGold()
	{
		return makeUrl(getHost())+"?action=action_pagination&genre=all&page=%d&typex=fapped&search=all&pagetype=home";
	}
	
}
