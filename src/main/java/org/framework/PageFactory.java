package org.framework;

import org.framework.util.ClassUtil;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static org.framework.util.ClassUtil.findAnnotation;
import static org.framework.util.ClassUtil.scanForClassNames;

public class PageFactory {

    static Logger log = LoggerFactory.getLogger(PageFactory.class);

    private static final Map<Class<?>, Object> pages = new ConcurrentHashMap<>();


    @SuppressWarnings("all")
    public static <page> page loadPage(@Nonnull Class<page> pageClass, WebDriver driver) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        if (pages.containsKey(pageClass)) {
            return (page) pages.get(pageClass);
        }
        page page = ClassUtil.createInstanceForClass(pageClass);
        inject(pageClass, page, driver);
        return page;
    }


    public static void loadAllPages(WebDriver driver) throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Set<String> classNames = ClassUtil.scanForClassNames();
        for (String className : classNames) {
            Class<?> aClass = Class.forName(className);
            Page page = findAnnotation(aClass, Page.class);
            if (Objects.nonNull(page)) {
                log.info("create Page {}:{}", page.value(), aClass.getSimpleName());
                Object instance = ClassUtil.createInstanceForClass(aClass);
                inject(aClass, instance, driver);
                pages.put(aClass, instance);
            }
        }
    }

    @SuppressWarnings("all")
    static <T> T createUIProxy(Class<T> proxyClass, WebElementFinder webElementFinder) {
        return (T) Proxy.newProxyInstance(ClassUtil.getClassLoad(), new Class[]{proxyClass},
                new ElementProxy(webElementFinder));
    }

    static Set<Field> withPageClassFindUiFields(Class<?> pageClass) {
        // 获取自身及父类所有的 public
        List<Field> fields = Arrays.stream(pageClass.getFields()).filter(field -> field.isAnnotationPresent(Search.class)).collect(Collectors.toList());
        // 获取自身所有
        List<Field> declaredFields = Arrays.stream(pageClass.getDeclaredFields()).filter(field -> field.isAnnotationPresent(Search.class)).collect(Collectors.toList());
        Set<Field> fieldSet = new LinkedHashSet<>();
        fieldSet.addAll(fields);
        fieldSet.addAll(declaredFields);
        return fieldSet;
    }

    static void inject(Class<?> clas, @Nonnull Object instance, @Nonnull WebDriver driver) throws IllegalAccessException {
        Page page = findAnnotation(clas, Page.class);
        for (Field field : withPageClassFindUiFields(clas)) {
            Class<?> type = field.getType();
            Info info = field.getAnnotation(Info.class);
            String alias = Objects.nonNull(page) ?
                    Objects.nonNull(info) ? String.join(".", page.value(), info.name())
                            : String.join(".", page.value(), field.getName())
                    : String.join(".", clas.getSimpleName(), field.getName());
            Object proxy = createUIProxy(type, WebElementFinder.with(driver,
                    field.getAnnotation(Search.class).value(), alias));
            field.setAccessible(true);
            field.set(instance, proxy);
        }
    }


}
