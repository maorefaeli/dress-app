package finalproj.dressapp.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import finalproj.dressapp.R;
import finalproj.dressapp.Utils;

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
        int availableMoney = params.getInt("money");
        minDate = params.getLong("minDate");
        maxDate = params.getLong("maxDate");
        int cost = params.getInt("cost");
//        ((ImageView) dialogContainer.findViewById(R.id.itemImage)).setImageURI(Uri.parse(params.getString("imageSrc")));
        ((TextView) dialogContainer.findViewById(R.id.itemDescription)).setText(params.getString("description"));
        TextView costView = dialogContainer.findViewById(R.id.cost);
        costView.setText(String.valueOf(cost));
        ((TextView) dialogContainer.findViewById(R.id.owner)).setText(params.getString("owner"));
        Float itemRating = Float.parseFloat(params.getString("rating"));
        ((RatingBar) dialogContainer.findViewById(R.id.rating)).setRating(itemRating);
        final EditText fromDate = dialogContainer.findViewById(R.id.fromDate);
        final EditText toDate = dialogContainer.findViewById(R.id.toDate);

        TextView reviewers = dialogContainer.findViewById(R.id.numOfReviewerss);

        int numOfReviews = params.getInt("reviewers");
        String reviewersText = reviewers.getText() + " " +
                params.getInt("reviewers") +
                " reviews";
        reviewers.setText(reviewersText);

        if (availableMoney < cost) {
            fromDate.setEnabled(false);
            toDate.setEnabled(false);
            ((Button) dialogContainer.findViewById(R.id.order)).setEnabled(false);
            dialogContainer.findViewById(R.id.notEnoughCoins).setVisibility(View.VISIBLE);
        } else {
            fromDate.setOnClickListener(v -> {
                AlertDialog.Builder builder12 = new AlertDialog.Builder(getContext());
                LinearLayout dateContainer = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.date_picker, null);
                builder12.setView(dateContainer);

                final DatePicker date = dateContainer.findViewById(R.id.date);
                date.setMinDate(System.currentTimeMillis());
                date.setMaxDate(maxDate);

                builder12.setPositiveButton("OK", (dialog12, which) -> {
                    Calendar calendar = new GregorianCalendar(date.getYear(), date.getMonth(), date.getDayOfMonth());
                    minDate = calendar.getTimeInMillis();
                    String dateString = date.getDayOfMonth() + "/" + (date.getMonth() + 1)
                            + "/" + (date.getYear() - 2000);
                    fromDate.setText(dateString);
                    Utils.setFromDate(Utils.LongToDateFormat(minDate));
                });
                builder12.create().show();
            });

            toDate.setOnClickListener(v -> {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                LinearLayout dateContainer = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.date_picker, null);
                builder1.setView(dateContainer);

                final DatePicker date = dateContainer.findViewById(R.id.date);
                date.setMinDate(minDate);
                date.setMaxDate(maxDate);

                builder1.setPositiveButton("OK", (dialog1, which) -> {
                    Calendar calendar = new GregorianCalendar(date.getYear(), date.getMonth(), date.getDayOfMonth());
                    String dateString = date.getDayOfMonth() + "/" + (date.getMonth() + 1) + "/" + (date.getYear() - 2000);
                    toDate.setText(dateString);
                    Utils.setToDate(Utils.LongToDateFormat(maxDate));
                    int newCost = (int) (((calendar.getTimeInMillis() - minDate) / 86400000) + 1) * cost;
                    costView.post(() -> costView.setText(String.valueOf(newCost)));
                });
                builder1.create().show();
            });
        }

        dialogContainer.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        return dialog;
    }
}
