package ru.alexandertsebenko.yr_mind_fixer;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class RoomsToSort extends ListActivity {

    private Bundle b;
    private TextView textView;
    private String tnote = null;
    private long tnoteID;
    private RoomsDataSource datasource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            b = getIntent().getExtras();
            tnote = b.getString("Note");
            setContentView(R.layout.activity_rooms_to_sort);

        datasource = new RoomsDataSource(this);
        datasource.open();
        List<Room> values = datasource.getAllRooms();
        ArrayAdapter<Room> adapter = new ArrayAdapter<Room>(this,
                R.layout.room_raw,
                R.id.label,
                values);
        setListAdapter(adapter);
    }
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Room item = (Room) getListAdapter().getItem(position);
        Intent intent = new Intent(this, UnsortedNote.class);
        Bundle b = new Bundle();
        b.putString("Note", item.getRoom());
        b.putLong("ID", item.getId());
        intent.putExtras(b);
        startActivity(intent);
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

