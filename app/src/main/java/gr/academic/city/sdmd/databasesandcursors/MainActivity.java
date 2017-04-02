package gr.academic.city.sdmd.databasesandcursors;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import gr.academic.city.sdmd.databasesandcursors.db.StudentManagementContract;
import gr.academic.city.sdmd.databasesandcursors.db.StudentManagementDbHelper;

public class MainActivity extends AppCompatActivity {

    // Define a projection that specifies which columns from the database
    // you will actually use after this query.
    // To save on resources only return the column values that you actually need.
    private static final String[] PROJECTION = {
            StudentManagementContract.Student._ID,
            StudentManagementContract.Student.COLUMN_NAME_FIRST_NAME,
            StudentManagementContract.Student.COLUMN_NAME_LAST_NAME
    };

    // How you want the results sorted in the resulting Cursor
    private static final String SORT_ORDER = StudentManagementContract.Student.COLUMN_NAME_LAST_NAME + " ASC";


    private StudentManagementDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new StudentManagementDbHelper(this);

        findViewById(R.id.btn_insert).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = ((TextView) findViewById(R.id.txt_first_name)).getText().toString();
                String lastName = ((TextView) findViewById(R.id.txt_last_name)).getText().toString();
                String age = ((TextView) findViewById(R.id.txt_age)).getText().toString();

                insertStudent(firstName, lastName, age);
            }
        });

        findViewById(R.id.btn_query).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAllStudents();
            }
        });

        findViewById(R.id.btn_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emptyDb();
            }
        });
    }

    private void emptyDb() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL(dbHelper.SQL_DELETE_STUDENTS);
        db.execSQL(dbHelper.SQL_CREATE_STUDENTS);
        getAllStudents();
    }

    private void insertStudent(String firstName, String lastName, String age) {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        if (!lastName.equals("")) {
            values.put(StudentManagementContract.Student.COLUMN_NAME_FIRST_NAME, firstName);
            values.put(StudentManagementContract.Student.COLUMN_NAME_LAST_NAME, lastName);
            values.put(StudentManagementContract.Student.COLUMN_NAME_AGE, age);
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(
                StudentManagementContract.Student.TABLE_NAME, // the table to insert to
                null, // nullColumnHack - if the values are empty you need this
                values); // all the data to insert

        if (newRowId != -1) {
            Toast.makeText(MainActivity.this, "New record inserted - ID " + newRowId, Toast.LENGTH_SHORT).show();
            ((TextView) findViewById(R.id.txt_first_name)).setText("");
            ((TextView) findViewById(R.id.txt_last_name)).setText("");
            ((TextView) findViewById(R.id.txt_age)).setText("");
        }
        else
            Toast.makeText(MainActivity.this, "You must enter at least a surname!", Toast.LENGTH_SHORT).show();
    }

    private void getAllStudents() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                StudentManagementContract.Student.TABLE_NAME,           // The table to query
                PROJECTION,                                             // The columns to return
                null,                                                   // The columns for the WHERE clause
                null,                                                   // The values for the WHERE clause
                null,                                                   // don't group the rows
                null,                                                   // don't filter by row groups
                SORT_ORDER                                              // The sort order
        );

        String result = "";

        int firstNameColumn = cursor.getColumnIndexOrThrow(StudentManagementContract.Student.COLUMN_NAME_FIRST_NAME);
        int lastNameColumn = cursor.getColumnIndexOrThrow(StudentManagementContract.Student.COLUMN_NAME_LAST_NAME);
        while (cursor.moveToNext()) {
            String firstName = cursor.getString(firstNameColumn);
            String lastName = cursor.getString(lastNameColumn);

            result += lastName + "\t" + firstName + "\n";
        }

        TextView resultsTextView = (TextView) findViewById(R.id.tv_results);
        resultsTextView.setText(result);

        cursor.close();
    }
}
