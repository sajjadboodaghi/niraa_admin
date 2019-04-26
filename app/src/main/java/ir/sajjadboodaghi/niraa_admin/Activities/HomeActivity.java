package ir.sajjadboodaghi.niraa_admin.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ir.sajjadboodaghi.niraa_admin.Adapters.StoryAdapter;
import ir.sajjadboodaghi.niraa_admin.Handlers.AlertHandler;
import ir.sajjadboodaghi.niraa_admin.Handlers.Interfaces.AlertNoticeCallback;
import ir.sajjadboodaghi.niraa_admin.Handlers.Interfaces.AlertProblemCallback;
import ir.sajjadboodaghi.niraa_admin.Models.Item;
import ir.sajjadboodaghi.niraa_admin.Adapters.ItemAdapter;
import ir.sajjadboodaghi.niraa_admin.Models.Story;
import ir.sajjadboodaghi.niraa_admin.R;
import ir.sajjadboodaghi.niraa_admin.Handlers.Interfaces.RequestCallback;
import ir.sajjadboodaghi.niraa_admin.Handlers.RequestHandler;
import ir.sajjadboodaghi.niraa_admin.Handlers.SharedPrefManager;
import ir.sajjadboodaghi.niraa_admin.Urls;

public class HomeActivity extends AppCompatActivity {

    Toolbar homeToolbar;
    SwipeRefreshLayout mSwipeRefreshLayout;

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    List<Item> items;

    private final static float INCH_TO_CENTIMETER_RATE = 2.54f;
    private final static int MIN_ITEM_WIDTH_IN_CENTIMETER = 4;
    private final static int ITEM_HEIGHT_IN_DP = 110;

    private boolean loading = true;
    int pastVisibleItems, visibleItemCount, totalItemCount;
    ProgressBar progressBar;

    int columnCount;
    int itemsCount;
    int last_id;

    List<Story> storiesList;
    StoryAdapter storyAdapter;
    RecyclerView storyRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        homeToolbar = (Toolbar) findViewById(R.id.homeToolbar);
        setSupportActionBar(homeToolbar);

        if(!SharedPrefManager.getInstance(this).isLogin()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        setupColumnCountAndItemCount();
        setupRecyclerView();
        setupSwipeRefreshLayout();
        storyRecyclerViewSetup();
        deleteStories7();
    }

    @Override
    protected void onResume() {
        super.onResume();
        whatsUp();
        items.clear();
        last_id = 0;
        loading = true;
        loadItems();
        expireStories24();
        loadStories();
    }

    private void whatsUp() {
            RequestHandler.makeRequest(this, "POST", Urls.WHATSUP, paramsForWhatsUp(), new RequestCallback() {
                @Override
                public void onNoInternetAccess() {
                    String message = getResources().getString(R.string.alert_dialog_message_no_internet_access);
                    AlertHandler.alertProblemMaker(HomeActivity.this, message, new AlertProblemCallback() {
                        @Override
                        public void tryAgain() {
                            whatsUp();
                        }

                        @Override
                        public void cancel() {

                        }
                    });
                }

                @Override
                public void onSuccess(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String message = jsonObject.getString("message");
                        boolean error = jsonObject.getBoolean("error");
                        if(error) {
                            AlertHandler.alertProblemMaker(HomeActivity.this, message, new AlertProblemCallback() {
                                @Override
                                public void tryAgain() {
                                    whatsUp();
                                }

                                @Override
                                public void cancel() {

                                }
                            });
                            return;
                        }
                        if(message.length() != 0) {
                            AlertHandler.alertNoticeMaker(HomeActivity.this, message, new AlertNoticeCallback() {
                                @Override
                                public void okay() {

                                }

                            });
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        String message = getResources().getString(R.string.alert_dialog_message_problem_in_server);
                        AlertHandler.alertProblemMaker(HomeActivity.this, message, new AlertProblemCallback() {
                            @Override
                            public void tryAgain() {
                                whatsUp();
                            }

                            @Override
                            public void cancel() {

                            }
                        });
                    }

                }

                @Override
                public void onRequestError() {
                    String message = getResources().getString(R.string.alert_dialog_message_problem_with_connecting);
                    AlertHandler.alertProblemMaker(HomeActivity.this, message, new AlertProblemCallback() {
                        @Override
                        public void tryAgain() {
                            whatsUp();
                        }

                        @Override
                        public void cancel() {

                        }
                    });
                }
            });
    }
    private Map<String, String> paramsForWhatsUp() {
        Map<String, String> params = new HashMap<>();
        params.put("username", SharedPrefManager.getInstance(HomeActivity.this).getUsername());
        params.put("password", SharedPrefManager.getInstance(HomeActivity.this).getPassword());
        return params;
    }

    private void loadItems() {
        progressBar.setVisibility(View.VISIBLE);
        RequestHandler.makeRequest(this, "POST", Urls.GET_WAITING_ITEMS, paramsForLoadItems(), new RequestCallback() {
            @Override
            public void onNoInternetAccess() {
                progressBar.setVisibility(View.GONE);
                String message = getResources().getString(R.string.alert_dialog_message_no_internet_access);
                AlertHandler.alertProblemMaker(HomeActivity.this, message, new AlertProblemCallback() {
                    @Override
                    public void tryAgain() {
                        loadItems();
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }

            @Override
            public void onSuccess(String response) {
                progressBar.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean error = jsonObject.getBoolean("error");
                    if(error) {
                        String message = jsonObject.getString("message");
                        AlertHandler.alertProblemMaker(HomeActivity.this, message, new AlertProblemCallback() {
                            @Override
                            public void tryAgain() {
                                loadItems();
                            }

                            @Override
                            public void cancel() {

                            }
                        });
                        return;
                    }

                    JSONArray newItems = jsonObject.getJSONArray("items");
                    if(newItems.length() > 0) {
                        for(int i = 0; i < newItems.length(); i++) {
                            JSONObject object = newItems.getJSONObject(i);
                            Item item = new Item(
                                    object.getInt("id"),
                                    object.getString("phone_number"),
                                    object.getString("telegram_id"),
                                    object.getString("title"),
                                    object.getString("description"),
                                    object.getString("price"),
                                    object.getString("place"),
                                    object.getString("subcat_name"),
                                    object.getInt("subcat_id"),
                                    object.getString("shamsi"),
                                    object.getString("timestamp"),
                                    object.getInt("image_count"),
                                    object.getInt("verified")
                            );

                            items.add(item);
                        }

                        adapter.notifyDataSetChanged();

                        // this prevent multi load items by scrolling down
                        loading = true;
                    } else {
                        Toast.makeText(HomeActivity.this, "...", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    String message = getResources().getString(R.string.alert_dialog_message_problem_in_server);
                    AlertHandler.alertProblemMaker(HomeActivity.this, message, new AlertProblemCallback() {
                        @Override
                        public void tryAgain() {
                            loadItems();
                        }

                        @Override
                        public void cancel() {

                        }
                    });
                }
            }

            @Override
            public void onRequestError() {
                progressBar.setVisibility(View.GONE);
                String message = getResources().getString(R.string.alert_dialog_message_problem_with_connecting);
                AlertHandler.alertProblemMaker(HomeActivity.this, message, new AlertProblemCallback() {
                    @Override
                    public void tryAgain() {
                        loadItems();
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        });

    }
    private Map<String, String> paramsForLoadItems() {
        Map<String, String> params = new HashMap<>();
        params.put("username", SharedPrefManager.getInstance(HomeActivity.this).getUsername());
        params.put("password", SharedPrefManager.getInstance(HomeActivity.this).getPassword());
        params.put("last_id", String.valueOf(last_id));
        params.put("items_count", String.valueOf(itemsCount));
        return params;
    }
    private void setupColumnCountAndItemCount() {
        // we use this variable for getting width, height,...
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float widthInInch = metrics.widthPixels / (float) metrics.densityDpi;
        int widthInCentimeter = (int) (widthInInch * INCH_TO_CENTIMETER_RATE);
        columnCount = widthInCentimeter / MIN_ITEM_WIDTH_IN_CENTIMETER;


        int itemHeightInPixelForCurrentDisplay = (int) Math.ceil(metrics.density * ITEM_HEIGHT_IN_DP);
        int numberOfItemRows = (int) Math.floor(metrics.heightPixels / itemHeightInPixelForCurrentDisplay);
        itemsCount = columnCount * numberOfItemRows;
        last_id = 0;
    }
    private void setupRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        final GridLayoutManager mGridLayoutManager = new GridLayoutManager(this, columnCount);
        recyclerView.setLayoutManager(mGridLayoutManager);
        items = new ArrayList<>();
        adapter = new ItemAdapter(items, HomeActivity.this);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(dy > 0) //check for scroll down
                {
                    visibleItemCount = mGridLayoutManager.getChildCount();
                    totalItemCount = mGridLayoutManager.getItemCount();
                    pastVisibleItems = mGridLayoutManager.findFirstVisibleItemPosition();

                    if (loading)
                    {
                        if ( (visibleItemCount + pastVisibleItems) >= totalItemCount)
                        {
                            loading = false;
                            last_id = items.get(items.size()-1).getId();
                            loadItems();
                        }
                    }
                }
            }
        });
    }
    private void setupSwipeRefreshLayout() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                items.clear();
                last_id = 0;
                loading = true;
                mSwipeRefreshLayout.setRefreshing(false);
                loadItems();
                whatsUp();
                loadStories();
            }
        });
    }

    private void storyRecyclerViewSetup() {
        storiesList = new ArrayList<>();
        storyAdapter = new StoryAdapter(HomeActivity.this, storiesList);
        storyRecyclerView = (RecyclerView) findViewById(R.id.storyRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        storyRecyclerView.setLayoutManager(linearLayoutManager);
        storyRecyclerView.setAdapter(storyAdapter);
    }
    private void loadStories() {
        storyRecyclerView.setVisibility(View.GONE);
        RequestHandler.makeRequest(this, "GET", Urls.GET_STORIES, null, new RequestCallback() {
            @Override
            public void onNoInternetAccess() {

            }

            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");
                    if(error) {
                        return;
                    }

                    storiesList.clear();
                    Story story;
                    JSONArray storiesArray = jsonObject.getJSONArray("stories");
                    if(storiesArray.length() > 0) {
                        storyRecyclerView.setVisibility(View.VISIBLE);
                        JSONObject storyObject;
                        for(int i = 0; i < storiesArray.length(); i++) {
                            storyObject = storiesArray.getJSONObject(i);
                            story = new Story(storyObject.getInt("id"), storyObject.getString("phone_number"));
                            storiesList.add(story);
                        }
                    }
                    storyAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onRequestError() {

            }
        });

    }

    private void expireStories24() {
        RequestHandler.makeRequest(this, "POST", Urls.EXPIRE_STORIES_24, expireStories24Params(), new RequestCallback() {
            @Override
            public void onNoInternetAccess() {

            }

            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");
                    if(error) {
                        String message = jsonObject.getString("message");
                        AlertHandler.alertProblemMaker(HomeActivity.this, message, new AlertProblemCallback() {
                            @Override
                            public void tryAgain() {
                                expireStories24();
                            }

                            @Override
                            public void cancel() {

                            }
                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    String message = getResources().getString(R.string.alert_dialog_message_problem_in_server);
                    AlertHandler.alertProblemMaker(HomeActivity.this, message, new AlertProblemCallback() {
                        @Override
                        public void tryAgain() {
                            expireStories24();
                        }

                        @Override
                        public void cancel() {

                        }
                    });
                }

            }

            @Override
            public void onRequestError() {
                String message = getResources().getString(R.string.alert_dialog_message_problem_with_connecting);
                AlertHandler.alertProblemMaker(HomeActivity.this, message, new AlertProblemCallback() {
                    @Override
                    public void tryAgain() {
                        expireStories24();
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        });
    }
    private Map<String, String> expireStories24Params() {
        Map<String, String> params = new HashMap<>();
        params.put("username", SharedPrefManager.getInstance(HomeActivity.this).getUsername());
        params.put("password", SharedPrefManager.getInstance(HomeActivity.this).getPassword());
        return params;
    }

    private void deleteStories7() {
        RequestHandler.makeRequest(this, "POST", Urls.DELETE_STORIES_7, deleteStories7Params(), new RequestCallback() {
            @Override
            public void onNoInternetAccess() {

            }

            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");
                    if(error) {
                        String message = jsonObject.getString("message");
                        AlertHandler.alertProblemMaker(HomeActivity.this, message, new AlertProblemCallback() {
                            @Override
                            public void tryAgain() {
                                deleteStories7();
                            }

                            @Override
                            public void cancel() {

                            }
                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    String message = getResources().getString(R.string.alert_dialog_message_problem_in_server);
                    AlertHandler.alertProblemMaker(HomeActivity.this, message, new AlertProblemCallback() {
                        @Override
                        public void tryAgain() {
                            deleteStories7();
                        }

                        @Override
                        public void cancel() {

                        }
                    });
                }

            }

            @Override
            public void onRequestError() {
                String message = getResources().getString(R.string.alert_dialog_message_problem_with_connecting);
                AlertHandler.alertProblemMaker(HomeActivity.this, message, new AlertProblemCallback() {
                    @Override
                    public void tryAgain() {
                        deleteStories7();
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        });
    }
    private Map<String, String> deleteStories7Params() {
        Map<String, String> params = new HashMap<>();
        params.put("username", SharedPrefManager.getInstance(HomeActivity.this).getUsername());
        params.put("password", SharedPrefManager.getInstance(HomeActivity.this).getPassword());
        return params;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.usersMenu:
                startActivity(new Intent(HomeActivity.this, UsersActivity.class));
                break;
            case R.id.suggestsMenu:
                startActivity(new Intent(HomeActivity.this, SuggestsActivity.class));
                break;
            case R.id.reportsMenu:
                startActivity(new Intent(HomeActivity.this, ReportsActivity.class));
                break;
            case R.id.logoutMenu:
                AlertDialog.Builder alertDialogbuilder = new AlertDialog.Builder(this);
                alertDialogbuilder.setTitle("توجه");
                alertDialogbuilder.setMessage("آیا میخواهید خارج شوید؟");
                alertDialogbuilder.setCancelable(false);
                alertDialogbuilder.setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                alertDialogbuilder.setPositiveButton("بله", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPrefManager.getInstance(HomeActivity.this).logout();
                        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                    }
                });
                alertDialogbuilder.show();
                break;
        }
        return true;
    }
}
