package org.framework.util;


import org.testng.Assert;
import org.testng.annotations.Test;

import static org.framework.util.StringUtil.isBlank;
import static org.framework.util.StringUtil.isNotBlank;
import static org.framework.util.StringUtil.isEquals;


public class StringUtilTest {

    @Test
    public void testIsBlank() {
        Assert.assertTrue(isBlank(""));
        Assert.assertTrue(isBlank(null));
        Assert.assertFalse(isBlank(" "));
        Assert.assertFalse(isBlank("123"));
    }

    @Test
    public void testIsNotBlank() {
        Assert.assertFalse(isNotBlank(""));
        Assert.assertFalse(isNotBlank(null));
        Assert.assertTrue(isNotBlank(" "));
        Assert.assertTrue(isNotBlank("123"));

    }

    @Test
    public void testTestEquals() {
        Assert.assertTrue(isEquals("", ""));
        Assert.assertFalse(isEquals(" ", ""));
        Assert.assertFalse(isEquals(null, ""));
        Assert.assertTrue(isEquals(null, null));
        Assert.assertFalse(isEquals("12", "21"));
        Assert.assertFalse(isEquals("12", "12 "));
        Assert.assertTrue(isEquals("12", "12"));
    }
}