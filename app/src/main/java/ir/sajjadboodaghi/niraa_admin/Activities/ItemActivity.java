package ir.sajjadboodaghi.niraa_admin.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ir.sajjadboodaghi.niraa_admin.Handlers.AlertHandler;
import ir.sajjadboodaghi.niraa_admin.Handlers.Interfaces.AlertNoticeCallback;
import ir.sajjadboodaghi.niraa_admin.Handlers.TelegramIntent;
import ir.sajjadboodaghi.niraa_admin.R;
import ir.sajjadboodaghi.niraa_admin.Handlers.Interfaces.RequestCallback;
import ir.sajjadboodaghi.niraa_admin.Handlers.RequestHandler;
import ir.sajjadboodaghi.niraa_admin.Handlers.SharedPrefManager;
import ir.sajjadboodaghi.niraa_admin.Urls;
import ir.sajjadboodaghi.niraa_admin.Adapters.ViewPagerAdapter;

import static android.view.View.GONE;

public class ItemActivity extends AppCompatActivity {

    private int dotsCount;
    private ImageView[] dots;

    int itemId;
    String itemTitle;
    String itemDescription;
    String itemPrice;
    String itemPlace;
    String itemSubcatName;
    String itemPhoneNumber;
    String itemShamsi;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int imageCount = getIntent().getIntExtra("itemImageCount", 0);
        itemId = getIntent().getIntExtra("itemId", 0);
        if(imageCount > 0) {
            setContentView(R.layout.activity_item);
            makeImageSlider(imageCount);
        } else {
            setContentView(R.layout.activity_item_noimage);
        }
        setupItemFields();
    }
    private void makeImageSlider(int imageCount) {
        String[] imageUrls = new String[imageCount];
        for(int i = 0; i < imageCount; i++) {
            imageUrls[i] = Urls.IMAGES_BASE_URL + "item_" + itemId + "_" + (i+1) + ".jpg";
        }
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this, imageUrls);
        viewPager.setAdapter(viewPagerAdapter);

        LinearLayout sliderDotsLinearLayout = (LinearLayout) findViewById(R.id.sliderDotsLinearLayout);
        dotsCount = viewPagerAdapter.getCount();
        if(dotsCount > 1) {
            dots = new ImageView[dotsCount];
            for (int i = 0; i < dotsCount; i++) {
                dots[i] = new ImageView(this);
                dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.noactive_dot));
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(6, 0, 6, 0);
                sliderDotsLinearLayout.addView(dots[i], params);
            }
            dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));

            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    for (int i = 0; i < dotsCount; i++) {
                        dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.noactive_dot));
                        dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }
    }
    private void setupItemFields() {
        TextView itemTitleTV = (TextView) findViewById(R.id.itemTitle);
        LinearLayout priceLinearLayout = (LinearLayout) findViewById(R.id.priceLinearLayout);
        TextView itemPriceTV = (TextView) findViewById(R.id.itemPrice);
        TextView itemPlaceTV = (TextView) findViewById(R.id.itemPlace);
        TextView itemShamsiTV = (TextView) findViewById(R.id.itemShamsi);
        TextView itemCategory = (TextView) findViewById(R.id.itemCategory);
        TextView itemPhoneNumberTV = (TextView) findViewById(R.id.itemPhoneNumber);
        TextView itemDescriptionTV = (TextView) findViewById(R.id.itemDescription);


        itemTitle = getIntent().getStringExtra("itemTitle");
        itemDescription = getIntent().getStringExtra("itemDescription");
        itemPrice = getIntent().getStringExtra("itemPrice");
        itemPlace = getIntent().getStringExtra("itemPlace");
        itemSubcatName = getIntent().getStringExtra("itemSubcatName");
        itemPhoneNumber = getIntent().getStringExtra("itemPhoneNumber");
        itemShamsi = getIntent().getStringExtra("itemShamsi");

        itemTitleTV.setText(itemTitle);

        // if price was empty price part will disappear
        // else price value will set into price edittext
        if(itemPrice.replace(" ", "").equals("")) {
            priceLinearLayout.setVisibility(GONE);
        } else {
            itemPriceTV.setText("قیمت: " + itemPrice);
        }

        itemPlaceTV.setText("مکان: " + itemPlace);
        itemShamsiTV.setText("زمان: " + itemShamsi.substring(0,8) + " ساعت " + itemShamsi.substring(9));
        itemCategory.setText("دسته: " + itemSubcatName);
        itemPhoneNumberTV.setText("تلفن: " + itemPhoneNumber);
        itemDescriptionTV.setText(itemDescription);

        Button telegramButton = (Button) findViewById(R.id.telegramButton);
        if(!getIntent().getStringExtra("itemTelegramId").equals("")) {
            telegramButton.setVisibility(View.VISIBLE);
        }
    }
    public void closeItemPage(View v) {
        finish();
    }
    public void acceptItem(View v) {
        AlertDialog.Builder alertDialogbuilder = new AlertDialog.Builder(ItemActivity.this);
        alertDialogbuilder.setTitle(getResources().getString(R.string.alert_dialog_title_danger));
        alertDialogbuilder.setMessage(getResources().getString(R.string.alert_dialog_message_accept_item));
        alertDialogbuilder.setCancelable(false);
        alertDialogbuilder.setPositiveButton(getResources().getString(R.string.alert_dialog_button_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                final ProgressDialog progressDialog = new ProgressDialog(ItemActivity.this);
                progressDialog.setMessage(getResources().getString(R.string.progress_dialog_connecting));
                progressDialog.setCancelable(false);
                progressDialog.show();

                RequestHandler.makeRequest(ItemActivity.this, "POST", Urls.VERIFY_ITEM, acceptItemParams(), new RequestCallback() {
                    @Override
                    public void onNoInternetAccess() {
                        progressDialog.dismiss();
                        String message = getResources().getString(R.string.alert_dialog_message_no_internet_access);
                        AlertHandler.alertNoticeMaker(ItemActivity.this, message, new AlertNoticeCallback() {
                            @Override
                            public void okay() {

                            }
                        });
                    }

                    @Override
                    public void onSuccess(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String message = jsonResponse.getString("message");
                            AlertHandler.alertNoticeMaker(ItemActivity.this, message, new AlertNoticeCallback() {
                                @Override
                                public void okay() {
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                            String message = getResources().getString(R.string.alert_dialog_message_problem_in_server);
                            AlertHandler.alertNoticeMaker(ItemActivity.this, message, new AlertNoticeCallback() {
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
                        AlertHandler.alertNoticeMaker(ItemActivity.this, message, new AlertNoticeCallback() {
                            @Override
                            public void okay() {
                            }
                        });
                    }
                });
            }
        });
        alertDialogbuilder.setNegativeButton(getResources().getString(R.string.alert_dialog_button_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });
        alertDialogbuilder.show();

    }

    public void openTelegramProfile(View view) {
        new TelegramIntent().start(this, getIntent().getStringExtra("itemTelegramId"));
    }

    private Map<String, String> acceptItemParams() {
        Map<String, String> params = new HashMap<>();
        params.put("username", SharedPrefManager.getInstance(ItemActivity.this).getUsername());
        params.put("password", SharedPrefManager.getInstance(ItemActivity.this).getPassword());
        params.put("item_id", String.valueOf(itemId));
        params.put("item_phone_number", itemPhoneNumber);
        params.put("item_title", itemTitle);
        return params;
    }

    public void changeCategory(View v) {
        Intent intent = new Intent(this, ChangeCategoryActivity.class);
        intent.putExtra("itemId", itemId);
        startActivity(intent);
    }

    public void changePlace(View v) {
        Intent intent = new Intent(this, ChangePlaceActivity.class);
        intent.putExtra("itemId", itemId);
        startActivity(intent);
    }

    public void editItem(View v) {
        Intent intent = new Intent(this, EditItemActivity.class);
        intent.putExtra("itemTitle", itemTitle);
        intent.putExtra("itemDescription", itemDescription);
        intent.putExtra("itemPrice", itemPrice);
        intent.putExtra("itemId", itemId);
        startActivity(intent);
    }

    public void deleteItem(View v) {
        Intent intent = new Intent(this, DeleteItemActivity.class);
        intent.putExtra("itemId", itemId);
        intent.putExtra("itemPhoneNumber", itemPhoneNumber);
        intent.putExtra("itemTitle", itemTitle);
        startActivity(intent);

    }

    public void userItems(View v) {
        Intent intent = new Intent(this, UserItemsActivity.class);
        intent.putExtra("phoneNumber", itemPhoneNumber);
        startActivity(intent);
    }
}
