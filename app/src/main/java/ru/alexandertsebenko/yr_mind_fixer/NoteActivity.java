package ru.alexandertsebenko.yr_mind_fixer;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class NoteActivity extends Activity {

    private TextNoteDataSource datasource;

    private Bundle b;
    private TextView textView;
    private String tnote = null;
    private long tnoteID;
    private final int REQUEST_CODE_ACTIVITY_EDIT_TEXT = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        b = getIntent().getExtras();
        textView = (TextView) findViewById(R.id.note_text);
        tnote = b.getString(AllNotesListActivity.KEY_TEXT_OF_NOTE);
        tnoteID = b.getLong("ID");
        textView.setText(tnote);
        datasource = new TextNoteDataSource(this);
        datasource.open();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_delete_in_note_activity:
                createDialog(view);
                break;
            case R.id.button_later_in_note_activity:
                final Intent intentBack = new Intent(this, AllNotesListActivity.class);
                startActivity(intentBack);
                break;
            case R.id.button_edit_in_note_activity:
                Intent intentToEdit = new Intent(this, EditNoteActivity.class);
                intentToEdit.putExtra(AllNotesListActivity.KEY_TEXT_OF_NOTE, textView.getText().toString());
                startActivityForResult(intentToEdit,REQUEST_CODE_ACTIVITY_EDIT_TEXT);
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {//TODO упростить, сделать без активити реза
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_ACTIVITY_EDIT_TEXT:
                    String newText = data.getStringExtra(AllNotesListActivity.KEY_TEXT_OF_NOTE);
                    textView.setText(newText);
                    datasource.updateTextNoteById(tnoteID, newText);
                    break;
            }
        }

        }

 /*   @Override
    protected void onResume() {
  //      datasource.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
//        datasource.close();
        super.onPause();
    }*/

    public void createDialog(View view) {
        final Intent intentBack = new Intent(this, AllNotesListActivity.class);
        new AlertDialog.Builder(view.getContext())
                .setTitle(getString(R.string.dialog_alert))
                .setMessage(getString(R.string.dialog_mess_ask_for_delete))
                .setPositiveButton(getString(R.string.text_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        datasource.deleteTextNoteByID(tnoteID);
                        Toast.makeText(NoteActivity.this, R.string.toast_delete, Toast.LENGTH_SHORT).show();
                        startActivity(intentBack);
                    }
                })
                .setNegativeButton(getString(R.string.text_no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(NoteActivity.this, R.string.toast_dont_delete, Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }
}
