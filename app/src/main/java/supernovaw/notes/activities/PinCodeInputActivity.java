package supernovaw.notes.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.Objects;

import supernovaw.notes.PinCodeManager;
import supernovaw.notes.R;

public class PinCodeInputActivity extends AppCompatActivity {
	/* When launching 'switchToActivity', this boolean extra is
	 * sent to verify that the PIN was indeed entered correctly.
	 */
	public static final String PIN_ENTERED_CORRECTLY_EXTRA = "pin_correct";
	public static final String ACTIVITY_CLASSNAME_EXTRA = "activity_classname";
	static final char DOT_CHAR = '\u30FB'; // "・" char

	private TextView title_label;
	private TextView label_all_dots_anchor;
	private TextView dots_label;
	private Button[] numberButtons;
	private Button pad_clear;

	private int[] typedNumbers;
	private int typedNumbersAmt;
	// an activity class to switch to when the PIN is entered correctly
	private Class switchToActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pin_code_input);
		initViews();
		initActivityToSwitch();

		typedNumbers = new int[PinCodeManager.getPinLength()];

		String title = getIntent().getStringExtra(Intent.EXTRA_TITLE);
		title_label.setText(title == null ? getString(R.string.enter_pin_title) : title);

		char[] allDotsString = new char[PinCodeManager.getPinLength()];
		Arrays.fill(allDotsString, DOT_CHAR);
		label_all_dots_anchor.setText(new String(allDotsString));

		for (int i = 0; i < numberButtons.length; i++) {
			final int j = i;
			numberButtons[i].setOnClickListener(v -> onNumEntered(j));
		}

		pad_clear.setOnClickListener(v -> clear());
	}

	private void initViews() {
		title_label = findViewById(R.id.title_label);
		label_all_dots_anchor = findViewById(R.id.label_all_dots_anchor);
		dots_label = findViewById(R.id.dots_label);
		pad_clear = findViewById(R.id.pad_clear);

		numberButtons = new Button[10];
		numberButtons[0] = findViewById(R.id.pad_num_0);
		numberButtons[1] = findViewById(R.id.pad_num_1);
		numberButtons[2] = findViewById(R.id.pad_num_2);
		numberButtons[3] = findViewById(R.id.pad_num_3);
		numberButtons[4] = findViewById(R.id.pad_num_4);
		numberButtons[5] = findViewById(R.id.pad_num_5);
		numberButtons[6] = findViewById(R.id.pad_num_6);
		numberButtons[7] = findViewById(R.id.pad_num_7);
		numberButtons[8] = findViewById(R.id.pad_num_8);
		numberButtons[9] = findViewById(R.id.pad_num_9);
	}

	private void initActivityToSwitch() {
		try {
			String classname = getIntent().getStringExtra(ACTIVITY_CLASSNAME_EXTRA);
			switchToActivity = Class.forName(Objects.requireNonNull(classname));
		} catch (ClassNotFoundException | NullPointerException e) {
			switchToActivity = NotesListActivity.class;
		}
	}

	private void onNumEntered(int num) {
		typedNumbers[typedNumbersAmt] = num;

		typedNumbersAmt++;
		if (typedNumbersAmt == PinCodeManager.getPinLength()) {
			pinEntered(PinCodeManager.isPinCorrect(typedNumbers));
		} else {
			dots_label.append(Character.toString(DOT_CHAR));
		}
	}

	private void clear() {
		dots_label.setText(null);
		typedNumbersAmt = 0;
	}

	private void pinEntered(boolean correct) {
		if (correct) {
			Intent intent = new Intent(this, switchToActivity);
			intent.putExtra(PIN_ENTERED_CORRECTLY_EXTRA, true);

			finish();
			startActivity(intent);
		} else {
			clear();
		}
	}
}
