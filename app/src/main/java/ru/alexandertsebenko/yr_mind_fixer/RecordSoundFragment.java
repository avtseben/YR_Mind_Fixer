package ru.alexandertsebenko.yr_mind_fixer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.UUID;

import static ru.alexandertsebenko.yr_mind_fixer.R.color.colorAddNewBar;
import static ru.alexandertsebenko.yr_mind_fixer.R.color.colorPrimary;

public class RecordSoundFragment extends DialogFragment  implements DialogInterface.OnClickListener {

    Uri uri = null;
    boolean recordStarted = false;
    MediaRecorder recorder = new MediaRecorder();
//    String fileName;
    TextNoteDataSource datasource;
//    Button button;
    String LOG_TAG = "FragmentLog";


    public Dialog onCreateDialog(Bundle savedInstanceState) {
        startSoundRecord();
        setRetainInstance(true);
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
                .setTitle("Идет запись. Говорите ну!")
                .setPositiveButton(R.string.record_dialog_positive, this)
                .setNegativeButton(R.string.record_dialog_negative, this)
                .setMessage("Для остановки и сохранения нажмите ОК, если нехотите сохранять, нажмите Отмена");
        return adb.create();
    }

    public void onClick(DialogInterface dialog, int which) {
        int i = 0;
        switch (which) {
            case Dialog.BUTTON_POSITIVE:
                i = R.string.record_dialog_positive;
                stopAndSaveRecord();
                break;
            case Dialog.BUTTON_NEGATIVE:
                i = R.string.record_dialog_negative;
                releaseRecorder();
                break;
        }
        if (i > 0)
            Log.d(LOG_TAG, "Dialog 2: " + getResources().getString(i));
    }
/*
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
        setRetainInstance(true);
        return v;
    }*/
    public void startSoundRecord() {
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
    }
    private void stopAndSaveRecord(){
      if (recorder != null) {
          try {
              recorder.stop();
              datasource = new TextNoteDataSource(getActivity().getApplicationContext());
              datasource.open();
              datasource.createTextNote(uri.toString(), null, AllNotesListActivity.NOTE_TYPE_AUDIO, System.currentTimeMillis());
              Log.d(LOG_TAG, "record OK");
              datasource.close();
          } catch (Exception e) {
              e.printStackTrace();
          }
          recordStarted = false;
      }
    }
    private void releaseRecorder() {
        if (recorder != null) {
            recorder.release();
            recorder = null;
            Log.d(LOG_TAG, "record Canceled");
        }
    }
    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage(null);
        }
        super.onDestroyView();
    }
}
