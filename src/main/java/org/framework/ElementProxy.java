package org.framework;

import org.framework.emun.Timeout;
import org.framework.ex.ExceptionWrapper;
import org.framework.ui.WrapEleUI;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;

public class ElementProxy implements InvocationHandler {

    Logger log = LoggerFactory.getLogger(ElementProxy.class);
    Timeout timeout = Timeout.getInstance();
    final WebElementFinder finder;

    public ElementProxy(WebElementFinder finder) {
        this.finder = finder;
    }


    /**
     * Processes a method invocation on a proxy instance and returns
     * the result.  This method will be invoked on an invocation handler
     * when a method is invoked on a proxy instance that it is
     * associated with.
     *
     * @param proxy  the proxy instance that the method was invoked on
     * @param method the {@code Method} instance corresponding to
     *               the interface method invoked on the proxy instance.  The declaring
     *               class of the {@code Method} object will be the interface that
     *               the method was declared in, which may be a superinterface of the
     *               proxy interface that the proxy class inherits the method through.
     * @param args   an array of objects containing the values of the
     *               arguments passed in the method invocation on the proxy instance,
     *               or {@code null} if interface method takes no arguments.
     *               Arguments of primitive types are wrapped in instances of the
     *               appropriate primitive wrapper class, such as
     *               {@code java.lang.Integer} or {@code java.lang.Boolean}.
     * @return the value to return from the method invocation on the
     * proxy instance.  If the declared return type of the interface
     * method is a primitive type, then the value returned by
     * this method must be an instance of the corresponding primitive
     * wrapper class; otherwise, it must be a type assignable to the
     * declared return type.  If the value returned by this method is
     * {@code null} and the interface method's return type is
     * primitive, then a {@code NullPointerException} will be
     * thrown by the method invocation on the proxy instance.  If the
     * value returned by this method is otherwise not compatible with
     * the interface method's declared return type as described above,
     * a {@code ClassCastException} will be thrown by the method
     * invocation on the proxy instance.
     * @see UndeclaredThrowableException
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

//        if (WebElement.class.isAssignableFrom(method.getDeclaringClass())) {
//            try {
//                return method.invoke(finder.findElement(), args);
//            } catch (InvocationTargetException | IllegalAccessException e) {
//                throw new RuntimeException(e);
//            }
//        }
//        String[] commands = method.getAnnotation(CommandRule.class).value();
//        Object result = null;
//        for (String command : commands) {
//            result = CommandRunner.getInstance().execute(proxy, finder, command, args);
//        }
//        return result;
        return dispatchAndRetry(proxy, method, args);



        /*讲道理不讲道理 。。。分类一下 就两种常规事件  鼠标点击 键盘输入
        so 一组命令中 无非就是点击输入组合 都没有返回值
        其他的 只有在断言的情况下 才会去获取某个 元素的属性或某种状态的查看
        so 有返回值的情况
        getValue| getCssValue| getSize| getLocation| getStatus| getSnapshot| getPageSource| ……

        but 可以看后面会不会有 将前面步骤执行的结果用到后面的步骤的需求
        */
    }

    Object dispatchAndRetry(Object proxy, Method method, Object[] args) throws Throwable {
        Stopwatch stopwatch = new Stopwatch(timeout.getRetryTimeoutTimeout());

        Throwable lastError;
        do {
            try {
                if (WebElement.class.isAssignableFrom(method.getDeclaringClass()) && !WrapEleUI.class.isAssignableFrom(method.getDeclaringClass())) {
                    return method.invoke(finder.findElement(), args);
                }
                String[] commands = method.getAnnotation(CommandRule.class).value();
                Object result = null;
                for (String command : commands) {
                    result = CommandRunner.getInstance().execute(proxy, finder, command, args);
                }
                return result;
            } catch (InvocationTargetException e) {
                lastError = e.getTargetException();
            } catch (WebDriverException | IndexOutOfBoundsException | AssertionError e) {
                lastError = e;
            }
            if (Cleanup.of.isInvalidSelectorError(lastError)) {
                throw Cleanup.of.wrapInvalidSelectorException(lastError);
            } else if (!shouldRetryAfterError(lastError)) {
                throw lastError;
            }
            log.info("--------------重试----------");
            stopwatch.sleep(timeout.getPollingInterval());
        }
        while (!stopwatch.isTimeoutReached());

        throw new ExceptionWrapper().wrap(lastError, finder);
    }

    static boolean shouldRetryAfterError(Throwable e) {
        if (e instanceof FileNotFoundException) {
            return false;
        }
        if (e instanceof IllegalArgumentException) {
            return false;
        }
        if (e instanceof ReflectiveOperationException) {
            return false;
        }
        if (e instanceof JavascriptException) {
            return false;
        }

        return e instanceof Exception || e instanceof AssertionError;
    }

}
