package ots.il.ac.shenkar.ots.controlers;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ots.il.ac.shenkar.ots.R;
import ots.il.ac.shenkar.ots.apputiles.AppUtils;
import ots.il.ac.shenkar.ots.common.User;
import ots.il.ac.shenkar.ots.listeners.ItemClickListener;
import ots.il.ac.shenkar.ots.listeners.ItemLongClickListener;

/**
 * Created by moshe on 23-02-16.
 */
public class UserListAdapter  extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {
    private List<User> Employee;
    private ItemClickListener mItemClickListener;
    private ItemLongClickListener mItemLongClickListener;
    public UserListAdapter(List<User> listTeam) {
        this.Employee = listTeam;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_user_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(v , mItemClickListener , mItemLongClickListener);
        return viewHolder;
    }

    public void setmItemClickListener(ItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public void setmItemLongClickListener(ItemLongClickListener listener) {
        this.mItemLongClickListener = listener;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = Employee.get(position);
        holder.mUserFName.setText(user.getUserName());
        holder.mUserLName.setText(user.getUserLName());
        holder.mUserPhone.setText(user.getUserPhone());



    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return Employee.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener ,
            View.OnLongClickListener {
        private ItemClickListener mListener;
        private ItemLongClickListener mLongListener;

        private TextView mUserFName;
        private TextView mUserLName;
        private TextView mUserPhone;
        private CardView mCardView;

        private boolean click;


        public ViewHolder(final View itemView, ItemClickListener mItemClickListener,
                          ItemLongClickListener mItemLongClickListener) {
            super(itemView);
            this.mListener = mItemClickListener;
            this.mLongListener = mItemLongClickListener;
            click = false;
            mUserFName = (TextView) itemView.findViewById(R.id.id_user_list_fname);
            mUserLName = (TextView) itemView.findViewById(R.id.id_user_list_lname);
            mUserPhone = (TextView) itemView.findViewById(R.id.id_user_list_phone);
            mCardView = (CardView) itemView.findViewById(R.id.card_view_team_management);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onItemClick(v, getPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (mLongListener != null) {
                mLongListener.onItemLongClick(v, getPosition());
                if(click == false) {
                    mCardView.setCardBackgroundColor((Color.rgb(182, 222, 251)));
                    click = true;
                }else if (click == true){
                        mCardView.setCardBackgroundColor((Color.rgb(255, 255, 255)));
                        click = false;

                }
            }
            return true;
        }

    }
}