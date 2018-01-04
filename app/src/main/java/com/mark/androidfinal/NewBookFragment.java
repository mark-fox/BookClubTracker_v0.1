package com.mark.androidfinal;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * New Book Fragment Class for adding a new entry to database.
 */

public class NewBookFragment extends Fragment {

    // Interface listener.
    private NewBookListener mNewBookListener;
    // Error string variables.
    private String emptyField;
    private String parseError;
    private String genError;



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Instantiates listener if not already done.
        if (context instanceof NewBookListener) {
            mNewBookListener = (NewBookListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement NewBookListener");
        }
    }



    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflates View.
        View view = inflater.inflate(R.layout.fragment_new_book, container, false);

        // Gets string resources.
        emptyField = getString(R.string.empty_field_msg);
        parseError = getString(R.string.parse_error_msg);
        genError = getString(R.string.general_error_msg);

        // Sets up widgets.
        final EditText nameEditText = (EditText) view.findViewById(R.id.new_book_name);
        final EditText readerEditText = (EditText) view.findViewById(R.id.new_book_reader);
        final EditText pagesEditText = (EditText) view.findViewById(R.id.new_book_pages);
        Button submitButton = (Button) view.findViewById(R.id.new_book_submit_button);

        // Submit Button's click event.
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    // Retrieves values from widgets.
                    String name = nameEditText.getText().toString();
                    String reader = readerEditText.getText().toString();
                    String pagesStr = pagesEditText.getText().toString();

                    // Verifies fields aren't empty before continuing.
                    if (name.equals("") || reader.equals("") || pagesStr.equals("")) {
                        Toast.makeText(getActivity(), emptyField, Toast.LENGTH_SHORT).show();
                    } else {
                        // Converts pages string to int.
                        int pages = Integer.parseInt(pagesStr);
                        // Sends values back to MainActivity.
                        mNewBookListener.newBookData(name, reader, pages);
                    }
                }
                catch (NumberFormatException error) {
                    Toast.makeText(getActivity(), parseError, Toast.LENGTH_SHORT).show();
                }
                catch (Exception error) {
                    Toast.makeText(getActivity(), genError, Toast.LENGTH_SHORT).show();
                }
            }
        });
        // Returns view to be viewed.
        return view;
    }



    // newInstance function.
    public static NewBookFragment newInstance() {
        return new NewBookFragment();
    }


    // Interface function for interacting with MainActivity.
    public interface NewBookListener {
        void newBookData(String name, String reader, int pages);
    }
}
