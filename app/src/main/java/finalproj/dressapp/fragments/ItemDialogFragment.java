package finalproj.dressapp.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import finalproj.dressapp.R;

/**
 * Created by Shai on 19/05/2020.
 */
public class ItemDialogFragment extends DialogFragment {
    private long minDate;
    private long maxDate;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final LinearLayout dialogContainer =
                (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.item_window, null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogContainer);

        final Dialog dialog = builder.create();

        Bundle params = getArguments();
        minDate = params.getLong("minDate");
        maxDate = params.getLong("maxDate");
//        ((ImageView) dialogContainer.findViewById(R.id.itemImage)).setImageURI(Uri.parse(params.getString("imageSrc")));
        ((TextView) dialogContainer.findViewById(R.id.itemDescription)).setText(params.getString("description"));
        ((TextView) dialogContainer.findViewById(R.id.cost)).setText(String.valueOf(params.getInt("cost")));
        final EditText fromDate = (EditText) dialogContainer.findViewById(R.id.fromDate);
        fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                LinearLayout dateContainer = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.date_picker, null);
                builder.setView(dateContainer);

                final DatePicker date = ((DatePicker) dateContainer.findViewById(R.id.date));
                date.setMinDate(minDate);
                date.setMaxDate(maxDate);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Calendar calendar = new GregorianCalendar(date.getYear(), date.getMonth(), date.getDayOfMonth());
                        minDate = calendar.getTimeInMillis();
                        String dateString = date.getDayOfMonth() + "/" + (date.getMonth() + 1)
                                + "/" + (date.getYear() - 2000);
                        fromDate.setText(dateString);
                    }
                });
                builder.create().show();
            }
        });

        final EditText toDate = (EditText) dialogContainer.findViewById(R.id.toDate);
        toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                LinearLayout dateContainer = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.date_picker, null);
                builder.setView(dateContainer);

                final DatePicker date = ((DatePicker) dateContainer.findViewById(R.id.date));
                date.setMinDate(minDate);
                date.setMaxDate(maxDate);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String dateString = date.getDayOfMonth() + "/" + (date.getMonth() + 1) + "/" + (date.getYear() - 2000);
                        toDate.setText(dateString);
                    }
                });
                builder.create().show();
            }
        });

        dialogContainer.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        return dialog;
    }
}
