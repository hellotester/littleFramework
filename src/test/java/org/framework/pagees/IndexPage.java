package org.framework.pagees;


import org.framework.Page;
import org.framework.Search;
import org.framework.ui.Button;
import org.framework.ui.WrapEleUI;


@Page("淘宝首页")
public class IndexPage {

    String url = "https://www.taobao.com/";

    @Search("//*[text()='登录']")
   public Button loginButton;

    @Search("//*[text()='中国大陆']")
    public WrapEleUI polist;



}
