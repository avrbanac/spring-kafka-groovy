package hr.avrbanac.skg.starters.groovy.loader;

import groovy.lang.GroovyClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(GroovyLoaderProperties.class)
public class GroovyLoaderConfiguration {
    public static final String CONFIGURATION_PREFIX = "hr.avrbanac.groovy.loader";

    private static final Logger LOG = LoggerFactory.getLogger(GroovyLoaderConfiguration.class);
    private static final String GROOVY_FILE_EXTENSION = ".groovy";
    private static final GroovyClassLoader GCL = new GroovyClassLoader(GroovyLoaderConfiguration.class.getClassLoader());
}
