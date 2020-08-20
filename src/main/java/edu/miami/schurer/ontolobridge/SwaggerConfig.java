package edu.miami.schurer.ontolobridge;

import com.google.common.base.Predicates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.paths.RelativePathProvider;
import springfox.documentation.spring.web.plugins.ApiSelectorBuilder;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.ServletContext;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

@Configuration
@EnableSwagger2
public class SwaggerConfig extends WebMvcConfigurationSupport{

    @Value("${spring.profiles.active:Unknown}")
    private String activeProfile;

    @Bean
    public Docket api(ServletContext servletContext)

    {
        ApiSelectorBuilder dock = new Docket(DocumentationType.SWAGGER_2)
                .pathProvider(new RelativePathProvider(servletContext) {
                    @Override
                    public String getApplicationBasePath() {
                        if(activeProfile.equals("prod"))
                            return "/api";
                        else
                            return "/";

                    }
                })
                .select()
                .apis(RequestHandlerSelectors.basePackage("edu.miami.schurer.ontolobridge"))
                .paths(PathSelectors.any());
        if(!activeProfile.equals("dev")){
            dock = dock.paths(Predicates.not(Predicates.or(PathSelectors.ant("/frontend/*"),PathSelectors.ant("/"),PathSelectors.ant("/csrf"))));
        }else{
            dock = dock.paths(Predicates.not(Predicates.or(PathSelectors.ant("/"),PathSelectors.ant("/csrf"))));
        }
        return dock.build()
                .apiInfo(apiInfo())
                .produces(new HashSet<String>(Arrays.asList("application/json")));
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "Ontolobridge API",
                "Some custom description of API.",
                "0.1",
                "Terms of service",
                new Contact("John Turner", "http://dev3.ccs.miami.edu:8080/sigc-api", "jpt55@med.miami.edu"),
                "License of API", "API license URL", Collections.emptyList());
    }

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

}
