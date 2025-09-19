package app.spring.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.tiles3.TilesConfigurer;
import org.springframework.web.servlet.view.tiles3.TilesViewResolver;

@Configuration
public class TilesConfig {

	@Bean
    TilesConfigurer tilesConfigurer() {
        TilesConfigurer configurer = new TilesConfigurer();
        // Location of your tiles definition files
        configurer.setDefinitions("/WEB-INF/tiles/tiles.xml");
        configurer.setCheckRefresh(true); // auto-refresh if changed
        return configurer;
    }

    @Bean
    TilesViewResolver tilesViewResolver() {
        return new TilesViewResolver();
    }
}
