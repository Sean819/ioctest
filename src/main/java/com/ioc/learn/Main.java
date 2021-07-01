package com.ioc.learn;

import com.ioc.learn.service.User;
import com.ioc.learn.service.UserService;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.sql.DataSource;
import java.io.*;
import java.sql.SQLException;
import java.time.ZoneId;

@ComponentScan
@Configuration
@EnableAspectJAutoProxy
public class Main {
    public static void main(String[] args) throws SQLException {
        ApplicationContext context = new AnnotationConfigApplicationContext(Main.class);//ioc容器的接口
        UserService userService = context.getBean(UserService.class);//从ioc容器中获取实例
        User user = userService.register("sean4@example.com", "password", "sean4");
        System.out.println(user.getName());
    }

    @Bean
    @Primary
    ZoneId createZoneOfZ(){
        return ZoneId.of("Z");
    }
    @Bean
    @Qualifier("utc8")
    ZoneId createZoneOfUTC8(){
        return ZoneId.of("UTC+08:00");
    }

    @Bean
//    @Qualifier("hikariDataSource")
    HikariDataSource getDataSource(){
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setJdbcUrl("jdbc:mysql://localhost:3306/learnjdbc");
        config.setUsername("root");
        config.setPassword("sean");
        return new HikariDataSource(config);
    }
}
