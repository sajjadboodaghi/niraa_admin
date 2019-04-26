package ir.sajjadboodaghi.niraa_admin.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ir.sajjadboodaghi.niraa_admin.Adapters.StoryAdapter;
import ir.sajjadboodaghi.niraa_admin.Handlers.AlertHandler;
import ir.sajjadboodaghi.niraa_admin.Handlers.Interfaces.AlertNoticeCallback;
import ir.sajjadboodaghi.niraa_admin.Handlers.Interfaces.AlertProblemCallback;
import ir.sajjadboodaghi.niraa_admin.Handlers.Interfaces.AlertQuestionCallback;
import ir.sajjadboodaghi.niraa_admin.Handlers.Interfaces.RequestCallback;
import ir.sajjadboodaghi.niraa_admin.Handlers.RequestHandler;
import ir.sajjadboodaghi.niraa_admin.Handlers.SharedPrefManager;
import ir.sajjadboodaghi.niraa_admin.R;
import ir.sajjadboodaghi.niraa_admin.Urls;

public class StoryActivity extends AppCompatActivity {

    int storyId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // make layout full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_story);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        Intent intent = getIntent();
        storyId = intent.getIntExtra("storyId", 0);
        Picasso.with(this).load(Urls.STORIES_BASE_URL + "large_" + storyId + ".jpg")
                .error(ResourcesCompat.getDrawable(getResources(), R.drawable.default_image, null))
                .into(imageView);
        Button userPhoneNumberButton = (Button) findViewById(R.id.userPhoneNumberButton);
        final String phoneNumber = intent.getStringExtra("phoneNumber");
        userPhoneNumberButton.setText(phoneNumber);
        userPhoneNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userItemsIntent = new Intent(StoryActivity.this, UserItemsActivity.class);
                userItemsIntent.putExtra("phoneNumber", phoneNumber);
                startActivity(userItemsIntent);
            }
        });
    }

    public void deleteStory(View v) {
        String message = getResources().getString(R.string.want_delete_story);
        AlertHandler.alertQuestionMaker(this, message, new AlertQuestionCallback() {
            @Override
            public void yes() {
                sendDeleteStoryRequest();
            }

            @Override
            public void no() {

            }
        });
    }
    private void sendDeleteStoryRequest() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.progress_dialog_connecting));
        progressDialog.setCancelable(false);
        progressDialog.show();
        RequestHandler.makeRequest(this, "POST", Urls.DELETE_STORY, deleteStoryParams(), new RequestCallback() {
            @Override
            public void onNoInternetAccess() {
                progressDialog.dismiss();
                String message = getResources().getString(R.string.alert_dialog_message_no_internet_access);
                AlertHandler.alertProblemMaker(StoryActivity.this, message, new AlertProblemCallback() {
                    @Override
                    public void tryAgain() {
                        sendDeleteStoryRequest();
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
                    String message = jsonResponse.getString("message");
                    boolean error = jsonResponse.getBoolean("error");
                    if(error) {
                        AlertHandler.alertNoticeMaker(StoryActivity.this, message, new AlertNoticeCallback() {
                            @Override
                            public void okay() {
                                finish();
                            }
                        });
                    } else {
                        AlertHandler.alertNoticeMaker(StoryActivity.this, message, new AlertNoticeCallback() {
                            @Override
                            public void okay() {
                                finish();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    String message = getResources().getString(R.string.alert_dialog_message_problem_in_server);
                    AlertHandler.alertNoticeMaker(StoryActivity.this, message, new AlertNoticeCallback() {
                        @Override
                        public void okay() {

                        }
                    });
                }

            }

            @Override
            public void onRequestError() {
                progressDialog.dismiss();
                String message = getResources().getString(R.string.alert_dialog_message_problem_with_connecting);
                AlertHandler.alertProblemMaker(StoryActivity.this, message, new AlertProblemCallback() {
                    @Override
                    public void tryAgain() {
                        sendDeleteStoryRequest();
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        });
    }
    private Map<String, String> deleteStoryParams() {
        Map<String, String> params = new HashMap<>();
        params.put("username", SharedPrefManager.getInstance(StoryActivity.this).getUsername());
        params.put("password", SharedPrefManager.getInstance(StoryActivity.this).getPassword());
        params.put("story_id", String.valueOf(storyId));
        return params;
    }
    public void closeStory(View v) {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
