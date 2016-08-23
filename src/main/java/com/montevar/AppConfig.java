package com.montevar;

import javax.annotation.PreDestroy;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Sprint Configuration class. Creates beans for dependency injection. Most of
 * the beans are autowired automatically from their class names because we use
 * ComponentScan.
 *
 */
@Configuration
@ComponentScan(basePackages = "com.montevar")
public class AppConfig {
	private JavaSparkContext sc;

	@Bean
	public JavaSparkContext javaSparkContext() {
		SparkConf conf = new SparkConf().setAppName("monte").setMaster("local");
		JavaSparkContext sc = new JavaSparkContext(conf);
		this.sc = sc;
		return sc;
	}

	@PreDestroy
	public void closeSparkConext() {
		sc.close();
	}

}
