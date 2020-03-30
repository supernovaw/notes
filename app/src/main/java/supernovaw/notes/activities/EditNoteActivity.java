package supernovaw.notes.activities;

import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import supernovaw.notes.Note;
import supernovaw.notes.Notes;
import supernovaw.notes.R;

public class EditNoteActivity extends AppCompatActivity {
	public static final String NOTE_INDEX_EXTRA = "note_index";

	private Note note;

	private EditText title_input;
	private EditText text_input;
	private FloatingActionButton save_button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_note);
		initViews();
		initNote();

		save_button.setOnClickListener(v -> finish());
	}

	private void initViews() {
		title_input = findViewById(R.id.title_input);
		text_input = findViewById(R.id.text_input);
		save_button = findViewById(R.id.save_button);
	}

	private void initNote() {
		int noteIndex = getIntent().getIntExtra(NOTE_INDEX_EXTRA, -1);
		if (noteIndex == -1) {
			note = new Note("", "");
			Notes.addNote(note);
		} else {
			note = Notes.getNotes().get(noteIndex);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		title_input.setText(note.getTitle());
		text_input.setText(note.getText());
	}

	@Override
	protected void onPause() {
		super.onPause();
		note.update(title_input.getText().toString(), text_input.getText().toString());
	}
}
