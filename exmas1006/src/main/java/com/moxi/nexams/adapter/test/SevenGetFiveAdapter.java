package com.moxi.nexams.adapter.test;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.moxi.nexams.R;
import com.moxi.nexams.cache.ACache;
import com.moxi.nexams.model.papermodel.DetailsTestModel;
import com.moxi.nexams.utils.TitleUtils;
import com.mx.mxbase.interfaces.OnItemClickListener;
import com.mx.mxbase.utils.GsonTools;

import java.util.ArrayList;
import java.util.List;

/**
 * 七选五选项适配器
 * Created by Archer on 16/8/10.
 */
public class SevenGetFiveAdapter extends RecyclerView.Adapter {

    private Context context;
    private String strOpints;
    private List<TextView> listOptions = new ArrayList<>();
    private List<ImageView> listImg = new ArrayList<>();
    private OnItemClickListener onItemClickListener;
    private int result;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public SevenGetFiveAdapter(Context context, String strOpints, int result) {
        this.context = context;
        this.strOpints = strOpints;
        this.result = result;
    }

    public void setResult(int result) {
        this.result = result;
        this.notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.mx_recycler_view_item_seven_get_five, null);
        return new AllOptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final List<DetailsTestModel> listX = GsonTools.getPersons(strOpints, DetailsTestModel.class);
        for (int i = 0; i < listX.size(); i++) {
            TitleUtils.setTestTitle(listX.get(i).getDesc(), listOptions.get(i), context);
            if (result == i) {
                listImg.get(i).setImageResource(R.mipmap.mx_img_check_box_chosed);
            } else {
                listImg.get(i).setImageResource(R.mipmap.mx_img_check_box_normal);
            }
            final int finalI = i;
            if (onItemClickListener != null) {
                listOptions.get(i).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onItemClickListener.onItemClick(listOptions.get(finalI), finalI);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    class AllOptionViewHolder extends RecyclerView.ViewHolder {
        TextView tvADesc, tvBDesc, tvCDesc, tvDDesc, tvEDesc, tvFDesc, tvGDesc;
        ImageView imgA, imgB, imgC, imgD, imgE, imgF, imgG;

        public AllOptionViewHolder(View itemView) {
            super(itemView);
            tvADesc = (TextView) itemView.findViewById(R.id.tv_index_a_desc);
            tvBDesc = (TextView) itemView.findViewById(R.id.tv_index_b_desc);
            tvCDesc = (TextView) itemView.findViewById(R.id.tv_index_c_desc);
            tvDDesc = (TextView) itemView.findViewById(R.id.tv_index_d_desc);
            tvEDesc = (TextView) itemView.findViewById(R.id.tv_index_e_desc);
            tvFDesc = (TextView) itemView.findViewById(R.id.tv_index_f_desc);
            tvGDesc = (TextView) itemView.findViewById(R.id.tv_index_g_desc);

            imgA = (ImageView) itemView.findViewById(R.id.img_index_a);
            imgB = (ImageView) itemView.findViewById(R.id.img_index_b);
            imgC = (ImageView) itemView.findViewById(R.id.img_index_c);
            imgD = (ImageView) itemView.findViewById(R.id.img_index_d);
            imgE = (ImageView) itemView.findViewById(R.id.img_index_e);
            imgF = (ImageView) itemView.findViewById(R.id.img_index_f);
            imgG = (ImageView) itemView.findViewById(R.id.img_index_g);

            listOptions.add(tvADesc);
            listOptions.add(tvBDesc);
            listOptions.add(tvCDesc);
            listOptions.add(tvDDesc);
            listOptions.add(tvEDesc);
            listOptions.add(tvFDesc);
            listOptions.add(tvGDesc);

            listImg.add(imgA);
            listImg.add(imgB);
            listImg.add(imgC);
            listImg.add(imgD);
            listImg.add(imgE);
            listImg.add(imgF);
            listImg.add(imgG);
        }
    }
}
