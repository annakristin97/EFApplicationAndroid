package com.example.ecofashion.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import android.support.v4.app.Fragment;

import com.example.ecofashion.R;

public class FilterFragment extends Fragment {

    public FilterFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_filters, container, false);
        Button b = (Button) v.findViewById(R.id.searchButton);
        final Spinner mTypeFilter = (Spinner) v.findViewById(R.id.typeFilter);
        final Spinner mSizeFilter = (Spinner) v.findViewById(R.id.sizeFilter);
        final Spinner mPriceFilter = (Spinner) v.findViewById(R.id.priceFilter);
        final Spinner mBrandFilter = (Spinner) v.findViewById(R.id.brandFilter);
        final Spinner mColorFilter = (Spinner) v.findViewById(R.id.colorFilter);
        final Spinner mGenderFilter = (Spinner) v.findViewById(R.id.genderFilter);

        //Sækja filtera úr spinnerum þegar ýtt er á "search"
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity) getActivity();
                activity.getFilteredItems(
                        mTypeFilter.getSelectedItem().toString(),
                        mPriceFilter.getSelectedItem().toString(),
                        mSizeFilter.getSelectedItem().toString(),
                        mBrandFilter.getSelectedItem().toString(),
                        mColorFilter.getSelectedItem().toString(),
                        mGenderFilter.getSelectedItem().toString()
                );
                closeFragment();
            }
        });
        return v;
    }

    /**
     * Lokar fragment glugga
     */
    public void closeFragment() {
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }
}
