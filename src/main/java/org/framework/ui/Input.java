package org.framework.ui;

import org.framework.CommandRule;
import org.framework.Conj;
import org.framework.Should;

public interface Input extends Should {

    @CommandRule("type")
    void waitForType(CharSequence str);

    @CommandRule({"click", "type"})
    void waitForClickAndType(CharSequence str);

}
