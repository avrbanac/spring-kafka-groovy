package hr.avrbanac.skg.starters.groovy.loader;

import groovy.lang.GroovyClassLoader;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Main configuration file for the Groovy Loader Starter. This is an entry point for the starter. Make sure that
 * {@link org.springframework.context.annotation.ComponentScan} sees this class by selecting root of the package (hr.avrbanac.skg).
 */
@Configuration
@EnableConfigurationProperties(GroovyLoaderProperties.class)
public class GroovyLoaderConfiguration {
    public static final String CONFIGURATION_PREFIX = "hr.avrbanac.groovy.loader";

    private static final Logger LOG = LoggerFactory.getLogger(GroovyLoaderConfiguration.class);
    private static final String GROOVY_FILE_EXTENSION = ".groovy";
    private static final GroovyClassLoader GCL = new GroovyClassLoader(GroovyLoaderConfiguration.class.getClassLoader());

    private final GroovyLoaderProperties properties;

    @Autowired
    public GroovyLoaderConfiguration(GroovyLoaderProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void init() {
        LOG.info(">>> Initialized with properties: {}", properties);
    }

    /**
     * There is a need for configuration property before configuration and property class have been loaded as Beans. Since we want to be
     * able to load Groovy classes very early in bootstrap phase, regular configuration properties are not yet ready. One way to get the
     * target folder for Groovy loading is by manually parsing the application.properties file. This approach has several issues. The other
     * way to go around this is to use Spring provided hook for early loading with environment provided by Spring. The following
     * implementation of the {@link BeanFactoryPostProcessor} interface does exactly that. BTW in the similar way Spring framework does
     * its early loading if it needs environment information.
     * This method also wraps bean registration for all Groovy classes annotated with {@link Component} annotation (base annotation for all
     * other component like structures e.g. Controller, RestController, Service...).
     * @param environment {@link Environment} Spring provided
     * @return {@link BeanFactoryPostProcessor} implementation
     */
    @Bean
    public static BeanFactoryPostProcessor beanFactoryPostProcessor(final Environment environment) {
        DtoJsonSerde.setClassLoader(GCL); // supporting Groovy land in SerDe class (static call)
        return beanFactory -> {
            BindResult<GroovyLoaderProperties> result = Binder.get(environment).bind(CONFIGURATION_PREFIX, GroovyLoaderProperties.class);
            GroovyLoaderProperties properties = result.get();

            for (Class<?> clazz : getGroovySources(properties.getGroovyPath())) {
                if (clazz.isAnnotationPresent(Component.class)) {
                    GenericBeanDefinition bd = new GenericBeanDefinition();
                    bd.setBeanClass(clazz);

                    String name = getName(bd);
                    LOG.info("Found component: {}", name);
                    ((DefaultListableBeanFactory) beanFactory).registerBeanDefinition(name, bd);
                }
                // TODO: what if there is a @Bean annotation in Groovy class? These beans need registration too...
            }
        };
    }

    /**
     * This method does the real class loading. Found files in target folder (filter files and only those with ".groovy" extension) are
     * parsed using GroovyClassLoader that is appended in loader three structure (via GCL CTOR - current java loader is passed through).
     * Some additional logging and exception handling is done here as well.
     * @param loadPath {@link String} root folder to be scanned for ".groovy" classes
     * @return {@link List} of parsed classes
     */
    private static List<Class<?>> getGroovySources(final String loadPath) {
        List<Class<?>> classes = new ArrayList<>();

        LOG.debug("Loading only following files: [path: {}, extension:{}]", loadPath, GROOVY_FILE_EXTENSION);
        try (Stream<Path> groovyPathCandidate = Files.walk(Paths.get(loadPath))) {
            groovyPathCandidate
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(GROOVY_FILE_EXTENSION))
                    .forEach(path -> {
                        try {
                            classes.add(GCL.parseClass(path.toFile()));
                        } catch (IOException e) {
                            LOG.error("Error parsing class file: {}", e.getMessage());
                            throw GroovyLoaderException.PARSE_ERROR;
                        }
                    });
        } catch (SecurityException se) {
            LOG.error("Access denied to the file/folder");
            throw GroovyLoaderException.SECURITY_ERROR;
        } catch (IOException ioe) {
            LOG.error("Error occurred while accessing file/folder");
            throw GroovyLoaderException.ACCESS_ERROR;
        }

        switch (classes.size()) {
            case 0:
                LOG.error("Found no Groovy implementation classes.");
                throw GroovyLoaderException.NO_CLASSES_FOUND;
            case 1:
                LOG.info("Found one Groovy implementation class");
                break;
            default:
                LOG.info("Found {} Groovy implementation classes", classes.size());
                break;
        }
        return classes;
    }

    private static String getName(final GenericBeanDefinition genericBeanDefinition) {
        String name = genericBeanDefinition.getBeanClassName();
        return name == null ? UUID.randomUUID().toString() : name;
    }

}
