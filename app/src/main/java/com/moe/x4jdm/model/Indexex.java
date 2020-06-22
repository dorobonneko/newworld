package com.moe.x4jdm.model;
import java.util.Map;
import com.alibaba.fastjson.JSONArray;
import org.jsoup.Jsoup;
import java.io.IOException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import com.alibaba.fastjson.JSONObject;

public class Indexex extends Index
{

	@Override
	public String getIndex(int page)
	{
		JSONArray index=new JSONArray();
		try
		{
			Document doc=Jsoup.connect(getHost() + "/?lang=zh_CN").get();
			for(Element e:doc.select("div.post_content.index_page:has(> span.img_block2)")){
				JSONObject title=new JSONObject();
				title.put("title",e.selectFirst("div.title").text());
				index.add(title);
				for(Element item:e.select("span")){
					JSONObject post=new JSONObject();
					index.add(post);
					try{post.put("src",item.selectFirst("img.img_sp").absUrl("src"));}catch(Exception ee){}
					try{post.put("title",item.selectFirst("div.img_block_text > a").text());
					
					String[] id=item.selectFirst("img.img_sp").attr("id").split("_");
					String src=post.getString("src");
					post.put("source",getHost()+"/pictures/get_image/"+id[id.length-1]+"-"+post.getString("title")+src.substring(src.lastIndexOf(".")));
					}catch(Exception ee){}
					post.put("viewtype","imagepreview");
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
		return null;
	}

	@Override
	public String getPost(String url)
	{
		return null;
	}

	@Override
	public Map<String, String> getVideoUrl(String url)
	{
		return null;
	}

	@Override
	public String getHost()
	{
		return "https://anime-pictures.net";
	}
	
}
