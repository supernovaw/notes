package supernovaw.notes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NotesAdapter extends BaseAdapter {
	private List<Note> list;

	private LayoutInflater inflater;
	private Context context;

	public NotesAdapter(Context c) {
		fetchList();
		context = c;
		inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.note_element, parent, false);
		}

		Note note = list.get(position);

		String title = note.getTitle();
		String text = note.getText();
		String date = note.getDisplayedDate();

		boolean displayTitle = !title.isEmpty();
		boolean displayText = !text.isEmpty();

		TextView note_title = convertView.findViewById(R.id.note_title);
		TextView note_text = convertView.findViewById(R.id.note_text);
		TextView note_date = convertView.findViewById(R.id.note_date);

		note_title.setText(title);
		note_title.setVisibility(displayTitle ? View.VISIBLE : View.GONE);

		note_text.setText(text);
		note_text.setVisibility(displayText ? View.VISIBLE : View.GONE);

		note_date.setText(date);

		return convertView;
	}

	@Override
	public void notifyDataSetChanged() {
		fetchList();
		super.notifyDataSetChanged();
	}

	private void fetchList() {
		list = new ArrayList<>(Notes.getNotes());
	}
}
