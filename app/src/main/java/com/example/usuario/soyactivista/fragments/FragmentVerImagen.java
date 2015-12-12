package com.example.usuario.soyactivista.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import soy_activista.quartzapp.com.soy_activista.R;

/**
 * Created by Brahyam on 1/12/2015.
 */
public class FragmentVerImagen extends Fragment {

    private ImageView imageView;
    private Button botonSalir;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_ver_imagen, container, false);

        // Assign holders
        imageView = (ImageView)v.findViewById(R.id.valueImage);

        botonSalir = (Button)v.findViewById(R.id.botonSalir);

        String url = getArguments().getString("imageUrl");

        Log.d("DETALLE", "url "+url );

        if(url != null){
            Glide.with(getContext())
                    .load(url)
                    .fitCenter()
                    .placeholder(R.mipmap.ic_placeholder)
                    .into(imageView);
        }
        else{
            Toast.makeText(getActivity(), "La imagen no pude ser cargada. Por favor intente de nuevo más tarde.", Toast.LENGTH_SHORT).show();
        }




        // On Click listener to report Message
        botonSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager()
                        .popBackStack();
            }
        });

        return v;
    }
}