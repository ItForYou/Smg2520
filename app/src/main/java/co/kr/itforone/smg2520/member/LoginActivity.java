package co.kr.itforone.smg2520.member;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.kr.itforone.smg2520.R;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import util.Common;
import util.RetrofitService;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.mb_idEdit)
    EditText mb_idEdit;
    @BindView(R.id.mb_passwordEdit)
    EditText mb_passwordEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }
    @OnClick(R.id.loginBtn)
    public void submit(View view) {
        switch (view.getId()) {
            case R.id.loginBtn:
                String mb_id = mb_idEdit.getText().toString().trim();
                String mb_password = mb_passwordEdit.getText().toString().trim();
                //유효성체크
                if (mb_id.equals("") || mb_id.equals(null)) {
                    Toast.makeText(this, "아이디를 입력해 주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mb_password.equals("") || mb_password.equals(null)) {
                    Toast.makeText(this, "비밀번호를 입력해 주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                //http 로그를 보여주기(가로채기)
                HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
                httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                //서버와 클라이언트 연결
                OkHttpClient client = new OkHttpClient.Builder()
                        .addInterceptor(httpLoggingInterceptor)
                        .build();
                //레트로핏으로 json 파싱 준비
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(getString(R.string.url))
                        .client(client)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                //파라미터 넘기는 배열
                Map map = new HashMap();
                map.put("division", "login");
                map.put("mb_id", mb_id);
                map.put("mb_password", mb_password);
                //json 문서 가져오기
                RetrofitService retrofitService = retrofit.create(RetrofitService.class);
                Call<LoginData> call = retrofitService.postLogin(map);
                call.enqueue(new Callback<LoginData>() {

                    @Override
                    public void onResponse(Call<LoginData> call, Response<LoginData> response) {
                        //실제 문서 가져오기 성공했을 경우
                        if (response.isSuccessful()) {
                            LoginData repo = response.body();
                            Log.d("response", response + "");
                            //false이면 로그인 오류 메시지 띄우기
                            if (repo.getSuccess().equals("false")) {
                                Toast.makeText(LoginActivity.this, repo.getMessage(), Toast.LENGTH_SHORT).show();
                                return;
                                //true이면 로그인 성공 메세지 띄우기
                            } else {
                                Common.savePref(getApplicationContext(), "ss_mb_id", repo.getMb_id());
                                Common.savePref(getApplicationContext(), "ss_mb_level", repo.getMb_level());
                                Common.savePref(getApplicationContext(), "ss_mb_name", repo.getMb_name());
                                Toast.makeText(LoginActivity.this, repo.getMessage(), Toast.LENGTH_SHORT).show();
                                Intent resultIntent=new Intent();
                                setResult(RESULT_OK,resultIntent);
                                finish();
                            }
                            //실패했을 경우
                        } else {
                            Toast.makeText(LoginActivity.this, "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginData> call, Throwable t) {

                    }
                });

                break;
        }
    }
}
