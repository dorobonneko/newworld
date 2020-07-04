function getIndex() {
	var doc=Http.sync({method:"get",url:"https://www.24cos.com"});
var index=[];
if(doc){
var header=[];
index.push(header);
array(doc.select("div.main > ul > li > a")).forEach(item=>{
			header.push({
				title:item.text(),
			href:item.absUrl("href")+"%d/",
			click:"list"
			});
		});
array(doc.select("div.cdiv")).forEach(element=>{
	index.push({title:element.selectFirst("h2").text()});
		array(element.select("ul > li")).forEach(post=>{
			index.push({
				title:post.selectFirst("img").attr("alt"),
			src:post.selectFirst("img").absUrl("src"),
			href:post.selectFirst("a").absUrl("href").replace(".html","_%d.html"),
			click:"list",
			viewtype:"post",
			desc:post.child(post.childrenSize()-1).text()
			});
		});
	});
	array(doc.select("div#fltag > div > a")).forEach(item=>{
			index.push({
				title:item.text(),
			href:item.absUrl("href")+"%d/",
			click:"list"
			});
		});
		}
		return index;
}
function getList(url){
	var uri=Uri.parse(url);
	var path=uri.getPath();
	if(path.startsWith("/coser/"))
		return getPostList(url);
	else if(path.endsWith(".html"))
		return getPosts(url);
	else
		return getPostList(url);
	}
	
function getPostList(url){
	if(url.endsWith("/1/"))
		url=url.substring(0,url.length-2);
	var doc=Http.sync({method:"get",url:url});
	var pages=doc.selectFirst("div.page");
	//var page=pages.selectFirst("a.on")?pages.selectFirst("a.on").text():1;
	var page=pages.ownText().split("\\s|/");
	return{
		page:page[3],
		count:page[4],
		item:array(doc.select("div.cdiv ul > li")).map(post=>{
			return{
				title:post.selectFirst("img").attr("alt"),
			src:post.selectFirst("img").absUrl("src"),
			href:post.selectFirst("a").absUrl("href").replace(".html","_%d.html"),
			click:"list",
			viewtype:"post",
			desc:post.child(post.childrenSize()-1).text()
			}
		})
		}
	}
	
function getPosts(url){
	var start=url.indexOf("_");
	var end=url.lastIndexOf("\.");
	url=url.substring(0,start+1)+(Number(url.substring(start+1,end))-1)+url.substring(end);
	
	var doc=Http.sync({method:"get",url:url});
	var pages=doc.selectFirst("div.page");
	var page=pages.selectFirst("a.on")?pages.selectFirst("a.on").text():1;
	var last=pages.select("a").last();
	var count=last?last.text():1;
	return {
		page:page,
		count:count,
		item:array(doc.select("div.mtp li")).map(post=>{
			return {
				title:post.selectFirst("img").attr("alt"),
				src:post.selectFirst("img").absUrl("src"),
				source:post.selectFirst("img").absUrl("src").replace("/m","/"),
				viewtype:"imagepreview"
			}
		})
	}
}
function search(key){
	return "https://www.24cos.com/serch.aspx?page=%d&key="+key;
}
