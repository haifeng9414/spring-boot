package org.springframework.boot.demo.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.demo.service.DemoService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
// 根据application.properties的配置创建DemoProperties bean
@EnableConfigurationProperties(DemoProperties.class)
public class DemoAutoConfiguration {

	@Resource
	private DemoProperties demoProperties;

	@Bean
	@ConditionalOnMissingBean(DemoService.class)
	public DemoService DemoService() {
		DemoService demoService = new DemoService();
		demoService.setProp(demoProperties.getProp());
		return demoService;
	}

}