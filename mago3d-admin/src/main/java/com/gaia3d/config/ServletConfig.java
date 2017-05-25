package com.gaia3d.config;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.springframework.web.servlet.support.RequestDataValueProcessor;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.gaia3d.interceptor.CSRFHandlerInterceptor;
import com.gaia3d.interceptor.ConfigInterceptor;
import com.gaia3d.interceptor.LogInterceptor;
import com.gaia3d.interceptor.SecurityInterceptor;

import lombok.extern.slf4j.Slf4j;

/**
 * <mvc:annotation-driven> 대신 EnableWebMvc
 * 
 * @author Cheon JeongDae
 *
 */
@Slf4j
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = { ",com.gaia3d.interceptor,com.gaia3d.controller,com.gaia3d.validator" }, includeFilters = {
		@Filter(type = FilterType.ANNOTATION, value = Component.class),
		@Filter(type = FilterType.ANNOTATION, value = Controller.class) })
public class ServletConfig extends WebMvcConfigurerAdapter {

	@Override
    public void addInterceptors(InterceptorRegistry registry) {
		log.info(" @@@ ServletConfig addInterceptors @@@@ ");
		
        registry.addInterceptor(new ConfigInterceptor())
        		.addPathPatterns("/**");
        registry.addInterceptor(new LogInterceptor())
        		.addPathPatterns("/**");
        registry.addInterceptor(new SecurityInterceptor())
        		.addPathPatterns("/**")
        		.excludePathPatterns("/login/**");
        registry.addInterceptor(new CSRFHandlerInterceptor())
        		.addPathPatterns("/**")
        		.excludePathPatterns("/login/**");
    }
	
//	@Bean
//	public LocaleResolver localeResolver() {
//		SessionLocaleResolver slr = new SessionLocaleResolver();
//		slr.setDefaultLocale(locale);
//		return slr;
//	}
//	
//	@Bean
//	public LocaleChangeInterceptor localeChangeInterceptor() {
//		LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
//		lci.setParamName("lang");
//		return lci;
//	}

//	@Bean
//	public ReloadableResourceBundleMessageSource messageSource(){
//		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
//		messageSource.setBasename("classpath:message/messages");
//		messageSource.setDefaultEncoding("UTF-8");
//		//messageSource.setCacheSeconds(messagesCacheSeconds);
//		return messageSource;
//	}
//
//	@Bean
//	public MessageSourceAccessor getMessageSourceAccessor(){
//		ReloadableResourceBundleMessageSource m = messageSource();
//		return new MessageSourceAccessor(m);
//	}
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		log.info(" @@@ ServletConfig addResourceHandlers @@@");
		registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
	}

	@Bean
	public SimpleMappingExceptionResolver exceptionResolver() {
		log.info(" @@@ ServletConfig exceptionResolver @@@");
		
		SimpleMappingExceptionResolver exceptionResolver = new SimpleMappingExceptionResolver();
	 
		exceptionResolver.setOrder(1);
		exceptionResolver.setDefaultErrorView("/error/default-error");
		
		Properties exceptionMappings = new Properties();
	 	exceptionMappings.put("com.gaia3d.exception.BusinessLogicException", "/error/business-error");
	 	exceptionMappings.put("java.lang.RuntimeException", "/error/runtime-error");
	 	exceptionResolver.setExceptionMappings(exceptionMappings);
	 	
	 	Properties statusCodes = new Properties();
	 	statusCodes.put("/WEB-INF/views/error/error.jsp", "404");
	 	exceptionResolver.setStatusCodes(statusCodes);
	 	
	 	return exceptionResolver;
	}
	 
	@Bean
	public InternalResourceViewResolver viewResolver() {
		log.info(" @@@ ServletConfig viewResolver @@@");
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setPrefix("/WEB-INF/views");
		viewResolver.setSuffix(".jsp");
		viewResolver.setOrder(3);
		
		return viewResolver;
	}
	
	@Bean
	public RequestDataValueProcessor requestDataValueProcessor() {
		log.info(" @@@ ServletConfig requestDataValueProcessor @@@ ");
		return new CSRFRequestDataValueProcessor();
	}
}