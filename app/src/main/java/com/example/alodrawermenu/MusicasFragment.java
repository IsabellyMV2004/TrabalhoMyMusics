package com.example.alodrawermenu;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.alodrawermenu.db.bean.Genero;
import com.example.alodrawermenu.db.bean.Musica;
import com.example.alodrawermenu.db.dal.GeneroDAL;
import com.example.alodrawermenu.db.dal.MusicaDAL;

import java.util.List;

public class MusicasFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private MainActivity mainActivity;
    private ListView lvMusicas;

    public MusicasFragment() {
        // Required empty public constructor
    }

    public static MusicasFragment newInstance(String param1, String param2) {
        MusicasFragment fragment = new MusicasFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity=(MainActivity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_musicas, container, false);

        lvMusicas =view.findViewById(R.id.lvMusicas);

        lvMusicas.setOnItemLongClickListener((adapterView, view1, i, l) -> {
            MusicaDAL dal =new MusicaDAL(view.getContext());
            Musica musica = (Musica)adapterView.getItemAtPosition(i);
            dal.apagar(musica.getId());
            //atualiza o listView
            //((ArrayAdapter)lvGeneros.getAdapter()).notifyDataSetChanged();
            carregarMusicas(view);
            return true;
        });

        lvMusicas.setOnItemClickListener((adapterView, view2, i, l) -> {
            mainActivity.cadastrarMusicas((Genero)adapterView.getItemAtPosition(i));
        });
        carregarMusicas(view);
        return view;
    }

    private void carregarMusicas(View view) {
        MusicaDAL dal =new MusicaDAL(view.getContext());
        List<Musica> musicaList=dal.get("");
        lvMusicas.setAdapter(new ArrayAdapter<Musica>(view.getContext(),
                android.R.layout.simple_list_item_1,musicaList));
    }
}
