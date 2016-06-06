package ru.alexandertsebenko.yr_mind_fixer;


import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class AllNotesListActivity extends Activity {
    private TextNoteDataSource datasource;

    final static int REQUEST_CODE_TAKE_FOTO = 301;
    final static int REQUEST_CODE_RECORD_AUDIO = 302;

    final static String PUBLIC_APP_DIRECTORY = "MindFixerFiles";
    final static String FOTO_SUB_DIRECTORY = "foto";
    final static String AUDIO_SUB_DIRECTORY = "audio";
    final static String VIDEO_SUB_DIRECTORY = "video";
    final static String APP_LOG_TAG = "app_log_tag";

    final static String KEY_TEXT_OF_NOTE = "textOfNote";
    final static String NOTE_TYPE_TEXT = "text";
    final static String NOTE_TYPE_AUDIO = "audio";
    final static String NOTE_TYPE_FOTO = "foto";
    final static String NOTE_TYPE_VIDEO = "video";

    NoteAdapter noteAdapter;
    Uri uri = null;
    MediaRecorder recorder = new MediaRecorder();
    boolean recordStarted = false;
//    String fileName;
    RecordSoundFragment soundRecordDialog;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_all_notes);

//        fileName = Environment.getExternalStorageDirectory() + "/record.3gpp";
        datasource = new TextNoteDataSource(this);
        datasource.open();
        //makeAdapter();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }
/*    public void makeAdapter() {
        List<TextNote> values = datasource.getAllTextNotes();
        ArrayAdapter<TextNote> adapter = new ArrayAdapter<TextNote>(this,
                R.layout.note_raw,
                R.id.label,
                values);
        setListAdapter(adapter);
    }*/
    public void makeAdapter() {
        List<TextNote> values = datasource.getAllTextNotes();
        noteAdapter = new NoteAdapter(this, values);
        ListView listView = (ListView)findViewById(R.id.lvMain);
        listView.setAdapter(noteAdapter);
    }
/*    protected void onListItemClick(ListView l, View v, int position, long id) {
        TextNote item = (TextNote) getListAdapter().getItem(position);
        Intent intent = new Intent(this, NoteActivity.class);
        Bundle b = new Bundle();
        b.putString(KEY_TEXT_OF_NOTE, item.getTextNote());
        b.putLong("ID", item.getId());
        intent.putExtras(b);
        startActivity(intent);
    }*/

    public void onClick(View view) throws IOException {
        Intent intent;
        switch (view.getId()) {
            case R.id.btn_add_video:
                Toast.makeText(AllNotesListActivity.this, view.getContext().toString(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_add_foto:
                Toast.makeText(AllNotesListActivity.this, "Завожу фотоаппарат. Пару секунд..", Toast.LENGTH_SHORT).show();
                intent = new Intent();
                if (isExternalStorageWritable()) {
                    String randomFileName = UUID.randomUUID().toString();
                    randomFileName = new StringBuffer(randomFileName).append(".jpeg").toString();
                    uri = prepareFileUri(FOTO_SUB_DIRECTORY, randomFileName);//получаем uri, оно нужно нам для ссылки из БД
                    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//намерение на фотокамеру
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);//указываем куда сохранить
                    startActivityForResult(intent, REQUEST_CODE_TAKE_FOTO);//Запускаем фото
                } else {
                    Toast.makeText(AllNotesListActivity.this, "Внешняя память недоступна! Не куда сохранять", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_add_audio:
                soundRecordDialog = new RecordSoundFragment();
                soundRecordDialog.setCancelable(false);
                soundRecordDialog.show(getFragmentManager(),"RECORD");
                break;
            case R.id.btn_add_note:
                Toast.makeText(AllNotesListActivity.this, "note", Toast.LENGTH_SHORT).show();
                intent = new Intent(this, WriteNoteActivity.class);
                startActivity(intent);
                break;
        }
    }
    protected static Uri prepareFileUri(String album, String filename) {
        Uri uri = null;
        try {
            File path = getAppStorageDir(album);
            File fotoFile = new File(path.getCanonicalPath(), filename);
            uri = Uri.fromFile(fotoFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uri;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_TAKE_FOTO:
                    datasource.open();//дополнительно открываем доступ к базе
                    datasource.createTextNote(uri.toString(), null, NOTE_TYPE_FOTO, System.currentTimeMillis());
                    break;
                case REQUEST_CODE_RECORD_AUDIO:
                    break;
            }
        }

    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static File getAppStorageDir(String albumName) throws IOException {
        File file = new File(Environment.getExternalStoragePublicDirectory(
                PUBLIC_APP_DIRECTORY), albumName);
        if (!file.mkdirs()) {
            Log.v(APP_LOG_TAG, "Directory not created, maybe already exist");
        }
        return file;
    }

    protected void onResume() {
        datasource.open();
        Toast.makeText(AllNotesListActivity.this, "onResume1", Toast.LENGTH_SHORT).show();
        makeAdapter();//формируем list
        super.onResume();
    }

    @Override
    protected void onPause() {
        datasource.close();
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (uri != null)
            outState.putString("uri", uri.toString());
/*        if (recordStarted) {
            outState.putBoolean("recordStarted", recordStarted);
            outState.putParcelable("recorder", (Parcelable) recorder);
        }*/

    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        Toast.makeText(AllNotesListActivity.this, "onRestoreInstanceState", Toast.LENGTH_SHORT).show();
        if (state.getString("uri") != null)
            uri = Uri.parse(state.getString("uri"));
/*        if (state.getBoolean("recordStarted")) {
            recordStarted = state.getBoolean("recordStarted");
            recorder = (MediaRecorder) state.getParcelable("recorder");
        }*/
    }

/*    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "AllNotesList Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://ru.alexandertsebenko.yr_mind_fixer/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "AllNotesList Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://ru.alexandertsebenko.yr_mind_fixer/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }*/
}
