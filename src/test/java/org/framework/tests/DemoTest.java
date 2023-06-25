package org.framework.tests;

import org.framework.PageOptional;
import org.framework.pagees.TaoBaoPage;
import org.framework.ui.WrapEleUI;
import org.testng.annotations.Test;

public class DemoTest {


    @Test
    public void test1() {

        TaoBaoPage page = PageOptional.visit(TaoBaoPage.class);
        PageOptional.shouldBeTitle("淘宝");
        page.locationByChina.waitForClickSelect("中国香港");
        PageOptional.waitPageLoadsCompletely();
        PageOptional.shouldBeTitle("taobao | 淘寶");
        page.loginButton.waitForClick();
        PageOptional.shouldBeNumberOfWindows(2);
        PageOptional.switchWindowByTitle("Taobao login | 淘寶登入页");
        PageOptional.iframe("#J_Member");
        page.whenLoginStatus().searchXXX();

    }


    @Test
    void formTest(){
        PageOptional.open("https://webdriveruniversity.com/");
        PageOptional.waitForClick("#contact-us");
        PageOptional.shouldBeNumberOfWindows(2);
        PageOptional.switchWindowByTitle("WebDriver | Contact Us");
        WrapEleUI contact_form = PageOptional.get("#contact_form");
        contact_form.descendant("name='first_name'").waitForType("李");
        contact_form.descendant("name='last_name'").waitForType("特斯");
        contact_form.descendant("name='email'").waitForType("admin@he.com");
        contact_form.descendant("name='message'").waitForType("hello,world!");
        contact_form.submit();
        PageOptional.shouldBeTitle("Gianni Bruno - Designer");
        PageOptional.get("#contact_reply").should("textContent.contains","Thank You for your Message!");

    }

}
