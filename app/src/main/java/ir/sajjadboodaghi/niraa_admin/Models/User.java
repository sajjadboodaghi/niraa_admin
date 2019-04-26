package ir.sajjadboodaghi.niraa_admin.Models;

/**
 * Created by Sajjad on 05/14/2018.
 */

public class User {
    private int id;
    private String phoneNumber;

    public User(int id, String phoneNumber) {
        this.id = id;
        this.phoneNumber = phoneNumber;
    }

    public int getId() {
        return this.id;
    }
    public String getPhoneNumber() {
        return this.phoneNumber;
    }
}
