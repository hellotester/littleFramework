package org.framework.ui;

import org.framework.CommandRule;
import org.framework.Conj;
import org.framework.Should;

public interface Button extends Should , Conj{

    @CommandRule("click")
    void waitForClick();

    @CommandRule("rightClick")
    void waitForRightClick();

    @CommandRule("doubleClick")
    void waitForDoubleClick();

    @CommandRule("jsClick")
    void waitForJSClick();

}
