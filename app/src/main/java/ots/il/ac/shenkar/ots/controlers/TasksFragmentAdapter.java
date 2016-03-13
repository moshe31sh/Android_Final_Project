package ots.il.ac.shenkar.ots.controlers;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import java.util.List;

import ots.il.ac.shenkar.ots.R;
import ots.il.ac.shenkar.ots.apputiles.AppConst;
import ots.il.ac.shenkar.ots.apputiles.AppUtils;
import ots.il.ac.shenkar.ots.common.Task;
import ots.il.ac.shenkar.ots.common.User;
import ots.il.ac.shenkar.ots.listeners.ItemClickListener;
import ots.il.ac.shenkar.ots.listeners.ItemLongClickListener;

/**
 * Created by moshe on 22-02-16.
 */
public class TasksFragmentAdapter  extends RecyclerView.Adapter<TasksFragmentAdapter.ViewHolder> {

    private List<Task> mTaskList;
    private ItemClickListener mItemClickListener;
    private ItemLongClickListener mItemLongClickListener;
    private boolean mIsManager;
    private Context context;

    public TasksFragmentAdapter(List<Task> taskList , Context context) {
        this.mTaskList = taskList;
        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_task_fragment, parent, false);
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
    public void onBindViewHolder(ViewHolder holder, final int position) {
        ParseUser parseUser = ParseUser.getCurrentUser();
        User user = AppUtils.createUserFromPars(parseUser);

        Task task = mTaskList.get(position);

        holder.mTaskTitle.setText(task.getTitle());
        holder.mTaskTitle.setTypeface(null, Typeface.BOLD);

        if(task.getPriority().equals("Urgent")) {
            holder.mImageView.setColorFilter(Color.RED);
        }else if(task.getPriority().equals("Low")){
            holder.mImageView.setColorFilter(Color.GREEN);
        }else{
            holder.mImageView.setColorFilter(Color.YELLOW);
        }
        holder.mData.setText(task.getDate());
        holder.mTime.setText(task.getTime());
        holder.mStatus.setText(task.getTaskStatus());
        holder.mCategory.setText(task.getCategory());
        if (user.getIsManager() == false) {
            if (task.isFirstRead() == AppConst.TRUE) {
                holder.mTime.setTypeface(null, Typeface.BOLD);
                holder.mData.setTypeface(null, Typeface.BOLD);
                holder.mStatus.setTypeface(null, Typeface.BOLD);
                holder.mCategory.setTypeface(null, Typeface.BOLD);
                holder.mTime.setTextColor(Color.BLACK);
                holder.mData.setTextColor(Color.BLACK);
                holder.mStatus.setTextColor(Color.BLACK);
                holder.mCategory.setTextColor(Color.BLACK);
                holder.mCardView.setCardBackgroundColor(Color.rgb(255, 87, 34));
            }
            holder.mUserEmail.setVisibility(View.GONE);
        }else {
            holder.mUserEmail.setText(task.getUser());
        }
        if(task.getTaskStatus().equals("Done")){
            holder.mStatusXImageView.setVisibility(View.INVISIBLE);
            holder.mStatus.setVisibility(View.INVISIBLE);

        }else if (task.getTaskStatus().equals("Reject")){
            holder.mStatusVImageView.setVisibility(View.INVISIBLE);
            holder.mStatus.setVisibility(View.INVISIBLE);
        }else{
            holder.mStatusXImageView.setVisibility(View.INVISIBLE);
            holder.mStatusVImageView.setVisibility(View.INVISIBLE);
        }
    }



    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return mTaskList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener ,
            View.OnLongClickListener {

        private TextView mTaskTitle;
        private TextView mData;
        private TextView mTime;
        private TextView mStatus;
        private TextView mCategory;
        private TextView mUserEmail;
        private CardView mCardView;
        private ImageView mImageView;
        private ImageView mStatusVImageView;
        private ImageView mStatusXImageView;
        private boolean click;
        private User user;


        private ItemClickListener mListener;
        private ItemLongClickListener mLongListener;

        public ViewHolder(View itemView, ItemClickListener mItemClickListener,
                          ItemLongClickListener mItemLongClickListener) {
            super(itemView);
            this.mListener = mItemClickListener;
            this.mLongListener = mItemLongClickListener;
            click = false;
            user = AppUtils.createUserFromPars(ParseUser.getCurrentUser());
            mTaskTitle = (TextView) itemView.findViewById(R.id.id_task_fragment_title);
            mData = (TextView) itemView.findViewById(R.id.id_task_fragment_date);
            mCardView = (CardView) itemView.findViewById(R.id.card_view_team_management);
            mTime = (TextView) itemView.findViewById(R.id.id_task_fragment_time);
            mImageView = (ImageView)itemView.findViewById(R.id.id_circle);
            mStatusVImageView = (ImageView)itemView.findViewById(R.id.id_status_v);
            mStatusXImageView = (ImageView)itemView.findViewById(R.id.id_status_x);
            mStatus = (TextView) itemView.findViewById(R.id.id_task_fragment_status);
            mCategory = (TextView)itemView.findViewById(R.id.id_task_fragment_category);
            mUserEmail =(TextView)itemView.findViewById(R.id.id_task_fragment_user);
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
                if (user.getIsManager()) {
                    if (click == false) {
                        mCardView.setCardBackgroundColor((Color.rgb(182, 222, 251)));
                        click = true;
                    } else if (click == true) {
                        mCardView.setCardBackgroundColor((Color.rgb(255, 255, 255)));
                        click = false;

                    }
                }
            }
            return true;
        }

    }
}