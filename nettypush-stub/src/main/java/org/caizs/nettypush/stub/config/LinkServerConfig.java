package org.caizs.nettypush.stub.config;


import org.caizs.nettypush.core.bootstrap.LinkBootstrap;
import org.caizs.nettypush.core.common.ConfigLoader;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;

import java.util.Arrays;
import java.util.Properties;
import java.util.stream.StreamSupport;

//@Configuration
public class LinkServerConfig implements EnvironmentAware {

    @Override public void setEnvironment(Environment env) {
        Properties props = new Properties();
        MutablePropertySources propSrcs = ((AbstractEnvironment) env).getPropertySources();
        StreamSupport.stream(propSrcs.spliterator(), false)
                     .filter(ps -> ps instanceof EnumerablePropertySource)
                     .map(ps -> ((EnumerablePropertySource) ps).getPropertyNames())
                     .flatMap(Arrays::<String>stream)
                     .filter(propName -> propName.startsWith("link."))
                     .forEach(propName -> props.setProperty(propName, env.getProperty(propName)));
        ConfigLoader.loadProperties(props);
        new LinkBootstrap().start();
    }
}
