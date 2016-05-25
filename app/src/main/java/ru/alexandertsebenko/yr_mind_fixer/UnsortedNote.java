package ru.alexandertsebenko.yr_mind_fixer;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class UnsortedNote extends Activity {

    private TextNoteDataSource datasource;

    private Bundle b;
    private TextView textView;
    private String tnote = null;
    private long tnoteID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unsorted_note);
        b = getIntent().getExtras();
        textView = (TextView) findViewById(R.id.textView5);
        tnote = b.getString("Note");
        tnoteID = b.getLong("ID");
        textView.setText(tnote);
        datasource = new TextNoteDataSource(this);
        datasource.open();
    }

    public void onClick(View view) {
        @SuppressWarnings("unchecked")
        final Intent intentBack = new Intent(this, FixedUnsortedNotes.class);
        switch (view.getId()) {
            case R.id.button_delete_in_note_activity:
                createDialog(view);
                datasource.deleteTextNoteByID(tnoteID);
                startActivity(intentBack);
                break;
            case R.id.button_later_in_note_activity:
                Log.d("MyLog", "LATER PRESS");
                startActivity(intentBack);
                break;
        }
    }

    protected void onResume() {
        datasource.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        datasource.close();
        super.onPause();
    }

    public void createDialog(View view) {
        final Intent intentBack = new Intent(this, FixedUnsortedNotes.class);
        System.out.print("CreateDIALOG");
        new AlertDialog.Builder(view.getContext())
                .setTitle("Внимание!")
                .setMessage("Удалить эту заметку?")
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        datasource.deleteTextNoteByID(tnoteID);
                        Toast.makeText(UnsortedNote.this, "Хорошо, бывает, забыли...", Toast.LENGTH_SHORT).show();
                        startActivity(intentBack);
                    }
                })
                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(UnsortedNote.this, "Не будем удалять", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
