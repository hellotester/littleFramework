package org.framework.util;

import org.framework.Value;
import org.framework.io.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

import static org.framework.util.YamlUtil.getRequiredProperty;

public class ClassUtil {
    static Logger logger = LoggerFactory.getLogger(ClassUtil.class);

    public static void injectFieldFromYml(Object instance) throws ReflectiveOperationException {
        injectFieldProperties(instance.getClass(), instance);
    }


    static void injectFieldProperties(Class<?> clazz, Object instance) throws ReflectiveOperationException {
        for (Field field : clazz.getDeclaredFields()) {
            tryInjectProperties(clazz, instance, field);
        }
        for (Method method : clazz.getDeclaredMethods()) {
            tryInjectProperties(clazz, instance, method);
        }
        Class<?> superClazz = clazz.getSuperclass();
        if (superClazz != null && !superClazz.equals(Object.class)) {
            injectFieldProperties(superClazz, instance);
        }

    }


    static void tryInjectProperties(Class<?> clazz, Object bean, AccessibleObject acc) throws ReflectiveOperationException {
        Value value = acc.getDeclaredAnnotation(Value.class);
        if (value == null) {
            return;
        }
        Field field = null;
        Method method = null;
        if (acc instanceof Field) {
            field = (Field) acc;
        }
        if (acc instanceof Method) {
            if (((Method) acc).getParameters().length != 1) {
                throw new RuntimeException(String.format("Cannot inject a non-setter method %s for beanInstance %s", ((Method) acc).getName(), clazz.getSimpleName()));
            }
            method = (Method) acc;
        }
        checkFieldOrMethod((Member) acc);
        acc.setAccessible(true);

        if (field != null) {
            Class<?> accessibleType = field.getType();
            Object propValue = getRequiredProperty(value.value(), accessibleType);
            field.set(bean, propValue);
        }
        if (method != null) {
            Class<?> accessibleType = method.getParameterTypes()[0];
            Object propValue = getRequiredProperty(value.value(), accessibleType);
            method.invoke(bean, propValue);
        }
    }

    public static void checkFieldOrMethod(Member m) {
        int mod = m.getModifiers();
        if (Modifier.isStatic(mod)) {
            throw new RuntimeException("Cannot inject static field: " + m);
        }
        if (Modifier.isFinal(mod)) {
            if (m instanceof Field) {
                throw new RuntimeException("Cannot inject final field: " + m);
            }
            if (m instanceof Method) {
                System.err.println(
                        "Inject final method should be careful because it is not called on target bean when bean is proxied and may cause NullPointerException.");
            }
        }
    }

    public static Set<String> scanForClassNames(String... scanPath) {
        String[] scanPackages;
        if (scanPath == null || scanPath.length == 0) {
            Class<?> threadBootClass = getCurrentThreadBootClass();
            if (Objects.isNull(threadBootClass)) {
                throw new RuntimeException("");
            }
            scanPackages = new String[]{threadBootClass.getPackage().getName()};
        } else {
            scanPackages = scanPath;
        }
        logger.debug("scan in packages: {}", Arrays.toString(scanPackages));
        Set<String> classNameSet = new HashSet<>();
        for (String pkg : scanPackages) {
            // 扫描package:
            logger.debug("scan package: {}", pkg);
            ResourceResolver rr = new ResourceResolver(pkg);
            List<String> classList = rr.scan(res -> {
                String name = res.getName();
                if (name.endsWith(".class")) {
                    return name.substring(0, name.length() - 6).replaceAll("/", ".").replaceAll("\\\\", ".");
                }
                return null;
            });
            logger.debug("classes found by page scan: {}", classList);
            classNameSet.addAll(classList);
        }
        return classNameSet;
    }


    public static Class<?> getCurrentThreadBootClass() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement element = stackTrace.length > 0 ? stackTrace[stackTrace.length - 1] : null;
        if (Objects.nonNull(element)) {
            try {
                return Class.forName(element.getClassName());
            } catch (ClassNotFoundException error) {
                logger.warn(error.getMessage());
                return null;
            }
        }
        return null;
    }


    public static ClassLoader getClassLoad() {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        if (contextClassLoader == null) {
            return ClassUtil.class.getClassLoader();
        }
        return contextClassLoader;
    }

    public static Constructor<?> getSuitableConstructor(Class<?> clazz) {
        Constructor<?>[] cons = clazz.getConstructors();
        if (cons.length == 0) {
            cons = clazz.getDeclaredConstructors();
            if (cons.length != 1) {
                throw new RuntimeException("More than one constructor found in class " + clazz.getName() + ".");
            }
        }
        if (cons.length != 1) {
            throw new RuntimeException("More than one public constructor found in class " + clazz.getName() + ".");
        }
        return cons[0];
    }

    public static <A extends Annotation> A findAnnotation(Class<?> target, Class<A> annoClass) {
        A a = target.getAnnotation(annoClass);
        for (Annotation anno : target.getAnnotations()) {
            Class<? extends Annotation> annoType = anno.annotationType();
            if (!annoType.getPackage().getName().equals("java.lang.annotation")) {
                A found = findAnnotation(annoType, annoClass);
                if (found != null) {
                    if (a != null) {
                        throw new RuntimeException("Duplicate @" + annoClass.getSimpleName() + " found on class " + target.getSimpleName());
                    }
                    a = found;
                }
            }
        }
        return a;
    }

    @SuppressWarnings("all")
    public static <T> T createInstanceForClass(@Nonnull Class<T> clas, Object[] args) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        int mod = clas.getModifiers();
        if (Modifier.isAbstract(mod)) {
            throw new RuntimeException("class " + clas.getName() + " must not be abstract.");
        }
        if (Modifier.isPrivate(mod)) {
            throw new RuntimeException("class " + clas.getName() + " must not be private.");
        }
        Constructor<?> constructor = getSuitableConstructor(clas);
        return (T) constructor.newInstance(args);
    }

    public static <T> T createInstanceForClass(@Nonnull Class<T> clas) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<?> constructor = getSuitableConstructor(clas);
        if (constructor.getParameters().length > 0) {
            throw new RuntimeException("class " + clas.getName() + " must have non-parameter constructor.");
        }
        return createInstanceForClass(clas, null);
    }
}
