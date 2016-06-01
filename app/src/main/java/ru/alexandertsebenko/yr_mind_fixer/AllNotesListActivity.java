package ru.alexandertsebenko.yr_mind_fixer;


import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class AllNotesListActivity extends ListActivity {
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

    Uri uri = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_all_notes);

        datasource = new TextNoteDataSource(this);
        datasource.open();
        makeAdapter();

    }
    private void makeAdapter() {
        List<TextNote> values = datasource.getAllTextNotes();
        ArrayAdapter<TextNote> adapter = new ArrayAdapter<TextNote>(this,
                R.layout.note_raw,
                R.id.label,
                values);
        setListAdapter(adapter);
    }
    protected void onListItemClick(ListView l, View v, int position, long id) {
        TextNote item = (TextNote) getListAdapter().getItem(position);
        Intent intent = new Intent(this, NoteActivity.class);
        Bundle b = new Bundle();
        b.putString(KEY_TEXT_OF_NOTE, item.getTextNote());
        b.putLong("ID", item.getId());
        intent.putExtras(b);
        startActivity(intent);
    }
    public void onClick(View view){
        Intent intent;
        switch (view.getId()) {
            case R.id.btn_add_video:
                Toast.makeText(AllNotesListActivity.this,view.getContext().toString(),Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_add_foto:
                Toast.makeText(AllNotesListActivity.this,"Завожу фотоаппарат. Пару секунд..",Toast.LENGTH_SHORT).show();
                intent = new Intent();
                if(isExternalStorageWritable()) {
                    String randomFileName = UUID.randomUUID().toString();
                    randomFileName = new StringBuffer(randomFileName).append(".jpeg").toString();
                    uri = prepareFileUri(FOTO_SUB_DIRECTORY, randomFileName);//получаем uri, оно нужно нам для ссылки из БД
                    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//намерение на фотокамеру
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);//указываем куда сохранить
                    startActivityForResult(intent,REQUEST_CODE_TAKE_FOTO);//Запускаем фото
                } else {
                    Toast.makeText(AllNotesListActivity.this,"Внешняя память недоступна! Не куда сохранять",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_add_audio:
                Toast.makeText(AllNotesListActivity.this,"audio",Toast.LENGTH_SHORT).show();
                intent = new Intent();
                if(isExternalStorageWritable()) {
                    String randomFileName = UUID.randomUUID().toString();
                    randomFileName = new StringBuffer(randomFileName).append(".mp3").toString();
                    uri = prepareFileUri(AUDIO_SUB_DIRECTORY, randomFileName);//получаем uri, оно нужно нам для ссылки из БД
                    intent.setAction(MediaStore.Audio.Media.RECORD_SOUND_ACTION);//намерение на фотокамеру
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);//указываем куда сохранить
                    startActivityForResult(intent,REQUEST_CODE_RECORD_AUDIO);//Запускаем фото
                } else {
                    Toast.makeText(AllNotesListActivity.this,"Внешняя память недоступна! Не куда сохранять",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_add_note:
                Toast.makeText(AllNotesListActivity.this,"note",Toast.LENGTH_SHORT).show();
                intent = new Intent(this, WriteNoteActivity.class);
                startActivity(intent);
                break;
        }
    }
    protected Uri prepareFileUri(String album, String filename) {
        Uri uri = null;
        try {
            File path = getAppStorageDir(album);
            File fotoFile = new File (path.getCanonicalPath(), filename);
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
                    datasource.open();//TODO дополнительно открываем доступ к базе
                    datasource.createTextNote(uri.toString(),null,NOTE_TYPE_FOTO,System.currentTimeMillis());
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
    public File getAppStorageDir(String albumName) throws IOException {
        File file = new File(Environment.getExternalStoragePublicDirectory(
                PUBLIC_APP_DIRECTORY), albumName);
        if (!file.mkdirs()) {
            Log.v(APP_LOG_TAG, "Directory not created, maybe already exist");
        }
        return file;
    }
    protected void onResume() {
        datasource.open();
        Toast.makeText(AllNotesListActivity.this,"onResume1",Toast.LENGTH_SHORT).show();
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
        if(uri != null)
            outState.putString("uri",uri.toString());
    }
    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        Toast.makeText(AllNotesListActivity.this,"onRestoreInstanceState",Toast.LENGTH_SHORT).show();
        if(state.getString("uri") != null) {
            uri = Uri.parse(state.getString("uri"));
        }
    }
}
