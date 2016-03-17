package ots.il.ac.shenkar.ots.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import ots.il.ac.shenkar.ots.R;
import ots.il.ac.shenkar.ots.apputiles.AppConst;
import ots.il.ac.shenkar.ots.apputiles.AppUtils;
import ots.il.ac.shenkar.ots.common.Task;
import ots.il.ac.shenkar.ots.common.User;
import ots.il.ac.shenkar.ots.controlers.AppController;
import ots.il.ac.shenkar.ots.controlers.TasksFragmentAdapter;
import ots.il.ac.shenkar.ots.listeners.ItemClickListener;
import ots.il.ac.shenkar.ots.listeners.ItemLongClickListener;

/**
 * Created by moshe on 26-02-16.
 */
public class DoneTaskFragment extends Fragment implements ItemLongClickListener,
        ItemClickListener {
    private AppController mController;
    private RecyclerView mRecyclerView;
    private TasksFragmentAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private View mView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<Task> taskList;
    private CloudRefreshReceiver r;
    private Animation animAlpha;
    private Task currentTask;
    private TextView mTvEmptyWaitingList , waitingTask;
    private User user;
    private Menu menu;
    private List<Task> toDeleteList;
    ////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////View task dialog vars///////////////////////////
    private TextView mTitle, mNote, mCategory, mTaskTime, mTaskDate, mPriority, mStatus,
            mEmployeeTvCurrentUser , mEmployeeTv;
    private Button mBtnAccept, mBtnReject, mBtnOk, mBtnCancel ,mBtnSave;
    private Spinner mCategorySpinner , mPrioritySpinner ,mStatusSpinner;
    private ImageButton mIbCamera;
    private ImageView mIvTaskPic;

    /////////////////////////////////////////////////////////////////////////////////

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_layout, container, false);
        setHasOptionsMenu(true);
        user = AppUtils.createUserFromPars(ParseUser.getCurrentUser());
        if(user.getIsManager()){
            toDeleteList = new ArrayList<>();
        }
        init();
        return mView;

    }



    public void init() {
        animAlpha = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_alpha);
        mController = new AppController(this.getContext());
        mTvEmptyWaitingList = (TextView) mView.findViewById(R.id.id_waiting_empty_list);
        mTvEmptyWaitingList.setVisibility(View.INVISIBLE);
        waitingTask = (TextView) mView.findViewById(R.id.id_task_number);

        mRecyclerView = (RecyclerView) mView.findViewById(R.id.id_fragment_recycle_view);
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mSwipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.id_fragment_swipe);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);

                show();
            }
        });
        show();
    }

    public void  show(){
          taskList = mController.getTasksListByStatus(AppConst.DONE);
        Integer numOfTask = mController.getTasksListByStatus(AppConst.WAITING).size();
        waitingTask.setText(numOfTask.toString());
        mAdapter = new TasksFragmentAdapter(taskList, getContext());
        mAdapter.setmItemClickListener(this);
        mAdapter.setmItemLongClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
    }





    @Override
    public void onItemClick(View view, int pos) {
        view.startAnimation(animAlpha);
        showTaskContent(taskList.get(pos));
    }

    @Override
    public void onItemLongClick(View view, int pos) {
        if (user.getIsManager()) {
            view.startAnimation(animAlpha);
            MenuItem item = menu.getItem(2);
            if(!toDeleteList.contains(taskList.get(pos))){
                toDeleteList.add(taskList.get(pos));
            }else{
                toDeleteList.remove(taskList.get(pos));
            }

            if(toDeleteList.size() > 0) {
                item.setVisible(true);
            }else{
                item.setVisible(false);
            }
        }
    }

    public void showTaskContent(final Task task) {
        currentTask = task;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(true);
        final AlertDialog dialog = builder.create();
        View dialogView = dialog.getLayoutInflater().inflate(R.layout.view_task_page, null);
        dialog.setView(dialogView);

        mIbCamera = (ImageButton) dialogView.findViewById(R.id.id_task_view_camera);
        mEmployeeTv = (TextView) dialogView.findViewById(R.id.id_employee_tv);
        mStatus = (TextView) dialogView.findViewById(R.id.id_view_task_status);
        mTitle = (TextView) dialogView.findViewById(R.id.id_view_task_title);
        mNote = (TextView) dialogView.findViewById(R.id.id_view_task_notes);
        mCategory = (TextView) dialogView.findViewById(R.id.id_view_task_category);
        mTaskTime = (TextView) dialogView.findViewById(R.id.id_view_task_time);
        mTaskDate = (TextView) dialogView.findViewById(R.id.id_view_task_date);
        mPriority = (TextView) dialogView.findViewById(R.id.id_view_task_priority);
        mBtnReject = (Button) dialogView.findViewById(R.id.id_view_task_reject_button);
        mBtnAccept = (Button) dialogView.findViewById(R.id.id_view_task_accept_button);
        mBtnOk = (Button) dialogView.findViewById(R.id.id_view_task_ok_button);
        mBtnCancel = (Button) dialogView.findViewById(R.id.id_view_task_cancel_button);
        mBtnSave = (Button) dialogView.findViewById(R.id.id_view_task_save_button);
        mEmployeeTvCurrentUser = (TextView) dialogView.findViewById(R.id.id_view_task_employee);
        mCategorySpinner = (Spinner) dialogView.findViewById(R.id.Category_array);
        mPrioritySpinner = (Spinner) dialogView.findViewById(R.id.Priority_array);
        mStatusSpinner = (Spinner) dialogView.findViewById(R.id.id_status_array);
        mIvTaskPic = (ImageView) dialogView.findViewById(R.id.id_task_view_pic);
        mStatusSpinner.setVisibility(View.GONE);
        mCategorySpinner.setVisibility(View.GONE);
        mPrioritySpinner.setVisibility(View.GONE);
        mBtnCancel.setVisibility(View.GONE);
        mBtnSave.setVisibility(View.GONE);
        mStatusSpinner.setVisibility(View.GONE);
        mBtnReject.setVisibility(View.GONE);
        mBtnAccept.setVisibility(View.GONE);
        mIbCamera.setVisibility(View.GONE);

        if(user.getIsManager() == true) {
            mIvTaskPic.setImageBitmap(AppUtils.createImageFromBytes(currentTask.getDoneTaskPic()));

////if employee
        }else{
            mEmployeeTv.setVisibility(View.GONE);
            mEmployeeTvCurrentUser.setVisibility(View.GONE);
            mIvTaskPic.setImageBitmap(AppUtils.createImageFromBytes(currentTask.getDoneTaskPic()));
        }

        mBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        mTitle.setText(currentTask.getTitle());
        mNote.setText(currentTask.getContent());
        mCategory.setText(currentTask.getCategory());
        mEmployeeTvCurrentUser.setText(currentTask.getUser());
        mTaskTime.setText(currentTask.getTime());
        mTaskDate.setText(currentTask.getDate());
        mPriority.setText(currentTask.getPriority());
        mStatus.setText(currentTask.getTaskStatus());
        dialog.show();


    }

    public void refresh() {
        show();
        if(toDeleteList != null) {
            toDeleteList.clear();
        }
    }

    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(r);
    }

    public void onResume() {
        super.onResume();
        r = new CloudRefreshReceiver();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(r,
                new IntentFilter("TAG_REFRESH"));
    }



    private class CloudRefreshReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            DoneTaskFragment.this.refresh();
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (user.getIsManager()) {
            super.onCreateOptionsMenu(menu, inflater);
            this.menu = menu;
            MenuItem item = menu.getItem(2);
            item.setVisible(false);
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (user.getIsManager()) {
            switch (item.getItemId()) {
                case R.id.id_action_manager_delete:
                    // Not implemented here
                    mController.deleteTasksDialog(toDeleteList);
                    item.setVisible(false);
                    show();
                    return false;
                default:
                    break;
            }
        }
        return false;
    }
}




