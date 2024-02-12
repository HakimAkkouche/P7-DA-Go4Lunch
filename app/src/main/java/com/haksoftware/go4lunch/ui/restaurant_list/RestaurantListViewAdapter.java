package com.haksoftware.go4lunch.ui.restaurant_list;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.haksoftware.go4lunch.databinding.RestaurantItemListBinding;
import com.haksoftware.go4lunch.utils.PlacesApiHelper;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantListViewAdapter extends ListAdapter<RestaurantViewState, RestaurantListViewAdapter.RestaurantViewHolder> {

    protected RestaurantListViewAdapter() {
        super(new DiffUtil.ItemCallback<RestaurantViewState>() {
            @Override
            public boolean areItemsTheSame(@NonNull RestaurantViewState oldItem, @NonNull RestaurantViewState newItem) {
                boolean result = oldItem.getRestaurantId().equals(newItem.getRestaurantId());
                return result;
            }

            @Override
            public boolean areContentsTheSame(@NonNull RestaurantViewState oldItem, @NonNull RestaurantViewState newItem) {
                boolean result = oldItem.equals(newItem);
                return result;
            }
        });
    }

    /**
     * Called when RecyclerView needs a new {@link RestaurantViewHolder} of the given type to represent
     * an item.
     * <p>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p>
     * The new ViewHolder will be used to display items of the adapter using
     * {@link #onBindViewHolder(RestaurantViewHolder, int)}. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(RestaurantViewHolder, int)
     */
    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RestaurantItemListBinding binding = RestaurantItemListBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new RestaurantViewHolder(binding);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link RestaurantViewHolder#itemView} to reflect the item at the given
     * position.
     * <p>
     * Note that unlike {@link ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use {@link RestaurantViewHolder#getAdapterPosition()} which will
     * have the updated adapter position.
     * <p>
     * Override {@link #onBindViewHolder(RestaurantViewHolder, int)} instead if Adapter can
     * handle efficient partial bind.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        holder.bind( (RestaurantViewState) getItem(position));
    }

    public static class RestaurantViewHolder extends RecyclerView.ViewHolder {

        RestaurantItemListBinding binding;
        public RestaurantViewHolder(@NonNull RestaurantItemListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        public void bind(@NonNull RestaurantViewState restaurant) {
            binding.restaurantNameTv.setText(restaurant.getName());
            binding.addressTv.setText(restaurant.getAddress());
            binding.distanceTv.setText(String.valueOf(restaurant.getDistance()));
            binding.colleaguesCountTv.setText(String.valueOf(restaurant.getColleaguesCount()));
            binding.openingTimeTv.setText("close at 14");
            binding.rating.setRating(restaurant.getRating());
            Call<ResponseBody> call = PlacesApiHelper.getRestaurantPhoto(restaurant.getUrlPicture(), 400, 400);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        try {
                            byte[] imageBytes = response.body().bytes();

                            Glide.with(binding.getRoot())
                                    .load(imageBytes).centerCrop()
                                    .into(binding.photImgv);
                            // Faites quelque chose avec les données binaires de l'image (par exemple, enregistrez-les dans un fichier)
                        } catch (IOException e) {
                            Log.e("Erreur", "Onfailure" + call.request().url() + " " + call.request().body().toString());
                            e.printStackTrace();
                        }
                    } else {
                        System.err.println("Erreur : " + response.code() + " - " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("Erreur", "Onfailure" + call.request().url() + " " + call.request().body().toString());
                    t.printStackTrace();
                }
            });
        }
    }
}
