package edu.xiao.webservice.bean;

import edu.xiao.webservice.model.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.UUID;

public class UserResponseBean {
    private UUID id;
    private String first_name;
    private String last_name;
    private String username;
    private String account_created;
    private String account_updated;

    static public UserResponseBean createBeanFromUser(User user) {
        UserResponseBean res = new UserResponseBean();
        res.setId(user.getId());
        res.setFirst_name(user.getFirstName());
        res.setLast_name(user.getLastName());
        res.setUsername(user.getUsername());

        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        df.setTimeZone(tz);
        res.setAccount_created(df.format(user.getAccountCreated()));
        res.setAccount_updated(df.format(user.getAccountUpdated()));
        return res;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getAccount_created() {
        return account_created;
    }

    public void setAccount_created(String account_created) {
        this.account_created = account_created;
    }

    public String getAccount_updated() {
        return account_updated;
    }

    public void setAccount_updated(String account_updated) {
        this.account_updated = account_updated;
    }
}
