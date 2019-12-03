package co.kr.itforone.smg2520.bbs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.kr.itforone.smg2520.R;
import co.kr.itforone.smg2520.bbs.BoardData;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import util.RetrofitService;

public class BoardListFragment extends Fragment {
    BoardAdapter adapter;
    @BindView(R.id.bbsListView)
    ListView bbsListView;
    @BindView(R.id.bbsTabMenuLayout)
    TabLayout bbsTabMenuLayout;
    int mPosition;
    public BoardListFragment(){
    }
    @SuppressLint("ValidFragment")
    public BoardListFragment(int position){
        this.mPosition=position;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_board_list, container, false);

        ButterKnife.bind(this,view);
        //어댑터 선언
        adapter=new BoardAdapter(inflater.getContext());
        //listview에 담기
        bbsListView.setAdapter(adapter);

        Log.d("number",mPosition+"111");

        String bo_table=getString(getResources().getIdentifier("store"+mPosition,"string","co.kr.itforone.smg2520"));
        setCategoryList(bo_table);
        bbsTabMenuLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View view = tab.getCustomView();
                TextView tabTxtView=view.findViewById(R.id.cat_name);
                tabTxtView.setTextColor(Color.parseColor("#482d3c"));
                tabTxtView.setTypeface(null, Typeface.BOLD);
                if(tabTxtView.getText().toString().equals("전체")){
                    setBoardList(bo_table,"");
                }else {
                    setBoardList(bo_table, tabTxtView.getText().toString());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View view = tab.getCustomView();
                TextView tabTxtView=view.findViewById(R.id.cat_name);
                tabTxtView.setTextColor(Color.parseColor("#a79da1"));
                tabTxtView.setTypeface(null, Typeface.NORMAL);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                TextView tabTxtView=view.findViewById(R.id.cat_name);
                tabTxtView.setTextColor(Color.parseColor("#482d3c"));
                tabTxtView.setTypeface(null, Typeface.BOLD);
            }
        });
        return view;
    }
    public void setBoardList(String bo_table,String ca_name){

            adapter.clear();

        //httpok 로그 보기
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        //클라이언트 설정
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .build();
        //레트로핏 설정
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(getString(R.string.domain))
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        //파라미터 넘길 값 설정
        Map map=new HashMap();
        map.put("division","bbs_board_list");
        map.put("bo_table",bo_table);
        map.put("ca_name",ca_name);
        map.put("page","1");

        //레트로핏 서비스 실행하기
        RetrofitService retrofitService=retrofit.create(RetrofitService.class);
        //데이터 불러오기
        Call<co.kr.itforone.smg2520.bbs.BoardData> call=retrofitService.getBbsBoardList(map);
        call.enqueue(new Callback<co.kr.itforone.smg2520.bbs.BoardData>() {

            @Override
            public void onResponse(Call<co.kr.itforone.smg2520.bbs.BoardData> call, Response<co.kr.itforone.smg2520.bbs.BoardData> response) {
                //서버에 데이터 받기가 성공할시
                if(response.isSuccessful()){
                    co.kr.itforone.smg2520.bbs.BoardData repo=response.body();
                    Log.d("response",response+"");
                    if(Boolean.parseBoolean(repo.getSuccess())==false){
                    }else{

                        //어댑터에 담기
                        adapter.addAll(repo.getData());
                        if(!ca_name.equals("")) {
                            adapter.notifyDataSetChanged();
                        }




                        //리스트 뷰 클릭 이벤트
                        bbsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                                Intent intent = new Intent(getContext(),BoardWebActivity.class);
                                Toast.makeText(getContext(), ""+position, Toast.LENGTH_SHORT).show();
                                String bo_table=repo.getBo_table();
                                String wr_id=repo.getData().get(position).getWr_id();
                                String tel=repo.getData().get(position).getWr_2();
                                intent.putExtra("goUrl",getString(R.string.url)+"/bbs/board.php?bo_table="+bo_table+"&wr_id="+wr_id);
                                intent.putExtra("bo_table",bo_table);
                                intent.putExtra("wr_id",wr_id);
                                intent.putExtra("tel",tel);
                                startActivity(intent);
                                getActivity().overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);



                            }
                        });
                    }
                }else{

                }
            }

            @Override
            public void onFailure(Call<co.kr.itforone.smg2520.bbs.BoardData> call, Throwable t) {

            }
        });
    }

    public void setCategoryList(String bo_table){

        //httpok 로그 보기
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        //클라이언트 설정
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .build();
        //레트로핏 설정
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(getString(R.string.domain))
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        //파라미터 넘길 값 설정
        Map map=new HashMap();
        map.put("division","bbs_category_list");
        map.put("bo_table",bo_table);

        //레트로핏 서비스 실행하기
        RetrofitService retrofitService=retrofit.create(RetrofitService.class);
        //데이터 불러오기
        Call<co.kr.itforone.smg2520.bbs.BoardData> call=retrofitService.getBbsBoardList(map);
        call.enqueue(new Callback<co.kr.itforone.smg2520.bbs.BoardData>() {

            @Override
            public void onResponse(Call<co.kr.itforone.smg2520.bbs.BoardData> call, Response<co.kr.itforone.smg2520.bbs.BoardData> response) {
                //서버에 데이터 받기가 성공할시
                if(response.isSuccessful()){
                    co.kr.itforone.smg2520.bbs.BoardData repo=response.body();
                    Log.d("response1",response+"");
                    if(Boolean.parseBoolean(repo.getSuccess())==false){
                    }else{
                        ArrayList<BoardListData> boardListData=repo.getData();
                        Log.d("category",boardListData.size()+"");
                        for(int i=0;i<boardListData.size();i++){
                            try {
                                bbsTabMenuLayout.addTab(bbsTabMenuLayout.newTab().setCustomView(createTabView(boardListData.get(i).getBo_category_list())));

                            }catch (Exception e){

                            }
                        }
                        if(boardListData.size()>0) {
                            bbsTabMenuLayout.setVisibility(View.VISIBLE);
                        }
                        //어댑터에 담기
                    }
                }else{

                }
            }

            @Override
            public void onFailure(Call<co.kr.itforone.smg2520.bbs.BoardData> call, Throwable t) {

            }
        });
    }
    private View createTabView(String tabName) {
        View tabView = LayoutInflater.from(getContext()).inflate(R.layout.category_tab, null);
        TextView txt_name = (TextView) tabView.findViewById(R.id.cat_name);
        txt_name.setTextSize(12f);
        txt_name.setText(tabName);
        return tabView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}
