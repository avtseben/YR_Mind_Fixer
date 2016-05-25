package ru.alexandertsebenko.yr_mind_fixer;


import android.app.ActionBar;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class FixedUnsortedNotes extends ListActivity {
    private TextNoteDataSource datasource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fixed_unsorted_notes);

        datasource = new TextNoteDataSource(this);
        datasource.open();
        List<TextNote> values = datasource.getAllTextNotes();
        ArrayAdapter<TextNote> adapter = new ArrayAdapter<TextNote>(this,
                R.layout.note_raw,
                R.id.label,
                values);
        setListAdapter(adapter);
    }
    protected void onListItemClick(ListView l, View v, int position, long id) {
        TextNote item = (TextNote) getListAdapter().getItem(position);
        Intent intent = new Intent(this, UnsortedNote.class);
        Bundle b = new Bundle();
        b.putString("Note", item.getTextNote());
        b.putLong("ID", item.getId());
        intent.putExtras(b);
        startActivity(intent);
    }
    public void onClick(View view){
        Intent intent;
        switch (view.getId()) {
            case R.id.btn_add_video:
                Toast.makeText(FixedUnsortedNotes.this,view.getContext().toString(),Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_add_foto:
                Toast.makeText(FixedUnsortedNotes.this,"foto",Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_add_audio:
                Toast.makeText(FixedUnsortedNotes.this,"audio",Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_add_note:
                Toast.makeText(FixedUnsortedNotes.this,"note",Toast.LENGTH_SHORT).show();
                intent = new Intent(this, WriteNote.class);
                startActivity(intent);
                break;
        }
    }
    protected void onResume() {
        datasource.open();
        List<TextNote> values = datasource.getAllTextNotes();
        ArrayAdapter<TextNote> adapter = new ArrayAdapter<TextNote>(this,
                R.layout.note_raw,
                R.id.label,
                values);
        setListAdapter(adapter);
        super.onResume();
    }

    @Override
    protected void onPause() {
        datasource.close();
        super.onPause();
    }
}
