package supernovaw.notes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import supernovaw.notes.activities.PinCodeInput;

public final class PinCodeManager {
	private static final String LOG_TAG = PinCodeManager.class.getSimpleName();
	private static final String PIN_FILENAME = "pin";

	// size 0 stands for no PIN
	private static int[] pinCode;
	private static boolean loggedIn;

	public static int getPinLength() {
		return pinCode.length;
	}

	public static boolean isPinCorrect(int[] is) {
		if (pinCode.length != is.length)
			throw new IllegalArgumentException("Pin length doesn't match");

		for (int i = 0; i < is.length; i++) {
			if (pinCode[i] != is[i])
				return false;
		}
		loggedIn = true;
		return true;
	}

	public static boolean isPinSet() {
		return pinCode.length != 0;
	}

	// the context is used to straightaway save the pin
	public static void setNewPin(int[] pin, Activity context) {
		for (int i : pin) {
			if (i < 0 || i > 9) throw new IllegalArgumentException(
					"Number out of bounds in " + Arrays.toString(pin));
		}
		pinCode = pin;
		save(context);
	}

	public static void init(Activity c) {
		if (pinCode == null) {
			try (InputStream in = c.openFileInput(PIN_FILENAME);
				 ByteArrayOutputStream byteOut = new ByteArrayOutputStream()) {
				int read;
				byte[] buffer = new byte[16];
				while ((read = in.read(buffer)) != -1)
					byteOut.write(buffer, 0, read);

				byte[] data = byteOut.toByteArray();
				int len = data[0];
				pinCode = new int[len];
				for (int i = 0; i < len; i++) {
					pinCode[i] = data[i + 1]; // first 1 byte is taken by len
					if (pinCode[i] < 0 || pinCode[i] > 9)
						throw new Exception("Corrupted file");
				}
			} catch (Exception e) {
				pinCode = new int[0];
			}
			loggedIn = !isPinSet();
		}

		if (!loggedIn) {
			c.startActivity(new Intent(c, PinCodeInput.class));
			c.finish();
		}
	}

	private static void save(Activity context) {
		try (OutputStream fileOut = context.openFileOutput(PIN_FILENAME, Context.MODE_PRIVATE)) {
			byte[] toWrite = new byte[pinCode.length + 1]; // 1 extra byte represents the length
			toWrite[0] = (byte) pinCode.length;
			for (int i = 0; i < pinCode.length; i++)
				toWrite[i + 1] = (byte) pinCode[i];
			fileOut.write(toWrite);
		} catch (IOException e) {
			Log.v(LOG_TAG, "Failed to write the PIN code: " + e.getMessage());
		}
	}
}
