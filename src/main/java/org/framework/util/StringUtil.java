package org.framework.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class StringUtil {


    public static boolean isBlank(String str) {
        return Objects.isNull(str) || str.isEmpty();
    }

    public static boolean isNotBlank(String str) {
        return Objects.nonNull(str) && !str.isEmpty();
    }

    public static boolean isEquals(String first, String two) {
        if (first == null && two == null) {
            return true;
        }
        if (first != null && two != null) {
            if (first.length() != two.length()) {
                return false;
            }
            return first.equals(two);
        }
        return false;
    }

    public static boolean isEqualsIgnoreCase(String first, String two){
        if (Objects.nonNull(first) && Objects.nonNull(two)){
            first = first.toLowerCase();
            two = two.toLowerCase();
        }
        return isEquals(first,two);
    }

    public static String removeBeforeAndAfterCharacter(@Nonnull String originStr, @Nullable String pre, @Nullable String suf) {
        String newStr = originStr;
        if (isNotBlank(pre) && originStr.startsWith(pre)) {
            newStr = originStr.replaceFirst(pre, "");
        }
        if (isNotBlank(suf) && originStr.endsWith(suf)) {
            newStr = originStr.substring(0, originStr.length() - suf.length());
        }
        return newStr;
    }


}
