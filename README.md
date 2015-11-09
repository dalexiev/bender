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

Be sure to check the `DatabaseContentProvider` documentation for more details about the functinality and the methods you need to override.

After you have created your own provider, make sure you register it in your app's `AndroidManifest` file.

## Fetching Data using a Content Resolver

## Distribution
To use the library download the source code and include it as a module to your project.
It will be uploaded to a Maven Repository as soon as possible.
