# Bender

An Android library that helps you expose your SQLite data through Content Providers.

## Creating a Content Provider
First, be sure you are familiar with the concept of Content Providers.
Useful information can be found [here](http://developer.android.com/guide/topics/providers/content-providers.html).

Bender provides a skeleton implementation of a provider - the `DatabaseContentProvider` class. To make use of it you
should subclass it, providing an `authority` and a `SQLiteOpenHelper`. The following functionality is available out of the box:
* Query - fetch from table with URL `content://<authority>/{tableName}` and fetch specific row with URL `content://<auhtority>/{tableName}/{id}`.
Assumes that `BaseColumns._ID` is used as for the primary key column name.
* Insert - both single and bulk inserts are supported with URL `content://<authority>/{tableName}`.
All bulk inserts are done in a single DB transaction.
* Update - updating a table with URL `content://<authority>/{tableName}` and update a row with URL `content://<auhtority>/{tableName}/{id}`.
Assumes that `BaseColumns._ID` is used as for the primary key column name.
* Delete - delete from a table with URL `content://<authority>/{tableName}` and delete a row with URL `content://<auhtority>/{tableName}/{id}`.
Assumes that `BaseColumns._ID` is used as for the primary key column name.
* Batch Operations - submitted operation batches are wrapped in a single transaction.

All `selection` and `selectionArgs` passed to the operations are transformed into where clauses and used in the execution of the operations.

If you need to support additional URLs, you can create your own `DatabaseUriMatcher` and override the appropriate `DatabaseContentProvider` methods:
* `buildQuerySelection()` - used to create a SQLite Query from the provided URL and passed parameters
* `buildInsertion()` - used to create a SQLite `insert` statement from the provided URL and passed parameters
* `buildUpdateDeleteSelection()` - used to build the `where` clause of a SQLite `update` or `delete` statement from the provided URL and passed parameters

Be sure to check the `DatabaseContentProvider` documentation for more details about the functionality and the methods you need to override.

After you have created your own provider, make sure you register it in your app's `AndroidManifest` file.

## Data Access Through a Content Resolver
Bender provides an easy and intuitive way of accessing Content Provider data through Content Resolvers.
The different methods of interaction are broken down into `commands`. You can create a command using a `ResolverCommandBuilder` instance.

Every `command` can be executed in a synchronous or asynchronous way.
The synchronous execution is performed on the current thread and it is blocking.
The asynchronous execution is performed on a dedicated worker thread and a callback is delivered on the calling thread.
Each asynchronous command has to provide a unique token, used for identification and can register a `callback` that gets
notified when the `command` has been successfuly executed.

### Insert
A typical Insert command looks like this. You can use the `set()` methods to specify column - value pairs you want to insert.

```java
mBuilder.insert(contentResolver)
        .onUri(<yourUri>)
        .set("column1", "value1")
        .set("column2", 10)
        .executeAsync(token, callback);
```

### Bulk Insert
The Bulk Insert command is very similar to the Insert. The only difference is the ability to specify a new row to be inserted
using the `newRow()` method.

```java
mBuilder.bulkInsert(contentResolver)
        .onUri(<yourUri>)
        .set("column1", "value1")
        .set("column2", 10)
        .newRow()
        .set("column1", "value2")
        .set("column2", 31)
        .executeAsync(token, callback);
```

### Update
The Update command lets you create a selection clause and specify the column - value pairs you want to use for the update.

```java
mBuilder.update(contentResolver)
        .onUri(<yourUri>)
        .set("column1", "value1")
        .set("column2", 10)
        .where(eq("foo", "bar"))
        .orWhere(gt("baz", "10"))
        .executeAsync(token, callback);
```

### Delete
The Delete command helps you specify a selection clause for a fine - grained deletion.
```java
mBuilder.delete(contentResolver)
        .onUri(<yourUri>)
        .where(isNull("column"))
        .executeAsync(token, callback);
```

### Query
You can use the Query command to build your selection clause, apply projection and ordering
and execute searches. Like all other commands, the Query provides a natural and easy to use interface
for building complex selections using `Predicate`s and combining them using logical operators.

```java
EntityCursor<Entity> cursor =  mBuilder.query(contentResolver, Entity.class)
                                        .onUri(<yourUri>)
                                        .select("column1", "column2")
                                        .where(eq("foo", "bar"))
                                        .orWhere(gt("baz", "10"))
                                        .useRowMapper(new RowMapper<Entity>(){..})
                                        .executeAsync(token, callback);
```
The result of a Query is an `EntityCursor`. This is a special kind of cursor, capable of transforming database rows into
instances of a specific class.

The easiest way of transforming your result set is providing a `RowMapper` when executing the query. This component will
transform each result set row into a single instance of a class.

If you have to deal with a more complex result set (for example ones that contains a one to many relationship) you can
instead provide a `CursorMapper`. It operates over the entire result set and, after processing it, must return a collection
of result objects, that will be used to back the `EntityCursor` instance, returned by the query.

### Loaders
If you want, you can use the provided `EntityCursorLoader` and `SupportEntityCursorLoader` classes to access you data
through the Loader mechanism, provided by the Android platform.

## Distribution
To use the library download the source code and include it as a module to your project.
It will be uploaded to a Maven Repository as soon as possible.
