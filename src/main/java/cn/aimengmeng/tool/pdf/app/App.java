package cn.aimengmeng.tool.pdf.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@ComponentScan(basePackages="cn.aimengmeng.tool.pdf")

@EnableAutoConfiguration
@EnableAsync
@EnableScheduling
public class App {
	public static void main(String[] args) {
		SpringApplication springApplication = new SpringApplication(App .class);
		springApplication.run(args);
	}
}
