package bg.dalexiev.bender.content;

import android.content.UriMatcher;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import bg.dalexiev.bender.util.Preconditions;

/**
 * Wraps a {@code UriMatcher} to simplify the extraction of information from the content URIs. The results of this class are consumed by a {@code DatbaseContentProvider}.
 *
 * @author danail.alexiev
 * @since 1.0
 *
 * @see UriMatcher
 * @see DatabaseContentProvider
 */
public class DatabaseUriMatcher {

    public static final int TYPE_TABLE = 0;
    public static final int TYPE_ROW = 1;

    private final UriMatcher mUriMatcher;

    public DatabaseUriMatcher(@NonNull String authority) {
        Preconditions.argumentNotNull(authority, "Authority can't be null;");

        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        mUriMatcher.addURI(authority, "*", TYPE_TABLE);
        mUriMatcher.addURI(authority, "*/#", TYPE_ROW);
    }

    /**
     * Tries to match a provided content URIs to the ones registered on this matcher.
     *
     * @param uri required. The content URI to match.
     * @return an object, wrapping the content URI info or {@code null}, if there has been no match.
     */
    @Nullable
    public Result match(@NonNull Uri uri) {
        switch (mUriMatcher.match(uri)) {
            case TYPE_TABLE:
                return new Result(TYPE_TABLE, uri.getLastPathSegment(), false, null);

            case TYPE_ROW:
                return new Result(TYPE_ROW, uri.getPathSegments().get(0), true, uri.getLastPathSegment());

            default:
                return null;
        }
    }

    /**
     * Encapsulates the important information passed using a content URI.
     *
     * @author danail.alexiev
     * @since 1.0
     */
    public static final class Result {

        /**
         * The code of the match
         */
        public final int code;

        /**
         * The name of the requested database table
         */
        public final String table;

        /**
         * A flag to indicate that a specific row from the table has been requested
         */
        public final boolean isRow;

        /**
         * The id of the requested row or {@code null}
         */
        public final String id;

        public Result(int code, @NonNull String table, boolean isRow, @Nullable String id) {
            this.code = code;
            this.table = table;
            this.isRow = isRow;
            this.id = id;
        }
    }
}
