package com.example.doan_mau;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class NotificationActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_notifications);

        tabLayout = findViewById(R.id.tabLayoutAdminNoti);
        viewPager = findViewById(R.id.viewPagerAdminNoti);

        setupViewPager();
    }

    private void setupViewPager() {
        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                // Trả về các Fragment tương ứng cho mỗi tab
                switch (position) {
                    case 0: return new PendingQuestionsFragment();
                    case 1: return new ApprovedQuestionsFragment();
                    case 2: return new ReportsFragment();
                    default: return new Fragment();
                }
            }

            @Override
            public int getItemCount() {
                return 3;
            }
        });

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("Chờ duyệt"); break;
                case 1: tab.setText("Đã duyệt"); break;
                case 2: tab.setText("Báo cáo"); break;
            }
        }).attach();
    }
}