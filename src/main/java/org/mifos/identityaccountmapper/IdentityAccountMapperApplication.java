package org.mifos.identityaccountmapper;

import org.mifos.identityaccountmapper.util.AbstractApplicationConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@EnableCaching
@SpringBootApplication
@ComponentScan("org.mifos.pheeidaccountvalidatorimpl")
public class IdentityAccountMapperApplication extends AbstractApplicationConfiguration {
	public static void main(String[] args) {
		SpringApplication.run(IdentityAccountMapperApplication.class, args);
	}
}
