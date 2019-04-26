package ir.sajjadboodaghi.niraa_admin.Models;

/**
 * Created by Sajjad on 10/09/2018.
 */

public class Story {
    private int id;
    private String phoneNumber;
    public Story(int id, String phoneNumber) {
        this.id = id;
        this.phoneNumber = phoneNumber;
    }
    public int getId() {
        return id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

}
