package com.example.ecofashion.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecofashion.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ItemActivity extends AppCompatActivity {

    @BindView(R.id.buyButton)
    Button mBuyButton;
    @BindView(R.id.youHaveToLogIn)
    TextView mYouHaveToLogIn;

    public Long itemID;
    public static final String TAG = ItemActivity.class.getSimpleName();
    private SharedPreferences mPrefs;
    final String PREFERENCE_STRING = "LoggedInUser";
    private long userID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        ButterKnife.bind(this);

        //Náð í upplýsingar úr sharedPrefrences og prufu user búinn til
        mPrefs =  getSharedPreferences(PREFERENCE_STRING, MODE_PRIVATE);

        String imageId1 = (String) getIntent().getSerializableExtra("image1");
        String url1 = "http://10.0.2.2:8090/Image/" + imageId1;
        String imageId2 = (String) getIntent().getSerializableExtra("image2");
        String url2 = "http://10.0.2.2:8090/Image/" + imageId2;
        String imageId3 = (String) getIntent().getSerializableExtra("image3");
        String url3 = "http://10.0.2.2:8090/Image/" + imageId3;
        String imageId4 = (String) getIntent().getSerializableExtra("image4");
        String url4 = "http://10.0.2.2:8090/Image/" + imageId4;
        System.out.println(url1);
        String[] imageUrls = new String[]{url1,url2,url3,url4};

        // Myndirnar settar inn í viewPager í viðmóti svo hægt sé að skrolla í gegnum þær
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(this, imageUrls);
        viewPager.setAdapter(adapter);

        itemID = (Long) getIntent().getSerializableExtra("itemID");
        String itemName = (String) getIntent().getSerializableExtra("name");
        Long itemPrice = (Long) getIntent().getSerializableExtra("price");
        String itemSize = (String) getIntent().getSerializableExtra("size");
        String itemCondition = (String) getIntent().getSerializableExtra("condition");
        String itemDescription = (String) getIntent().getSerializableExtra("description");


        //Setja upplýsingar um item og birta
        TextView name = (TextView) findViewById(R.id.nametext);
        name.setText(itemName);
        System.out.println(itemPrice.toString());
        TextView price = (TextView) findViewById(R.id.pricetext);
        price.setText(itemPrice.toString());
        TextView size = (TextView) findViewById(R.id.sizetext);
        size.setText(itemSize);
        TextView condition = (TextView) findViewById(R.id.conditiontext);
        condition.setText(itemCondition);
        TextView description = (TextView) findViewById(R.id.descriptiontext);
        description.setText(itemDescription);

        mBuyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(mPrefs.getString("LoggedInUser", null) == null){
                    noLoggedInUser();
                }else{
                    System.out.println("ytt a buy");
                    FragmentManager fm = getSupportFragmentManager();
                    BuyFragment fragment =  new BuyFragment();
                    fm.beginTransaction().replace(R.id.activity_item_container, fragment).commit();

                    String json = mPrefs.getString("LoggedInUser", "");
                    try{
                        JSONObject jsonObk= new JSONObject(json);
                        JSONObject jsonObj = jsonObk.getJSONObject("user");
                        userID = jsonObj.getLong("id");
                    }catch (JSONException e){
                        Log.e(TAG, "JSON caught: ", e);
                    }

                    changeBuyerID(((Long) getIntent().getSerializableExtra("itemID")).toString(), userID);
                }
            }
        });
    }

    /**
     * Senda á loginpage ef enginn user er loggaður inn
     */
    public void noLoggedInUser() {
        System.out.println("Enginn notandi skráður inn");
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * Hjálparfall til þess að breyta buyerID
     */
    public void changeBuyerID(String itemID, Long userID) {

        RequestBody formBody = new FormBody.Builder()
                .add("itemID", itemID)
                .add("buyerID", userID.toString())
                .build();

        Request request = new Request.Builder()
                .url("http://10.0.2.2:8090/changeBuyerID")
                .post(formBody)
                .build();

        callBackend(request);
    }

    /**
     * Kalla á bakenda til þess að breyta buyerID
     */
    private void callBackend(Request request){
        OkHttpClient client = new OkHttpClient();

        if(isNetworkAvailable()) {
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                    alertUserAboutError();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    System.out.println("búið að eyða item");
                                }
                            });
                        } else {
                            alertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    }
                }
            });
        } else {
            Toast.makeText(this, "Network unavailable", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Athugar hvort network sé aðgengilegt
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if(networkInfo!= null && networkInfo.isConnected()) isAvailable = true;
        return isAvailable;
    }

    /**
     * Upplýsir notanda ef um villu er að ræða
     */
    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");
    }
}
























