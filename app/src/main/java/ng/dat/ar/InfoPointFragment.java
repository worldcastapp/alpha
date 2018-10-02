package ng.dat.ar;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import ng.dat.ar.model.ViewPagerAdapter;

public class InfoPointFragment extends Fragment {

    private String placeTitle;
    private String placeDescription;
    private TextView placeTitleTextView;
    private TextView placeDescriptionTextView;
    private Button goToInfoSite;
    private ImageButton closeFragment;
    private ViewPager SliderImages;

    private OnFragmentInteractionListener mListener;

    public InfoPointFragment() {
        // Required empty public constructor
    }

    public static InfoPointFragment newInstance(String placeTitle, String placeDescription) {
        InfoPointFragment fragment = new InfoPointFragment();
        Bundle args = new Bundle();
        args.putString("placeTitle",placeTitle);
        args.putString("placeDescription",placeDescription);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            placeTitle = getArguments().getString("placeTitle");
            placeDescription = getArguments().getString("placeDescription");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.info_point_fragment, container, false);
        placeTitleTextView = (TextView) view.findViewById(R.id.siteTitle);
        placeTitleTextView.setText(placeTitle);
        placeDescriptionTextView = (TextView) view.findViewById(R.id.siteDescription);
        placeDescriptionTextView.setText(placeDescription);

        //Button Go to Site
        goToInfoSite = (Button) view.findViewById(R.id.aceptReference);
        goToInfoSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent InfoSite = new Intent(getContext(), InfoSiteActivity.class);
                startActivity(InfoSite);
                try {
                    this.finalize();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });

        //Button close fragment
        closeFragment = (ImageButton) view.findViewById(R.id.closeFragment);
        closeFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Toast.makeText(v.getContext(),"click",Toast.LENGTH_SHORT).show();
                    ((Activity) v.getContext()).getFragmentManager().popBackStack();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });

        SliderImages = (ViewPager) view.findViewById(R.id.sliderImageSite);
        ViewPagerAdapter vp = new ViewPagerAdapter(this.getContext());
        SliderImages.setAdapter(vp);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    //OnCLick Cross Button
    public void closeFragment(View v){
        try {
            this.finalize();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
