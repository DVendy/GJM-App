package com.example.adi.gjm_app;

/**
 * Created by Adi on 6/8/2015.
 */
public class User {
    private int id;
    private String name, username, password, email, phone;

    public User(int id, String username, String password, String name, String email, String phone) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public User(String _username, String _password)
    {
        this.username = _username;
        this.password = _password;
    }

    public boolean isValid(String _username, String _password)
    {
        String md5Pass = getHashMD5(_password);
        if(_username.equals(this.username) && md5Pass.equals(this.password))
            return true;
        else
            return false;
    }

    public boolean isSet()
    {
        if(this.username.equals("") && this.password.equals(""))
            return false;
        else
            return true;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = getHashMD5(password);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getHashMD5(String string) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(string.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            return null;
        }
    }
}
