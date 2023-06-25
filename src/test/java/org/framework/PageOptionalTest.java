package org.framework;

import org.openqa.selenium.By;
import org.testng.annotations.Test;

public class PageOptionalTest {

    @Test
    public void test() throws InterruptedException {
        PageOptional.open("https://www.baidu.com");
        PageOptional.waitForType("#kw", "java");
        PageOptional.waitForClick("#su");
        PageOptional.get("#s_btn_wr").findElement(By.xpath(" following-sibling::*[1]"));
    }

}