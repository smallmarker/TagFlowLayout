package com.smallmarker.tagflowlayout;

import android.widget.Checkable;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;

/**
 * @author zl
 * @Date 2022/7/27
 **/
public interface MaterialCheckable<T extends MaterialCheckable<T>> extends Checkable {

    @IdRes
    int getId();

    void setInternalOnCheckedChangeListener(@Nullable MaterialCheckable.OnCheckedChangeListener<T> listener);

    /**
     * Interface definition for a callback to be invoked when a {@link MaterialCheckable} is checked
     * or unchecked.
     */
    interface OnCheckedChangeListener<C> {
        /**
         * Called when the checked state of a {@link MaterialCheckable} has changed.
         *
         * @param checkable The compound button view whose state has changed.
         * @param isChecked The new checked state of buttonView.
         */
        void onCheckedChanged(C checkable, boolean isChecked);
    }
}
