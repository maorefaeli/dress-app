package finalproj.dressapp.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.os.Bundle;
import android.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import finalproj.dressapp.R;
import finalproj.dressapp.Utils;
import finalproj.dressapp.httpclient.APIClient;
import finalproj.dressapp.httpclient.APIInterface;
import finalproj.dressapp.httpclient.models.ProductEdit;

public class EditItemFragment extends DialogFragment {
    private long minDate;
    private long maxDate;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final LinearLayout dialogContainer =
                (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.edit_item_window, null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogContainer);

        final Dialog dialog = builder.create();

        Bundle params = getArguments();
        minDate = System.currentTimeMillis();
        EditText itemDescription = dialogContainer.findViewById(R.id.itemDescription);
        itemDescription.setText(params.getString("title"));

        TextView cost =  dialogContainer.findViewById(R.id.cost);
        cost.setText(String.valueOf(params.getInt("cost")));

        RatingBar rating = dialogContainer.findViewById(R.id.rating);
        rating.setRating(params.getInt("rating"));

        final EditText fromDate = dialogContainer.findViewById(R.id.fromDate);
        fromDate.setText(Utils.LongToDateFormat(params.getLong("from")));
        fromDate.setOnClickListener(v -> {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
            LinearLayout dateContainer = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.date_picker, null);
            builder1.setView(dateContainer);

            final DatePicker date = dateContainer.findViewById(R.id.date);
            date.setMinDate(minDate);

            builder1.setPositiveButton("OK", (dialog1, which) -> {
                Calendar calendar = new GregorianCalendar(date.getYear(), date.getMonth(), date.getDayOfMonth());
                minDate = calendar.getTimeInMillis();
                String dateString = date.getDayOfMonth() + "/" + (date.getMonth() + 1)
                        + "/" + (date.getYear() - 2000);
                fromDate.setText(dateString);
            });
            builder1.create().show();
        });

        final EditText toDate = dialogContainer.findViewById(R.id.toDate);
        toDate.setText(Utils.LongToDateFormat(params.getLong("to")));
        toDate.setOnClickListener(v -> {
            AlertDialog.Builder builder12 = new AlertDialog.Builder(getContext());
            LinearLayout dateContainer = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.date_picker, null);
            builder12.setView(dateContainer);

            final DatePicker date = dateContainer.findViewById(R.id.date);
            date.setMinDate(minDate);

            builder12.setPositiveButton("OK", (dialog12, which) -> {
                String dateString = date.getDayOfMonth() + "/" + (date.getMonth() + 1) + "/" + (date.getYear() - 2000);
                toDate.setText(dateString);
                Calendar calendar = new GregorianCalendar(date.getYear(), date.getMonth(), date.getDayOfMonth());
                maxDate = calendar.getTimeInMillis();
            });
            builder12.create().show();
        });

        dialogContainer.findViewById(R.id.cancel).setOnClickListener(v -> dialog.dismiss());

//        dialogContainer.findViewById(R.id.ok).setOnClickListener(v -> {
//            dialog.dismiss();
//
//            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
//            ProductEdit productEdit = new ProductEdit(params.getString("id"), itemDescription.getText().toString(),
//                    Integer.parseInt(cost.getText().toString()), String.valueOf(minDate), String.valueOf(maxDate));
//            apiInterface.doEditProduct(productEdit);
//        });

        return dialog;
    }
}
