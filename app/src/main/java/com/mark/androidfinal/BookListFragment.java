package com.mark.androidfinal;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 *  Book List Fragment that displays list of database entries.
 */

public class BookListFragment extends Fragment {

    private BookListAdapter mBookListAdapter;
    private static final String BOOK_LIST_ARGS = "arguments for book list";
    protected static ArrayList<Book> allBooksList;

    // Interface listener.
    private ClickedBookListener mClickedBookListener;



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Instantiates listener if not already done.
        if (context instanceof ClickedBookListener) {
            mClickedBookListener = (ClickedBookListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement ClickedBookListener");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflates view.
        View view = inflater.inflate(R.layout.fragment_book_list, container, false);

        // Sets up widget.
        ListView bookListView = (ListView) view.findViewById(R.id.book_list);

        // Grabs ArrayList from passed Arguments and updates global variable.
        allBooksList = getArguments().getParcelableArrayList(MainActivity.ALL_BOOKS_KEY);

        // Sorts ArrayList in reverse order by pages read attribute.
        Collections.sort(allBooksList, new Comparator<Book>() {
            @Override
            public int compare(Book book1, Book book2) {
                return Integer.valueOf(book2.getPages_read()).compareTo(book1.getPages_read());
            }
        });

        // Instantiates ArrayAdapter and assigns to ListView.
        mBookListAdapter = new BookListAdapter(getActivity(), allBooksList);
        bookListView.setAdapter(mBookListAdapter);
        mBookListAdapter.notifyDataSetChanged();

        // Defines ListView's click event.
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Captures clicked item and adds to a bundle.
                Book clickedBook = (Book) adapterView.getItemAtPosition(position);
                Bundle bundle = new Bundle();
                bundle.putParcelable(MainActivity.BOOK_KEY, clickedBook);
                // Sends bundle to MainActivity with listener.
                mClickedBookListener.openClickedBook(bundle);
            }
        });
        // Returns view to be viewed.
        return view;
    }


    // Interface for interacting with MainActivity.
    public interface ClickedBookListener {
        void openClickedBook(Bundle bundle);
    }

    // NewInstance function.
    public static BookListFragment newInstance() {
        return new BookListFragment();
    }
}
