package org.jasig.portlet.announcements;

import com.google.common.collect.ImmutableMap;
import org.hibernate.SessionFactory;
import org.jasig.portlet.announcements.model.Topic;
import org.jasig.portlet.announcements.mvc.IViewNameSelector;
import org.jasig.portlet.announcements.mvc.UserAgentViewNameSelector;
import org.jasig.portlet.announcements.service.AnnouncementCleanupThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate5.support.OpenSessionInViewInterceptor;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import javax.persistence.EntityManagerFactory;
import java.util.Arrays;

@SpringBootApplication
@EnableJpaRepositories
@EnableTransactionManagement
@EnableCaching
@PropertySource("classpath:/configuration.properties")
@ComponentScan(basePackages = {"org.jasig.portlet.announcements"},
        excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX,
                pattern = "org.jasig.portlet.announcements.mvc.*"))
public class AnnouncementsApplication extends WebMvcConfigurerAdapter {

    private static final Logger log = LoggerFactory.getLogger(AnnouncementsApplication.class);

    @Autowired
    private Environment env;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(AnnouncementsApplication.class, args);
        String[] beans = context.getBeanDefinitionNames();
        Arrays.sort(beans);
        for (String bean : beans) {
            System.out.println(bean);
        }
    }

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("i18n/messages", "i18n/validation");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Bean
    ViewResolver viewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setCache(true);
        resolver.setPrefix("/WEB-INF/jsp/");
        resolver.setSuffix(".jsp");
        resolver.setViewClass(JstlView.class);
        return resolver;
    }

    @Bean
    public OpenSessionInViewInterceptor openSessionInViewInterceptor() {
        OpenSessionInViewInterceptor interceptor = new OpenSessionInViewInterceptor();
        interceptor.setSessionFactory(entityManagerFactory.unwrap(SessionFactory.class));
        return interceptor;
    }

    @Bean(name = "emergencyTopic")
    public Topic getEmergencyTopic() {

        Topic topic = new Topic();
        topic.setCreator("automatic");
        topic.setTitle("EMERGENCY");
        topic.setDescription("Do not edit this topic!");
        topic.setAllowRss(false);
        topic.setSubscriptionMethod(Topic.EMERGENCY);
        topic.getAudience().add("Everyone");
        topic.getAdmins().add("Portal_Administrators");
        return topic;
    }

    @Bean
    public Integer historyExpireThreshold() {
        return env.getProperty("history.expire.threshold", Integer.class);
    }

    @Bean(initMethod = "start", destroyMethod = "stopThread")
    /** Periodically evict expired announcements */
    public AnnouncementCleanupThread cleanupThread() {
        AnnouncementCleanupThread cleanupThread = new AnnouncementCleanupThread();
        cleanupThread.setHourToCheck(3);
        cleanupThread.setMinuteToCheck(0);
        cleanupThread.setExpireThreshold(historyExpireThreshold());
        return cleanupThread;
    }

    @Bean
    public IViewNameSelector viewNameSelector() {
        UserAgentViewNameSelector selector = new UserAgentViewNameSelector();
        selector.setUserAgentMappings(
                ImmutableMap.of(
                        ".*iPhone.*", ".mobile",
                        ".*Android.*", ".mobile",
                        ".*Safari.*Pre.*", ".mobile",
                        ".*Nokia.*AppleWebKit.*", ".mobile"
                ));
        return selector;
    }
}
