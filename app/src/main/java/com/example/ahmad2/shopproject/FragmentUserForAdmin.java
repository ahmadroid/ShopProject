package com.example.ahmad2.shopproject;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class FragmentUserForAdmin extends Fragment {

    private String user;
//    private String userName;
    private ViewPager viewPager;
    private List<Fragment> fragmentList;
    private List<String> titleList;
    private FragmentAdapter adapter;
    private TabLayout tabLayout;
//    private SharedPreferences preference;
//    private String mobile;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(App.TAG_FRAGMENT,"CFUserForAdmin");

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(App.TAG_FRAGMENT,"CVFUserForAdmin");
        getPreference();
        final View view=inflater.inflate(R.layout.layout_fragment_user_for_admin,container,false);
        viewPager = view.findViewById(R.id.pager_fragment_user_for_admin);
        tabLayout = view.findViewById(R.id.tab_fragment_user_for_admin);
        fragmentList = new ArrayList<>();
        titleList = new ArrayList<>();

        final FragmentOrderForUser fragmentOrderForUser = FragmentOrderForUser.newInstance(user);
        App.fragmentOrderForUser = fragmentOrderForUser;
        final FragmentObjectListForUser fragmentObjectListForUser = FragmentObjectListForUser.newInstance(user, fragmentOrderForUser);
        App.fragmentObjectListForUser=fragmentObjectListForUser;
        fragmentList.add(fragmentObjectListForUser);
        titleList.add(getString(R.string.title_tab_object_list));
        fragmentList.add(fragmentOrderForUser);
        titleList.add(getString(R.string.title_tab_order_user));
        final FragmentShopList fragmentShopList=new FragmentShopList();
        App.fragmentShopList=fragmentShopList;
        fragmentList.add(fragmentShopList);
        titleList.add(getString(R.string.title_tab_shop_list));

        final FragmentSellSpecialForUser fragmentSellSpecialForUser =new FragmentSellSpecialForUser();
        fragmentList.add(fragmentSellSpecialForUser);
        titleList.add(getString(R.string.title_tab_sell_special));

        FragmentObjectListForUserAgree fragmentObjectListForUserAgree=new FragmentObjectListForUserAgree();
        fragmentList.add(fragmentObjectListForUserAgree);
        titleList.add(getString(R.string.title_tab_objectList_agree));

        FragmentShopListAgree fragmentShopListAgree=new FragmentShopListAgree();
        fragmentList.add(fragmentShopListAgree);
        titleList.add(getString(R.string.title_tab_shop_agree));

        FragmentSellSpecialForUserAgree fragmentSellSpecialForUserAgree=new FragmentSellSpecialForUserAgree();
        fragmentList.add(fragmentSellSpecialForUserAgree);
        titleList.add(getString(R.string.title_tab_sell_special_agree));

        FragmentShowComment fragmentShowComment=new FragmentShowComment();
        fragmentList.add(fragmentShowComment);
        titleList.add(getString(R.string.title_tab_commen));

        adapter = new FragmentAdapter(getFragmentManager(), fragmentList, titleList);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(ContextCompat.getDrawable(getContext(),R.drawable.ic_shop_green));
        tabLayout.getTabAt(1).setIcon(ContextCompat.getDrawable(getContext(),R.drawable.ic_order_green));
        tabLayout.getTabAt(2).setIcon(ContextCompat.getDrawable(getContext(),R.drawable.ic_pasazh_green));
        tabLayout.getTabAt(3).setIcon(ContextCompat.getDrawable(getContext(),R.drawable.sell_special_green1));

        tabLayout.getTabAt(4).setIcon(ContextCompat.getDrawable(getContext(),R.drawable.ic_shop_green));
        tabLayout.getTabAt(5).setIcon(ContextCompat.getDrawable(getContext(),R.drawable.ic_pasazh_green));
        tabLayout.getTabAt(6).setIcon(ContextCompat.getDrawable(getContext(),R.drawable.sell_special_green1));
        tabLayout.getTabAt(7).setIcon(ContextCompat.getDrawable(getContext(),R.drawable.ic_order_green));

        //        tabLayout.getTabAt(3).setIcon(getResources().getDrawable(R.drawable.sell_special_green1));
//        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                String text = (String) tab.getText();
//                if (text.equals(getString(R.string.title_tab_object_list))){
//                    fragmentObjectListForUser.getObjectList();
//                }else if (text.equals(getString(R.string.title_tab_order_user))){
//                    fragmentOrderForUser.getOrderList();
//                }else if (text.equals(getString(R.string.title_tab_shop_list))){
//                    fragmentShopList.getShopList();
//                }else if (text.equals(getString(R.string.title_tab_sell_special))){
//                    fragmentSellSpecialForUser.getObjectList();
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
        return view;
    }

    private void getPreference() {
//        preference = getContext().getSharedPreferences("userInfoPre", Context.MODE_PRIVATE);
        if (App.prefUserInfo != null) {
            user = App.prefUserInfo.getString("user", "");
//            userName = preference.getString("name", "");
//            mobile = preference.getString("mobile", "");
        }
    }
}
