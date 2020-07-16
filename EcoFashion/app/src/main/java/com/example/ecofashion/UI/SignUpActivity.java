package com.example.ecofashion.UI;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.ecofashion.Entities.User;
import com.example.ecofashion.R;

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

public class SignUpActivity extends AppCompatActivity {
    @BindView(R.id.signUpButton)
    Button signUpButton;
    @BindView(R.id.input_name)
    EditText userNameInput;
    @BindView(R.id.input_email)
    EditText userEmailInput;
    @BindView(R.id.input_password)
    EditText passwordInput;
    @BindView(R.id.input_reEnterPassword)
    EditText rePasswordInput;
    @BindView(R.id.input_address)
    EditText userAddressInput;
    @BindView(R.id.input_phone)
    EditText userPhoneInput;
    @BindView(R.id.input_zip)
    EditText userZipInput;

    String p1;
    String p2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            p1 = passwordInput.getText().toString();
            p2 = rePasswordInput.getText().toString();

            //Eru passwordin eins?
            if(!p1.equals(p2)) {
                rePasswordInput.setError("Passwords do not match");
            }else {
                String username = userNameInput.getText().toString();
                String email = userEmailInput.getText().toString();
                String rePass = rePasswordInput.getText().toString();
                String address = userAddressInput.getText().toString();
                String zip = userZipInput.getText().toString();
                String phone = userPhoneInput.getText().toString();
                User newUser = new User(email, username, rePass, phone, address, zip, "0");
                saveUser(newUser);

                goToLoginPage();
            }
            }

    });
    }

    /**
     * Býr til request með upplýsingum um nýjan notanda
     * @param newUser
     */
    private void saveUser(User newUser) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Býr til notanda fyrir þig");
        progressDialog.show();
        String URL_DATA = "http://10.0.2.2:8090/saveUser";
        RequestBody formBody = new FormBody.Builder()
                .add("username", newUser.getUserName())
                .add("useremail", newUser.getUserEmail())
                .add("userpassword", newUser.getUserPassword())
                .add("userphone", newUser.getUserPhone())
                .add("useraddress", newUser.getUserAddress())
                .add("userzip", newUser.getUserZip())
                .add("userstatus", newUser.getUserStatus())
                .build();

        Request request = new Request.Builder()
                .url(URL_DATA)
                .post(formBody)
                .build();
        callBackend(request);
    }

    /**
     * Kallar í bakenda til að vista nýjan user
     * @param request
     */
    private void callBackend(Request request){
        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("Bilað");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException { System.out.println("Virkar"); }
        });
    }

    /**
     * Færir okkur yfir á loginpage
     */
    private void goToLoginPage() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
