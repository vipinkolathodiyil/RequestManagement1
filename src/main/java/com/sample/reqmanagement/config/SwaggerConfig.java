package com.sample.reqmanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.base.Predicate;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;

import static springfox.documentation.builders.PathSelectors.regex;
import static com.google.common.base.Predicates.or;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket postsApi(){
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .globalOperationParameters(
                        Arrays.asList(new ParameterBuilder()
                                .name("Authorization")
                                .description("Authorization")
                                .modelRef(new ModelRef("string"))
                                .parameterType("header")
                                .build()))
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }

    private Predicate<String> postPaths() {
        return or(regex("/authenticate-user"), regex("/xyz/service.*"));
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("Request Management API")
                .description("Request Management API reference for developers")
                .termsOfServiceUrl("sample@xyz.com")
                .contact("sample@xyz.com").license("Req Management License")
                .licenseUrl("sample@xyz.com").version("1.0").build();
    }

}