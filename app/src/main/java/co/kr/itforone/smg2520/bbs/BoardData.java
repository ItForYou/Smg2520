package co.kr.itforone.smg2520.bbs;

import java.util.ArrayList;



public class BoardData {
    public String getSuccess() {
        return success;
    }

    public String getBo_table() {
        return bo_table;
    }

    public ArrayList<BoardListData> getData() {
        return data;
    }

    String success;
    String bo_table;
    ArrayList<BoardListData> data;

}
