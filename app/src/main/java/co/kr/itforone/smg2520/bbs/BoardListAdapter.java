package co.kr.itforone.smg2520.bbs;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import co.kr.itforone.smg2520.MainActivity;

public class BoardListAdapter extends FragmentStatePagerAdapter {
    private int mPageCount;
    Map hashMap = new HashMap();
    public BoardListAdapter(FragmentManager fm,int pageCount) {
        super(fm);
        this.mPageCount=pageCount;
    }

    @Override
    public Fragment getItem(int position) {
            BoardListFragment boardListFragment = new BoardListFragment(position);
            Log.d("number", position + "//");
            return boardListFragment;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        if(hashMap.put(position,position)==null) {
            hashMap.put(position, position);
            super.destroyItem(container, position, object);
        }

    }

    @Override
    public int getCount() {
        return mPageCount;
    }
}
