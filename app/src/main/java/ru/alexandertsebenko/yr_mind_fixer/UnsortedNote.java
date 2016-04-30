package ru.alexandertsebenko.yr_mind_fixer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
        Intent intentBack = new Intent(this,FixedUnsortedNotes.class);
        Intent intentForward = new Intent(this,RoomsToSort.class);
        switch (view.getId()) {
            case R.id.button_delete_in_note_activity:
                datasource.deleteTextNoteByID(tnoteID);
                startActivity(intentBack);
                break;
            case R.id.button_later_in_note_activity:
                startActivity(intentBack);
                break;
            case R.id.button_put_to_room_in_note_activity:
                intentForward.putExtras(b);//Передаём информацию о заметке в следующий активити
                startActivity(intentForward);
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


}
