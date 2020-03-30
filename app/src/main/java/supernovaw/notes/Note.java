package supernovaw.notes;

import android.content.Context;
import android.text.format.DateFormat;

import java.text.SimpleDateFormat;

public class Note {
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

	/* The context is used to obtain a correct date-time format. If there is
	 * no date to be displayed (settings option), an empty string is returned.
	 */
	public String getDisplayedDate(Context context) {
		SimpleDateFormat dateFormat = (SimpleDateFormat) DateFormat.getLongDateFormat(context);
		return dateFormat.format(created);
	}
}
