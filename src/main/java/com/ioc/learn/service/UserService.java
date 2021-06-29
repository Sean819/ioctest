package com.ioc.learn.service;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserService {

    private static MailService mailService;
    private static HikariDataSource hikariDataSource;
    @Autowired
    public void setMailService(MailService mailService) {
        UserService.mailService = mailService;
    }
    @Autowired
    public void setHikariDataSource(HikariDataSource dataSource) {
        UserService.hikariDataSource = dataSource;
    }

    private static List<User> users = new ArrayList<>(); // tom


    public static User login(String email, String password) throws SQLException {
//        for (User user : users) {
//            if (user.getEmail().equalsIgnoreCase(email) && user.getPassword().equals(password)) {
//                mailService.sendLoginMail(user);
//                return user;
//            }
//        }
        try(Connection conn = hikariDataSource.getConnection()){
            try(PreparedStatement ps = conn.prepareStatement("select * from dbuser where dbemail=? and dbpassword=?;")){
                ps.setObject(1,email);
                ps.setObject(2,password);
                try(ResultSet rs = ps.executeQuery()){
                    if(rs.next()){
                        User user = new User(rs.getInt("id"),rs.getString("dbemail"),
                                rs.getString("dbpassword"),rs.getString("dbname"));
                        mailService.sendLoginMail(user);
                        System.out.println(user);
                        return user;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        throw new RuntimeException("login failed.");
    }

    public User getUser(long id) {
        return this.users.stream().filter(user -> user.getId() == id).findFirst().orElseThrow();
    }

    public User register(String email, String password, String name) {
        users.forEach((user) -> {
            if (user.getEmail().equalsIgnoreCase(email)) {
                throw new RuntimeException("email exist.");
            }
        });
        User user = new User(users.stream().mapToLong(u -> u.getId()).max().getAsLong() + 1, email, password, name);
        users.add(user);
        mailService.sendRegistrationMail(user);
        return user;
    }


}
