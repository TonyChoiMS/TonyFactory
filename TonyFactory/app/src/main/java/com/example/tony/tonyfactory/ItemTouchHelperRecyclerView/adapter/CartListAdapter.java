package com.example.tony.tonyfactory.ItemTouchHelperRecyclerView.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tony.tonyfactory.ItemTouchHelperRecyclerView.model.Item;
import com.example.tony.tonyfactory.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017-11-21.
 */

public class CartListAdapter extends RecyclerView.Adapter<CartListAdapter.MyViewHolder> implements Filterable{

    private Context context;
    private List<Item> itemList;
    private List<Item> filterItemList;
    private CartListAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.description)
        TextView description;
        @BindView(R.id.price)
        TextView price;
        @BindView(R.id.thumbnail)
        ImageView thumbnail;
        @BindView(R.id.view_background)
        RelativeLayout viewBackground;
        @BindView(R.id.view_foreground)
        public RelativeLayout viewForeground;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            viewForeground.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemSelected(filterItemList.get(getAdapterPosition()));
                }
            });
        }
    }

    public CartListAdapter(Context context, List<Item> cartList, CartListAdapterListener listener) {
        this.context = context;
        this.itemList = cartList;
        this.filterItemList = cartList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.cart_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Item item = filterItemList.get(position);
        holder.name.setText(item.getName());
        holder.description.setText(item.getDescription());
        holder.price.setText("$" + item.getPrice());

        Glide.with(context)
                .load(item.getThumbnail())
                .into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return filterItemList.size();
    }

    public void removeItem(int position) {
        filterItemList.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Item item, int position) {
        filterItemList.add(position, item);

        notifyItemInserted(position);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty())
                    filterItemList = itemList;
                else {
                    List<Item> filteredList = new ArrayList<>();
                    for (Item item : itemList) {
                        if (item.getName().toLowerCase().contains(charString.toLowerCase()) ||
                                item.getDescription().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(item);
                        }
                    }
                    filterItemList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filterItemList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filterItemList = (ArrayList<Item>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface CartListAdapterListener {
        void onItemSelected(Item item);
    }
}
