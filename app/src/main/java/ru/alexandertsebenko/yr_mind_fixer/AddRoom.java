package ru.alexandertsebenko.yr_mind_fixer;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddRoom extends Activity {

    private RoomsDataSource datasource;
    EditText edtext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_room);

        datasource = new RoomsDataSource(this);
        datasource.open();

    }
    public void onClick(View view) {
        @SuppressWarnings("unchecked")
        TextNote tnote = null;
        switch (view.getId()) {
            case R.id.button_add_room:
                edtext = (EditText) findViewById(R.id.editText);
                String note = edtext.getText().toString();
                Toast.makeText(this,"Комната готова",Toast.LENGTH_SHORT).show();//TODO out from hardcode
                edtext.setText("");
                datasource.createRoom(note);
                break;
        }
    }
}

