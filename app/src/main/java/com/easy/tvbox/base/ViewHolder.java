package com.easy.tvbox.base;

import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by clarence on 2018/1/15.
 */
public class ViewHolder {

    public static <T extends View> T getView(View view, int id) {
        SparseArray<View> sparseArray = (SparseArray) view.getTag();
        if (sparseArray == null) {
            sparseArray = new SparseArray();
            view.setTag(sparseArray);
        }

        View viewHolder = (View) sparseArray.get(id);
        if (viewHolder == null) {
            viewHolder = view.findViewById(id);
            sparseArray.put(id, viewHolder);
        }
        return (T) viewHolder;
    }

    public static TextView getTextView(View view, int id) {
        return (TextView) ViewHolder.getView(view, id);
    }

    public static ImageView getImageView(View view, int id) {
        return (ImageView) ViewHolder.getView(view, id);
    }

    public static Button getButton(View view, int id) {
        return (Button) ViewHolder.getView(view, id);
    }
    public static CheckBox getCheckBox(View view, int id) {
        return (CheckBox) ViewHolder.getView(view, id);
    }
}
