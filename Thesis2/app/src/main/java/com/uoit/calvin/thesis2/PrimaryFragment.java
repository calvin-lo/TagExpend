package com.uoit.calvin.thesis2;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by calvin on 05/10/16.
 */

public class PrimaryFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View x = inflater.inflate(R.layout.primary_layout,null);

        FloatingActionButton btnFab = (FloatingActionButton) x.findViewById(R.id.fab1);
        btnFab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FormActivity.class);
                startActivity(intent);
            }
        });

        return x;

    }
}
