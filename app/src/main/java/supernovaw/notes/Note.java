package supernovaw.notes;

import java.text.DateFormat;

public class Note {
	private static final DateFormat DATE_FORMAT = DateFormat.
			getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT);

	private String title;
	private String text;

	private long created, modified, accessed;
	private long deadline;
	private boolean hasDeadline;

	public Note(String title, String text) {
		this.title = title;
		this.text = text;

		long t = System.currentTimeMillis();
		created = modified = accessed = t;
	}

	public String getTitle() {
		return title;
	}

	public String getText() {
		return text;
	}

	public void update(String title, String text) {
		this.title = title;
		this.text = text;

		long t = System.currentTimeMillis();
		modified = accessed = t;
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
	}

	public void removeDeadline() {
		hasDeadline = false;
	}

	// if there is no date to be displayed (settings option), an empty string is returned
	public String getDisplayedDate() {
		if (!hasDeadline) return "";
		return format(deadline);
	}

	public static String format(long time) {
		return DATE_FORMAT.format(time);
	}
}
