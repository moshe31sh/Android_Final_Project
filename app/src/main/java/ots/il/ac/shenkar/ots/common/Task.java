package ots.il.ac.shenkar.ots.common;

import android.graphics.Bitmap;

/**
 * Created by moshe on 21-02-16.
 */
public class Task {
    private String title;
    private String text;
    private String date;
    private String manager;
    private String user;
    private String taskStatus;
    private String priority;
    private String category;
    private String time;
    private String taskId;
    private int firstRead;
    private int firstSync;
    private byte[] doneTaskPic;


    public Task(){
        title = "";
        text = "";
        date = "";
        manager = "";
        user = "";
        taskStatus = "";
        priority = "";
        category = "";
        time = "";
        taskId = "";
        firstRead = 0;
        firstSync = 0;
        doneTaskPic = null;
    }


    public String getContent() {
        return text;
    }

    public void setContent(String text) {
        this.text = text;
    }

    public String getDate() {

        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int isFirstRead() {
        return firstRead;
    }

    public void setFirstRead(int firstRead) {
        this.firstRead = firstRead;
    }


    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", date='" + date + '\'' +
                ", manager='" + manager + '\'' +
                ", user='" + user + '\'' +
                ", taskStatus='" + taskStatus + '\'' +
                ", priority='" + priority + '\'' +
                ", category='" + category + '\'' +
                ", time='" + time + '\'' +
                ", taskId='" + taskId + '\'' +
                ", firstRead=" + firstRead +
                '}';
    }

    public int getFirstSync() {
        return firstSync;
    }

    public void setFirstSync(int firstSync) {
        this.firstSync = firstSync;
    }

    public byte[] getDoneTaskPic() {
        return doneTaskPic;
    }

    public void setDoneTaskPic(byte[] doneTaskPic) {
        this.doneTaskPic = doneTaskPic;
    }


}
