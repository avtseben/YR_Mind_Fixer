package ru.alexandertsebenko.yr_mind_fixer.ui.activity;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import ru.alexandertsebenko.yr_mind_fixer.R;
import ru.alexandertsebenko.yr_mind_fixer.db.NoteDataSource;
import ru.alexandertsebenko.yr_mind_fixer.ui.fragment.TextNoteFragment;


public class NoteActivity extends Activity {

    private NoteDataSource datasource;

    private Bundle b;
    private TextView textView;
    private String tnote = null;
    private String noteType = null;
    private long tnoteID;
    private final int REQUEST_CODE_ACTIVITY_EDIT_TEXT = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        b = getIntent().getExtras();
        tnote = b.getString(AllNotesListActivity.KEY_TEXT_OF_NOTE);
        tnoteID = b.getLong("ID");
        datasource = new NoteDataSource(this);
        datasource.open();
        noteType = datasource.getNoteTypeByID(tnoteID);
        //Fragment works
        switch (noteType) {
            case AllNotesListActivity.NOTE_TYPE_TEXT:
                TextNoteFragment tf = new TextNoteFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.add(R.id.fragment_container_in_note_activity, tf);
//                transaction.addToBackStack(null);//TODO разобраться как поступать с backStack если он есть то при нажати не кнопку назад заметка очищается, это не нужно.
                transaction.commit();
                tf.setTextOfNote(tnote);//Отдаём текст заметки фрагменту
                break;
            case AllNotesListActivity.NOTE_TYPE_FOTO:
                break;
            case AllNotesListActivity.NOTE_TYPE_AUDIO:
                break;
            case AllNotesListActivity.NOTE_TYPE_VIDEO:
                break;

        }
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
                startActivityForResult(intentToEdit, REQUEST_CODE_ACTIVITY_EDIT_TEXT);
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
    }*/


/*    @Override
    protected void onStop() {
        datasource.close();
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
                        if(noteType.equals(AllNotesListActivity.NOTE_TYPE_AUDIO) || noteType.equals(AllNotesListActivity.NOTE_TYPE_FOTO))
                            deleteFileByURI(Uri.parse(tnote));
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
    public static void deleteFileByURI(Uri uri){
        File file = new File(uri.getPath());
        try {
            boolean fileDeleted = file.delete();
        } catch (Exception e) {
            System.out.print(e);
        }
    }
}
