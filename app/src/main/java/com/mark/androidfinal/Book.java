package com.mark.androidfinal;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;

/**
 * This Class outlines a Book object and the attributes it contains.
 */

public class Book implements Parcelable {
    // Attributes of Book objects.
    private String uniqueId;
    private String book_name;
    private String reader;
    private Date start_date;
    private Date end_date;
    private int total_pages;
    private int pages_read;
    private ArrayList<Float> hours_spent_per_week;
    private boolean completed;
    private String userId;
    private String firebaseKey;

    Book() {
    }

    // Constructor.
    public Book(String book_name, String reader, int total_pages) {
        this.book_name = book_name;
        this.reader = reader;
        this.start_date = new Date();
        this.total_pages = total_pages;
        this.completed = false;
    }

// Getters and Setters.
    public String getBook_name() {
        return book_name;
    }

    public String getReader() {
        return reader;
    }

    public Date getStart_date() {
        return start_date;
    }

    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }

    public Date getEnd_date() {
        return end_date;
    }

    public void setEnd_date() {
        this.end_date = new Date();
    }

    public int getTotal_pages() {
        return total_pages;
    }

    public int getPages_read() {
        return pages_read;
    }

    // This Setter might want to be merged with add_hours_for_week method/setter.
    public void setPages_read(int pages_read) {
        this.pages_read = pages_read;
    }

    public ArrayList<Float> getHours_spent_per_week() {
        return hours_spent_per_week;
    }

    public void add_hours_for_week(float hours) {
        // TODO determine which week it currently is
        // TODO add hours to that week in arraylist  --  hours_spent_per_week[3] = 42
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirebaseKey() {
        return firebaseKey;
    }

    public void setFirebaseKey(String firebaseKey) {
        this.firebaseKey = firebaseKey;
    }

    protected Book(Parcel in) {
        uniqueId = in.readString();
        book_name = in.readString();
        reader = in.readString();
        long tmpStart_date = in.readLong();
        start_date = tmpStart_date != -1 ? new Date(tmpStart_date) : null;
        long tmpEnd_date = in.readLong();
        end_date = tmpEnd_date != -1 ? new Date(tmpEnd_date) : null;
        total_pages = in.readInt();
        pages_read = in.readInt();
        if (in.readByte() == 0x01) {
            hours_spent_per_week = new ArrayList<Float>();
            in.readList(hours_spent_per_week, Float.class.getClassLoader());
        } else {
            hours_spent_per_week = null;
        }
        completed = in.readByte() != 0x00;
        userId = in.readString();
        firebaseKey = in.readString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uniqueId);
        dest.writeString(book_name);
        dest.writeString(reader);
        dest.writeLong(start_date != null ? start_date.getTime() : -1L);
        dest.writeLong(end_date != null ? end_date.getTime() : -1L);
        dest.writeInt(total_pages);
        dest.writeInt(pages_read);
        if (hours_spent_per_week == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(hours_spent_per_week);
        }
        dest.writeByte((byte) (completed ? 0x01 : 0x00));
        dest.writeString(userId);
        dest.writeString(firebaseKey);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Book> CREATOR = new Parcelable.Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };
}
