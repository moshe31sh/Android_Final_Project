package ots.il.ac.shenkar.ots.common;

/**
 * Created by moshe on 21-02-16.
 */
public class User {
    private String userName;
    private String userLName;
    private String userPassword;
    private String userMail;
    private String userPhone;
    private boolean isManager;
    private String manager;
    private String userId;

    /**
     * default CTOR
     */
    public User(){}


    public User(String name , String lName ,String pass , String mail , String userPhone){
        setUserName(name);
        setUserLName(lName);
        setPassword(pass);
        setMail(mail);
        setPassword(pass);
        setUserPhone(userPhone);
        setIsManager(false);

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return userPassword;
    }

    public void setPassword(String password) {
        this.userPassword = password;
    }

    public String getMail() {
        return userMail;
    }

    public void setMail(String mail) {
        this.userMail = mail;
    }

    public String getUserLName() {
        return userLName;
    }

    public void setUserLName(String userLNamel) {
        this.userLName = userLNamel;
    }

    public boolean getIsManager() {
        return isManager;
    }

    public void setIsManager(boolean isManager) {
        this.isManager = isManager;
    }


    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }
}
