package co.kr.itforone.smg2520;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.kr.itforone.smg2520.bbs.BoardListAdapter;
import co.kr.itforone.smg2520.bbs.CompanySearchFragment;
import co.kr.itforone.smg2520.main.MainFragment;
import co.kr.itforone.smg2520.member.LoginActivity;
import util.BackPressCloseHandler;
import util.Common;

public class MainActivity extends AppCompatActivity {
    private BackPressCloseHandler backPressCloseHandler;
    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    @BindView(R.id.drawerMenuLayout)
    LinearLayout drawerMenuLayout;
    @BindView(R.id.mainFrameLayout)
    FrameLayout mainFrameLayout;
    @BindView(R.id.mainFrameLayout2)
    FrameLayout mainFrameLayout2;
    @BindView(R.id.mainRelativeLayout)
    RelativeLayout mainRelativeLayout;
    @BindView(R.id.tabMenuLayout)
    TabLayout tabMenuLayout;
    @BindView(R.id.board_list_pager)
    ViewPager board_list_pager;
    @BindView(R.id.lineView)
    LinearLayout lineView;
    @BindView(R.id.backImg)
    ImageView backImg;
    @BindView(R.id.searchRelativeLayout)
    RelativeLayout searchRelativeLayout;
    @BindView(R.id.searchEdt)
    EditText searchEdt;
    @BindView(R.id.homeBtn)
    ImageView homeBtn;
    @BindView(R.id.homeTxt)
    TextView homeTxt;
    @BindView(R.id.searchBtn)
    ImageView searchBtn;
    @BindView(R.id.searchTxt)
    TextView searchTxt;
    @BindView(R.id.loginTxt)
    TextView loginTxt;
    @BindView(R.id.joinTxt)
    TextView joinTxt;
    BoardListAdapter boardListAdapter;
    String[] menuStr={"음식점","정육점","철물점","병원","약국","전통시장","옷가게","화장품"};
    int tabNumber=0;
    final public int LOGIN_REQUEST=1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent=new Intent(this,SplashActivity.class);
        startActivity(intent);
        backPressCloseHandler = new BackPressCloseHandler(this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //프레그먼트 적용하기
        getSupportFragmentManager().beginTransaction().replace(R.id.mainFrameLayout, new MainFragment()).commit();

        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        searchEdt.setOnKeyListener(mOnKeyListener);
        if(!Common.getPref(this,"ss_mb_id","").equals("")){
            loginTxt.setText(Common.getPref(this,"ss_mb_name",""));
            joinTxt.setText("로그아웃");
        }
    }
    //드로어 메뉴 보여주기
    @OnClick({R.id.rightBtnImg,R.id.backImg,R.id.joinTxt,R.id.loginTxt})
    public void onClick(View view){
        if(view.getId()==R.id.rightBtnImg) {
            drawerLayout.openDrawer(drawerMenuLayout);

        }else if(view.getId()==R.id.joinTxt){
            if(Common.getPref(this,"ss_mb_id","").equals("")||Common.getPref(this,"ss_mb_id","").equals(null)) {
                Intent intent = new Intent(this, WebActivity.class);
                intent.putExtra("goUrl", getString(R.string.url) + "/bbs/register.php");
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
                drawerLayout.closeDrawer(drawerMenuLayout);
            }else{
                Common.savePref(this,"ss_mb_id","");
                Common.savePref(this,"ss_mb_name","");
                Common.savePref(this,"ss_mb_level","");
                loginTxt.setText("로그인을 하세요");
                joinTxt.setText("회원가입");
            }

        }else if(view.getId()==R.id.loginTxt) {

            if(Common.getPref(this,"ss_mb_id","").equals("")||Common.getPref(this,"ss_mb_id","").equals(null)) {
                Intent intent = new Intent(this, LoginActivity.class);


                startActivityForResult(intent, LOGIN_REQUEST);
            }else{
                //Logout();
            }

        }else{
            mainFrameLayout.setVisibility(View.VISIBLE);
            backImg.setVisibility(View.INVISIBLE);
            mainRelativeLayout.setVisibility(View.INVISIBLE);
            mainFrameLayout2.setVisibility(View.INVISIBLE);
        }
    }
    //메인에서 메뉴를 선택할 시 다음 프레그먼트 가게
    public void getChangeFragment(int num){
        Log.d("number",num+"~~~");
        if(0<tabMenuLayout.getTabCount()){
            tabMenuLayout.removeAllTabs();
        }
        //뒤로가기 보여주기
        backImg.setVisibility(View.VISIBLE);
        //메인 프레임레이아웃 가리기
        mainFrameLayout.setVisibility(View.GONE);
        //메인 업체 화면 뷰페이저 보여주기
        mainRelativeLayout.setVisibility(View.VISIBLE);
        for(int i=0;i<menuStr.length;i++) {
            tabMenuLayout.addTab(tabMenuLayout.newTab().setCustomView(createTabView(menuStr[i])));
        }
        //각각의 뷰를 생성
        boardListAdapter=new BoardListAdapter(getSupportFragmentManager(),tabMenuLayout.getTabCount());

        //게시판 어댑터 추가
        board_list_pager.setAdapter(boardListAdapter);
        board_list_pager.setCurrentItem(num);
        //탭 레이아웃 아이템 추가
        TabLayout.Tab tab=tabMenuLayout.getTabAt(num);
        TextView currentTabTextView=tab.getCustomView().findViewById(R.id.txt_name);
        currentTabTextView.setTextColor(Color.parseColor("#ff4f50"));
        //뷰페이저 이벤트
        board_list_pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabMenuLayout));


        //탭레이아웃 이벤트
        tabMenuLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                board_list_pager.setCurrentItem(tab.getPosition());
                View view = tab.getCustomView();
                TextView tabTxtView=view.findViewById(R.id.txt_name);
                tabTxtView.setTextColor(Color.parseColor("#ff4f50"));
                tabTxtView.setTypeface(null, Typeface.BOLD);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View view = tab.getCustomView();
                TextView tabTxtView=view.findViewById(R.id.txt_name);
                tabTxtView.setTextColor(Color.parseColor("#492f38"));
                tabTxtView.setTypeface(null, Typeface.NORMAL);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                board_list_pager.setCurrentItem(tab.getPosition());
                View view = tab.getCustomView();
                TextView tabTxtView=view.findViewById(R.id.txt_name);
                tabTxtView.setTextColor(Color.parseColor("#492f38"));
                tabTxtView.setTypeface(null, Typeface.BOLD);
            }
        });

    }
    //상단 라인 보여주기
    public void showTopLine(){
        lineView.setVisibility(View.VISIBLE);
    }
    //상단 라인 감추기
    public void hiddenTopLine(){
        lineView.setVisibility(View.INVISIBLE);
    }
    private View createTabView(String tabName) {
        View tabView = LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        TextView txt_name = (TextView) tabView.findViewById(R.id.txt_name);
        txt_name.setText(tabName);
        return tabView;
    }
    //뒤로가기를 눌렀을 때
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(drawerMenuLayout)){
            drawerLayout.closeDrawer(drawerMenuLayout);
            return;
        }
        if(mainFrameLayout.getVisibility()==View.VISIBLE){
            backPressCloseHandler.onBackPressed();
        }else{

            mainFrameLayout.setVisibility(View.VISIBLE);
            mainFrameLayout2.setVisibility(View.INVISIBLE);
            backImg.setVisibility(View.INVISIBLE);
            mainRelativeLayout.setVisibility(View.INVISIBLE);
        }
    }
    @OnClick({R.id.homeLayout,R.id.searchLayout,R.id.searchRelativeLayout,R.id.searchImg,R.id.myStoreTxt,R.id.settingTxt,R.id.noticeTxt,R.id.eventTxt})
    public void searchLayoutView(View view){
        switch (view.getId()) {
            //홈버튼
            case R.id.homeLayout:
                backImg.setVisibility(View.INVISIBLE);
                mainFrameLayout.setVisibility(View.VISIBLE);
                mainRelativeLayout.setVisibility(View.INVISIBLE);
                mainFrameLayout2.setVisibility(View.INVISIBLE);
                homeBtn.setImageResource(R.drawable.ft_icon01_on);
                homeTxt.setTextColor(Color.parseColor("#ff4f4f"));
                searchBtn.setImageResource(R.drawable.ft_icon02_off);
                searchTxt.setTextColor(Color.parseColor("#7c7c7c"));
                break;
            //검색
            case R.id.searchLayout:
                searchRelativeLayout.setVisibility(View.VISIBLE);
                homeBtn.setImageResource(R.drawable.ft_icon01_off);
                homeTxt.setTextColor(Color.parseColor("#7c7c7c"));
                searchBtn.setImageResource(R.drawable.ft_icon02_on);
                searchTxt.setTextColor(Color.parseColor("#ff4f4f"));

                break;
            //검색 외부
            case R.id.searchRelativeLayout:
                searchRelativeLayout.setVisibility(View.INVISIBLE);
                break;
            //검색 버튼
            case R.id.searchImg:
                mainFrameLayout.setVisibility(View.INVISIBLE);
                mainRelativeLayout.setVisibility(View.INVISIBLE);
                mainFrameLayout2.setVisibility(View.VISIBLE);

                backImg.setVisibility(View.VISIBLE);
                FragmentManager fragmentManager=getSupportFragmentManager();
                FragmentTransaction fragmentTransaction  = fragmentManager.beginTransaction();
                CompanySearchFragment fragment=new CompanySearchFragment();
                Bundle bundle=new Bundle();
                bundle.putString("search",searchEdt.getText().toString());
                fragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.mainFrameLayout2,fragment).commit();
                //getSupportFragmentManager().beginTransaction().replace(R.id.mainRelativeLayout, new CompanySearchFragment()).commit();

                searchRelativeLayout.setVisibility(View.INVISIBLE);
                searchEdt.setText("");
                keyBoardDown();
                break;
            case R.id.myStoreTxt:
                Toast.makeText(this, "준비중입니다.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.noticeTxt:
                Toast.makeText(this, "준비중입니다.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.settingTxt:
                Toast.makeText(this, "준비중입니다.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.eventTxt:
                Toast.makeText(this, "준비중입니다.", Toast.LENGTH_SHORT).show();
                break;

            case R.id.infoLayout:
                drawerLayout.closeDrawer(drawerMenuLayout);
                break;
        }
    }
    //키이벤트
    View.OnKeyListener mOnKeyListener=new View.OnKeyListener() {
        @Override
        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            if(view.getId()==R.id.searchEdt){
                if((keyEvent.getAction()==KeyEvent.ACTION_DOWN)&&(i==KeyEvent.KEYCODE_ENTER)){
                    mainFrameLayout.setVisibility(View.INVISIBLE);
                    mainRelativeLayout.setVisibility(View.INVISIBLE);
                    mainFrameLayout2.setVisibility(View.VISIBLE);
                    backImg.setVisibility(View.VISIBLE);
                    FragmentManager fragmentManager=getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction  = fragmentManager.beginTransaction();
                    CompanySearchFragment fragment=new CompanySearchFragment();
                    Bundle bundle=new Bundle();
                    bundle.putString("search",searchEdt.getText().toString());
                    fragment.setArguments(bundle);
                    fragmentTransaction.replace(R.id.mainFrameLayout2,fragment).commit();
                    //getSupportFragmentManager().beginTransaction().replace(R.id.mainRelativeLayout, new CompanySearchFragment()).commit();

                    searchRelativeLayout.setVisibility(View.INVISIBLE);
                    searchEdt.setText("");
                    keyBoardDown();
                    return true;
                }
            }
            return false;
        }
    };
    //키보드 내리기
    public void keyBoardDown(){
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchTxt.getWindowToken(), 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==LOGIN_REQUEST){
            if(resultCode==RESULT_OK){
                loginTxt.setText(Common.getPref(this,"ss_mb_name",""));
                joinTxt.setText("로그아웃");
            }
        }
    }
}
