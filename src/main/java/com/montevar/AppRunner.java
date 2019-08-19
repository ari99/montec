package com.montevar;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 
 * Sets up the Spring Context and runs the {@link App} class.
 *
 */
@Named
public class AppRunner {

	public static void main(String[] args) {

		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
		AppRunner appRunner = context.getBean(AppRunner.class);
		appRunner.run();

		context.close();
	}

	private App app;

	@Inject
	public void setApp(App app) {
		this.app = app;
	}

	/**
	 * Starts the app.
	 */
	public void run() {
		app.start();
	}

}
