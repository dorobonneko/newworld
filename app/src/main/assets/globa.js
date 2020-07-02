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
