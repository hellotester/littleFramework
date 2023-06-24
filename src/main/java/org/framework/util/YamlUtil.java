package org.framework.util;


import org.framework.StaticResource;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.resolver.Resolver;

import java.time.*;
import java.util.*;
import java.util.function.Function;

@SuppressWarnings("all")
public class YamlUtil {
    final static Map<String, Object> loadYamlAsPlainMap = YamlUtil.loadYamlAsPlainMap(StaticResource.source);
    static Map<Class<?>, Function<String, Object>> converters = new HashMap<>();

    static {
        converters.put(String.class, s -> s);
        converters.put(boolean.class, Boolean::parseBoolean);
        converters.put(Boolean.class, Boolean::valueOf);

        converters.put(byte.class, Byte::parseByte);
        converters.put(Byte.class, Byte::valueOf);

        converters.put(short.class, Short::parseShort);
        converters.put(Short.class, Short::valueOf);

        converters.put(int.class, Integer::parseInt);
        converters.put(Integer.class, Integer::valueOf);

        converters.put(long.class, Long::parseLong);
        converters.put(Long.class, Long::valueOf);

        converters.put(float.class, Float::parseFloat);
        converters.put(Float.class, Float::valueOf);

        converters.put(double.class, Double::parseDouble);
        converters.put(Double.class, Double::valueOf);

        converters.put(LocalDate.class, LocalDate::parse);
        converters.put(LocalTime.class, LocalTime::parse);
        converters.put(LocalDateTime.class, LocalDateTime::parse);
        converters.put(ZonedDateTime.class, ZonedDateTime::parse);
        converters.put(Duration.class, Duration::parse);
        converters.put(ZoneId.class, ZoneId::of);
    }

    static Object convert(Class<?> clazz, String value) {
        Function<String, Object> fn = converters.get(clazz);
        if (fn == null) {
            throw new IllegalArgumentException("Unsupported value type: " + clazz.getName());
        }
        return fn.apply(value);
    }

    public static Map<String, Object> loadYaml(String path) {
        LoaderOptions loaderOptions = new LoaderOptions();
        DumperOptions dumperOptions = new DumperOptions();
        Representer representer = new Representer(dumperOptions);
        NoImplicitResolver resolver = new NoImplicitResolver();
        Yaml yaml = new Yaml(new Constructor(loaderOptions), representer, dumperOptions, loaderOptions, resolver);
        return ClassPathUtil.readInputStream(path, yaml::load);
    }

    public static Map<String, Object> loadYamlAsPlainMap(String path) {
        Map<String, Object> data = loadYaml(path);
        Map<String, Object> plain = new LinkedHashMap<>();
        convertTo(data, "", plain);
        return plain;
    }

    static void convertTo(Map<String, Object> source, String prefix, Map<String, Object> plain) {
        for (String key : source.keySet()) {
            Object value = source.get(key);
            if (value instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> subMap = (Map<String, Object>) value;
                convertTo(subMap, prefix + key + ".", plain);
            } else if (value instanceof List) {
                plain.put(prefix + key, value);
            } else {
                plain.put(prefix + key, value.toString());
            }
        }
    }

    public static <T> T getRequiredProperty(String key, Class<T> targetType) {
        return Objects.requireNonNull(getProperty(key, targetType), "Property '" + key + "' not found.");
    }

    static <T> T getProperty(String key, Class<T> targetType) {
        T value;
        PropertyExpr propertyExpr = parsePropertyExpr(key);
        if (propertyExpr == null) {
            Object o = loadYamlAsPlainMap.get(key);
            if (o == null) {
                return null;
            }
            value = parseValue(o.toString(), targetType);

        } else {
            Object o = loadYamlAsPlainMap.get(propertyExpr.key);
            if (o == null) {
                value = parseValue(propertyExpr.defaultValue, targetType);
            } else {
                value = parseValue(o.toString(), targetType);
            }
        }
        return value;
    }

    static <T> T parseValue(String value, Class<T> targetType) {
        if (converters.containsKey(targetType)) {
            return (T) convert(targetType, value);
        } else {
            return (T) value;
        }
    }

    static PropertyExpr parsePropertyExpr(String key) {
        if (key.startsWith("${") && key.endsWith("}")) {
            int n = key.indexOf(':');
            if (n == -1) {
                // ${key}
                return new PropertyExpr(key.substring(2, key.length() - 1), null);
            } else {
                // ${key:default}
                return new PropertyExpr(key.substring(2, n), key.substring(n + 1, key.length() - 1));
            }
        }
        return null;
    }

    static class PropertyExpr {
        PropertyExpr(String key, String defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;
        }

        String key;
        String defaultValue;

    }

}

/**
 * Disable ALL implicit convert and treat all values as string.
 */
class NoImplicitResolver extends Resolver {

    public NoImplicitResolver() {
        super();
        super.yamlImplicitResolvers.clear();
    }

}
