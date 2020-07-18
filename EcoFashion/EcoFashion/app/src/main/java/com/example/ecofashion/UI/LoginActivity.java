package com.example.ecofashion.UI;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ecofashion.Entities.User;
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

public class LoginActivity extends AppCompatActivity {


    final String PREFERENCE_STRING = "LoggedInUser";
    @BindView(R.id.signUpButton)
    Button signUpButton;
    @BindView((R.id.loginButton))
    Button loginButton;
    @BindView((R.id.userName_input))
    EditText userNameInput;
    @BindView((R.id.userPassword_input))
    EditText userPasswordInput;
    @BindView((R.id.wrongInfo))
    TextView mWrongInfo;
    private MainActivity mainActivity;
    private SharedPreferences mPrefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mPrefs =  getSharedPreferences(PREFERENCE_STRING, MODE_PRIVATE);
        final ProgressDialog progressDialog = new ProgressDialog(this);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSignUpPage();
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setMessage("Logging In!");
                progressDialog.show();
                loginUser(userNameInput.getText().toString(), userPasswordInput.getText().toString());
                progressDialog.dismiss();
            }
        });
    }

    /**
     * Býr til request með notendanafni, pw og email og kallar á callBackend með því
     */
    private void loginUser(String userName, String password) {
        String URL_DATA = "http://10.0.2.2:8090/login";
        RequestBody formBody = new FormBody.Builder()
                .add("username", userName)
                .add("userpassword", password)
                .build();

        Request request = new Request.Builder()
                .url(URL_DATA)
                .post(formBody)
                .build();
        callBackend(request);
    }

    /**
     * Kallar á bakenda til þess að skrá notanda inns
     */
    private void callBackend(Request request){
        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("bilad");
                Log.e("IO Exception/Failure", "Exception caught: ", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String userData = response.body().string();
                    System.out.println(userData);
                    User user = parseUserData(userData);
                    Log.v("Error", userData);
                    if (response.isSuccessful()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                goToMain();
                            }
                        });
                    } else {
                        System.out.println("Error!");
                        mWrongInfo.setText("Incorrect login info");
                    }
                } catch (IOException e) {
                    Log.e("IO Exception", "Exception caught: ", e);
                }catch (JSONException e) {
                    Log.e("JSON Exception", "JSON caught: ", e);
                }
            }
        });
    }

    /**
     * Sækir upplýsingar um núverandi user og setur inn í "user"
     */
    private User parseUserData(String userData) throws JSONException {
        JSONObject jsonObk= new JSONObject(userData);

        //Write JSON file
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString("LoggedInUser", userData);
        prefsEditor.commit();
        JSONObject json = jsonObk.getJSONObject("user");
        return new User(json.get("userEmail").toString(),
                json.get("userName").toString(),
                "",
                "",
                "",
                "",
                "");
    }

    /**
     * Sendir mann á signup síðuna
     */
    private void openSignUpPage() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);

    }

    /**
     * Sendir mann á upphafssíðu
     */
    private void goToMain(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


}

