package com.gjq.demo.commom;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SortCursor extends CursorWrapper implements Comparator<SortCursor.SortEntry> {

    public SortCursor(Cursor cursor) {
        super(cursor);
    }

    Cursor mCursor;
    ArrayList<SortEntry> sortList = new ArrayList<SortEntry>();
    int mPos = 0;
    public static class SortEntry {
        public String key;
        public int order;
    }

    public int compare(SortEntry entry1, SortEntry entry2) {
        return entry1.key == null ? 0 : -entry1.key.compareTo(entry2.key);
    }

    public SortCursor(Cursor cursor, String columnName) {
        super(cursor);

        mCursor = cursor;
        if (mCursor != null && mCursor.getCount() > 0) {
            int i = 0;
            int column = cursor.getColumnIndexOrThrow(columnName);
            for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext(), i++) {
                SortEntry sortKey = new SortEntry();
                sortKey.key = cursor.getString(column);
                sortKey.order = i;
                sortList.add(sortKey);
            }
        }
        Collections.sort(sortList, this);
    }
    public boolean moveToPosition(int position) {
        if (position >= 0 && position < sortList.size()) {
            mPos = position;
            int order = sortList.get(position).order;
            return mCursor.moveToPosition(order);
        }
        if (position < 0) {
            mPos = -1;
        }
        if (position >= sortList.size()) {
            mPos = sortList.size();
        }
        return mCursor.moveToPosition(position);
    }
    public boolean moveToFirst() {
        return moveToPosition(0);
    }
    public boolean moveToLast() {
        return moveToPosition(getCount() - 1);
    }
    public boolean moveToNext() {
        return moveToPosition(mPos + 1);
    }
    public boolean moveToPrevious() {
        return moveToPosition(mPos - 1);
    }
    public boolean move(int offset) {
        return moveToPosition(mPos + offset);
    }
    public int getPosition() {
        return mPos;
    }
}