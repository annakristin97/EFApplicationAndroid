package com.example.ecofashion.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.ecofashion.R;

public class BuyFragment extends Fragment {

    public BuyFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_buy, container, false);

        Button b = (Button) v.findViewById(R.id.messageButton);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFragment();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });

        return v;
    }

    public void closeFragment() {
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }
}
