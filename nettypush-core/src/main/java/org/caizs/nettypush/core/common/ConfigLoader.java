package org.caizs.nettypush.core.common;

import org.caizs.nettypush.core.base.Config;
import io.netty.util.internal.StringUtil;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

/**
 * 加载配置，默认在初始化client和server时加载
 */
public class ConfigLoader {

    private static volatile Properties prop;

    /**
     * 读当前jar所在config.properties配置文件
     */
    public synchronized static void load() {
        if (Config.loaded) {
            return;
        }
        String jarPath = new File(".").getAbsolutePath();
        if (jarPath.endsWith(".")) {
            jarPath = jarPath.substring(0, jarPath.length() - 1);
        }
        if (!jarPath.endsWith(File.separator)) {
            jarPath = jarPath + File.separator;
        }
        File file = new File(jarPath + "config.properties");
        if (file == null) {
            throw new RuntimeException("当前目录配置文件config.properties找不到");
        }
        loadFile(file);
    }

    public synchronized static void load(String classPath) {
        if (classPath == null) {
            load();
        }
        try {
            File file = new ClassPathResource(classPath).getFile();
            loadFile(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized static void loadProperties(Properties properties) {
        prop = properties;

        Config.heartbeatTick = getPropertyInt("link.heartbeatTick");
        Config.retryTime = getPropertyInt("link.retryTime");
        Config.maxFrameLength = getPropertyInt("link.maxFrameLength");
        Config.mode = getPropertyInt("link.mode");
        Config.connToIp = getProperty("link.connToIp");
        Config.connToPort = getPropertyInt("link.connToPort");
        Config.listenPort = getPropertyInt("link.listenPort");
        Config.group = getProperty("link.group");
        Config.identity = getProperty("link.identity");
        Config.loaded = true;
    }

    private static void loadFile(File file) {
        Properties properties = new Properties();
        try {
            properties.load(FileUtils.openInputStream(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        loadProperties(properties);
    }


    public static String getProperty(String key) {
        lazyLoad();
        String value = prop.getProperty(key);
        if (StringUtil.isNullOrEmpty(value)) {
            return null;
        }
        return value;
    }

    public static String getProperty(String key, String defaultValue) {
        return Optional.ofNullable(getProperty(key)).orElse(defaultValue);
    }

    public static Integer getPropertyInt(String key) {
        String value = getProperty(key);
        return value == null ? null : Integer.valueOf(value);
    }

    public static Integer getPropertyInt(String key, Integer defaultValue) {
        return Optional.ofNullable(getPropertyInt(key)).orElse(defaultValue);
    }

    public static Boolean getPropertyBoolean(String key) {
        String value = getProperty(key);
        return value == null ? null : Boolean.valueOf(value);
    }

    public static Boolean getPropertyBoolean(String key, Boolean defaultValue) {
        return Optional.ofNullable(getPropertyBoolean(key)).orElse(defaultValue);
    }

    private static void lazyLoad() {
        if (prop == null) {
            load();
        }
    }


}
