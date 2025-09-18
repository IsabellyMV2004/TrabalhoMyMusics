package com.example.alodrawermenu.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBMySongs extends SQLiteOpenHelper {
    private static final int VERSAO = 3;
    public DBMySongs(Context context) {
        super(context, "mysongs.db", null, 3);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE genero( gen_id INTEGER PRIMARY KEY AUTOINCREMENT, gen_nome VARCHAR (15));");

        db.execSQL("CREATE TABLE musica " +
           "(mus_id  INTEGER PRIMARY KEY AUTOINCREMENT, " +
           "mus_ano INTEGER, mus_titulo VARCHAR (40), " +
           "mus_interprete VARCHAR (30), " +
           "mus_genero INTEGER REFERENCES genero (gen_id), " +
           "mus_duracao NUMERIC (4, 1) );");
        db.execSQL("INSERT INTO genero (gen_nome) VALUES ('rock nacional')");
        db.execSQL("INSERT INTO genero (gen_nome) VALUES ('rock internacional')");
        db.execSQL("INSERT INTO genero (gen_nome) VALUES ('sertanejo')");
        db.execSQL("INSERT INTO genero (gen_nome) VALUES ('pop nacional')");
        db.execSQL("INSERT INTO genero (gen_nome) VALUES ('pop internacional')");

        db.execSQL("INSERT INTO musica (mus_ano, mus_titulo, mus_interprete, mus_genero, mus_duracao)" +
                "VALUES (1986, 'Tempo Perdido', 'Legião Urbana', 1, 4.2)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL ("DROP TABLE IF EXISTS musica");
        db.execSQL ("DROP TABLE IF EXISTS genero");
        onCreate(db);
        
    }
}

