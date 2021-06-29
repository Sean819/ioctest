package com.ioc.learn.service;

import com.ioc.learn.factory.ZoneIdFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class MailService {
//    private ZoneId zoneId = ZoneId.systemDefault();
//    @Autowired
//    @Qualifier("utc8")
//    public void setZoneId(ZoneId zoneId) {
//        this.zoneId = zoneId;
//    }
    private ZoneIdFactoryBean zoneIdFactoryBean;
    @Autowired
    public void setZoneIdFactoryBean(ZoneIdFactoryBean zoneIdFactoryBean) {
        this.zoneIdFactoryBean = zoneIdFactoryBean;
    }




    public String getTime() throws Exception {
        return ZonedDateTime.now(this.zoneIdFactoryBean.getObject()).format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
    }

    public void sendLoginMail(User user) throws Exception {
        System.err.println(String.format("Hi, %s! You are logged in at %s", user.getName(), getTime()));
    }

    public void sendRegistrationMail(User user) {
        System.err.println(String.format("Welcome, %s!", user.getName()));

    }
    @PostConstruct
    public void init() throws Exception {
        System.out.println("Init mail service with zoneId = " + this.zoneIdFactoryBean.getObject());
    }

    @PreDestroy
    public void shutdown() {
        System.out.println("Shutdown mail service");
    }
}
