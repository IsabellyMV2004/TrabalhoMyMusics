package com.example.alodrawermenu;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alodrawermenu.db.bean.Genero;
import com.example.alodrawermenu.db.bean.Musica;
import com.example.alodrawermenu.db.dal.GeneroDAL;
import com.example.alodrawermenu.db.dal.MusicaDAL;

import java.time.LocalDate;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NovaMusicaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NovaMusicaFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private EditText txtedInterp, txtedTitulo, etAno;
    private SeekBar sbDuracao;
    private Spinner spGenero;
    private TextView tvDuracao;
    private Button btCadastrar;
    private static Musica musicaAlterar = null;

    public NovaMusicaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NovaMusicaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NovaMusicaFragment newInstance(String param1, String param2) {
        NovaMusicaFragment fragment = new NovaMusicaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static void setMusicaAlterar(Musica musica){
        musicaAlterar=musica;
    }

    public static Musica getMusicaAlterar(){
        return musicaAlterar;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_nova_musica, container, false);
        txtedInterp = view.findViewById(R.id.txtedInterp);
        sbDuracao = view.findViewById(R.id.sbDuracao);
        spGenero = view.findViewById(R.id.spGenero);
        tvDuracao = view.findViewById(R.id.tvDuracao);
        txtedTitulo = view.findViewById(R.id.txtedTitulo);
        etAno = view.findViewById(R.id.etAno);
        btCadastrar = view.findViewById(R.id.btCadastrar);
        carregarGeneros(view);
        // --- preencher campos se estivermos em modo EDIÇÃO ---
        Musica musicaParaEditar = NovaMusicaFragment.getMusicaAlterar();

        if (musicaParaEditar != null) {
            // Preenche campos simples
            txtedTitulo.setText(musicaParaEditar.getTitulo() == null ? "" : musicaParaEditar.getTitulo());
            etAno.setText(musicaParaEditar.getAno() == 0 ? "" : String.valueOf(musicaParaEditar.getAno()));
            txtedInterp.setText(musicaParaEditar.getInterprete() == null ? "" : musicaParaEditar.getInterprete());

            // SeekBar / duração (supondo que sua duração é inteiro em segundos no sb)
            int duracaoSegundos = (int) musicaParaEditar.getDuracao();
            sbDuracao.setProgress(duracaoSegundos);
            int minutos = duracaoSegundos / 60;
            int segundos = duracaoSegundos % 60;
            tvDuracao.setText(String.format("%d:%02d", minutos, segundos));

            // Selecionar o gênero no Spinner — fazemos via post para garantir que o adapter
            // já esteja setado pelo carregarGeneros(view)
            final int generoId = musicaParaEditar.getGenero() == null ? -1 : musicaParaEditar.getGenero().getId();
            spGenero.post(() -> {
                try {
                    if (spGenero.getAdapter() == null) return; // adapter não criado ainda
                    for (int i = 0; i < spGenero.getAdapter().getCount(); i++) {
                        Object item = spGenero.getAdapter().getItem(i);
                        if (item instanceof Genero) {
                            Genero g = (Genero) item;
                            if (g.getId() == generoId) {
                                spGenero.setSelection(i);
                                break;
                            }
                        }
                    }
                } catch (Exception ex) {
                    // Proteção: se algo falhar aqui não quebramos o resto da tela
                    ex.printStackTrace();
                }
            });
        }

        btCadastrar.setOnClickListener(e -> {
            MusicaDAL dal = new MusicaDAL(view.getContext());

            // validação básica dos campos (evita NumberFormatException)
            String anoText = etAno.getText() == null ? "" : etAno.getText().toString().trim();
            int ano = 0;
            if (!anoText.isEmpty()) {
                try { ano = Integer.parseInt(anoText); }
                catch (NumberFormatException ex) { ano = 0; }
            }

            Genero generoSelecionado = null;
            try {
                Object sel = spGenero.getSelectedItem();
                if (sel instanceof Genero) generoSelecionado = (Genero) sel;
            } catch (Exception ex) { generoSelecionado = null; }

            int duracaoAtual = sbDuracao.getProgress();

            Musica musicaAtual = NovaMusicaFragment.getMusicaAlterar();
            if (musicaAtual != null) {
                // --- EDIÇÃO: atualiza o objeto e chama alterar() ---
                musicaAtual.setTitulo(txtedTitulo.getText().toString().trim());
                musicaAtual.setAno(ano);
                musicaAtual.setInterprete(txtedInterp.getText().toString().trim());
                musicaAtual.setDuracao(duracaoAtual);
                if (generoSelecionado != null) musicaAtual.setGenero(generoSelecionado);

                boolean ok = dal.alterar(musicaAtual);
                if (ok) {
                    // limpa o cache de edição para evitar reuso indesejado
                    NovaMusicaFragment.setMusicaAlterar(null);
                    Toast.makeText(getContext(), "Música atualizada com sucesso", Toast.LENGTH_SHORT).show();

                    if (getActivity() != null) getActivity().getSupportFragmentManager().popBackStack();
                } else {
                    Toast.makeText(getContext(), "Erro ao atualizar música", Toast.LENGTH_SHORT).show();
                }
            } else {
                cadastrarMusica(view);
            }
        });


        sbDuracao.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int minutos = i / 60;
                int segundos = i % 60;

                // Formata sempre com 2 dígitos nos segundos (ex.: 3:05)
                String tempoFormatado = String.format("%d:%02d", minutos, segundos);

                tvDuracao.setText(tempoFormatado);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btCadastrar.setOnClickListener(e -> {
            cadastrarMusica(view);
        });

        return view;
    }

    private void carregarGeneros(View view){
        GeneroDAL dal =new GeneroDAL(view.getContext());
        List<Genero> generoList=dal.get("");
        spGenero.setAdapter(new ArrayAdapter<Genero>(view.getContext(),
                android.R.layout.simple_spinner_dropdown_item,generoList));
    }

    private boolean verificarAno(){
        int anoAtual = LocalDate.now().getYear();
        int anoMusica = Integer.parseInt(etAno.getText().toString());
        if(anoMusica>anoAtual) {
            Toast.makeText(getContext(), "Digite um ano válido!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validarCampos() {
        String titulo = txtedTitulo.getText().toString().trim();
        String interprete = txtedInterp.getText().toString().trim();
        String anoStr = etAno.getText().toString().trim();
        int duracao = sbDuracao.getProgress();
        Genero generoSelecionado = (Genero) spGenero.getSelectedItem();

        if (titulo.isEmpty()) {
            Toast.makeText(getContext(), "Digite o título", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (interprete.isEmpty()) {
            Toast.makeText(getContext(), "Digite o intérprete", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (anoStr.isEmpty()) {
            Toast.makeText(getContext(), "Digite o ano", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            Integer.parseInt(anoStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Ano inválido", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (duracao <= 0) {
            Toast.makeText(getContext(), "Defina a duração da música", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (generoSelecionado == null) {
            Toast.makeText(getContext(), "Selecione um gênero", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true; // Todos os campos preenchidos corretamente
    }

    private boolean cadastrarMusica(View view){
        if(validarCampos() && verificarAno()) {
            Genero generoSelecionado = (Genero) spGenero.getSelectedItem();
            double duracaoMinutos = sbDuracao.getProgress() / 60.0;
            int ano;
            try {
                ano = Integer.parseInt(etAno.getText().toString());
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Digite um ano válido!", Toast.LENGTH_SHORT).show();
                return false;
            }
            Musica musica = new Musica(ano, txtedTitulo.getText().toString(), txtedInterp.getText().toString(), generoSelecionado, duracaoMinutos);
            MusicaDAL dal = new MusicaDAL(view.getContext());
            dal.salvar(musica);
            Toast.makeText(getContext(), "Música " + musica.getTitulo() + " cadastrada!", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

}