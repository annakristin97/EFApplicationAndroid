package com.example.ecofashion.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecofashion.Entities.FilteredItems;
import com.example.ecofashion.Entities.Item;
import com.example.ecofashion.Entities.User;
import com.example.ecofashion.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CartActivity extends AppCompatActivity {
    public static final String TAG = UserHomeActivity.class.getSimpleName();

    private SharedPreferences mPrefs;
    final String PREFERENCE_STRING = "LoggedInUser";
    private Long userID;
    private String userIDString = "";
    private long cartTotal = 0;
    private User user;

    private FilteredItems mFilteredItems;
    private ItemAdapterUserHome mAdapter;

    @BindView(R.id.itemList)
    ListView mItemList;
    @BindView(R.id.checkoutButton)
    Button mCheckoutButton;
    @BindView(R.id.emptyCartButton)
    Button mEmptyCartButton;
    @BindView(R.id.mypageButton)
    ImageButton mMypageButton;
    @BindView(R.id.searchButton)
    ImageButton mSearchButton;
    @BindView(R.id.totalPriceTextView)
    TextView mTotalPriceTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPrefs = getSharedPreferences(PREFERENCE_STRING, MODE_PRIVATE);

        setContentView(R.layout.activity_cart);
        ButterKnife.bind(this);

        String json = mPrefs.getString("LoggedInUser", "");
        if (json == null)
            System.out.println("Ekkert json");
        try{
            parseUserData(json);
        }catch (JSONException e){
            Log.e(TAG, "JSON caught: ", e);
        }

        mCheckoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCartItems();
            }
        });

        mEmptyCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emptyCartItems();
            }
        });

        mMypageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openUserHome();
            }
        });

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSearch();
            }
        });

        getItems();
    }

    /**
     * Sækir upplýsingar um núverandi user og setur inn í "user"
     */
    private void parseUserData(String userData) throws JSONException {
        if (userData == null){
            goToMain();
        }
        JSONObject jsonObk= new JSONObject(userData);
        JSONObject json = jsonObk.getJSONObject("user");
        userID = json.getLong("id");
        userIDString = userID.toString();
        user =  new User(json.get("userEmail").toString(),
                json.get("userName").toString(),
                json.get("userPassword").toString(),
                json.get("userPhone").toString(),
                json.get("userAddress").toString(),
                json.get("userZip").toString(),
                json.get("userStatus").toString());
    }

    /**
     * Fara yfir á upphafssíðu
     */
    private void goToMain(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Hjálparfall til þess að sækja öll items
     */
    private void getItems() {
        Request request = new Request.Builder()
                .url("http://10.0.2.2:8090/Items")
                .build();

        callBackendItems(request);
    }

    /**
     * Hjálparfall til þess að eyða items
     */
    private void deleteCartItems() {
        RequestBody formBody = new FormBody.Builder()
                .add("buyerID", userIDString)
                .build();

        Request request = new Request.Builder()
                .url("http://10.0.2.2:8090/deleteBybuyerID")
                .post(formBody)
                .build();

        callBackendDeleteItems(request);
    }

    private void emptyCartItems() {
        RequestBody formBody = new FormBody.Builder()
                .add("buyerID", userIDString)
                .build();

        Request request = new Request.Builder()
                .url("http://10.0.2.2:8090/emptyBybuyerID")
                .post(formBody)
                .build();

        callBackendEmptyItems(request);
    }

    /**
     * Kallar á bakenda til þess að sækja öll items
     */
    private void callBackendItems(Request request) {
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
                            mFilteredItems = parseItemListDetails(jsonData);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateDisplay();
                                }
                            });
                        } else {
                            alertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON caught: ", e);
                    }
                }
            });
        } else {
            Toast.makeText(this, R.string.network_unavailable_message, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Kallar á bakenda til þess að eyða items með buyerid=userid
     */
    private void callBackendDeleteItems(Request request) {
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
                            Intent intent = new Intent(CartActivity.this, MainActivity.class);
                            startActivity(intent);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateDisplay();
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
            Toast.makeText(this, R.string.network_unavailable_message, Toast.LENGTH_LONG).show();
        }
    }

    private void callBackendEmptyItems(Request request) {
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
                            Intent intent = new Intent(CartActivity.this, MainActivity.class);
                            startActivity(intent);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateDisplay();
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
            Toast.makeText(this, R.string.network_unavailable_message, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Bætir items við filteraða listann
     */
    private FilteredItems parseItemListDetails(String jsonData) throws JSONException{
        FilteredItems filteredItems = new FilteredItems();

        ArrayList<Item> items =new ArrayList<Item>();
        JSONArray array=new JSONArray(jsonData);
        for(int i=0;i<array.length();i++){
            JSONObject elem=(JSONObject)array.get(i);
            if (elem.getLong("itemBuyerID") == userID ) {
                Item item = new Item(elem.getLong("itemSellerID"),
                        elem.getLong("itemBuyerID"),
                        elem.getString("itemName"),
                        elem.getString("itemDescription"),
                        elem.getLong("itemPrice"),
                        elem.getString("itemType"),
                        elem.getString("itemSize"),
                        elem.getString("itemColor"),
                        elem.getString("itemBrand"),
                        elem.getString("itemCondition"),
                        elem.getString("itemGender"),
                        elem.getString("image1"),
                        elem.getString("image2"),
                        elem.getString("image3"),
                        elem.getString("image4"));
                items.add(item);
                cartTotal += elem.getLong("itemPrice");
            }
        }

        filteredItems.setItems(items);

        mTotalPriceTextView.setText(cartTotal + " ISK");

        return filteredItems;
    }

    /**
     * Update-ar view
     */
    private void updateDisplay() {
        mAdapter = new ItemAdapterUserHome(this, mFilteredItems.getItems());

        mItemList.setAdapter(mAdapter);
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

    private void openUserHome() {
        if(mPrefs.getString("LoggedInUser", null) == null){
            System.out.println("Enginn notandi skráður inn");
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }else{
            System.out.println("Innskráður notandi er:" + mPrefs.getString("LoggedInUser", null));
            Intent intent = new Intent(this, UserHomeActivity.class);
            startActivity(intent);
        }
    }

    private void openSearch() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
