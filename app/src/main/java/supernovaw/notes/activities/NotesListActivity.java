package supernovaw.notes.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import supernovaw.notes.Notes;
import supernovaw.notes.NotesAdapter;
import supernovaw.notes.NotesSettings;
import supernovaw.notes.PinCodeManager;
import supernovaw.notes.R;

public class NotesListActivity extends AppCompatActivity {
	// indicates that the activity is created for the first time
	private static boolean initialized;

	private NotesAdapter adapter;

	private ListView notes_list_view;
	private FloatingActionButton new_note_button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notes_list);
		initViews();
		onStartup(this);

		adapter = new NotesAdapter(this);
		notes_list_view.setAdapter(adapter);
		notes_list_view.setOnItemClickListener((parent, v, position, id) -> noteClick(position));
		notes_list_view.setOnItemLongClickListener((parent, v, position, id) -> {
			noteLongClick(position);
			return true;
		});
		new_note_button.setOnClickListener(v -> startActivity(new Intent(this, EditNoteActivity.class)));
	}

	private void initViews() {
		notes_list_view = findViewById(R.id.notes_list_view);
		new_note_button = findViewById(R.id.new_note_button);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_notes_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if (item.getItemId() == R.id.open_settings) {
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	// after editing or adding a note, update the list
	@Override
	protected void onResume() {
		super.onResume();
		adapter.notifyDataSetChanged();
	}

	private void noteClick(int pos) {
		Intent intent = new Intent(this, EditNoteActivity.class);
		intent.putExtra(EditNoteActivity.NOTE_INDEX_EXTRA, pos);
		startActivity(intent);
	}

	private void noteLongClick(int pos) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);

		String title = Notes.getNotes().get(pos).getTitle();
		if (title.isEmpty()) {
			dialog.setMessage("Do you want to delete this note?");
		} else {
			dialog.setMessage("Do you want to delete \"" + title + "\"?");
		}

		dialog.setPositiveButton("Yes", (d, btn) -> {
			if (btn == AlertDialog.BUTTON_POSITIVE) removeNote(pos);
		});
		dialog.setNegativeButton("Cancel", null);
		dialog.create().show();
	}

	private void removeNote(int pos) {
		Notes.removeNote(pos);
		adapter.notifyDataSetChanged();
		Notes.save(this);
	}

	private static void onStartup(Activity context) {
		if (initialized) return;
		else initialized = true;

		PinCodeManager.init(context);
		NotesSettings.init(context);
		Notes.init(context);
	}
}
