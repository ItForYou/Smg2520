package co.kr.itforone.smg2520.bbs;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.kr.itforone.smg2520.R;

public class BoardAdapter extends ArrayAdapter<BoardListData> {
    LayoutInflater inflater;
    public BoardAdapter(Context context,int resource){
        super(context,resource);
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    public BoardAdapter(Context context) {
        super(context,0);
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }



    @Override
    public View getView(int position,  View convertView, ViewGroup parent) {
        final BoardListData data=getItem(position);
        ViewHolder holder;

        //ButterKnife를 쓸 시 반드시 이걸로 써야 함
        if(convertView!=null){
            holder = (ViewHolder) convertView.getTag();
        }else{
            convertView  = inflater.inflate(R.layout.company_list_view,null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        if(data.getFile1().equals("")){
            //Glide로 이용한이미지 넣기
            Glide.with(inflater.getContext())
                    .load(R.drawable.noimage_list)
                    .override(300, 300)
                    .centerCrop()
                    .into(holder.listFile1ImgView);
        }else {
            //Glide로 이용한이미지 넣기
            Glide.with(inflater.getContext())
                    .load(data.getFile1())
                    .override(300, 300)
                    .centerCrop()
                    .into(holder.listFile1ImgView);

        }
        //각 textview에 데이터 넣기
        holder.listSubjectTxt.setText(data.getWr_subject());
        //holder.listLikeTxt.setText(data.getWr_1());
        //holder.listReviewTxt.setText(data.getWr_2());
        holder.listMenuTxt.setText(data.getWr_3());
        return convertView;
    }

    class ViewHolder{
        @BindView(R.id.listFile1ImgView)
        ImageView listFile1ImgView;
        @BindView(R.id.listLikeTxt)
        TextView listLikeTxt;
        @BindView(R.id.listSubjectTxt)
        TextView listSubjectTxt;
        @BindView(R.id.listMenuTxt)
        TextView listMenuTxt;
        @BindView(R.id.listReviewTxt)
        TextView listReviewTxt;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
