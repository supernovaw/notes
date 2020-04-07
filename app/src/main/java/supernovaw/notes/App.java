package supernovaw.notes;

import android.app.Activity;
import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class App {
	// indicates whether initializeIfNecessary is run for the first time
	private static boolean isInitialized;

	public static void initializeIfNecessary(Activity context) {
		if (isInitialized) return;
		else isInitialized = true;

		PinCodeManager.init(context);
		NotesSettings.init(context);
		Notes.init(context);
	}

	public static byte[] readFile(Context context, String filename) throws IOException {
		try (InputStream in = context.openFileInput(filename);
			 ByteArrayOutputStream readBytes = new ByteArrayOutputStream()) {
			byte[] buffer = new byte[1024];
			int read;

			while ((read = in.read(buffer)) != -1) readBytes.write(buffer, 0, read);
			return readBytes.toByteArray();
		}
	}

	public static void writeFile(Context context, String filename, byte[] bytes) throws IOException {
		try (OutputStream out = context.openFileOutput(filename, Context.MODE_APPEND)) {
			out.write(bytes);
		}
	}
}
