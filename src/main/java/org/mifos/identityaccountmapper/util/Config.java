package org.mifos.identityaccountmapper.util;

import org.mifos.pheeidaccountvalidatorimpl.gsmaconnector.GSMAAccountValidation;
import org.mifos.pheeidaccountvalidatorimpl.mojaloopconnector.MojaloopAccountValidation;
import org.mifos.pheeidaccountvalidatorimpl.service.AccountValidationService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class Config {
}
