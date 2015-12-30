package com.example.usuario.soyactivista.fragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;
import java.util.concurrent.TimeUnit;

import soy_activista.quartzapp.com.soy_activista.R;

/**
 * Created by RSMAPP on 30/12/2015.
 */
public class FragmentContestarPregunta extends Fragment {


    private static final String FORMAT = "%02d:%02d:%02d";

public static int i=0;
        public int seconds;
      public TextView pregunta,tiempo;
    public Button respuesta1,respuesta2,respuesta3,respuesta4;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_contestar_pregunta,container,false);
        pregunta=(TextView)v.findViewById(R.id.textViewPregunta);
        tiempo=(TextView)v.findViewById(R.id.textViewTiempo);
        respuesta1=(Button)v.findViewById(R.id.buttonRespuesta1);
        respuesta2=(Button)v.findViewById(R.id.buttonRespuesta2);
        respuesta3=(Button)v.findViewById(R.id.buttonRespuesta3);
        respuesta4=(Button)v.findViewById(R.id.buttonRespuesta4);

        respuesta1.setOnClickListener(verificarCorrecta);
        respuesta2.setOnClickListener(verificarCorrecta);
        respuesta3.setOnClickListener(verificarCorrecta);
        respuesta4.setOnClickListener(verificarCorrecta);


             setPregunta();






        return v;
    }
    public void contador(int segundos)
    {
        new CountDownTimer( segundos,1000) { // adjust the milli seconds here

            public void onTick(long millisUntilFinished) {


                tiempo.setText("" + String.format(FORMAT,
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                                TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }

            public void onFinish() {
                tiempo.setText("Hecho!");
                setPregunta();
            }
        }.start();
    }

    public void setPregunta()
    {
        Bundle bundle=this.getArguments();
        String dificultadElegida=bundle.getString("dificultad");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Pregunta");
        query.whereEqualTo("dificultad", dificultadElegida);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> PreguntaList, ParseException e) {
                if (e == null) {
                 if(i<PreguntaList.size()) {
                     pregunta.setText(PreguntaList.get(i).getString("pregunta"));
                     respuesta1.setText(PreguntaList.get(i).getString("opcion1"));
                     respuesta2.setText(PreguntaList.get(i).getString("opcion2"));
                     respuesta3.setText(PreguntaList.get(i).getString("opcion3"));
                     respuesta4.setText(PreguntaList.get(i).getString("opcion4"));
                     int timer = PreguntaList.get(i).getInt("tiempo");
                     String tiempochar = String.valueOf(timer);

                     seconds = Integer.parseInt(tiempochar) * 1000;
                     contador(seconds);

                     // Store data in bundle to send to next fragment
                 }else {
                     Toast.makeText(getActivity(), "FINALIZADO", Toast.LENGTH_SHORT).show();
                 }
                } else {
                    Toast.makeText(getActivity(), "No se trae datos", Toast.LENGTH_LONG);
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });

    }

 View.OnClickListener verificarCorrecta=new View.OnClickListener() {
     @Override
     public void onClick(View v) {
         Button seleccion=(Button)v;
         if(seleccion.getText().toString().equals(respuesta1.getText().toString()))
         {
             Toast.makeText(getActivity(), "CORRECTO", Toast.LENGTH_SHORT).show();
         }else{
             Toast.makeText(getActivity(), "INCORRECTO", Toast.LENGTH_SHORT).show();
         }
         i++;
         setPregunta();
     }
 };


}
