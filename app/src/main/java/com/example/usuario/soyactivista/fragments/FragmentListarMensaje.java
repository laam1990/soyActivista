package com.example.usuario.soyactivista.fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

import logica.ListarMensajeParseAdapter;
import soy_activista.quartzapp.com.soy_activista.R;

/**
 * Created by root on 26/11/15.
 */
public class FragmentListarMensaje extends Fragment  {


    private static final String TAG = "ListMensajeParse";
    private ListarMensajeParseAdapter listarMensajeMainAdapter, listarMensajeEstadoAdapter, listarMensajePropiosAdapter, listarMensajeReportadoAdapter;
    private MenuItem filtroReportados;
    View view;
    private ListView listView;
    private TextView listaVacia,textProgress;
    private ParseUser currentUser;
    private ProgressDialog progressDialog;
    private ProgressDialog progressBar;
    private int progressStatus = 0;
    private Handler handler = new Handler();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Inflate View
        view = inflater.inflate(R.layout.fragment_listar_mensaje, container, false);

        textProgress = (TextView)view.findViewById(R.id.textProgress);

        // Initialize main ParseQueryAdapter
        listarMensajeMainAdapter = new ListarMensajeParseAdapter(this.getContext());

        // Initialize list view
        listView = (ListView) view.findViewById(R.id.mensajesListView);

        // Set empty list message
        listaVacia = (TextView) view.findViewById(R.id.listaVacia);

        listView.setEmptyView(listaVacia);

        if (listarMensajeMainAdapter != null) {
            listarMensajeMainAdapter.clear();
            progressBar = new ProgressDialog(getActivity());
            progressBar.setCancelable(false);
            progressBar.setTitle("Cargando");
            progressBar.setMessage("iniciando");
            progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressBar.setMax(100);
            progressBar.setProgress(0);
            progressBar.show();
            ProcessData p = new ProcessData();
            p.execute(10);
            listView.setAdapter(listarMensajeMainAdapter);
            listarMensajeMainAdapter.loadObjects();
        } else {
            Log.d("ADAPTER", "Adapter returned null!");
        }

        currentUser = ParseUser.getCurrentUser();

        // Handle Item OnClick Events
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Store data in bundle to send to next fragment
                ParseObject mensaje = (ParseObject) listView.getItemAtPosition(position);
                ParseUser autor = mensaje.getParseUser("autor");
                ParseFile adjunto;
                ParseGeoPoint ubicacion;

                adjunto = mensaje.getParseFile("adjunto");
                ubicacion = mensaje.getParseGeoPoint("ubicacion");


                SimpleDateFormat format = new SimpleDateFormat("HH:mm dd/MM/yyyy");

                Bundle datos = new Bundle();
                datos.putString("id", mensaje.getObjectId());
                datos.putString("nombre", autor.getString("nombre") + " " + autor.getString("apellido"));
                datos.putString("estado",autor.getString("estado"));
                datos.putString("municipio", autor.getString("municipio"));
                datos.putString("texto", mensaje.getString("texto"));
                datos.putString("fechaCreacion", format.format(mensaje.getCreatedAt()));
                datos.putBoolean("reportado",mensaje.getBoolean("reportado"));
                datos.putString("autor",autor.getObjectId());
                datos.putBoolean("directo",false);

                // Check if images are null and save URLs
                if (adjunto != null){
                    datos.putString("nombreAdjunto",adjunto.getName());
                    datos.putString("adjunto", adjunto.getUrl());
                }

                // Check if location available
                if (ubicacion != null){
                    Log.d("ENVIO", ubicacion.toString());
                    datos.putString("ubicacion", String.valueOf(ubicacion.getLatitude())+","+String.valueOf(ubicacion.getLongitude()));
                }


                // Redirect View to next Fragment
                Fragment fragment = new FragmentDetalleMensaje();
                fragment.setArguments(datos);
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        FloatingActionButton botonCrearMensaje = (FloatingActionButton) view.findViewById(R.id.botonCrearMensaje);

        // Create new Activity Type Button
        botonCrearMensaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment fragment = new FragmentCrearMensaje();
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        // Let the fragment know we will be loading some options for this fragment
        setHasOptionsMenu(true);

        return view;
    }

    // Inflates custom menu for fragment.
    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate Custom Menu
        inflater.inflate(R.menu.menu_listar_mensaje, menu);

        filtroReportados = menu.findItem(R.id.filtroReportados);

        if(currentUser != null && currentUser.getInt("rol") == 1){
            filtroReportados.setVisible(true);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.filtroEstados:
                // Generate List Holder
                final AlertDialog filterDialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Filtrar por estado");

                // Fill Holder with State List from String Array
                final ListView listViewDialog = new ListView(getActivity());
                final ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.Estados)));

                // Add element All.
                arrayList.add(0, "Todos");

                ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,arrayList);
                listViewDialog.setAdapter(stringArrayAdapter);
                builder.setView(listViewDialog);

                // Show Dialog
                filterDialog = builder.create();
                filterDialog.show();

                listViewDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        filterDialog.dismiss();
                        // Request List to filter
                        // TODO: Create Progress Dialog

                        Log.d(TAG, "Filtering by State");

                        listarMensajeMainAdapter.clear();

                        listarMensajeEstadoAdapter = new ListarMensajeParseAdapter(getContext(), "estado="+listViewDialog.getItemAtPosition(position).toString());

                        listView.setAdapter(listarMensajeEstadoAdapter);

                        listarMensajeEstadoAdapter.loadObjects();

                        listarMensajeEstadoAdapter.notifyDataSetChanged();

                        Log.d(TAG, "Adapter has " + listarMensajeEstadoAdapter.getCount() + " items");


                    }
                });

                return true;

            case R.id.filtroPropios:

                // Request List to filter
                // TODO: Create Progress Dialog
                if(currentUser != null){

                    listarMensajeMainAdapter.clear();
                    listarMensajePropiosAdapter = new ListarMensajeParseAdapter(getContext(),"propios="+currentUser.getUsername());
                    listView.setAdapter(listarMensajePropiosAdapter);
                    listarMensajePropiosAdapter.loadObjects();
                    listarMensajePropiosAdapter.notifyDataSetChanged();

                    Log.d(TAG, "Adapter has " + listarMensajePropiosAdapter.getCount() + " items");

                }
                break;

            case R.id.filtroReportados:

                // User is admin
                if(currentUser.getInt("rol") == 1){
                    listarMensajeMainAdapter.clear();
                    listarMensajeReportadoAdapter = new ListarMensajeParseAdapter(getContext(),"reportados=true");
                    listView.setAdapter(listarMensajeReportadoAdapter);
                    listarMensajeReportadoAdapter.loadObjects();
                    listarMensajeReportadoAdapter.notifyDataSetChanged();
                }
                else{
                    Toast.makeText(getContext(), "Credenciales Invalidas.", Toast.LENGTH_LONG).show();
                }
                return true;

            case R.id.filtroTodos:

                listarMensajeMainAdapter.clear();
                listarMensajePropiosAdapter = new ListarMensajeParseAdapter(getContext(),"todos=true");
                listView.setAdapter(listarMensajePropiosAdapter);
                listarMensajePropiosAdapter.loadObjects();
                listarMensajePropiosAdapter.notifyDataSetChanged();

                Log.d(TAG, "Adapter has " + listarMensajePropiosAdapter.getCount() + " items");
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public class ProcessData extends AsyncTask<Integer, String, String> {

        @Override
        protected String doInBackground(Integer... integers) {

            int progress = 0;
            int total = integers[0];

            while (progress <= total) {

                try {

                    Thread.sleep(250); // 0.25 segundos

                } catch(InterruptedException e) {

                }

                String m = progress % 2 == 0 ? "Cargando Mensajes" : "Boletin de Mensajes";

                // exibimos o progresso
                this.publishProgress(String.valueOf(progress), String.valueOf(total), m);

                progress++;
            }

            return "DONE";
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            Float progress = Float.valueOf(values[0]);
            Float total = Float.valueOf(values[1]);

            String message = values[2];

            progressBar.setProgress((int) ((progress / total) * 100));
            progressBar.setMessage(message);

            // se os valores são iguais, termianos nosso processamento
            if (values[0].equals(values[1])) {
                // removemos a dialog
                progressBar.cancel();
            }
        }
    }



}
