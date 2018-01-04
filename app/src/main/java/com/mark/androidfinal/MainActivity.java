package com.mark.androidfinal;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NewBookFragment.NewBookListener,
        BookListFragment.ClickedBookListener, ViewBookFragment.ViewBookListener {

    // Static key strings for passing values between fragments and MainActivity.
    protected static final String NAV_KEY = "navigation menu";
    protected static final String ALL_BOOKS_KEY = "all_books";
    protected static final String BOOK_KEY = "one_book_to_rule_them_all";
    protected static final String PAGES_KEY = "updating_pages";

    // Debugging tag string.
    private static final String TAG = "debugger extraordinaire";

    // Database variables.
    private DatabaseReference mDatabaseReference;
    private ArrayList<Book> mBookArrayList;

    // Main screen variables.
    private boolean mainActive;
    private TextView mainTextView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Sets up main screen.
        mainActive = true;
        mainTextView = (TextView) findViewById(R.id.main_welcome);

        // Sets up nav bar.
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Sets up database.
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference dbReference = db.getReference();
        mDatabaseReference = dbReference.child(ALL_BOOKS_KEY);

        // Performs query.
        queryAllBooks();
    }




    // Navigation Bar's Listener for navigating to fragments.
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            // Create placeholder Fragment outside of switch. Its content will be decided
            // in the switch.
            Fragment fragment = new Fragment();
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Bundle bundle = new Bundle();

            // Switch statement to determine which nav bar button was pressed.
            switch (item.getItemId()) {
                // Book List button.
                case R.id.navigation_book_list:
                    fragment = BookListFragment.newInstance();
                    // Adds latest ArrayList to bundle to populate Book List.
                    bundle.putParcelableArrayList(ALL_BOOKS_KEY, mBookArrayList);
                    break;

                // Add Book button.
                case R.id.navigation_add_book:
                    fragment = NewBookFragment.newInstance();
                    break;
                default:
                    break;
            }
            // Checks if the fragment was set to anything.
            if (fragment != null) {
                fragment.setArguments(bundle);
                // Replaces current fragment with indicated one.
                ft.replace(R.id.main_container, fragment).commit();

                // Hides main screen widget.
                loadMainPage();
                return true;
            }
            return false;
        }
    };









//     ############################################################

//        ############     DATABASE FUNCTIONS     ################

//     ############################################################

    // Database function to add new book to database and ArrayList.
    private void saveNewBook(Book newBook) {
        // Saving Book to database.
        // Creates a new child reference of global DatabaseReference.
        DatabaseReference newReference = mDatabaseReference.push();

        // Captures the reference's key value for easier queries.
        String uniqueId = newReference.getKey();
        newBook.setUniqueId(uniqueId);

        // Gets user preferences for accessing the database.
        String userId = getSharedPreferences(SignInActivity.USERS_PREFS, MODE_PRIVATE)
                .getString(SignInActivity.FIREBASE_USER_ID_PREF_KEY, "something is wrong");
        newBook.setUserId(userId);

        // Set the value of new child reference to the passed Book object.
        newReference.setValue(newBook, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    Log.d(TAG, "entry added to database");
                } else {
                    Log.e(TAG, "failed adding to database");
                }
            }
        });

        // Adds new book to ArrayList. Book will have 0 pages read so no need for sorting.
        mBookArrayList.add(newBook);
    }



    // Database function for getting all entries.
    private void queryAllBooks() {
        // Queries the database for all entries.
        mDatabaseReference.orderByChild("pages_read").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "ALL DB ENTRIES: " + dataSnapshot.toString());

                // Creates new ArrayList to hold query results.
                ArrayList<Book> allBooks = new ArrayList<Book>();

                // Loops through snapshot and adds children to ArrayList.
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    Book book = childSnapshot.getValue(Book.class);
//                    Book book = (Book) childSnapshot.getValue();
                    book.setFirebaseKey(childSnapshot.getKey());
                    allBooks.add(book);
                }
                // Updates global ArrayList with updated query results.
                mBookArrayList = allBooks;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Firebase Error fetching all entries", databaseError.toException());
            }
        });
    }



    // Databaase function for updating pages read Book attribute.
    @Override
    public void updatePages(Bundle bundle) {
        // Retrieves values passed from fragment.
        Book book = bundle.getParcelable(BOOK_KEY);
        int pages = bundle.getInt(PAGES_KEY);
        // Book will be null if Cancel button was pressed.
        if (book != null) {
            String bookId = book.getUniqueId();

            // Exception handler.
            try {
                // Gets page attributes and calculates new total.
                int prevPages = book.getPages_read();
                int newTotal = prevPages + pages;
                // Checks that new entry doesn't put total over the limit.
                if (newTotal <= book.getTotal_pages()) {
                    // Updates the pages read value in database.
                    mDatabaseReference.child(bookId).child("pages_read").setValue(pages + prevPages);
                    Toast.makeText(MainActivity.this, "Update Successful", Toast.LENGTH_SHORT).show();

                    // Locates Book in global ArrayList and updates its attribute.
                    for (Book x : mBookArrayList) {
                        if (x.getUniqueId().equals(bookId)) {
                            x.setPages_read(newTotal);
                            break;
                        }
                    }
                    // Loads main screen.
                    loadMainPage();
                } else {
                    // Displays Toast if new total is higher than limit.
                    Toast.makeText(MainActivity.this, "Can't read more than the total", Toast.LENGTH_SHORT).show();
                }
                // Exception for database query.
            } catch (NullPointerException er) {
                er.fillInStackTrace();
                Toast.makeText(MainActivity.this, "Can't locate book", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Cancel button was pressed so load main screen.
            loadMainPage();
        }
    }







//     ############################################################

//        ############     INTERFACE FUNCTIONS     ################

//     ############################################################


    // Returning function/call from NewBookFragment.
    @Override
    public void newBookData(String name, String reader, int pages) {
        // Creates new Book object.
        Book newBook = new Book(name, reader, pages);
        // New Book object is added to Firebase.
        saveNewBook(newBook);
        Toast.makeText(MainActivity.this, "Book added", Toast.LENGTH_SHORT).show();

        // Returns to main screen.
        loadMainPage();
    }



    // Returning function/call from BookListFragment.
    @Override
    public void openClickedBook(Bundle bundle) {
        // Instantiates new fragment.
        ViewBookFragment fragment = ViewBookFragment.newInstance();
        // Attaches passed bundle containing Book info to fragment.
        fragment.setArguments(bundle);
        // Opens fragment.
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.main_container, fragment);
        ft.commit();
    }








//     ############################################################

//        ############     MISC FUNCTIONS     ################

//     ############################################################

    private void loadMainPage() {
        // Hides or unhides TextView widget depending on flag variable.
        if (mainActive) {
            mainTextView.setVisibility(View.GONE);
            mainActive = false;
        } else {
            mainTextView.setVisibility(View.VISIBLE);
            mainActive = true;
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_container, new HomeFragment()).commit();
        }

    }
}




// References:
// parcing class - http://www.parcelabler.com/
// column spacing - https://stackoverflow.com/questions/1666685/android-stretch-columns-evenly-in-a-tablelayout
// fragment managing strategy - http://blog.iamsuleiman.com/using-bottom-navigation-view-android-design-support-library/
// simple update statement - https://stackoverflow.com/questions/33315353/update-specific-keys-using-firebase-for-android
// getting past firebase permissions - https://stackoverflow.com/questions/37477644/firebase-permission-denied-error
// reverse order of arraylist - https://stackoverflow.com/questions/2784514/sort-arraylist-of-custom-objects-by-property
// resizing dialog box window - https://stackoverflow.com/questions/19133822/custom-dialog-too-small
