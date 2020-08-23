package finalproj.dressapp;
import finalproj.dressapp.activities.MyClothesActivity;
import finalproj.dressapp.activities.ProfileActivity;
import finalproj.dressapp.fragments.ItemDialogFragment;
import finalproj.dressapp.httpclient.models.MyAppContext;
import finalproj.dressapp.httpclient.models.Product;
import finalproj.dressapp.httpclient.models.RentingDate;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder> {
    private List<Product> mDataset;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Activity mCallingActivity;
    private Context mContext;
    private List<Product> mUserWishlist;
    private Boolean mIsWishlist = false;
    private Boolean mIsMyClothes = false;

    // data is passed into the constructor
    public RecycleViewAdapter(Activity activity, Context context, List<Product> data) {
        this.mContext = context;
        this.mCallingActivity = activity;
        this.mInflater = LayoutInflater.from(context);
        this.mDataset = data;

        if (activity.getClass().getSimpleName().equals("WishListActivity")) {
            this.mIsWishlist = true;
        }
        else if (activity.getClass().getSimpleName().equals("MyClothesActivity")) {
            this.mIsMyClothes = true;
        }
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView titleTextView;
        public TextView datesTextView;
        public TextView ownerTextView;
        public TextView addressTextView;
        public TextView wishlistIcon;
        public TextView priceView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            titleTextView = (TextView) itemView.findViewById(R.id.postTitle);
            datesTextView = (TextView) itemView.findViewById(R.id.dates);
            ownerTextView = (TextView) itemView.findViewById(R.id.owner);
            addressTextView = (TextView) itemView.findViewById(R.id.address);
            wishlistIcon = (TextView) itemView.findViewById(R.id.postTitleWishlistIcon);
            priceView = (TextView) itemView.findViewById(R.id.price);
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.post_template, parent, false);
        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        final Product currentProduct = mDataset.get(position);

        String dates = Utils.DateFormatToShow(currentProduct.fromdate) + "-" + Utils.DateFormatToShow(currentProduct.todate);

        // Set item views based on your views and data model
        TextView titleTextView = holder.titleTextView;
        titleTextView.setText(currentProduct.name);
        TextView datesTextView = holder.datesTextView;
        datesTextView.setText(dates);
        TextView ownerTextView = holder.ownerTextView;
        ownerTextView.setText("Owner's name here");
        TextView addressTextView = holder.addressTextView;
        addressTextView.setText("Default Addrress, 220, Tel Aviv");
        holder.priceView.setText(String.valueOf(currentProduct.price));

        // For guests, don't allow to see the item renting popup dialog.
        if (Utils.getGuestStatus()) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                    new AlertDialog.Builder(mContext)
                            .setTitle("Not logged in.")
                            .setMessage("If you want to begin renting clothes, please login or register.")
                            .show();
               }
            });
        }
        else {
            // Setting the product ID on the wishlist icon for when the user presses it.
            TextView wishlistIcon = holder.wishlistIcon;
            Boolean isInWishList = false;

            if (mIsMyClothes) {
                wishlistIcon.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.icons8_empty, 0);
            }
            else {
                mUserWishlist = Utils.getCurrentUserWishlistItems();

                if (mUserWishlist != null && mUserWishlist.size() > 0) {
                    for (Product productIterator:mUserWishlist) {
                        if (productIterator.id.equals(currentProduct.id)) {
                            isInWishList = true;
                            break;
                        }
                    }
                }

                wishlistIcon.setTag(isInWishList);
                ((View)wishlistIcon.getParent()).setTag(currentProduct.id);

                if (isInWishList) {
                    wishlistIcon.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.icons8_heart_26_full, 0);
                }

                // Don't allow the item popup in wishlist view.
                if (!mIsWishlist) {
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ItemDialogFragment dialogFragment = new ItemDialogFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("description", currentProduct.name);
                            bundle.putString("imgSrc", currentProduct.image);
                            bundle.putInt("cost", currentProduct.price.intValue());
                            bundle.putLong("minDate", Utils.DateFormatToLong(currentProduct.fromdate));
                            bundle.putLong("maxDate", Utils.DateFormatToLong(currentProduct.todate));
                            bundle.putString("owner", currentProduct.name);
                            bundle.putInt("rating", 4);
                            bundle.putInt("numOfRenting", currentProduct.rentingDates.size());

                            for (int i = 0; i < currentProduct.rentingDates.size(); i++) {
                                RentingDate rentingDate = currentProduct.rentingDates.get(i);
                                bundle.putLong("startRent" + i, Utils.DateFormatToLong(rentingDate.fromDate));
                                bundle.putLong("endRent" + i, Utils.DateFormatToLong(rentingDate.toDate));
                            }

                            dialogFragment.setArguments(bundle);
                            dialogFragment.show(mCallingActivity.getFragmentManager(), "ItemDialog");
                        }
                    });
                }
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    // Convenience method for getting data at click position
    public Product getItem(int id) {
        return mDataset.get(id);
    }

    // Allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // Parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}