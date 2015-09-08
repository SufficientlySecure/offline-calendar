/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sufficientlysecure.localcalendar.util;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

import org.sufficientlysecure.localcalendar.R;

/**
 * Methods modified from https://android.googlesource.com/platform/developers/samples/android/+/master/ui/actionbar/DoneBar/
 *
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ActionBarHelper {

    private static View initView(ActionBar actionBar,
                                 int viewId,
                                 OnClickListener onDone) {

        // Inflate a custom view and set a Done handler
        actionBar.setCustomView(viewId);
        View view = actionBar.getCustomView();
        view.findViewById(R.id.actionbar_done).setOnClickListener(onDone);

        // Show the custom action bar view and hide the normal Home icon and title.
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        return view;
    }

    /**
     * Sets custom view on ActionBar for Done/Cancel activities
     *
     * @param actionBar
     * @param onDone
     * @param onCancel
     */
    public static void setDoneCancelView(ActionBar actionBar,
                                         OnClickListener onDone,
                                         OnClickListener onCancel) {

        View view = initView(actionBar, R.layout.actionbar_custom_view_done_cancel, onDone);

        // Set a Cancel handler
        view.findViewById(R.id.actionbar_cancel).setOnClickListener(onCancel);

        // Set the view again, this time with custom layout parameters
        actionBar.setCustomView(view, new ActionBar.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    /**
     * Sets custom view on ActionBar for Done activities
     *
     * @param actionBar
     * @param onDone
     */
    public static void setDoneView(ActionBar actionBar,
                                   OnClickListener onDone) {

        initView(actionBar, R.layout.actionbar_custom_view_done, onDone);
    }

}
