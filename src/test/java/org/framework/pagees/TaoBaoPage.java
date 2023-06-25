package org.framework.pagees;


import org.framework.Page;
import org.framework.Search;
import org.framework.ui.Button;
import org.framework.ui.Input;
import org.framework.ui.Select;


@Page("淘宝首页")
public class TaoBaoPage {

    String url = "https://www.taobao.com/";


    @Search("登录")
    public Button loginButton;

    @Search("中国大陆")
    public Select locationByChina;

    @Search("#fm-login-id")
    Input accountBox;

    @Search("#fm-login-password")
    Input passwordBox;


    public TaoBaoPage whenLoginStatus() {

        // if condition? to login
        accountBox.waitForClickAndType("1234");
        accountBox.should("value.be", "1234");
        passwordBox.waitForClickAndType("12345");
        //loginButton.waitForClick();
        // end if success?
        return this;
    }

    public void searchXXX() {
        // do service...
    }


}
