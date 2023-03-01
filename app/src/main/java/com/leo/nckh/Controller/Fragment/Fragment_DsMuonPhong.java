package com.leo.nckh.Controller.Fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.leo.nckh.Controller.Activity.Activity_Home;
import com.leo.nckh.Controller.Adapter.Adapter_MyFragment;
import com.leo.nckh.R;

public class Fragment_DsMuonPhong extends Fragment {
    TabLayout tabLayout;
    ViewPager2 viewPager;
    View viewlayout;
    Toolbar toolbar;
    SearchView searchView;
    TextView logo;
    DataViewModel dataViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewlayout = inflater.inflate(R.layout.fragment_dsmuonphong, container, false);
        tabLayout = viewlayout.findViewById(R.id.tabDs_muonPhong);
        viewPager = viewlayout.findViewById(R.id.pagerDs_MuonPhong);
        toolbar = viewlayout.findViewById(R.id.toolbar);
        logo = viewlayout.findViewById(R.id.logo);

        dataViewModel = new ViewModelProvider(requireActivity()).get(DataViewModel.class);


        //toolbar
        toolbar.setTitle("");
        ((Activity_Home) requireActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);

        //tab

        tabLayout.addTab(tabLayout.newTab().setText("Danh sách mượn phòng"));
        tabLayout.addTab(tabLayout.newTab().setText("Lịch sử mượn phòng"));

        Adapter_MyFragment adapter = new Adapter_MyFragment(requireActivity());
        viewPager.setAdapter(adapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });
        return viewlayout;
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        requireActivity().getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.btnSearch);
        searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("nhập vào đây");
        EditText editText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        editText.setTextColor(requireActivity().getColor(R.color.background));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                dataViewModel.setQuery(newText);
                return false;
            }
        });

    }


    public static class DataViewModel extends ViewModel {
        public MutableLiveData<String> query = new MutableLiveData<>();

        public void setQuery(String queryData) {
            query.setValue(queryData);
        }

        public LiveData<String> getQuery() {
            return query;
        }

    }
}