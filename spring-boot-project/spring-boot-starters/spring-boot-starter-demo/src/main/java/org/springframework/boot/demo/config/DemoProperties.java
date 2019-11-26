package org.springframework.boot.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "demo")
public class DemoProperties {

	private static final String DEFAULT_PROP = "hello world";

	private String prop = DEFAULT_PROP;

	public String getProp() {
		return prop;
	}

	public void setProp(String prop) {
		this.prop = prop;
	}

}