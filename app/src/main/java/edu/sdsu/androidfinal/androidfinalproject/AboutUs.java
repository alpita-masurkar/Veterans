package edu.sdsu.androidfinal.androidfinalproject;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class AboutUs extends Fragment {


    private TextView aboutUsTitle, aboutUsDescription;
    private ImageView imageView;
    public AboutUs() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View userView = inflater.inflate(R.layout.fragment_about_us, container, false);

        aboutUsTitle = (TextView) userView.findViewById(R.id.about_us_text);
        aboutUsDescription = (TextView) userView.findViewById(R.id.about_us_description);

        imageView = (ImageView) userView.findViewById(R.id.about_us_icon);
        return userView;
    }

}
