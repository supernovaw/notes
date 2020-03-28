package supernovaw.notes.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import supernovaw.notes.PinCodeManager;
import supernovaw.notes.R;

public class NotesListActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notes_list);

		PinCodeManager.init(this);
	}
}
