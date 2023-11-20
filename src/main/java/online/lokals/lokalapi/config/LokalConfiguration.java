package online.lokals.lokalapi.config;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;

@Configuration
public class LokalConfiguration implements WebMvcConfigurer {

    public static final String LOKAL_USER_ID_HEADER = "X-LOKAL-USER";
    public static final String LOKAL_USER_TOKEN_HEADER = "X-LOKAL-TOKEN";

    public static final Locale[] LOKAL_AVAILABLE_LOCALES = new Locale[] {new Locale("tr", "TR")};
    public static final Locale DEFAULT_LOCALE = LOKAL_AVAILABLE_LOCALES[0];

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setDefaultLocale(DEFAULT_LOCALE);
        return messageSource;
    }

    @Bean
    public LocalValidatorFactoryBean validator() {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource());
        return bean;
    }

    @Bean
    public LocaleResolver localeResolver() {
        FixedLocaleResolver localeResolver = new FixedLocaleResolver();
        localeResolver.setDefaultLocale(DEFAULT_LOCALE);
        return localeResolver;
    }

//    @Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**")
//                        .allowedHeaders(LOKAL_USER_ID_HEADER, LOKAL_USER_TOKEN_HEADER)
//                        .allowedMethods("*")
//                        .allowedOrigins("http://localhost:19006", "http://192.168.2.27:8080");
//            }
//        };
//    }

}
