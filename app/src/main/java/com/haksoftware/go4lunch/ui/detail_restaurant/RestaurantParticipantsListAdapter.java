package com.haksoftware.go4lunch.ui.detail_restaurant;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.haksoftware.go4lunch.R;
import com.haksoftware.go4lunch.databinding.ColleagueItemListBinding;
import com.haksoftware.go4lunch.model.Colleague;

import java.util.Objects;

public class RestaurantParticipantsListAdapter extends ListAdapter<Colleague, RestaurantParticipantsListAdapter.ColleagueViewHolder> {

    protected RestaurantParticipantsListAdapter() {
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

    @NonNull
    @Override
    public ColleagueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ColleagueItemListBinding binding = ColleagueItemListBinding.inflate(LayoutInflater.from(parent.getContext()));
            return new ColleagueViewHolder(binding);
            }

    @Override
    public void onBindViewHolder(@NonNull ColleagueViewHolder holder, int position) {
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

            if (colleague.getLastSelectedRestaurantDate() != null) {
                todayRestaurant += " " + binding.getRoot().getContext().getString(R.string.choice_made);
                binding.nameColleague.setTextColor(itemView.getResources().getColor(R.color.black));
                todayRestaurant += " " + Objects.requireNonNull(colleague.getSelectedRestaurant()).getName();
            }
            binding.nameColleague.setText(todayRestaurant);
            Glide.with(binding.getRoot())
                    .load(colleague.getUrlPicture())
                    .circleCrop()
                    .into(binding.colleagueAvatar);

        }
    }
}