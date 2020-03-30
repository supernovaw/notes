package supernovaw.notes;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

public class Notes {
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
		allNotes = new ArrayList<>();
	}
}
