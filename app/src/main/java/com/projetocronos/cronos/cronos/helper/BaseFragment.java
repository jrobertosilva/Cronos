package com.projetocronos.cronos.cronos.helper;

import android.support.v4.app.Fragment;

/**
 * Created by jose- on 13/02/2018.
 */

public class BaseFragment extends Fragment {
    /**
     * Could handle back press.
     * @return true if back press was handled
     */
    public boolean onBackPressed() {
        return false;
    }
}