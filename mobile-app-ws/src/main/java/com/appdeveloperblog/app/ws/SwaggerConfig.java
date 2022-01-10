package com.appdeveloperblog.app.ws;

import java.util.Arrays;
import java.util.HashSet;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
	
	Contact contact = new Contact("Ornella Fasciolo", "", "ornella_sofia@hotmail.com");
	
	ApiInfo apiInfo = new ApiInfoBuilder().title("Rest app RESTful Web Service documentation").version("1.0.0")
						.description("This page documents Rest app RESTful Web Service endpoints").contact(contact).build();
								  
	@Bean
	public Docket apiDocket() {
		
		Docket docket = new Docket(DocumentationType.SWAGGER_2).protocols(new HashSet<>(Arrays.asList("HTTP","HTTPs")))
				.apiInfo(apiInfo).select().apis(RequestHandlerSelectors.basePackage("com.appdeveloperblog.app.ws"))
						.paths(PathSelectors.any()).paths(PathSelectors.any()).build();
		
		return docket;
	}
}
