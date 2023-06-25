package org.framework.ui;


import org.framework.CommandRule;
import org.framework.Conj;
import org.framework.Should;
import org.openqa.selenium.WebElement;


public interface WrapEleUI extends WebElement, Button, Select, Input, Should, Conj {

    /**
     * find ancestor
     * example:
     * el.descendant("class^='class'&text()='text'")
     *
     * @param locator
     * @return
     */
    @CommandRule("getParent")
    WrapEleUI ancestor(String locator);

    /**
     * find descendant
     * example:
     * el.descendant("class^='class'&text()='text'")
     *
     * @param locator
     * @return
     */
    @CommandRule("getChild")
    WrapEleUI descendant(String locator);

    /**
     * find sibling for index.
     * If the index is greater than zero,Find following , if it is less than zero, Find preceding
     *
     * @param index
     * @return
     */
    @CommandRule("getSibling")
    WrapEleUI sibling(int index);

    @CommandRule("setAttribute")
    WrapEleUI setAttribute(String attName, String attVal);

    @CommandRule("dropTo")
    void dropTo(String to);


}
