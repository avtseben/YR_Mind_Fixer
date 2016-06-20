package ru.alexandertsebenko.yr_mind_fixer.ui.activity;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import ru.alexandertsebenko.yr_mind_fixer.ui.adapter.NoteAdapter;
import ru.alexandertsebenko.yr_mind_fixer.R;
import ru.alexandertsebenko.yr_mind_fixer.ui.fragment.RecordSoundFragment;
import ru.alexandertsebenko.yr_mind_fixer.datamodel.TextNote;
import ru.alexandertsebenko.yr_mind_fixer.db.TextNoteDataSource;

public class AllNotesListActivity extends Activity {
    private TextNoteDataSource datasource;

    public final static int REQUEST_CODE_TAKE_FOTO = 301;
    public final static int REQUEST_CODE_RECORD_AUDIO = 302;

    public final static String PUBLIC_APP_DIRECTORY = "MindFixerFiles";
    public final static String FOTO_SUB_DIRECTORY = "foto";
    public final static String AUDIO_SUB_DIRECTORY = "audio";
    public final static String VIDEO_SUB_DIRECTORY = "video";
    public final static String APP_LOG_TAG = "app_log_tag";

    public final static String KEY_TEXT_OF_NOTE = "textOfNote";
    public final static String NOTE_TYPE_TEXT = "text";
    public final static String NOTE_TYPE_AUDIO = "audio";
    public final static String NOTE_TYPE_FOTO = "foto";
    public final static String NOTE_TYPE_VIDEO = "video";

    NoteAdapter noteAdapter;
    Uri uri = null;
    RecordSoundFragment soundRecordDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_all_notes);

        datasource = new TextNoteDataSource(this);
        datasource.open();
    }
    public void makeAdapter() {
        List<TextNote> values = datasource.getAllTextNotes();
        noteAdapter = new NoteAdapter(this, values);
        ListView listView = (ListView)findViewById(R.id.lvMain);
        listView.setAdapter(noteAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                TextNote item = (TextNote) parent.getItemAtPosition(position);
                Intent intent = new Intent(AllNotesListActivity.this, NoteActivity.class);
                Bundle b = new Bundle();
                b.putString(KEY_TEXT_OF_NOTE, item.getTextNote());
                b.putLong("ID", item.getId());
                intent.putExtras(b);
                startActivity(intent);
            }});
        }

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
                intent = new Intent(this, WriteNoteActivity.class);
                startActivity(intent);
                break;
        }
    }
    public static Uri prepareFileUri(String album, String filename) {
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
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        if (state.getString("uri") != null)
            uri = Uri.parse(state.getString("uri"));
    }
}
