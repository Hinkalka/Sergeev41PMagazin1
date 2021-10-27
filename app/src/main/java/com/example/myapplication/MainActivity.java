package com.example.myapplication;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button btnAdd, btnRead;
    EditText etSumm, etNaz, etCena;

    DBHelper dbHelper;
    SQLiteDatabase database;
    ContentValues contentValues;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);

        btnRead = (Button) findViewById(R.id.btnRead);
        btnRead.setOnClickListener(this);

        etSumm = (EditText) findViewById(R.id.etSumm);
        etNaz = (EditText) findViewById(R.id.etNaz);

        dbHelper = new DBHelper(this);
        database = dbHelper.getWritableDatabase();

        UpdateTable();
    }
    public void setBtnAdd(){
        btnAdd.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        Toast.makeText(MainActivity.this, etSumm.getText(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
    public void UpdateTable(){
        Cursor cursor = database.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            int cenaIndex = cursor.getColumnIndex(DBHelper.KEY_CENA);
            int nazIndex = cursor.getColumnIndex(DBHelper.KEY_NAZ);
            TableLayout dbOutput = findViewById(R.id.dbOutput);
            dbOutput.removeAllViews();
            do {
                TableRow dbOutputRow = new TableRow(this);
                dbOutputRow.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT);
                TextView outputeID = new TextView(this);
                params.weight = 1.0f;
                outputeID.setLayoutParams(params);
                outputeID.setText(cursor.getString(idIndex));
                dbOutputRow.addView(outputeID);

                TextView outputeName = new TextView(this);
                params.weight = 3.0f;
                outputeName.setLayoutParams(params);
                outputeName.setText(cursor.getString(nazIndex));
                dbOutputRow.addView(outputeName);

                TextView outputeMail =  new TextView(this);
                params.weight = 3.0f;
                outputeMail.setLayoutParams(params);
                outputeMail.setText(cursor.getString(cenaIndex));
                dbOutputRow.addView(outputeMail);

                Button deleteBtn = new Button(this);
                deleteBtn.setOnClickListener(this);
                params.weight = 1.0f;
                deleteBtn.setLayoutParams(params);
                deleteBtn.setText("Удалить товар");
                deleteBtn.setId(cursor.getInt(idIndex));
                dbOutputRow.addView(deleteBtn);

                Button korBtn = new Button(this);
                korBtn.setOnClickListener(this);
                params.weight = 1.0f;
                korBtn.setLayoutParams(params);
                korBtn.setText("Добавить в корзину");
                korBtn.setId(cursor.getInt(idIndex));
                dbOutputRow.addView(korBtn);


                dbOutput.addView(dbOutputRow);

            }
            while (cursor.moveToNext());
        }
        cursor.close();
    }
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btnAdd:
                //String name = etName.getText().toString();
                String naz = etNaz.getText().toString();
                String cena = etCena.getText().toString();
                contentValues = new ContentValues();
                contentValues.put(DBHelper.KEY_NAZ, naz);
                contentValues.put(DBHelper.KEY_CENA, cena);

                database.insert(DBHelper.TABLE_CONTACTS, null, contentValues);
                UpdateTable();
                break;

            default:
                View outputDBRow = (View) v.getParent();
                ViewGroup outputDB = (ViewGroup) outputDBRow.getParent();
                outputDB.removeView(outputDBRow);
                outputDB.invalidate();
                database.delete(DBHelper.TABLE_CONTACTS, DBHelper.KEY_ID + " = ?", new String[]{String.valueOf((v.getId()))});
                contentValues = new ContentValues();
                Cursor cursorUpdater = database.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);
                if (cursorUpdater.moveToFirst()) {
                    int idIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_ID);
                    int mailIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_NAZ);
                    int licenseIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_CENA);
                    int realId=1;
                    do{
                        if(cursorUpdater.getInt(idIndex)>realId)
                        {
                            contentValues.put(DBHelper.KEY_ID, realId);
                            contentValues.put(DBHelper.KEY_NAZ,cursorUpdater.getString(mailIndex));
                            contentValues.put(DBHelper.KEY_CENA, cursorUpdater.getString(licenseIndex));
                            database.replace(DBHelper.TABLE_CONTACTS,null,contentValues);
                        }
                        realId++;
                    }
                    while (cursorUpdater.moveToNext());
                    if (cursorUpdater.moveToLast() && v.getId()!=realId)
                    {
                        database.delete(DBHelper.TABLE_CONTACTS,DBHelper.KEY_ID+" = ?",new String[]{cursorUpdater.getString(idIndex)});
                    }
                    UpdateTable();
                    break;
                }
                dbHelper.close();
        }
    }}
