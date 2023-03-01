package com.leo.nckh.Controller.Adapter;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.leo.nckh.Controller.Fragment.Fragment_Tab_DsMuonPhong;
import com.leo.nckh.Controller.Fragment.Fragment_Tab_LichSu;

public class Adapter_MyFragment extends FragmentStateAdapter {
    FragmentActivity context;

    public Adapter_MyFragment(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        this.context = fragmentActivity;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new Fragment_Tab_DsMuonPhong();
            case 1:
                return new Fragment_Tab_LichSu();
        }
        return new Fragment_Tab_DsMuonPhong();
    }

    @Override
    public int getItemCount() {
        return 2;
    }


}
