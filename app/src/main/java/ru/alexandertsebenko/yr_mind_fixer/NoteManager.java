package ru.alexandertsebenko.yr_mind_fixer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class NoteManager extends AppCompatActivity implements View.OnClickListener {

    Button sortBtn;
    Button searchBtn;
    Button toRoomBtn;
    Button addNoteBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_manager);

        sortBtn = (Button) findViewById(R.id.button);
        searchBtn = (Button) findViewById(R.id.button2);
        toRoomBtn = (Button) findViewById(R.id.button3);
        addNoteBtn = (Button) findViewById(R.id.button4);

        sortBtn.setOnClickListener(this);
        searchBtn.setOnClickListener(this);
        toRoomBtn.setOnClickListener(this);
        addNoteBtn.setOnClickListener(this);
    }


    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.button:
                Toast.makeText(this,"Sort",Toast.LENGTH_SHORT).show();
                intent = new Intent(this, FixedUnsortedNotes.class);
                startActivity(intent);
                break;
            case R.id.button2:
                Toast.makeText(this,"Search",Toast.LENGTH_SHORT).show();
                break;
            case R.id.button3:
                Toast.makeText(this,"Rooms",Toast.LENGTH_SHORT).show();
                intent = new Intent(this, RoomsManage.class);
                startActivity(intent);
                break;
            case R.id.button4:
                Toast.makeText(this, "AddNote", Toast.LENGTH_SHORT).show();
                intent = new Intent(this, WriteNote.class);
                startActivity(intent);
                break;
        }
    }
}
