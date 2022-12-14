package com.portfolio.configuration;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import com.portfolio.configuration.servlet.handler.BaseHandlerInterceptor;
import com.portfolio.framework.data.web.MySQLPageRequestHandleMethodArgumentResolver;
import com.portfolio.mvc.domain.BaseCodeLabelEnum;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {
	
	@Autowired
	private GlobalConfig config;
	
	private static final String WINDOWS_FILE = "file:///";
	private static final String LINUX_FILE = "file:";

	@Bean
	public ReloadableResourceBundleMessageSource messageSource() {
		ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
		source.setBasename("classpath:/messages/message_com.properties");
		source.setDefaultEncoding("UTF-8");
		source.setCacheSeconds(60);
		source.setDefaultLocale(Locale.KOREAN);
		source.setUseCodeAsDefaultMessage(true);
		return source;
	}

	@Bean
	public BaseHandlerInterceptor baseHandlerInterceptor() {
		return new BaseHandlerInterceptor();
	}

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		SimpleModule simpleModule = new SimpleModule();
		simpleModule.addSerializer(BaseCodeLabelEnum.class, new BaseCodeLabelEnumJsonSerializer());
		objectMapper.registerModule(simpleModule);
		return objectMapper;
	}

	@Bean
	public MappingJackson2JsonView mappingJackson2JsonView() {
		MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
		jsonView.setContentType(MediaType.APPLICATION_JSON_VALUE);
		jsonView.setObjectMapper(objectMapper());
		return jsonView;
	}
	
	@Bean
	public GlobalConfig config() {
		return new GlobalConfig();
	}
	
	@Bean
	public FilterRegistrationBean<SitemeshConfiguration> sitemeshBean() {
		FilterRegistrationBean<SitemeshConfiguration> filter = new FilterRegistrationBean<SitemeshConfiguration>();
		filter.setFilter(new SitemeshConfiguration());
		return filter;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(baseHandlerInterceptor());
	}
	
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		// ????????? ????????? ??????
		resolvers.add(new MySQLPageRequestHandleMethodArgumentResolver());
	}
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		//????????? ?????? static resource ?????? ??????
		String resourcePattern = config.getUploadResourcePath() + "**";
		//??????(????????? ??????)
		if(config.isLocal()) {
			registry.addResourceHandler(resourcePattern)
			.addResourceLocations("file:///" + config.getUploadFilePath());
		} else {
			//????????? ?????? ?????????
			registry.addResourceHandler(resourcePattern)
			.addResourceLocations("file:" + config.getUploadFilePath());
		}
	}
	
}
