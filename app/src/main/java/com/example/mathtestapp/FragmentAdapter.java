package com.example.mathtestapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class FragmentAdapter extends FragmentStateAdapter {

    private String lessonName;

    public FragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, String lessonName) {
        super(fragmentManager, lifecycle);
        this.lessonName = lessonName;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 1:
                return TestFragment.newInstance(this.lessonName);
        }
        return RuleFragment.newInstance(this.lessonName);
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
