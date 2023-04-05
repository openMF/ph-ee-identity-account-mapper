package org.mifos.identityaccountmapper;

import org.mifos.identityaccountmapper.util.AbstractApplicationConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IdentityAccountMapperApplication extends AbstractApplicationConfiguration {

	public static void main(String[] args) {
		SpringApplication.run(IdentityAccountMapperApplication.class, args);
	}
}
