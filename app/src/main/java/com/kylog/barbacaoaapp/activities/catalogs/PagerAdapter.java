package com.kylog.barbacaoaapp.activities.catalogs;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.kylog.barbacaoaapp.activities.catalogs.fragments.MesasFragment;
import com.kylog.barbacaoaapp.activities.catalogs.fragments.ProductsFragment;
import com.kylog.barbacaoaapp.activities.catalogs.fragments.UsersFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {

    private int numberTabs;

    public PagerAdapter(@NonNull FragmentManager fm, int numberTabs) {
        super(fm, numberTabs);
        this.numberTabs = numberTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:{
                return new UsersFragment();
            }
            case 1:{
                return new MesasFragment();
            }
            case 2:{
                return new ProductsFragment();
            }
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numberTabs;
    }
}
