package ir.sajjadboodaghi.niraa_admin.Models;

/**
 * Created by Sajjad on 05/14/2018.
 */

public class Report {
    private int id;
    private String reporterNumber;
    private int itemId;
    private String description;

    public Report(int id, String reporterNumber, int itemId, String description) {
        this.id = id;
        this.reporterNumber = reporterNumber;
        this.itemId = itemId;
        this.description = description;
    }

    public int getId() {
        return id;
    }
    public String getReporterNumber() {
        return reporterNumber;
    }
    public int getItemId() {
        return itemId;
    }
    public String getDescription() {
        return description;
    }
}
