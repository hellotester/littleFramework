function monkeyReq() {
    var oldOpen = XMLHttpRequest.prototype.open;
    window.openHTTPs = 0;
    XMLHttpRequest.prototype.open = function(method, url, async, user, pass) {
        window.openHTTPs++;
        this.addEventListener('readystatechange', function() {
            if(this.readyState == 4) {
                window.openHTTPs--;
            }
        }, false);
        oldOpen.call(this, method, url, async, user, pass);
    }
};

return (function waitForAjax(){
    if (!isNaN(window.openHTTPs)){
       console.log("isNaN(window.openHTTPs) "+ isNaN(window.openHTTPs));
       return 0 == window.openHTTPs;
    }
    else{
       monkeyReq();
    }
    return false;
})();


