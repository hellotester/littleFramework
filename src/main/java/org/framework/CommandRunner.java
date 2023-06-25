package org.framework;

import org.framework.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

public enum CommandRunner {

    CommandRunner;
    final Logger log = LoggerFactory.getLogger(org.framework.CommandRunner.class);

    public static CommandRunner getInstance() {
        return CommandRunner;
    }

    private final Map<String, Command<?>> commandMap = new ConcurrentHashMap<>(128);

    public final void add(String method, Command<?> command) {
        commandMap.put(method, command);
    }

    /*
    美丽的日子 快乐的写代码。。。。。

    要干的
    1、执行步骤之前可以记录步骤、记录并输出日志、自定义事件
    2、执行之后可以记录输出日志、自定义事件
    3、如果失败 可以提供重试机制

    看心情 考虑干不干的
    1、啧，记录的步骤可以生产报告、生产用例。。。so 秉承谁提需求谁干的原则 开放接口给他自个写
    2、或许用个对象将 单个element的命令链保存下来？ 然后让其自己可以获取到自己的命令链
    3、还没想好……
     */

    @Nullable
    public <T> T execute(Object proxy, WebElementFinder elementFinder, String commandName, @Nullable Object... args) throws Exception {
        Command<T> command = load(commandName);
        log.debug("execute:{} target is: {}", command.getClass().getSimpleName(), elementFinder.alias());
        return command.execute(proxy, elementFinder, args);
    }


    @SuppressWarnings("all")
    <T> Command<T> load(String methodName) {
        if (commandMap.containsKey(methodName.toLowerCase())) {
            return (Command<T>) commandMap.get(methodName.toLowerCase());
        }
        Iterator<Command> load = ServiceLoader.load(Command.class).iterator();
        while (load.hasNext()) {
            Command next = load.next();
            if (StringUtil.isEqualsIgnoreCase(next.getClass().getSimpleName(), methodName)) {
                add(methodName.toLowerCase(), next);
                return (Command<T>) next;
            }
        }
        throw new IllegalArgumentException("Unknown Event method: " + methodName);
    }
}
