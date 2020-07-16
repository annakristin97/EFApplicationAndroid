package com.example.ecofashion.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.example.ecofashion.Entities.FilteredItems;
import com.example.ecofashion.Entities.Item;
import com.example.ecofashion.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private FilteredItems mFilteredItems;
    private ItemAdapter mAdapter;

    @BindView(R.id.itemList)
    ListView mItemList;
    @BindView(R.id.filterButton)
    Button mFilterButton;
    @BindView(R.id.mypageButton)
    ImageButton mMypageButton;
    @BindView(R.id.cartButton)
    ImageButton mCartButton;

    private SharedPreferences mPrefs;

    final String PREFERENCE_STRING = "LoggedInUser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPrefs = getSharedPreferences(PREFERENCE_STRING, MODE_PRIVATE);
        ButterKnife.bind(this);

        getItems();

        mFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                FilterFragment fragment =  new FilterFragment();
                fm.beginTransaction().replace(R.id.container, fragment).commit();
            }
        });

        mMypageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUserPage();
            }
        });

        mCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCart();
            }
        });

        mItemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println("Ýtt á flík");
                List<Item> ItemList = mFilteredItems.getItems();
                Item item = ItemList.get(i);
                System.out.println(item.getItemName());
                openItemPage(item);
            }
        });
    }

    /**
     * Opnar ItemActivity
     */
    private void openItemPage(Item item) {
        Intent intent = new Intent(this, ItemActivity.class);
        intent.putExtra("name", item.getItemName());
        intent.putExtra("price", item.getItemPrice());
        intent.putExtra("size", item.getItemSize());
        intent.putExtra("condition", item.getItemCondition());
        intent.putExtra("description", item.getItemDescription());
        intent.putExtra("itemID", item.getItemID());
        intent.putExtra("image1", item.getImage1());
        intent.putExtra("image2", item.getImage2());
        intent.putExtra("image3", item.getImage3());
        intent.putExtra("image4", item.getImage4());

        startActivity(intent);
    }

    /**
     * Hjálparfall til þess að sækja öll items
     */
    public void getItems() {
        Request request = new Request.Builder()
                .url("http://10.0.2.2:8090/Items")
                .build();

        callBackend(request);
    }

    /**
     * Kallar á bakenda til þess að sækja öll items
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
            Toast.makeText(this, "Network unavailable", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Update-ar view
     */
    private void updateDisplay() {
        mAdapter = new ItemAdapter(this, mFilteredItems.getItems());

        mItemList.setAdapter(mAdapter);
    }

    /**
     * Sækir upplýsingar um filteruð items og setur í listann
     */
    private FilteredItems parseItemListDetails(String jsonData) throws JSONException{

        FilteredItems filteredItems = new FilteredItems();

        ArrayList<Item> items=new ArrayList<Item>();
        JSONArray array=new JSONArray(jsonData);
        for(int i=0;i<array.length();i++){
            JSONObject elem=(JSONObject)array.get(i);
            if(elem.getLong("itemBuyerID")== 0) {
                Item item = new Item(
                        elem.getLong("itemID"),
                        elem.getLong("itemSellerID"),
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
            }
        }

        filteredItems.setItems(items);

        return filteredItems;
    }

    /**
     * Hjálparfall sem býr til request til þess að sækja filteruð items
     */
    public void getFilteredItems(String type, String price, String size, String brand, String color, String gender) {
        RequestBody formBody = new FormBody.Builder()
                .add("itemPrice", price)
                .add("itemType", type)
                .add("itemSize", size)
                .add("itemBrand", brand)
                .add("itemColor", color)
                .add("itemGender", gender)
                .build();

        Request request = new Request.Builder()
                .url("http://10.0.2.2:8090/search")
                .post(formBody)
                .build();

        callBackend(request);
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
     * Upplýsir notanda um Error
     */
    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");
    }

    /**
     * Opnar UserHome ef einhver er loggaður inn - sendir annars áfram á login page
     */
    private void openUserPage() {
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

    /**
     * Opnar Cart ef einhver er loggaður inn - sendir annars áfram á login page
     */
    private void openCart() {
        if(mPrefs.getString("LoggedInUser", null) == null){
            System.out.println("Enginn notandi skráður inn");
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }else{
            System.out.println("Innskráður notandi er:" + mPrefs.getString("LoggedInUser", null));
            Intent intent = new Intent(this, CartActivity.class);
            startActivity(intent);
        }
    }
}
