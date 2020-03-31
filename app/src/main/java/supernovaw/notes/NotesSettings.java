package supernovaw.notes;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NotesSettings {
	private static final String SP_NAME = "notes_settings";
	private static final String SP_SORT_ORDER = "sort_order";
	private static final String SP_SORT_ASCENDING = "sort_ascending";

	public static final int SORT_CREATED = 0;
	public static final int SORT_MODIFIED = 1;
	public static final int SORT_ACCESSED = 2;
	public static final int SORT_DEADLINE = 3;

	private static final Comparator<Note> COMPARATOR_CREATED = (a, b) -> Long.compare(a.getCreated(), b.getCreated());
	private static final Comparator<Note> COMPARATOR_MODIFIED = (a, b) -> Long.compare(a.getModified(), b.getModified());
	private static final Comparator<Note> COMPARATOR_ACCESSED = (a, b) -> Long.compare(a.getAccessed(), b.getAccessed());
	private static final Comparator<Note> COMPARATOR_DEADLINE = (a, b) -> {
		// if one of the notes has a deadline and the other hasn't
		if (a.hasDeadline() != b.hasDeadline()) {
			return a.hasDeadline() ? 1 : -1;
		}
		// if both have a deadline
		if (a.hasDeadline()) {
			return Long.compare(a.getDeadline(), b.getDeadline());
		}
		// if neither has a deadline
		return Long.compare(a.getAccessed(), b.getAccessed());
	};

	private static int sortOrder;
	private static boolean sortAscending;
	private static Comparator<Note> currentSort;
	private static SharedPreferences sharedPreferences;

	public static void setSortOrder(int sortMagic, boolean ascending) {
		setSortOrderField(sortMagic, ascending);
		sort(Notes.getNotes());

		sortOrder = sortMagic;
		sortAscending = ascending;
		sharedPreferences.edit().putInt(SP_SORT_ORDER, sortOrder).
				putBoolean(SP_SORT_ASCENDING, ascending).apply();
	}

	private static void setSortOrderField(int sortMagic, boolean ascending) {
		Comparator<Note> comparator = getComparatorByMagicNumber(sortMagic);
		if (ascending) {
			currentSort = comparator;
		} else {
			currentSort = (a, b) -> comparator.compare(b, a);
		}
	}

	public static int getSortOrder() {
		return sortOrder;
	}

	public static boolean isSortAscending() {
		return sortAscending;
	}

	static void sort(List<Note> list) {
		Collections.sort(list, currentSort);
	}

	private static Comparator<Note> getComparatorByMagicNumber(int n) {
		switch (n) {
			case SORT_CREATED:
				return COMPARATOR_CREATED;
			case SORT_MODIFIED:
				return COMPARATOR_MODIFIED;
			case SORT_ACCESSED:
				return COMPARATOR_ACCESSED;
			case SORT_DEADLINE:
				return COMPARATOR_DEADLINE;
			default:
				throw new IllegalArgumentException("For magic number " + n);
		}
	}

	public static void init(Activity context) {
		sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
		sortOrder = sharedPreferences.getInt(SP_SORT_ORDER, SORT_DEADLINE);
		sortAscending = sharedPreferences.getBoolean(SP_SORT_ASCENDING, false);
		setSortOrderField(sortOrder, sortAscending);
	}
}
