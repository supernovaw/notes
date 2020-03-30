package supernovaw.notes;

import java.text.DateFormat;

public class Note {
	private static final DateFormat DATE_FORMAT = DateFormat.
			getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT);

	private String title;
	private String text;

	private long created;
	private long modified;
	private long accessed;

	private long deadline;
	private boolean hasDeadline;

	public Note(String title, String text) {
		this.title = title;
		this.text = text;

		long t = System.currentTimeMillis();
		created = modified = accessed = t;
	}

	Note(String title, String text, long created, long modified,
		 long accessed, boolean hasDeadline, long deadline) {
		this.title = title;
		this.text = text;

		this.created = created;
		this.modified = modified;
		this.accessed = accessed;

		this.hasDeadline = hasDeadline;
		this.deadline = deadline;
	}

	private void onModify() {
		modified = System.currentTimeMillis();
		onAccess();
	}

	public void onAccess() {
		accessed = System.currentTimeMillis();
		NotesSettings.sort(Notes.getNotes());
	}

	public String getTitle() {
		return title;
	}

	public String getText() {
		return text;
	}

	public void update(String title, String text) {
		if (!title.equals(this.title) || !text.equals(this.text)) {
			onModify();
			this.title = title;
			this.text = text;
		}
	}

	public boolean hasDeadline() {
		return hasDeadline;
	}

	public long getDeadline() {
		return deadline;
	}

	public void setDeadline(long deadline) {
		this.deadline = deadline;
		hasDeadline = true;
		onModify();
	}

	public void removeDeadline() {
		hasDeadline = false;
		onModify();
	}

	public long getCreated() {
		return created;
	}

	public long getModified() {
		return modified;
	}

	public long getAccessed() {
		return accessed;
	}

	// if there is no date to be displayed (settings option), an empty string is returned
	public String getDisplayedDate() {
		switch (NotesSettings.getSortOrder()) {
			case NotesSettings.SORT_CREATED:
				return format(created);
			case NotesSettings.SORT_MODIFIED:
				return format(modified);
			case NotesSettings.SORT_ACCESSED:
				return format(accessed);
			default: // the only remaining case is SORT_DEADLINE
				if (hasDeadline) return format(deadline);
				else return "";
		}
	}

	public static String format(long time) {
		return DATE_FORMAT.format(time);
	}
}
