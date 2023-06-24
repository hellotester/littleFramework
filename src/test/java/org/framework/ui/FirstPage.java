package org.framework.ui;


import org.framework.Page;
import org.framework.PageOptional;
import org.framework.Search;
import org.openqa.selenium.Keys;
import org.testng.annotations.Test;

@Page("demo")
public class FirstPage {

    @Search("#su")
    Button button;

    @Search("#kw")
    Input searchInput;


    @Test
    public void f() throws Exception{
        PageOptional.open("http://www.baidu.com");

        FirstPage visit = PageOptional.visit(FirstPage.class);
        visit
                .searchInput.waitForClickAndType("java"+ Keys.BACK_SPACE);
       visit. button.waitForClick();
        Thread.sleep(2000);


    }

}
