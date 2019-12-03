package co.kr.itforone.smg2520.bbs;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.kr.itforone.smg2520.R;
import co.kr.itforone.smg2520.main.BoardData;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import util.RetrofitService;

public class CompanySearchFragment extends Fragment {
    BoardAdapter adapter;
    @BindView(R.id.bbsListView)
    ListView bbsListView;
    String search;
    public CompanySearchFragment() {
        // Required empty public constructor
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_company_search, container, false);

        ButterKnife.bind(this,view);
        if(getArguments()!=null){
            search=getArguments().getString("search");
        }
        //어댑터 선언
        adapter=new BoardAdapter(inflater.getContext());
        //listview에 담기
        bbsListView.setAdapter(adapter);
        setBoardList();
        // Inflate the layout for this fragment
        return view;
    }
    public void setBoardList(){

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
        map.put("division","bbs_company_search");
        map.put("search",search);
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

                        //어댑터에 담기notifyDataSetChanged
                        adapter.addAll(repo.getData());

                        //리스트 뷰 클릭 이벤트
                        bbsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                                Intent intent = new Intent(getContext(),BoardWebActivity.class);
                                String bo_table=repo.getData().get(position).getBo_table();
                                String wr_id=repo.getData().get(position).getWr_id();
                                intent.putExtra("goUrl",getString(R.string.url)+"/bbs/board.php?bo_table="+bo_table+"&wr_id="+wr_id);
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
    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}
