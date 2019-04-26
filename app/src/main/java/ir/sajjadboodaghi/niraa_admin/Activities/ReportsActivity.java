package ir.sajjadboodaghi.niraa_admin.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ir.sajjadboodaghi.niraa_admin.Handlers.AlertHandler;
import ir.sajjadboodaghi.niraa_admin.Handlers.Interfaces.AlertProblemCallback;
import ir.sajjadboodaghi.niraa_admin.R;
import ir.sajjadboodaghi.niraa_admin.Models.Report;
import ir.sajjadboodaghi.niraa_admin.Adapters.ReportAdapter;
import ir.sajjadboodaghi.niraa_admin.Handlers.Interfaces.RequestCallback;
import ir.sajjadboodaghi.niraa_admin.Handlers.RequestHandler;
import ir.sajjadboodaghi.niraa_admin.Handlers.SharedPrefManager;
import ir.sajjadboodaghi.niraa_admin.Urls;

public class ReportsActivity extends AppCompatActivity {

    List<Report> reports;
    RecyclerView recyclerView;
    ReportAdapter reportAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        Toolbar toolbar = (Toolbar) findViewById(R.id.reportsToolbar);
        setSupportActionBar(toolbar);

        reports = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        reportAdapter = new ReportAdapter(reports, this);
        recyclerView.setAdapter(reportAdapter);
        getReports();
    }

    private void getReports() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.progress_dialog_connecting));
        progressDialog.setCancelable(false);
        progressDialog.show();
        RequestHandler.makeRequest(this, "POST", Urls.GET_REPORTS, getReportsParams(), new RequestCallback() {
            @Override
            public void onNoInternetAccess() {
                progressDialog.dismiss();
                String message = getResources().getString(R.string.alert_dialog_message_no_internet_access);
                AlertHandler.alertProblemMaker(ReportsActivity.this, message, new AlertProblemCallback() {
                    @Override
                    public void tryAgain() {
                        getReports();
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }

            @Override
            public void onSuccess(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    Boolean error = jsonResponse.getBoolean("error");
                    if(error) {
                        String message = jsonResponse.getString("message");
                        AlertHandler.alertProblemMaker(ReportsActivity.this, message, new AlertProblemCallback() {
                            @Override
                            public void tryAgain() {
                                getReports();
                            }

                            @Override
                            public void cancel() {

                            }
                        });
                        return;
                    }

                    JSONArray jsonArraySuggests = jsonResponse.getJSONArray("reports");
                    if(jsonArraySuggests.length() > 0) {
                        for(int i = 0; i < jsonArraySuggests.length(); i++) {
                            JSONObject object = jsonArraySuggests.getJSONObject(i);
                            Report report = new Report(
                                    object.getInt("id"),
                                    object.getString("reporter_number"),
                                    object.getInt("item_id"),
                                    object.getString("description")
                            );

                            reports.add(report);
                        }
                        reportAdapter.notifyDataSetChanged();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    String message = getResources().getString(R.string.alert_dialog_message_problem_in_server);
                    AlertHandler.alertProblemMaker(ReportsActivity.this, message, new AlertProblemCallback() {
                        @Override
                        public void tryAgain() {
                            getReports();
                        }

                        @Override
                        public void cancel() {

                        }
                    });
                }
            }

            @Override
            public void onRequestError() {
                progressDialog.dismiss();
                String message = getResources().getString(R.string.alert_dialog_message_problem_with_connecting);
                AlertHandler.alertProblemMaker(ReportsActivity.this, message, new AlertProblemCallback() {
                    @Override
                    public void tryAgain() {
                        getReports();
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        });
    }

    private Map<String, String> getReportsParams() {
        Map<String, String> params = new HashMap<>();
        params.put("username", SharedPrefManager.getInstance(ReportsActivity.this).getUsername());
        params.put("password", SharedPrefManager.getInstance(ReportsActivity.this).getPassword());
        return params;
    }

    public void deleteReport(final View v) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(getResources().getString(R.string.alert_dialog_title_danger));
        alertDialogBuilder.setMessage(getResources().getString(R.string.alert_dialog_message_delete_report));
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setNegativeButton(getResources().getString(R.string.alert_dialog_button_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });
        alertDialogBuilder.setPositiveButton(getResources().getString(R.string.alert_dialog_button_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final int position = (int) v.getTag();

                final ProgressDialog progressDialog = new ProgressDialog(ReportsActivity.this);
                progressDialog.setMessage(getResources().getString(R.string.progress_dialog_connecting));
                progressDialog.setCancelable(false);
                progressDialog.show();
                RequestHandler.makeRequest(ReportsActivity.this, "POST", Urls.DELETE_REPORT, deleteReportParams(position), new RequestCallback() {
                    @Override
                    public void onNoInternetAccess() {
                        progressDialog.dismiss();
                        String message = getResources().getString(R.string.alert_dialog_message_no_internet_access);
                        AlertHandler.alertProblemMaker(ReportsActivity.this, message, new AlertProblemCallback() {
                            @Override
                            public void tryAgain() {
                                deleteReport(v);
                            }

                            @Override
                            public void cancel() {

                            }
                        });
                    }

                    @Override
                    public void onSuccess(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            Boolean error = jsonResponse.getBoolean("error");
                            String message = jsonResponse.getString("message");
                            if(error) {
                                AlertHandler.alertProblemMaker(ReportsActivity.this, message, new AlertProblemCallback() {
                                    @Override
                                    public void tryAgain() {
                                        deleteReport(v);
                                    }

                                    @Override
                                    public void cancel() {

                                    }
                                });
                                return;
                            }

                            AlertDialog.Builder alertDialogbuilder = new AlertDialog.Builder(ReportsActivity.this);
                            alertDialogbuilder.setTitle(getResources().getString(R.string.alert_dialog_title_message));
                            alertDialogbuilder.setMessage(message);
                            alertDialogbuilder.setCancelable(false);
                            alertDialogbuilder.setNeutralButton(getResources().getString(R.string.alert_dialog_button_understand), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {}
                            });
                            alertDialogbuilder.show();

                            reports.remove(position);
                            reportAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            String message = getResources().getString(R.string.alert_dialog_message_problem_in_server);
                            AlertHandler.alertProblemMaker(ReportsActivity.this, message, new AlertProblemCallback() {
                                @Override
                                public void tryAgain() {
                                    deleteReport(v);
                                }

                                @Override
                                public void cancel() {

                                }
                            });
                        }
                    }

                    @Override
                    public void onRequestError() {
                        progressDialog.dismiss();
                        String message = getResources().getString(R.string.alert_dialog_message_problem_with_connecting);
                        AlertHandler.alertProblemMaker(ReportsActivity.this, message, new AlertProblemCallback() {
                            @Override
                            public void tryAgain() {
                                deleteReport(v);
                            }

                            @Override
                            public void cancel() {

                            }
                        });
                    }
                });
            }
        });
        alertDialogBuilder.show();

    }

    private Map<String, String> deleteReportParams(int position) {
        Map<String, String> params = new HashMap<>();
        params.put("username", SharedPrefManager.getInstance(ReportsActivity.this).getUsername());
        params.put("password", SharedPrefManager.getInstance(ReportsActivity.this).getPassword());
        params.put("report_id", String.valueOf(reports.get(position).getId()));
        return params;
    }

    public void showReportedItem(final View v) {
        final int itemId = (int) v.getTag();

        final ProgressDialog progressDialog = new ProgressDialog(ReportsActivity.this);
        progressDialog.setMessage(getResources().getString(R.string.progress_dialog_connecting));
        progressDialog.setCancelable(false);
        progressDialog.show();

        RequestHandler.makeRequest(this, "POST", Urls.GET_ITEM, showReportParams(itemId), new RequestCallback() {
            @Override
            public void onNoInternetAccess() {
                progressDialog.dismiss();
                String message = getResources().getString(R.string.alert_dialog_message_no_internet_access);
                AlertHandler.alertProblemMaker(ReportsActivity.this, message, new AlertProblemCallback() {
                    @Override
                    public void tryAgain() {
                        showReportedItem(v);
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }

            @Override
            public void onSuccess(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    Boolean error = jsonResponse.getBoolean("error");
                    if(error) {
                        String message = jsonResponse.getString("message");
                        AlertHandler.alertProblemMaker(ReportsActivity.this, message, new AlertProblemCallback() {
                            @Override
                            public void tryAgain() {
                                showReportedItem(v);
                            }

                            @Override
                            public void cancel() {

                            }
                        });
                        return;
                    }

                    JSONObject jsonItem = jsonResponse.getJSONObject("item");
                    Intent intent = new Intent(ReportsActivity.this, ItemActivity.class);
                    intent.putExtra("itemId", jsonItem.getInt("id"));
                    intent.putExtra("itemPhoneNumber", jsonItem.getString("phone_number"));
                    intent.putExtra("itemTelegramId", jsonItem.getString("telegram_id"));
                    intent.putExtra("itemTitle", jsonItem.getString("title"));
                    intent.putExtra("itemDescription", jsonItem.getString("description"));
                    intent.putExtra("itemPrice", jsonItem.getString("price"));
                    intent.putExtra("itemPlace", jsonItem.getString("place"));
                    intent.putExtra("itemSubcatName", jsonItem.getString("subcat_name"));
                    intent.putExtra("itemSubcatId", jsonItem.getString("subcat_id"));
                    intent.putExtra("itemShamsi", jsonItem.getString("shamsi"));
                    intent.putExtra("itemTimestamp", jsonItem.getString("timestamp"));
                    intent.putExtra("itemImageCount", jsonItem.getInt("image_count"));
                    intent.putExtra("itemVerified", jsonItem.getInt("verified"));
                    startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                    String message = getResources().getString(R.string.alert_dialog_message_problem_in_server);
                    AlertHandler.alertProblemMaker(ReportsActivity.this, message, new AlertProblemCallback() {
                        @Override
                        public void tryAgain() {
                            showReportedItem(v);
                        }

                        @Override
                        public void cancel() {

                        }
                    });
                }
            }

            @Override
            public void onRequestError() {
                progressDialog.dismiss();
                String message = getResources().getString(R.string.alert_dialog_message_problem_with_connecting);
                AlertHandler.alertProblemMaker(ReportsActivity.this, message, new AlertProblemCallback() {
                    @Override
                    public void tryAgain() {
                        showReportedItem(v);
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        });


    }

    private Map<String, String> showReportParams(int itemId) {
        Map<String, String> params = new HashMap<>();
        params.put("username", SharedPrefManager.getInstance(ReportsActivity.this).getUsername());
        params.put("password", SharedPrefManager.getInstance(ReportsActivity.this).getPassword());
        params.put("itemId", String.valueOf(itemId));
        return params;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.back, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menuBack) {
            finish();
        }
        return true;
    }
}
