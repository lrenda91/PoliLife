package it.polito.mad.polilife;

/**
 * Created by luigi on 04/05/15.
 */

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import it.polito.mad.polilife.PoliLifeNavigationDrawer.OnItemClickListener;

public class NavigationAdapter extends RecyclerView.Adapter<NavigationAdapter.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;

    private String mNavTitles[];
    private int mIcons[];

    String name;
    Bitmap profileBitmap;
    int profileResourceID;
    String email;
    OnItemClickListener listener;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        int Holderid;
        TextView textView;
        ImageView iconImageView;
        ImageView profileImageView;
        TextView Name;
        TextView email;

        public ViewHolder(View itemView, int ViewType) {
            super(itemView);
            Holderid = ViewType;
            switch(ViewType){
                case TYPE_ITEM:
                    textView = (TextView) itemView.findViewById(R.id.rowText);
                    iconImageView = (ImageView) itemView.findViewById(R.id.rowIcon);
                    break;
                case TYPE_HEADER:
                    Name = (TextView) itemView.findViewById(R.id.name);
                    email = (TextView) itemView.findViewById(R.id.email);
                    profileImageView = (ImageView) itemView.findViewById(R.id.circle_view);
                    break;
                case TYPE_FOOTER:
                    Name = (TextView) itemView.findViewById(R.id.rowText);
                    iconImageView = (ImageView) itemView.findViewById(R.id.rowIcon);
                    break;
            }
        }
    }


    public NavigationAdapter(String[] titles, int[] icons, OnItemClickListener itemClickListener) {
        listener = itemClickListener;
        mNavTitles = titles;
        mIcons = icons;
    }

    @Override
    public NavigationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final ViewGroup p = parent;
        int itemLayoutResID = 0;
        switch (viewType){
            case TYPE_HEADER:
                itemLayoutResID = R.layout.layout_nav_drawer_header;
                break;
            case TYPE_ITEM:
                itemLayoutResID = R.layout.layout_material_list_item;
                break;
            case TYPE_FOOTER:
                itemLayoutResID = R.layout.layout_material_list_item;
                break;
        }
        View v = LayoutInflater.from(parent.getContext()).inflate(itemLayoutResID, parent, false);
        if (viewType == TYPE_ITEM){
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int itemPosition = ((RecyclerView) p).getChildPosition(v);
                    if (listener != null){
                        listener.onItemClick(v, itemPosition-1);
                    }
                }
            });
        }
        else if (viewType == TYPE_FOOTER){
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        listener.onFooterClick(v);
                    }
                }
            });
        }
        else if (viewType == TYPE_HEADER){
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        listener.onHeaderClick(v);
                    }
                }
            });
        }
        return new ViewHolder(v, viewType);
    }

    @Override
    public void onBindViewHolder(NavigationAdapter.ViewHolder holder, int position) {
        if (holder.Holderid == TYPE_ITEM) {
            holder.textView.setText(mNavTitles[position - 1]);
            holder.iconImageView.setImageResource(mIcons[position - 1]);
            //holder.iconImageView.setImageBitmap();
        }
        else if (holder.Holderid == TYPE_HEADER){
            if (profileResourceID != 0){
                holder.profileImageView.setImageResource(profileResourceID);
            }
            else if (profileBitmap != null){
                holder.profileImageView.setImageBitmap(profileBitmap);
            }
            holder.Name.setText(name);
            holder.email.setText(email);
        }
        else if (holder.Holderid == TYPE_FOOTER){
            holder.Name.setText(R.string.logout);
            //holder.iconImageView.setImageResource(R.drawable.ic_action_logout);
        }
    }

    @Override
    public int getItemCount() {
        return mNavTitles.length + 2;
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0) return TYPE_HEADER;
        if (position == getItemCount()-1) return TYPE_FOOTER;
        return TYPE_ITEM;
    }

}