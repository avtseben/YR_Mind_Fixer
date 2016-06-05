package ru.alexandertsebenko.yr_mind_fixer;

import android.app.Fragment;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.UUID;

public class RecordSoundFragment extends Fragment {

    Uri uri = null;
    boolean recordStarted = false;
    MediaRecorder recorder = new MediaRecorder();
    String fileName;
    TextNoteDataSource datasource;
    Button button;
    String LOG_TAG = "FragmentLog";


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Fragment1 onCreateView");
        View v = inflater.inflate(R.layout.fragment_sound_record, null);
        datasource = new TextNoteDataSource(v.getContext());
        button = (Button)v.findViewById(R.id.button_fragment_record);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(LOG_TAG, "Button click in Fragment1");
                soundRecord();
            }
        });
        return v;
    }
    public void soundRecord() {
        if (!recordStarted) {
            recordStarted = true;
            try {
                releaseRecorder();
                String randomFileName = UUID.randomUUID().toString();
                randomFileName = new StringBuffer(randomFileName).append(".3gpp").toString();
                uri = AllNotesListActivity.prepareFileUri(AllNotesListActivity.AUDIO_SUB_DIRECTORY, randomFileName);//получаем uri, оно нужно нам для ссылки из БД
                recorder = new MediaRecorder();
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                recorder.setOutputFile(uri.getPath());
                recorder.prepare();
                recorder.start();//TODO отработать onSaveInstate
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            if (recorder != null) {
                try {
                    recorder.stop();
                    datasource.open();
                    datasource.createTextNote(uri.toString(), null, AllNotesListActivity.NOTE_TYPE_AUDIO, System.currentTimeMillis());
                    datasource.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                recordStarted = false;
            }
        }
    }
    private void releaseRecorder() {
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }
    }
}
