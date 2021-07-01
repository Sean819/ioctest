package com.ioc.learn.service;

import com.ioc.learn.annotation.MetricTime;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserService {

    private MailService mailService;
    private HikariDataSource hikariDataSource;
//    @Autowired
//    public void setMailService(MailService mailService) {
//        UserService.mailService = mailService;
//    }
//    @Autowired
//    public void setHikariDataSource(HikariDataSource dataSource) {
//        UserService.hikariDataSource = dataSource;
//    }

    public UserService(@Autowired MailService mailService, @Autowired HikariDataSource hikariDataSource) {
        this.mailService = mailService;
        this.hikariDataSource = hikariDataSource;
    }

    private static List<User> users = new ArrayList<>(); // tom


    public User login(String email, String password) throws SQLException {
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

    @MetricTime("register")
    public User register(String email, String password, String name) throws SQLException {
//        users.forEach((user) -> {
//            if (user.getEmail().equalsIgnoreCase(email)) {
//                throw new RuntimeException("email exist.");
//            }
//        });
//        User user = new User(users.stream().mapToLong(u -> u.getId()).max().getAsLong() + 1, email, password, name);
//        users.add(user);
//        mailService.sendRegistrationMail(user);
        User user;
        try (Connection conn = hikariDataSource.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("select * from dbuser where dbemail=?;");
            ps.setObject(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    ps = conn.prepareStatement("insert into dbuser (dbemail, dbpassword, dbname) values (?,?,?)", Statement.RETURN_GENERATED_KEYS);
                    ps.setObject(1, email);
                    ps.setObject(2, password);
                    ps.setObject(3, name);
                    ps.executeUpdate();
                    try (ResultSet rs1 = ps.getGeneratedKeys()) {
                        if (rs1.next()) {
                            System.out.println("333333333");
                            long id = rs1.getLong(1); // 注意：索引从1开始
                            user = new User(4, email, password, name);
                            mailService.sendRegistrationMail(user);
                            return user;
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    throw new RuntimeException("email exist.");
                }
            } finally {
                ps.close();
            }
        }
        throw new RuntimeException("register failed.");
    }

}
