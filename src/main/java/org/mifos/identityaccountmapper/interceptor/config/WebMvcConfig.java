package org.mifos.identityaccountmapper.interceptor.config;

import org.mifos.identityaccountmapper.interceptor.ValidatorInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
@Primary
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    ValidatorInterceptor validatorInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(validatorInterceptor).addPathPatterns("/beneficiary");
        registry.addInterceptor(validatorInterceptor).addPathPatterns("/accountLookup");
    }
}
