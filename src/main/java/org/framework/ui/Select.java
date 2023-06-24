package org.framework.ui;


import org.framework.CommandRule;
import org.framework.Conj;
import org.framework.Should;

public interface Select extends Should{

    @CommandRule("Select")
    void waitForSelect(String optionByText);

    @CommandRule({"click", "Select"})
    void waitForClickSelect(String optionByText);

    @CommandRule({"click", "Select"})
    void waitForClickSelect(int optionByIndex);

    @CommandRule("Select")
    void waitForSelect(int optionByIndex);

}
