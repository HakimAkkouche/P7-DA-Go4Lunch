package com.haksoftware.go4lunch.ui.colleagues;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.haksoftware.go4lunch.R;
import com.haksoftware.go4lunch.databinding.ColleagueItemListBinding;
import com.haksoftware.go4lunch.model.Colleague;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class ColleaguesListAdapter extends ListAdapter<Colleague, ColleaguesListAdapter.ColleagueViewHolder> {

    protected ColleaguesListAdapter() {
        super(new DiffUtil.ItemCallback<Colleague>() {
            @Override
            public boolean areItemsTheSame(@NonNull Colleague oldItem, @NonNull Colleague newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areContentsTheSame(@NonNull Colleague oldItem, @NonNull Colleague newItem) {
                return oldItem.equals(newItem);
            }
        });
    }

    /**
     * Called when RecyclerView needs a new {@link ColleagueViewHolder} of the given type to represent
     * an item.
     * <p>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p>
     * The new ViewHolder will be used to display items of the adapter using
     * {@link #onBindViewHolder(ColleagueViewHolder, int)}. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link ViewGroup#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(ColleagueViewHolder, int)
     */
    @NonNull
    @Override
    public ColleagueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ColleagueItemListBinding binding = ColleagueItemListBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new ColleagueViewHolder(binding);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link ColleagueViewHolder#itemView} to reflect the item at the given
     * position.
     * <p>
     * Note that unlike {@link ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use {@link ColleagueViewHolder#getAdapterPosition()} which will
     * have the updated adapter position.
     * <p>
     * Override  #onBindViewHolder(ColleagueViewHolder, int) instead if Adapter can
     * handle efficient partial bind.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ColleaguesListAdapter.ColleagueViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public static class ColleagueViewHolder extends RecyclerView.ViewHolder {

        ColleagueItemListBinding binding;
        public ColleagueViewHolder(@NonNull ColleagueItemListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        public void bind(@NonNull Colleague colleague) {
            String todayRestaurant = colleague.getUserName();
            binding.nameColleague.setTextColor(binding.getRoot().getContext().getResources().getColor(R.color.light_grey));
            String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            if(colleague.getLastSelectedRestaurantDate() != null) {
                if(Objects.requireNonNull(colleague.getLastSelectedRestaurantDate()).equals(currentDate))  {
                    todayRestaurant += " " + binding.getRoot().getContext().getString(R.string.choice_made) + " ";
                    if(colleague.getSelectedRestaurant() != null) {
                        todayRestaurant += " " + colleague.getSelectedRestaurant().getName();
                        binding.nameColleague.setTextColor(itemView.getResources().getColor(R.color.black));
                        binding.colleagueItem.setOnClickListener(view -> {
                            Bundle bundle = new Bundle();

                            bundle.putParcelable("selectedRestaurant", colleague.getSelectedRestaurant());
                            if (view.getContext() instanceof AppCompatActivity) {
                                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                                NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment_activity_main);
                                navController.navigate(R.id.action_ColleaguesFragment_to_RestaurantDetailFragment, bundle);
                            }
                        });
                    }
                }
            }
            else {
                todayRestaurant += " " +
                        binding.getRoot().getContext().getString(R.string.choice_not_made);
            }

            binding.nameColleague.setText(todayRestaurant);
            Glide.with(binding.getRoot())
                    .load(colleague.getUrlPicture())
                    .circleCrop()
                    .into(binding.colleagueAvatar);

        }
    }
}
