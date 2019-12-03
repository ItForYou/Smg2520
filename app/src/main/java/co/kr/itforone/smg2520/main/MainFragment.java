package co.kr.itforone.smg2520.main;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.kr.itforone.smg2520.MainActivity;
import co.kr.itforone.smg2520.R;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import util.RetrofitService;

public class MainFragment extends Fragment {



    @BindView(R.id.noticeTxt)
    TextView noticeTxt;
    @BindView(R.id.eventTxt)
    TextView eventTxt;
    @BindView(R.id.fragScrollView)
    ScrollView fragScrollView;
    View rootView;



    int aniNo = 1;
    Context mContext;
    public MainFragment(){

    }




    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this,rootView);
        setBoard("notice",R.id.noticeTxt);
        setBoard("event",R.id.eventTxt);
        //스크롤
        fragScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int y=fragScrollView.getScrollY();

                if(y==0){
                    ((MainActivity)getActivity()).hiddenTopLine();
                }else{
                    ((MainActivity)getActivity()).showTopLine();
                }
            }
        });


        for(int i=1;i<9;i++){
            int resourceId=getResources().getIdentifier("menuLayout"+i,"id","co.kr.itforone.smg2520");
            Animation anim = AnimationUtils.loadAnimation(getContext(),R.anim.alpha0);
            rootView.findViewById(resourceId).setAlpha(0f);
            rootView.findViewById(resourceId).startAnimation(anim);
        }
        mHandler.sendEmptyMessageDelayed(aniNo,1300);
        // Inflate the layout for this fragment
        return rootView;
    }

    public void setBoard(String bo_table,int viewId){
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
        map.put("division","board_list");
        map.put("bo_table",bo_table);
        map.put("limit","1");

        //레트로핏 서비스 실행하기
        RetrofitService retrofitService=retrofit.create(RetrofitService.class);
        //데이터 불러오기
        Call<BoardData> call=retrofitService.getBoardList(map);
        call.enqueue(new Callback<BoardData>() {

            @Override
            public void onResponse(Call<BoardData> call, Response<BoardData> response) {
                //서버에 데이터 받기가 성공할시
                if(response.isSuccessful()){
                    BoardData repo=response.body();
                    Log.d("response",response+"");
                    if(Boolean.parseBoolean(repo.getSuccess())==false){
                    }else{
                        //어댑터에 담기
                        String wr_id=repo.getData().get(0).getWr_id();
                        String wr_subject=repo.getData().get(0).getWr_subject();
                        if(viewId==R.id.noticeTxt){
                            Toast.makeText(getContext(), "111", Toast.LENGTH_SHORT).show();
                            noticeTxt.setText(wr_subject);
                        }else{
                            eventTxt.setText(wr_subject);
                        }
                    }
                }else{

                }
            }

            @Override
            public void onFailure(Call<BoardData> call, Throwable t) {

            }
        });
    }

    @OnClick({R.id.menuBtn1,R.id.menuBtn2,R.id.menuBtn3,R.id.menuBtn4,R.id.menuBtn5,R.id.menuBtn6,R.id.menuBtn7,R.id.menuBtn8})
    public void goMenu(View view){
        for(int i=1;i<=8;i++) {
            int resourceId=getResources().getIdentifier("menuBtn"+i,"id","co.kr.itforone.smg2520");
            int j=i-1;
            if(resourceId==view.getId()){
                Log.d("check","1"+j);
                ((MainActivity)getActivity()).getChangeFragment(j);
                break;
            }
        }
    }
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int resourceId=getResources().getIdentifier("menuLayout"+aniNo,"id","co.kr.itforone.smg2520");
            Animation anim = AnimationUtils.loadAnimation(getContext(),R.anim.alpha500);
            rootView.findViewById(resourceId).setAlpha(1f);
            rootView.findViewById(resourceId).startAnimation(anim);

            aniNo++;
            if(aniNo<9){
                mHandler.sendEmptyMessageDelayed(aniNo,100);
            }
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
