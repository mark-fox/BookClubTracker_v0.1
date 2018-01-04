package com.mark.androidfinal;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;

/**
 *  View Book Fragment Class for displaying a single Book object's attributes for updating.
 */

public class ViewBookFragment extends Fragment {

    private Book passedBook;
    // DateFormatter for Date field.
    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("M-dd-yyyy hh:mm:ss");

    // Interface listener.
    ViewBookListener viewBookListener;



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Instantiates listener if not already done.
        if (context instanceof ViewBookListener) {
            this.viewBookListener = (ViewBookListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement listener");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        // Inflates view.
        View view = inflater.inflate(R.layout.fragment_view_book, container, false);

        // Sets up widgets.
        TextView bookNameText = (TextView) view.findViewById(R.id.view_book_name_text);
        TextView readerText = (TextView) view.findViewById(R.id.view_reader_text);
        TextView pagesText = (TextView) view.findViewById(R.id.view_pages_read_text);
        TextView totalPagesText = (TextView) view.findViewById(R.id.view_total_pages_text);
        TextView dateStartText = (TextView) view.findViewById(R.id.view_date_start_text);
        Button updateButton = (Button) view.findViewById(R.id.view_update_button);
        Button cancelButton = (Button) view.findViewById(R.id.view_cancel_button);

        // Grabs Book object from passed Argument and updates global variable.
        passedBook = getArguments().getParcelable(MainActivity.BOOK_KEY);

        // Updates widgets with Book's attributes.
        bookNameText.setText(passedBook.getBook_name());
        readerText.setText(passedBook.getReader());
        pagesText.setText(passedBook.getPages_read() + "");
        totalPagesText.setText(passedBook.getTotal_pages() + "");
        dateStartText.setText(dateFormatter.format(passedBook.getStart_date()));


        // Update button's click event.
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Creates new Dialog object and sets initial values.
                final Dialog dialog = new Dialog(container.getContext());
                dialog.setContentView(R.layout.dialog_update);
                dialog.setTitle("Update Pages");

                // Sets size of window; otherwise, window is cut off.
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                int width = metrics.widthPixels;
                int height = metrics.heightPixels;
                dialog.getWindow().setLayout((6*width)/7, (height)/4);

                // Sets up custom Dialog's widgets.
                final EditText editText = (EditText) dialog.findViewById(R.id.dialog_update_edit);
                Button updateButton = (Button) dialog.findViewById(R.id.dialog_update_button);

                // Dialog's update button's click event.
                updateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Grabs value from widget.
                        Bundle bundle = new Bundle();
                        String pagesTxt = editText.getText().toString();
                        // Checks that something has been entered.
                        if (!pagesTxt.equals("")) {
                            // Converts value to integer and adds to bundle.
                            int pages = Integer.parseInt(pagesTxt);
                            bundle.putParcelable(MainActivity.BOOK_KEY, passedBook);
                            bundle.putInt(MainActivity.PAGES_KEY, pages);
                            // Passes values back to MainActivity with listener.
                            viewBookListener.updatePages(bundle);
                        }
                        // Closes dialog.
                        dialog.dismiss();
                    }
                });
                // Displays Dialog object.
                dialog.show();
            }
        });

        // Cancel button's click event.
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewBookListener.updatePages(new Bundle());
            }
        });

        // Returns view to be viewed.
        return view;
    }

    // Interface function for interacting with MainActivity.
    public interface ViewBookListener {
        void updatePages(Bundle bundle);
    }

    // New Instance function.
    public static ViewBookFragment newInstance() {
        return new ViewBookFragment();
    }
}
