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

public class AllNotesListActivity extends ListActivity {
    private TextNoteDataSource datasource;

    final static int REQUEST_CODE_AVATAR_FOTO = 301;

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
//        Toast.makeText(AllNotesListActivity.this, "DB version: " + datasource.getDB().getVersion(), Toast.LENGTH_SHORT).show();
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
                    uri = prepareFileUri(FOTO_SUB_DIRECTORY, "test1.jpeg");//TODO как делать уникальное новое имя файла?
                    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(intent,REQUEST_CODE_AVATAR_FOTO);
                } else {
                    Toast.makeText(AllNotesListActivity.this,"Внешняя память недоступна! Не куда сохранять",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_add_audio:
                Toast.makeText(AllNotesListActivity.this,"audio",Toast.LENGTH_SHORT).show();
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
                case REQUEST_CODE_AVATAR_FOTO:
                    Toast.makeText(AllNotesListActivity.this,uri.toString(),Toast.LENGTH_SHORT).show();//TODO здесь все падает, почемуто uri = null
                    datasource.createTextNote(uri.toString(),"no_title",NOTE_TYPE_FOTO,System.currentTimeMillis());
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

        makeAdapter();
        super.onResume();
    }

    @Override
    protected void onPause() {
        datasource.close();
        super.onPause();
    }
}
