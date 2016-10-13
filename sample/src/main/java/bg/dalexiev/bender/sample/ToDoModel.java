package bg.dalexiev.bender.sample;

import java.util.Calendar;

public class ToDoModel {

    private long mId;
    private String mTitle;
    private Calendar mCreationDate;
    private boolean isDone;

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Calendar getCreationDate() {
        return mCreationDate;
    }

    public void setCreationDate(Calendar creationDate) {
        mCreationDate = creationDate;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setIsDone(boolean isDone) {
        this.isDone = isDone;
    }
}
