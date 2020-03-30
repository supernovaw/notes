package supernovaw.notes;

import android.app.Activity;
import android.content.Intent;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import supernovaw.notes.activities.PinCodeInput;

public final class PinCodeManager {
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

	public static void init(Activity c) {
		if (pinCode == null) {
			try (InputStream in = c.openFileInput(PIN_FILENAME);
				 ByteArrayOutputStream byteOut = new ByteArrayOutputStream()) {
				int read;
				byte[] buffer = new byte[1024];
				while ((read = in.read(buffer)) != -1)
					byteOut.write(buffer, 0, read);

				byte[] data = byteOut.toByteArray();
				int len = data[0];
				pinCode = new int[len];
				for (int i = 0; i < len; i++) {
					pinCode[i] = data[i + 1]; // first 1 byte is taken by len
				}
			} catch (IOException | ArrayIndexOutOfBoundsException e) {
				pinCode = new int[0]; // OutOfBounds may occur in case of corrupted file
			}
			loggedIn = pinCode.length == 0;
		}

		if (!loggedIn) {
			c.startActivity(new Intent(c, PinCodeInput.class));
			c.finish();
		}
	}
}
