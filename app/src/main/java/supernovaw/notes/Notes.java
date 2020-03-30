package supernovaw.notes;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

// "notes.bin format.md" describes binary file format
public class Notes {
	private static final String LOG_TAG = Notes.class.getSimpleName();
	private static final String FILENAME = "notes.bin";
	private static final Charset STRING_ENCODING = StandardCharsets.UTF_8;

	private static List<Note> allNotes;

	public static List<Note> getNotes() {
		return allNotes;
	}

	public static void addNote(Note n) {
		allNotes.add(n);
	}

	public static void removeNote(int index) {
		allNotes.remove(index);
	}

	public static void init(Activity context) {
		byte[] readData;
		try (InputStream fileIn = context.openFileInput(FILENAME);
			 ByteArrayOutputStream byteOut = new ByteArrayOutputStream()) {
			int read;
			byte[] buffer = new byte[1024]; // 1 KB size
			while ((read = fileIn.read(buffer)) != -1)
				byteOut.write(buffer, 0, read);
			readData = byteOut.toByteArray();
		} catch (IOException e) {
			allNotes = new ArrayList<>();
			return;
		}

		try {
			ByteBuffer buffer = ByteBuffer.wrap(readData);
			int notesAmt = buffer.getInt();
			List<Note> readNotes = new ArrayList<>(notesAmt);

			for (int i = 0; i < notesAmt; i++) {
				long tCreated = buffer.getLong();
				long tModified = buffer.getLong();
				long tAccessed = buffer.getLong();
				byte hasDeadline = buffer.get();
				long tDeadline;

				if (hasDeadline == 0) {
					tDeadline = 0;
				} else if (hasDeadline == 1) {
					tDeadline = buffer.getLong();
				} else {
					throw new Exception("For hasDeadline indicator byte " + hasDeadline);
				}

				byte[] titleBytes = new byte[buffer.getInt()];
				buffer.get(titleBytes);

				byte[] textBytes = new byte[buffer.getInt()];
				buffer.get(textBytes);

				String title = new String(titleBytes, STRING_ENCODING);
				String text = new String(textBytes, STRING_ENCODING);

				readNotes.add(new Note(title, text, tCreated, tModified, tAccessed,
						hasDeadline == 1, tDeadline));
			}

			allNotes = readNotes;
		} catch (Exception e) {
			allNotes = new ArrayList<>();
			Log.v(LOG_TAG, "The file is corrupted (" +
					readData.length + " B). Initializing empty array.");
		}
	}

	public static void save(Activity context) {
		try (ByteArrayOutputStream toWrite = new ByteArrayOutputStream();
			 OutputStream fileOut = context.openFileOutput(FILENAME, Context.MODE_PRIVATE)) {
			ByteBuffer intBuffer = ByteBuffer.allocate(4);
			ByteBuffer longBuffer = ByteBuffer.allocate(8);

			int notesAmt = allNotes.size();
			toWrite.write(intBuffer.putInt(0, notesAmt).array());

			for (int i = 0; i < notesAmt; i++) {
				Note n = allNotes.get(i);

				byte[] title = n.getTitle().getBytes(STRING_ENCODING);
				byte[] text = n.getText().getBytes(STRING_ENCODING);

				toWrite.write(longBuffer.putLong(0, n.getCreated()).array());
				toWrite.write(longBuffer.putLong(0, n.getModified()).array());
				toWrite.write(longBuffer.putLong(0, n.getAccessed()).array());

				toWrite.write(n.hasDeadline() ? 1 : 0);
				if (n.hasDeadline()) {
					toWrite.write(longBuffer.putLong(0, n.getDeadline()).array());
				}

				toWrite.write(intBuffer.putInt(0, title.length).array());
				toWrite.write(title);
				toWrite.write(intBuffer.putInt(0, text.length).array());
				toWrite.write(text);
			}

			toWrite.writeTo(fileOut);
		} catch (IOException e) { // may only occur in writeTo or close
			Log.v(LOG_TAG, "IOException while saving: " + e.getMessage());
		}
	}
}
