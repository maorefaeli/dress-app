package finalproj.dressapp.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import finalproj.dressapp.activities.MyClothesActivity;
import finalproj.dressapp.Utils;
import finalproj.dressapp.httpclient.APIClient;
import finalproj.dressapp.httpclient.APIInterface;
import finalproj.dressapp.httpclient.models.Product;
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
                        Calendar calendar = new GregorianCalendar(date.getYear(), date.getMonth(), date.getDayOfMonth());
                        maxDate = calendar.getTimeInMillis();
                        String dateString = date.getDayOfMonth() + "/" + (date.getMonth() + 1)
                                + "/" + (date.getYear() - 2000);
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
                EditText name = (EditText) dialogContainer.findViewById(R.id.itemDescription);
                EditText cost = (EditText) dialogContainer.findViewById(R.id.cost);
                ImageView image = (ImageView) dialogContainer.findViewById(R.id.itemImage);
    
                Product product = new Product(name.getText().toString(), Integer.parseInt(cost.getText().toString()), Utils.LongToDateFormat(minDate), Utils.LongToDateFormat(maxDate), image.toString());
                APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
    
                Call<Product> call = apiInterface.doAddItem(product, Utils.getUserId(getActivity().getApplicationContext()));
                call.enqueue(new Callback<Product>() {
                    @Override
                    public void onResponse(Call<Product> call, Response<Product> response) {
    
                        if (response.code() == 200) {
                            dialog.dismiss();
                        }
                    }
    
                    @Override
                    public void onFailure(Call<Product> call, Throwable t) {
                        new AlertDialog.Builder(getActivity())
                            .setTitle("failure")
                            .setMessage(t.getMessage())
                            .show();
                        call.cancel();
                    }
                });
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
