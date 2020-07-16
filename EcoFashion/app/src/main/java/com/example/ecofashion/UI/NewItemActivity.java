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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.ecofashion.Entities.User;
import com.example.ecofashion.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Optional;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NewItemActivity extends AppCompatActivity {
    public static final String TAG = NewItemActivity.class.getSimpleName();

    private User user;
    private SharedPreferences mPrefs;
    final String PREFERENCE_STRING = "LoggedInUser";
    private Long userID;
    private String userIDString = "";

    @BindView(R.id.input_name)
    EditText mInputName;
    @BindView(R.id.input_description)
    EditText mInputDescription;
    @BindView(R.id.input_price)
    EditText mInputPrice;
    @BindView(R.id.typeSpinner)
    Spinner mInputType;
    @BindView(R.id.sizeSpinner)
    Spinner mInputSize;
    @BindView(R.id.colorSpinner)
    Spinner mInputColor;
    @BindView(R.id.brandSpinner)
    Spinner mInputBrand;
    @BindView(R.id.conditionSpinner)
    Spinner mInputCondition;
    @BindView(R.id.genderSpinner)
    Spinner mInputGender;
    @BindView(R.id.addItemButton)
    Button mAddItemButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPrefs = getSharedPreferences(PREFERENCE_STRING, MODE_PRIVATE);
        setContentView(R.layout.activity_new_item);
        ButterKnife.bind(this);



        mAddItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Long.parseLong(mInputPrice.getText().toString()) < 0) {
                    mInputPrice.setError("Price can not be less than zero");
                } else {
                    String json = mPrefs.getString("LoggedInUser", "");
                    if (json == null)
                        System.out.println("Ekkert json");
                    System.out.println(json);
                    try {
                        parseUserData(json);
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON caught: ", e);
                    }

                    newItem(userIDString,
                            "0",
                            mInputName.getText().toString(),
                            mInputDescription.getText().toString(),
                            mInputPrice.getText().toString(),
                            mInputType.getSelectedItem().toString(),
                            mInputSize.getSelectedItem().toString(),
                            mInputColor.getSelectedItem().toString(),
                            mInputBrand.getSelectedItem().toString(),
                            mInputCondition.getSelectedItem().toString(),
                            mInputGender.getSelectedItem().toString()
                    );
                }
            }
        });
    }

    public void newItem(String sellerID, String buyerID, String name, String description, String price, String type, String size, String color, String brand, String condition, String gender) {
        RequestBody formBody = new FormBody.Builder()
                .add("itemSellerID", sellerID)
                .add("itemBuyerID", buyerID)
                .add("itemName", name)
                .add("itemDescription", description)
                .add("itemPrice", price)
                .add("itemType", type)
                .add("itemSize", size)
                .add("itemColor", color)
                .add("itemBrand", brand)
                .add("itemCondition", condition)
                .add("itemGender", gender)
                .build();

        Request request = new Request.Builder()
                .url("http://10.0.2.2:8090/newItem")
                .post(formBody)
                .build();

        callBackend(request);
    }

    /**
     * Kallar á bakenda til þess að eyða items með buyerid=userid
     */
    private void callBackend(Request request) {
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
                            Intent intent = new Intent(NewItemActivity.this, MainActivity.class);
                            startActivity(intent);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    System.out.println("virkar");
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
     * Sækir upplýsingar um núverandi user og setur inn í "user"
     */
    private void parseUserData(String userData) throws JSONException {
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
