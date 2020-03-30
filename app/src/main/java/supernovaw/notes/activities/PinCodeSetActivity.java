package supernovaw.notes.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

import supernovaw.notes.PinCodeManager;
import supernovaw.notes.R;

import static supernovaw.notes.activities.PinCodeInput.DOT_CHAR;

public class PinCodeSetActivity extends AppCompatActivity {
	private static final int MIN_PIN_LENGTH = 3;
	private static final int MAX_PIN_LENGTH = 8;

	private TextView title_label;
	private TextView dots_label;
	private Button[] numberButtons;
	private Button pad_clear;
	private Button pad_ok;

	private int[] typedNumbers;
	private int typedNumbersAmt;

	private State state = State.NEW_PIN_INPUT;
	private int[] pinToBeVerified;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pin_code_set);
		initViews();

		typedNumbers = new int[MAX_PIN_LENGTH];

		for (int i = 0; i < numberButtons.length; i++) {
			final int j = i;
			numberButtons[i].setOnClickListener(v -> onNumEntered(j));
		}

		pad_clear.setOnClickListener(v -> clear());
		pad_ok.setOnClickListener(v -> confirm());
	}

	private void initViews() {
		title_label = findViewById(R.id.title_label);
		dots_label = findViewById(R.id.dots_label);
		pad_clear = findViewById(R.id.pad_clear);
		pad_ok = findViewById(R.id.pad_ok);

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

	private void onNumEntered(int num) {
		if (state == State.NEW_PIN_INPUT) {
			if (typedNumbersAmt == MAX_PIN_LENGTH) {
				title_label.setText("You can't set your PIN longer than " + MAX_PIN_LENGTH + " digits");
			} else {
				enterNum(num);
			}
		} else {
			if (typedNumbersAmt != MAX_PIN_LENGTH) {
				enterNum(num);
			} else {
				title_label.setText("You've exceeded the maximum PIN length");
			}
		}
	}

	private void enterNum(int num) {
		typedNumbers[typedNumbersAmt] = num;
		typedNumbersAmt++;
		dots_label.append(Character.toString(DOT_CHAR));
	}

	private void clear() {
		dots_label.setText(null);
		typedNumbersAmt = 0;
	}

	private void confirm() {
		if (state == State.NEW_PIN_INPUT) {
			if (typedNumbersAmt < MIN_PIN_LENGTH) {
				title_label.setText("The PIN has to be at least " +
						MIN_PIN_LENGTH + " digits long");
				clear();
			} else {
				state = State.NEW_PIN_VERIFICATION;
				title_label.setText("Verify the PIN you entered");
				pinToBeVerified = getEnteredPin();
				clear();
			}
		} else {
			int[] entered = getEnteredPin();
			if (Arrays.equals(pinToBeVerified, entered)) {
				boolean wasPinSet = PinCodeManager.isPinSet();
				PinCodeManager.setNewPin(entered, this);
				finish();
				Toast.makeText(this, wasPinSet ? "Changed PIN code" :
						"Set PIN code", Toast.LENGTH_SHORT).show();
			} else {
				clear();
				title_label.setText("The PIN codes don't match. Enter and verify your PIN again");
				state = State.NEW_PIN_INPUT;
			}
		}
	}

	private int[] getEnteredPin() {
		int[] result = new int[typedNumbersAmt];
		System.arraycopy(typedNumbers, 0, result, 0, typedNumbersAmt);
		return result;
	}

	private enum State {
		NEW_PIN_INPUT, NEW_PIN_VERIFICATION
	}
}
