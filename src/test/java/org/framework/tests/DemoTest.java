package org.framework.tests;

import org.framework.PageOptional;
import org.framework.pagees.IndexPage;
import org.framework.ui.WrapEleUI;
import org.testng.annotations.Test;

public class DemoTest {


    @Test
    public void test1() {

        IndexPage page = PageOptional.visit(IndexPage.class);
        PageOptional.shouldBeTitle("淘宝");
        page.loginButton.waitForClick();
        PageOptional.shouldBeNumberOfWindows(2);
        page.polist.waitForClickSelect("中国香港");
    }


    @Test
    void formTest(){
        PageOptional.open("https://webdriveruniversity.com/");
        PageOptional.waitForClick("#contact-us");
        PageOptional.shouldBeNumberOfWindows(2);
        PageOptional.switchWindowByTitle("WebDriver | Contact Us");
        WrapEleUI contact_form = PageOptional.getWebElement("#contact_form");
        contact_form.descendant("name='first_name'").waitForType("李");
        contact_form.descendant("name='last_name'").waitForType("特斯");
        contact_form.descendant("name='email'").waitForType("admin@he.com");
        contact_form.descendant("name='message'").waitForType("hello,world!");
        contact_form.submit();
        PageOptional.shouldBeTitle("Gianni Bruno - Designer");
        PageOptional.getWebElement("#contact_reply").should("textContent.contains","Thank You for your Message!");

    }

}
