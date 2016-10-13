package bg.dalexiev.bender.sample;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import bg.dalexiev.bender.content.DeleteCommand;
import bg.dalexiev.bender.content.EntityCursor;
import bg.dalexiev.bender.content.InsertCommand;
import bg.dalexiev.bender.content.QueryCommand;
import bg.dalexiev.bender.content.ResolverCommandBuilder;
import bg.dalexiev.bender.content.SupportEntityCursorLoader;
import bg.dalexiev.bender.content.UpdateCommand;
import bg.dalexiev.bender.db.Predicate;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AddToDoDialog.Listener,
        InsertCommand.Callback, AbsListView.MultiChoiceModeListener, DeleteCommand.Callback, UpdateCommand.Callback {

    private static final int LOAD_TO_DO_LIST = 1;

    private static final int INSERT_TODO = 2;
    private static final int DELETE_TODOS = 3;
    private static final int UPDATE_TODOS = 4;

    private ListView mToDoListView;

    private ToDoAdapter mAdapter;

    private ResolverCommandBuilder mBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mBuilder = new ResolverCommandBuilder();

        mAdapter = new ToDoAdapter(this);

        mToDoListView = (ListView) findViewById(R.id.toDoList);
        mToDoListView.setAdapter(mAdapter);
        mToDoListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        mToDoListView.setMultiChoiceModeListener(this);

        final TextView emptyTextView = (TextView) findViewById(R.id.toDoListEmptyText);
        mToDoListView.setEmptyView(emptyTextView);

        final FloatingActionButton addButton = (FloatingActionButton) findViewById(R.id.toDoAddButton);
        addButton.setOnClickListener(this);

        getSupportLoaderManager().initLoader(LOAD_TO_DO_LIST, null, new HandleLoad(this));
    }

    @Override
    public void onClick(View v) {
        if (R.id.toDoAddButton == v.getId()) {
            onAddButtonClick();
        }
    }

    private void onAddButtonClick() {
        final AddToDoDialog dialog = AddToDoDialog.newInstance(this);
        dialog.show(getSupportFragmentManager(), AddToDoDialog.TAG);
    }

    @Override
    public void onToDoTitleEntered(String toDo) {
        mBuilder.insert(getContentResolver())
                .onUri(Schema.ToDo.URL)
                .set(Schema.ToDo.TITLE, toDo)
                .set(Schema.ToDo.CREATION_DATE, Calendar.getInstance().getTimeInMillis())
                .set(Schema.ToDo.IS_DONE, 0)
                .executeAsync(INSERT_TODO, this);
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        // do nothing
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.to_do_action_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        String[] ids = getIdStrings(mToDoListView.getCheckedItemIds());
        switch (item.getItemId()) {
            case R.id.actionCheck:
                checkToDos(ids);
                break;

            case R.id.actionDelete:
                deleteToDos(ids);
                break;

            default:
                return false;
        }

        mode.finish();
        return true;
    }

    private void deleteToDos(String[] ids) {
        mBuilder.delete(getContentResolver())
                .onUri(Schema.ToDo.URL)
                .where(Predicate.in(Schema.ToDo._ID, ids))
                .executeAsync(DELETE_TODOS, this);
    }

    private void checkToDos(String[] ids) {
        mBuilder.update(getContentResolver())
                .onUri(Schema.ToDo.URL)
                .set(Schema.ToDo.IS_DONE, true)
                .where(Predicate.in(Schema.ToDo._ID, ids))
                .executeAsync(UPDATE_TODOS, this);
    }

    private static String[] getIdStrings(long[] ids) {
        final int length = ids.length;
        final String[] idStrings = new String[length];
        for (int i = 0; i < length; i++) {
            idStrings[i] = String.valueOf(ids[i]);
        }
        return idStrings;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        // do nothing
    }

    @Override
    public void onInsertComplete(int token, Uri uri) {
        if (INSERT_TODO == token) {
            Toast.makeText(this, "To Do inserted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDeleteComplete(int token, int deletedRows) {

    }

    @Override
    public void onUpdateComplete(int token, int updatedRows) {

    }

    private final class HandleLoad implements LoaderManager.LoaderCallbacks<EntityCursor<ToDoModel>> {

        private final Context mContext;

        private HandleLoad(Context context) {
            mContext = context;
        }

        @Override
        public Loader<EntityCursor<ToDoModel>> onCreateLoader(int id, Bundle args) {
            final QueryCommand query = mBuilder.query(mContext.getContentResolver(), ToDoModel.class)
                    .onUri(Schema.ToDo.URL)
                    .select(Schema.ToDo._ID, Schema.ToDo.TITLE, Schema.ToDo.CREATION_DATE, Schema.ToDo.IS_DONE)
                    .useRowMapper(new ToDoRowMapper());
            //noinspection unchecked
            return new SupportEntityCursorLoader<>(mContext, query, id);
        }

        @Override
        public void onLoadFinished(Loader<EntityCursor<ToDoModel>> loader, EntityCursor<ToDoModel> data) {
            mAdapter.setCursor(data);
        }

        @Override
        public void onLoaderReset(Loader<EntityCursor<ToDoModel>> loader) {

        }
    }
}
