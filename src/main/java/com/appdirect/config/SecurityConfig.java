package com.appdirect.config;

import com.appdirect.client.AppDirectClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth.common.signature.SharedConsumerSecretImpl;
import org.springframework.security.oauth.consumer.BaseProtectedResourceDetails;
import org.springframework.security.oauth.consumer.ProtectedResourceDetails;
import org.springframework.security.oauth.consumer.client.OAuthRestTemplate;
import org.springframework.web.client.RestTemplate;
import sun.net.www.protocol.http.logging.HttpLogFormatter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter{

    @Value("${oauth.consumer.key}")
    private String consumerKey;

    @Value("${oauth.consumer.secret}")
    private String consumerSecret;

    @Value("${app.mock}")
    private Boolean appMock;

    @Value("${spring.rest.user}")
    private String user;

    @Value("${spring.rest.password}")
    private String password;

    @Bean
    public RestTemplate restTemplate(){
        if(appMock){

            return new RestTemplate();
        }else {
            final BaseProtectedResourceDetails resource = new BaseProtectedResourceDetails();
            resource.setConsumerKey(consumerKey);
            resource.setSharedSecret(new SharedConsumerSecretImpl(consumerSecret));
            return new OAuthRestTemplate(resource);
        }
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/", "/proxy/api/events/**").permitAll()
                .anyRequest().authenticated();

        http.httpBasic();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder authManagerBuilder) throws Exception {
        authManagerBuilder.inMemoryAuthentication()
                .withUser(user).password(password).roles("USER");
    }
}
