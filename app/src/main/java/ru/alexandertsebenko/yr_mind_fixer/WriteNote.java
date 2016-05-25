package ru.alexandertsebenko.yr_mind_fixer;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;
import java.util.List;

public class WriteNote extends Activity {

    private TextNoteDataSource datasource;
    EditText edtext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_note);

        datasource = new TextNoteDataSource(this);
        datasource.open();

    }
    public void onClick(View view) {
        @SuppressWarnings("unchecked")
        Intent intentBack = new Intent(this,FixedUnsortedNotes.class);
        TextNote tnote = null;
        switch (view.getId()) {
            case R.id.add:
                edtext = (EditText) findViewById(R.id.editText);
                String note = edtext.getText().toString();
                Toast.makeText(this,"Сохранил",Toast.LENGTH_SHORT).show();//TODO out from hardcode
                edtext.setText("");
                datasource.createTextNote(note);//Заносим в БД
                startActivity(intentBack);
                break;
        }
    }
}

