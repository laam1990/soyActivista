package com.example.usuario.soyactivista.fragments;

import com.dd.processbutton.iml.ActionProcessButton;
import logica.ProgressGenerator;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.Toolbar;




import soy_activista.quartzapp.com.soy_activista.R;

import static android.widget.Toast.*;

/**
 * Created by Luis Adrian on 28/12/2015.
 */
public class FragmentTriviaPrincipal extends Fragment implements ProgressGenerator.OnCompleteListener{

    public static final String EXTRAS_ENDLESS_MODE = "EXTRAS_ENDLESS_MODE";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        //Choose fragment to inflate
        View v = inflater.inflate(R.layout.fragment_trivia_principal, container, false);

        final ProgressGenerator progressGenerator = new ProgressGenerator(this);
        final ActionProcessButton btnSignIn = (ActionProcessButton) v.findViewById(R.id.btnSignIn);

        btnSignIn.setMode(ActionProcessButton.Mode.PROGRESS);


        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressGenerator.start(btnSignIn);
                btnSignIn.setEnabled(false);
            }
        });

        return v;
    }

    @Override
    public void onComplete() {
        //Toast.makeText (getContext(), R.string.carga_completa, Toast.LENGTH_LONG).show();
    }

}
