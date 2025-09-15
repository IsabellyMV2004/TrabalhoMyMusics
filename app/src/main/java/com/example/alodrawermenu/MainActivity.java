package com.example.alodrawermenu;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.alodrawermenu.db.bean.Genero;
import com.example.alodrawermenu.db.bean.Musica;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FragmentManager fragmentManager;
    private FrameLayout frameLayout;
    private GenerosFragment generosFragment=new GenerosFragment();
    private NovaMusicaFragment novaMusicaFragment=new NovaMusicaFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawerLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        drawerLayout=findViewById(R.id.drawerLayout);
        navigationView=findViewById(R.id.navigationView);
        frameLayout=findViewById(R.id.frameLayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.open_menu,R.string.close_menu);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //açoes para os itens  do menu lateral

        fragmentManager=getSupportFragmentManager();
        navigationView.setNavigationItemSelectedListener(item->
        {
           if(item.getItemId()==R.id.it_nmusica){
               cadastrarMusicas(null);
           }
           if(item.getItemId()==R.id.it_lmusicas){}
           if(item.getItemId()==R.id.it_lgeneros){
               FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
               fragmentTransaction.replace(frameLayout.getId(),generosFragment);
               fragmentTransaction.commit();
           }
           if(item.getItemId()==R.id.it_fechar){
               finish();
           }
           drawerLayout.closeDrawer(GravityCompat.START);
           return true;
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }
    public void cadastrarMusicas(Genero musica)/* trocar o parâmetro para Musica*/{
        //caso música seja Null, suponha a inserção de uma nova
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(frameLayout.getId(),novaMusicaFragment);
        fragmentTransaction.commit();
    }
}