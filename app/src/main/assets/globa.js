for(var fn in runtime) {
  if(typeof runtime[fn] === 'function') {
    this[fn] = (function() {
      var method = runtime[fn];
      return function() {
         return method.apply(runtime,arguments);
      };
    })();
  }
}
function array(list){
	var arr=new Array();
	for(var i=0;i<list.size();i++)
	arr.push(list.get(i));
	return arr;
}
