package com.example.ahmad2.shopproject;

import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class FragmentMyShop extends Fragment {

    private ViewPager viewPager;
    private FragmentAdapter adapter;
    private List<Fragment> fragmentList;
    private TabLayout tabLayout;
    private List<String> titleList;
    private Toolbar toolbar;
    private String user="", userName="";
    private AppCompatTextView txtNameHeader, txtEmailHeader;
    private ImageView imgHeader;
//    private SharedPreferences preferences;
    private FragmentObjectListForAdmin fragmentObjectListForAdmin;
    private FragmentUserListForAdmin fragmentUserListForAdmin;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private String mobile="";
    private Bitmap bitmap;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(App.TAG_FRAGMENT,"CFMyShop");

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(App.TAG_FRAGMENT,"CVFMyShop");
        getPreference();
        View view=inflater.inflate(R.layout.layout_fragment_myshop,container,false);


        //        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        tabLayout = view.findViewById(R.id.tab_fragment_myshop);
        fragmentList = new ArrayList<>();
        titleList = new ArrayList<>();
        viewPager = view.findViewById(R.id.pager_fragment_myshop);

        final FragmentStore fragmentStore=new FragmentStore();
        final FragmentOrderForAdmin fragmentOrderForAdmin = new FragmentOrderForAdmin();
        App.fragmentOrderForAdmin = fragmentOrderForAdmin;
        fragmentList.add(fragmentOrderForAdmin);
        titleList.add(getString(R.string.title_tab_order_admin));

        fragmentObjectListForAdmin = new FragmentObjectListForAdmin();
        fragmentList.add(FragmentInsertObject.newInstance(fragmentObjectListForAdmin));
        titleList.add(getString(R.string.title_tab_insert_object));


        App.fragmentStore=fragmentStore;
        fragmentList.add(fragmentStore);
        titleList.add(getString(R.string.title_tab_store));


        App.fragmentObjectListForAdmin = fragmentObjectListForAdmin;
        fragmentList.add(fragmentObjectListForAdmin);
        titleList.add(getString(R.string.title_tab_my_object));

        final FragmentInsertSellSpecial fragmentInsertSellSpecial=new FragmentInsertSellSpecial();
        fragmentList.add(fragmentInsertSellSpecial);
        titleList.add(getString(R.string.title_tab_sell_special_admin));

        fragmentUserListForAdmin = new FragmentUserListForAdmin();
        App.fragmentUserListForAdmin = fragmentUserListForAdmin;
        fragmentList.add(fragmentUserListForAdmin);
        titleList.add(getString(R.string.title_tab_user_list));

        adapter = new FragmentAdapter(getFragmentManager(), fragmentList, titleList);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
//        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                CharSequence text = tab.getText();
//                if (text.equals(getString(R.string.title_tab_sell))){
//                    fragmentObjectListForAdmin.getObjectList();
//                }else if (text.equals(getString(R.string.title_tab_order_admin))){
//                    fragmentOrderForAdmin.getOrderList();
//                }else if (text.equals(getString(R.string.title_tab_sell_special_admin))){
//                    fragmentInsertSellSpecial.getSellSpecial();
//                }
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });

//        int count = viewPager.getAdapter().getCount();
//        Log.i("tagcount",String.valueOf(count));
//        viewPager.setCurrentItem(3,false);

//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int i, float v, int i1) {
//
//            }
//
//            @Override
//            public void onPageSelected(int i) {
//                Log.i("tagPage",String.valueOf(i));
//                Log.i("tagPageitem",String.valueOf(viewPager.getCurrentItem()));
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int i) {
//
//            }
//        });
////        viewPager.getAdapter().setPrimaryItem(viewPager,2,fragmentStore);
//
////        fragmentList.add(FragmentInsertObject.newInstance(fragmentObjectListForAdmin));
////        titleList.add(getString(R.string.title_tab_archive));


        tabLayout.getTabAt(0).setIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_order_green));
        tabLayout.getTabAt(1).setIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_add_green));
        tabLayout.getTabAt(2).setIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_store_green));
        tabLayout.getTabAt(3).setIcon(ContextCompat.getDrawable(getContext(),R.drawable.ic_shop_green));
        tabLayout.getTabAt(4).setIcon(ContextCompat.getDrawable(getContext(),R.drawable.sell_special));
        tabLayout.getTabAt(5).setIcon(ContextCompat.getDrawable(getContext(),R.drawable.ic_userlist));



//        tabLayout.getTabAt(3).setIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_order_green));
//        tabLayout.getTabAt(4).setIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_order_green));
//        tabLayout.getTabAt(3).setIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_order));
//        Log.i("tagposition",String.valueOf(viewPager.getAdapter().getItemPosition(FragmentObjectListForAdmin.class)));
//                    case "اضافه کردن کالا": {
//                        FragmentInsertObject fragmentInsertObject = (FragmentInsertObject) viewPager.getAdapter().instantiateItem(viewPager, i);
//                        fragmentInsertObject.getSpinner();
//                    }


        return view;
    }

    private void getPreference() {
//        preferences = getContext().getSharedPreferences("userInfoPre", Context.MODE_PRIVATE);
        if (App.prefUserInfo!=null){
            userName=App.prefUserInfo.getString("name","");
            user=App.prefUserInfo.getString("user","");
            mobile=App.prefUserInfo.getString("mobile","");
        }
    }
}
