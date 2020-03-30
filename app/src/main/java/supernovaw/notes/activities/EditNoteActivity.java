package supernovaw.notes.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;

import supernovaw.notes.Note;
import supernovaw.notes.Notes;
import supernovaw.notes.R;

public class EditNoteActivity extends AppCompatActivity {
	public static final String NOTE_INDEX_EXTRA = "note_index";

	private Note note;

	private EditText title_input;
	private EditText text_input;
	private Button deadline_button;
	private FloatingActionButton save_button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_note);
		initViews();
		initNote();

		save_button.setOnClickListener(v -> finish());
		deadline_button.setOnClickListener(v -> pickDeadline());
		deadline_button.setOnLongClickListener(v -> removeDeadline());
	}

	private void initViews() {
		title_input = findViewById(R.id.title_input);
		text_input = findViewById(R.id.text_input);
		deadline_button = findViewById(R.id.deadline_button);
		save_button = findViewById(R.id.save_button);
	}

	// gets the note to work with from getIntent or makes a new one
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
		note.onAccess();
		title_input.setText(note.getTitle());
		text_input.setText(note.getText());
		deadline_button.setText(getDeadlineButtonText());
	}

	@Override
	protected void onPause() {
		super.onPause();
		note.update(title_input.getText().toString(), text_input.getText().toString());
	}

	@Override
	protected void onStop() {
		super.onStop();
		Notes.save(this);
	}

	// shows date and time pickers to choose a deadline
	private void pickDeadline() {
		Calendar picked = Calendar.getInstance();
		if (note.hasDeadline()) {
			picked.setTimeInMillis(note.getDeadline());
		}
		// as there is no option to set S and MS, reset them
		picked.set(Calendar.SECOND, 0);
		picked.set(Calendar.MILLISECOND, 0);
		// values to use in pickers by default
		int defYear = picked.get(Calendar.YEAR);
		int defMonth = picked.get(Calendar.MONTH);
		int defDay = picked.get(Calendar.DAY_OF_MONTH);
		int defHour = picked.get(Calendar.HOUR_OF_DAY);
		int defMinute = picked.get(Calendar.MINUTE);
		boolean is24HourFormat = DateFormat.is24HourFormat(this);

		TimePickerDialog.OnTimeSetListener timeListener = (view, hourOfDay, minute) -> {
			picked.set(Calendar.HOUR_OF_DAY, hourOfDay);
			picked.set(Calendar.MINUTE, minute);
			setDeadline(picked.getTimeInMillis());
		};
		TimePickerDialog timePicker = new TimePickerDialog(this, timeListener, defHour, defMinute, is24HourFormat);

		DatePickerDialog.OnDateSetListener dateListener = (view, year, month, dayOfMonth) -> {
			picked.set(Calendar.YEAR, year);
			picked.set(Calendar.MONTH, month);
			picked.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			setDeadline(picked.getTimeInMillis());
			timePicker.show();
		};
		new DatePickerDialog(this, dateListener, defYear, defMonth, defDay).show();
	}

	private void setDeadline(long time) {
		note.setDeadline(time);
		deadline_button.setText(getDeadlineButtonText());
	}

	private String getDeadlineButtonText() {
		if (note.hasDeadline()) {
			return "Deadline at " + Note.format(note.getDeadline());
		} else {
			return "Set deadline";
		}
	}

	// returns whether the method did something
	private boolean removeDeadline() {
		if (!note.hasDeadline()) return false;

		note.removeDeadline();
		deadline_button.setText(getDeadlineButtonText());
		return true;
	}
}
