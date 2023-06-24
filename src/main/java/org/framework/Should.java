package org.framework;

public interface Should {


    /**
     * el.should(text.be,"exceptText")
     * el.should(text.not,"exceptText")
     * el.should(css.background-color.be,"exceptText")
     * el.should(css.background-color.not,"exceptText")
     * ……
     *
     * @param cm
     * @param args
     * @return
     */
    @CommandRule("Should")
    Conj should(String cm, String args);
}
