

function findEqualStr(str){
    let all = document.querySelectorAll("*");
    for (e of all){
        if (e.textContent==str){
          let id = getUniqueID();
          console.log("findEqualStr找到了。。。test-id="+id);
          e.setAttribute("test-id", id);
          return id;
        }
    }
}


function findStartWithStr(str){
    let all = document.querySelectorAll("*");
    for (e of all){
        if (e.textContent.startsWith(str)){
            let id = getUniqueID();
            console.log("findStartWithStr找到了。。。test-id="+id);
            e.setAttribute("test-id", id);
            return id;
        }
    }
}


function findEndsWithStr(str){
    let all = document.querySelectorAll("*");
    for (e of all){
        if (e.textContent.endsWith(str)){
            let id = getUniqueID();
            console.log("findEndsWithStr找到了。。。test-id="+id);
            e.setAttribute("test-id", id);
            return id;
        }
    }
}

function getUniqueID () {
    var time = Date.now().toString(36);
    var random = Math.random().toString(36);
    random = random.substring(2, random.length);
    return random + time
  }

function find(str) {
    if (str==null || str.length==0){
        console.log("不合法参数");
        return null;
    }
    if  (str.startsWith("^")){
        return findStartWithStr(str.substring(1));
    }
    if (str.startsWith("$")){
        return findEndsWithStr(str.substring(1));
    }
     return findEqualStr(str);
}

return find(arguments[0]);