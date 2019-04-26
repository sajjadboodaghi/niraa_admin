package ir.sajjadboodaghi.niraa_admin.Models;

/**
 * Created by Sajjad on 05/07/2018.
 */

public class Suggest {
    int id;
    String phoneNumber;
    String niraaVersion;
    String androidVersion;
    String description;

    public Suggest(int id, String phoneNumber, String niraaVersion, String androidVersion, String description) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.niraaVersion = niraaVersion;
        this.androidVersion = androidVersion;
        this.description = description;
    }

    public int getId() {
        return id;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public String getNiraaVersion() { return niraaVersion; }
    public String getAndroidVersion() { return androidVersion; }
    public String getDescription() {
        return description;
    }
}
