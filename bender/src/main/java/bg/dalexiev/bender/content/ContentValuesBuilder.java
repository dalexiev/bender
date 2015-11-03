package bg.dalexiev.bender.content;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import bg.dalexiev.bender.util.Preconditions;

/**
 */
class ContentValuesBuilder {

    private int mPosition;
    private final List<ContentValues> mContentValues;

    ContentValuesBuilder() {
        mContentValues = new ArrayList<>(10);
        mContentValues.add(new ContentValues());
    }

    private ContentValues getCurrent() {
        final ContentValues contentValues = mContentValues.get(mPosition);
        Preconditions.notNull(contentValues,
                "Invalid content builder state: value at position " + mPosition + " is null");

        return contentValues;
    }

    void newValue() {
        mPosition++;
        mContentValues.add(new ContentValues());
    }

    void setNull(@NonNull String column) {
        Preconditions.argumentNotNull(column, "Column is required");

        getCurrent().putNull(column);
    }

    void set(@NonNull String column, byte value) {
        Preconditions.argumentNotNull(column, "Column is required");

        getCurrent().put(column, value);
    }

    void set(@NonNull String column, short value) {
        Preconditions.argumentNotNull(column, "Column is required");

        getCurrent().put(column, value);
    }

    void set(@NonNull String column, int value) {
        Preconditions.argumentNotNull(column, "Column is required");

        getCurrent().put(column, value);
    }

    void set(@NonNull String column, long value) {
        Preconditions.argumentNotNull(column, "Column is required");

        getCurrent().put(column, value);
    }

    void set(@NonNull String column, float value) {
        Preconditions.argumentNotNull(column, "Column is required");

        getCurrent().put(column, value);
    }

    void set(@NonNull String column, double value) {
        Preconditions.argumentNotNull(column, "Column is required");

        getCurrent().put(column, value);
    }

    void set(@NonNull String column, boolean value) {
        Preconditions.argumentNotNull(column, "Column is required");

        getCurrent().put(column, value);
    }

    void set(@NonNull String column, @NonNull String value) {
        Preconditions.argumentNotNull(column, "Column is required");
        Preconditions.argumentNotNull(value, "Value is required. If you need to set null, use the setNull() method");

        getCurrent().put(column, value);
    }

    void set(@NonNull String column, byte[] value) {
        Preconditions.argumentNotNull(column, "Column is required");
        Preconditions.argumentNotNull(value, "Value is required. If you need to set null, use the setNull() method");

        getCurrent().put(column, value);
    }

    ContentValues getSingleValue() {
        Preconditions.stateCondition(mContentValues.size() != 1,
                "Invalid value count. Expected 1, found " + mContentValues.size());

        return mContentValues.get(0);
    }

    ContentValues[] getValuesAsArray() {
        return mContentValues.toArray(new ContentValues[mContentValues.size()]);
    }
}
