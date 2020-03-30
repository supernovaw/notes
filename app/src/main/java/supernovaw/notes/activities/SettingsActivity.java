package supernovaw.notes.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import supernovaw.notes.NotesSettings;
import supernovaw.notes.PinCodeManager;
import supernovaw.notes.R;

import static android.view.View.GONE;

public class SettingsActivity extends AppCompatActivity {
	private RadioButton button_dark;
	private RadioButton button_light;
	private RadioButton button_descending;
	private RadioButton button_ascending;
	private Spinner sort_by_spinner;
	private TextView pin_code_label;
	private Button set_pin_button;
	private Button remove_pin_button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		initViews();

		boolean pinVerifiedForRemoval = getIntent().getBooleanExtra(
				PinCodeInputActivity.PIN_ENTERED_CORRECTLY_EXTRA, false);
		if (pinVerifiedForRemoval) {
			PinCodeManager.setNewPin(new int[0], this);
			Toast.makeText(this, "Removed PIN code", Toast.LENGTH_SHORT).show();
		}

		initButtonGroup(null, button_dark, button_light);

		initButtonGroup(v -> updateSorting(), button_descending, button_ascending);
		button_descending.setChecked(!NotesSettings.isSortAscending());
		button_ascending.setChecked(NotesSettings.isSortAscending());
		initSortSpinner();

		updatePinSection();
		set_pin_button.setOnClickListener(v -> addOrChangePin());
		remove_pin_button.setOnClickListener(v -> removePin());
	}

	private void initViews() {
		button_dark = findViewById(R.id.button_dark);
		button_light = findViewById(R.id.button_light);
		button_descending = findViewById(R.id.button_descending);
		button_ascending = findViewById(R.id.button_ascending);
		sort_by_spinner = findViewById(R.id.sort_by_spinner);
		pin_code_label = findViewById(R.id.pin_code_label);
		set_pin_button = findViewById(R.id.set_pin_button);
		remove_pin_button = findViewById(R.id.remove_pin_button);
	}

	private void initSortSpinner() {
		List<Map<String, String>> data = new ArrayList<>(4);
		String textKey = "t";

		data.add(Collections.singletonMap(textKey, "Creation"));
		data.add(Collections.singletonMap(textKey, "Last modification"));
		data.add(Collections.singletonMap(textKey, "Last access"));
		data.add(Collections.singletonMap(textKey, "Deadline"));

		SimpleAdapter adapter = new SimpleAdapter(this, data,
				android.R.layout.simple_spinner_dropdown_item,
				new String[]{textKey}, new int[]{android.R.id.text1});

		sort_by_spinner.setAdapter(adapter);

		sort_by_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				updateSorting();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		sort_by_spinner.setSelection(NotesSettings.getSortOrder());
	}

	private void updateSorting() {
		int num = sort_by_spinner.getSelectedItemPosition();
		boolean asc = button_ascending.isChecked();
		NotesSettings.setSortOrder(num, asc);
	}

	private void updatePinSection() {
		if (PinCodeManager.isPinSet()) {
			pin_code_label.setText("Your notes are secured with a PIN code");
			set_pin_button.setText("Change PIN code");
			remove_pin_button.setVisibility(View.VISIBLE);
		} else {
			pin_code_label.setText("You can secure your notes with a PIN code");
			set_pin_button.setText("Add PIN code");
			remove_pin_button.setVisibility(GONE);
		}
	}

	private void addOrChangePin() {
		if (PinCodeManager.isPinSet()) {
			Intent verifyIdentity = new Intent(this, PinCodeInputActivity.class);
			verifyIdentity.putExtra(Intent.EXTRA_TITLE,
					"Verify your identity to proceed changing the PIN code");
			verifyIdentity.putExtra(PinCodeInputActivity.ACTIVITY_CLASSNAME_EXTRA, PinCodeSetActivity.class.getName());

			finish();
			startActivity(verifyIdentity);
		} else {
			Intent setPin = new Intent(this, PinCodeSetActivity.class);
			finish();
			startActivity(setPin);
		}
	}

	private void removePin() {
		Intent verifyIdentity = new Intent(this, PinCodeInputActivity.class);
		verifyIdentity.putExtra(Intent.EXTRA_TITLE,
				"Verify your identity to proceed removing the PIN code");
		verifyIdentity.putExtra(PinCodeInputActivity.ACTIVITY_CLASSNAME_EXTRA, getClass().getName());
		finish();
		startActivity(verifyIdentity);
	}

	private static void initButtonGroup(View.OnClickListener additional, RadioButton... buttons) {
		View.OnClickListener listener = v -> {
			for (RadioButton b : buttons)
				if (b != v) b.setChecked(false);

			if (additional != null)
				additional.onClick(v);
		};

		for (RadioButton b : buttons)
			b.setOnClickListener(listener);
	}
}
