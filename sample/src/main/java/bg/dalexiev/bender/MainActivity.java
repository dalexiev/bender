package bg.dalexiev.bender;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import bg.dalexiev.bender.content.EntityCursor;
import bg.dalexiev.bender.content.InsertCommand;
import bg.dalexiev.bender.content.QueryCommand;
import bg.dalexiev.bender.content.ResolverCommandBuilder;
import bg.dalexiev.bender.content.SupportEntityCursorLoader;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, AddToDoDialog.Listener,
        InsertCommand.Callback {

    private static final int LOAD_TO_DO_LIST = 1;

    private static final int INSERT_TODO = 2;

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

        final ListView toDoListView = (ListView) findViewById(R.id.toDoList);
        toDoListView.setAdapter(mAdapter);

        final TextView emptyTextView = (TextView) findViewById(R.id.toDoListEmptyText);
        toDoListView.setEmptyView(emptyTextView);

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
    public void onInsertComplete(int token, Uri uri) {
        if (INSERT_TODO == token) {
            Toast.makeText(this, "To Do inserted", Toast.LENGTH_SHORT).show();
        }
    }

    private class HandleLoad implements LoaderManager.LoaderCallbacks<EntityCursor<ToDoModel>> {

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
