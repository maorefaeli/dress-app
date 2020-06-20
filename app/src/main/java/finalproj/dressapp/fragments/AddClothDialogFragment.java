package finalproj.dressapp.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import finalproj.dressapp.R;

public class AddClothDialogFragment extends DialogFragment {
    public static final int PICK_IMAGE = 1;
    private long minDate;
    private long maxDate;
    private ImageView imageView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final LinearLayout dialogContainer =
                (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.add_item_window, null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogContainer);

        final Dialog dialog = builder.create();

        minDate = System.currentTimeMillis();

        imageView = dialogContainer.findViewById(R.id.itemImage);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });

        final EditText fromDate = dialogContainer.findViewById(R.id.fromDate);
        fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                LinearLayout dateContainer = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.date_picker, null);
                builder.setView(dateContainer);

                final DatePicker date = dateContainer.findViewById(R.id.date);
                date.setMinDate(minDate);

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

        final EditText toDate = dialogContainer.findViewById(R.id.toDate);
        toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                LinearLayout dateContainer = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.date_picker, null);
                builder.setView(dateContainer);

                final DatePicker date = dateContainer.findViewById(R.id.date);
                date.setMinDate(minDate);

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

        dialogContainer.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialogContainer.findViewById(R.id.addCloth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        return dialog;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE) {

        }
    }
}
