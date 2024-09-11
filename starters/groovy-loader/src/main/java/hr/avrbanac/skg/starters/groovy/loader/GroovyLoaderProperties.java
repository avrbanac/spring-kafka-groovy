package hr.avrbanac.skg.starters.groovy.loader;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(GroovyLoaderConfiguration.CONFIGURATION_PREFIX)
public class GroovyLoaderProperties {
    @NotEmpty
    private String groovyPath;

    public @NotEmpty String getGroovyPath() {
        return groovyPath;
    }

    public GroovyLoaderProperties setGroovyPath(@NotEmpty final String groovyPath) {
        this.groovyPath = groovyPath;
        return this;
    }

    @Override
    public String toString() {
        return "GroovyLoaderProperties{" +
                "groovyPath='" + groovyPath + '\'' +
                '}';
    }
}
