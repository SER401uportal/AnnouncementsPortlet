package org.jasig.portlet.announcements.mvc.config;

import org.jasig.portlet.announcements.mvc.servlet.AjaxApproveController;
import org.jasig.portlet.announcements.mvc.servlet.RssFeedController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableWebMvc
@SuppressWarnings("unused")
public class WebMvcConfig extends WebMvcConfigurerAdapter {
    
    @Bean
    public AjaxApproveController ajaxApproveController() {
        return new AjaxApproveController();
    }

    @Bean
    public RssFeedController rssFeedController() {
        return new RssFeedController();
    }
}
