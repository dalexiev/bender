package bg.dalexiev.bender.sample;

import android.content.Context;
import android.database.CursorIndexOutOfBoundsException;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import bg.dalexiev.bender.content.EntityCursor;

/**
 */
public class ToDoAdapter extends BaseAdapter {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat();

    private final Context mContext;

    private EntityCursor<ToDoModel> mData;

    public ToDoAdapter(Context context) {
        mContext = context;
    }

    public void setCursor(EntityCursor<ToDoModel> cursor) {
        final EntityCursor<ToDoModel> old = mData;

        mData = cursor;
        notifyDataSetChanged();

        if (old != null) {
            old.close();
        }
    }

    @Override
    public int getCount() {
        if (mData == null) {
            return 0;
        }

        return mData.getCount();
    }

    @Override
    public ToDoModel getItem(int position) {
        if (mData == null) {
            return null;
        }

        if (!mData.moveToPosition(position)) {
            throw new CursorIndexOutOfBoundsException("Invalid cursor position: " + position);
        }

        return mData.getEntity();
    }

    @Override
    public long getItemId(int position) {
        final ToDoModel model = getItem(position);
        if (model == null) {
            return 0;
        }
        return model.getId();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = createView(parent);
        }

        final ViewHolder holder = (ViewHolder) convertView.getTag();
        final ToDoModel item = getItem(position);
        bindViewHolder(holder, item);

        return convertView;
    }

    private void bindViewHolder(ViewHolder holder, ToDoModel item) {
        holder.mTitleTextView.setText(item.getTitle());
        holder.mCreationDateTextView.setText(DATE_FORMAT.format(item.getCreationDate().getTime()));
        holder.mIsDoneCheckBox.setChecked(item.isDone());
    }

    private View createView(ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(mContext);
        final View view = inflater.inflate(R.layout.list_item_to_do, parent, false);
        final ViewHolder holder = createViewHolder(view);
        view.setTag(holder);
        return view;
    }

    @NonNull
    private ViewHolder createViewHolder(View view) {
        final ViewHolder holder = new ViewHolder();
        holder.mTitleTextView = (TextView) view.findViewById(R.id.todoTitleTextView);
        holder.mCreationDateTextView = (TextView) view.findViewById(R.id.todoCreationDateTextView);
        holder.mIsDoneCheckBox = (CheckBox) view.findViewById(R.id.toDoDoneCheckbox);
        return holder;
    }

    private static class ViewHolder {

        private TextView mTitleTextView;
        private TextView mCreationDateTextView;
        private CheckBox mIsDoneCheckBox;
    }
}
