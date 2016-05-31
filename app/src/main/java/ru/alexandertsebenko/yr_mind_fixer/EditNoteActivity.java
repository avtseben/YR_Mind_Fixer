package ru.alexandertsebenko.yr_mind_fixer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class EditNoteActivity extends Activity {

    EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        editText = (EditText) findViewById(R.id.editText2);
        editText.setText(
                    getIntent().getStringExtra(AllNotesListActivity.KEY_TEXT_OF_NOTE)
                    );

    }
    public void onCancel(View view) {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }
    public void onSave(View view) {
        Intent intent = new Intent();
        intent.putExtra(AllNotesListActivity.KEY_TEXT_OF_NOTE, editText.getText().toString());
        setResult(RESULT_OK, intent);
        finish();
    }
}
