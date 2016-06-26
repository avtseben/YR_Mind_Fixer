package ru.alexandertsebenko.yr_mind_fixer.ui.activity;

import android.app.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.io.File;

import ru.alexandertsebenko.yr_mind_fixer.R;
import ru.alexandertsebenko.yr_mind_fixer.db.NoteDataSource;
import ru.alexandertsebenko.yr_mind_fixer.ui.fragment.ImageFragment;
import ru.alexandertsebenko.yr_mind_fixer.ui.fragment.PlaySoundFragment;
import ru.alexandertsebenko.yr_mind_fixer.ui.fragment.TextNoteFragment;
import ru.alexandertsebenko.yr_mind_fixer.util.Log_YR;


public class NoteActivity extends AppCompatActivity {

    private NoteDataSource datasource;

    private Bundle b;
    private String tnote = null;
    private String uri = null;
    private String noteType = null;
    private long tnoteID;
    private final int REQUEST_CODE_ACTIVITY_EDIT_TEXT = 300;
    private final String NOTE_ID_KEY = "NOTE_ID";
    private final String TEXT_FRAGMENT_TAG = "textFragmentTag";
    private Log_YR log = new Log_YR(getClass().toString());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log.v("NoteActivity created");
        setContentView(R.layout.activity_note);
        b = getIntent().getExtras();
        tnoteID = b.getLong(AllNotesListActivity.KEY_ID);
        datasource = new NoteDataSource(this);
        datasource.open();
        noteType = datasource.getNoteTypeByID(tnoteID);
        tnote = datasource.getNoteTextByID(tnoteID);
        //Fragment works
        setFragment(noteType, tnote);
    }

    public void setFragment(String noteType, String noteText) {
        log.v("setFragment called");
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = supportFragmentManager.beginTransaction();
//        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        switch (noteType) {
            case AllNotesListActivity.NOTE_TYPE_TEXT:
                log.v("setFragment TEXT TYPE");
                TextNoteFragment tf = new TextNoteFragment();
                transaction.add(R.id.fragment_container_in_note_activity, tf, TEXT_FRAGMENT_TAG);
//                transaction.addToBackStack(null);//TODO разобраться как поступать с backStack если он есть то при нажати не кнопку назад заметка очищается, это не нужно.
                transaction.commit();
                tf.setTextOfNote(tnote);//Отдаём текст заметки фрагменту
                break;
            case AllNotesListActivity.NOTE_TYPE_FOTO://TODO разобраться почему при смене ориентации выскакивает NullPointException в ImageFragment.onStart
                uri = tnote;//Если заметка фотографическая то в качестве текста франиться uri ссылка на файл фотографии
                ImageFragment imageFragment = new ImageFragment();
                imageFragment.setFileByStringUri(uri);
                transaction.add(R.id.fragment_container_in_note_activity, imageFragment);
                transaction.commit();
                break;
            case AllNotesListActivity.NOTE_TYPE_AUDIO:
                log.v("set fragment audio");
                uri = tnote;
                PlaySoundFragment psf = new PlaySoundFragment();
                transaction.add(R.id.fragment_container_in_note_activity, psf);
                transaction.commit();
                psf.setMediaPlayerSource(uri);
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
                onBackPressed();
                break;
            case R.id.button_edit_in_note_activity:
                Intent intentToEdit = new Intent(this, EditNoteActivity.class);
                intentToEdit.putExtra(AllNotesListActivity.KEY_TEXT_OF_NOTE, tnote);
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
                    log.v("Edit text Result");
                    //Update in storage
                    datasource.open();
                    datasource.updateTextNoteById(tnoteID, newText);
                    //Refresh current text to see it
                    TextNoteFragment tf = (TextNoteFragment) getSupportFragmentManager().findFragmentByTag(TEXT_FRAGMENT_TAG);
                    tf.setTextOfNote(newText);
                    break;
            }
        }

    }
   @Override
    protected void onStart() {
        log.v("NoteActivity started");
        super.onStart();
    }
    protected void onResume() {
        log.v("NoteActivity resumed");
        super.onResume();
    }
    @Override
    protected void onPause() {
        datasource.close();
        log.v("NoteActivity paused");
        super.onPause();
    }
    @Override
    protected void onStop() {
        log.v("NoteActivity stoped");
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        log.v("NoteActivity destroyed");
        super.onDestroy();
    }
    public void createDialog(View view) {
        final Intent intentBack = new Intent(this, AllNotesListActivity.class);
        new AlertDialog.Builder(view.getContext())
                .setTitle(getString(R.string.dialog_alert))
                .setMessage(getString(R.string.dialog_mess_ask_for_delete))
                .setPositiveButton(getString(R.string.text_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        log.v("Delete note clicked");
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Сохранем ссылку на заметку, только ID
        if (tnoteID > 0)
            outState.putLong(NOTE_ID_KEY, tnoteID);
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        if (state.getLong(NOTE_ID_KEY) > 0) {
            tnoteID = state.getLong(NOTE_ID_KEY);
            Toast.makeText(NoteActivity.this, "note id " + tnoteID +" restored",Toast.LENGTH_SHORT).show();
        }
    }
}
