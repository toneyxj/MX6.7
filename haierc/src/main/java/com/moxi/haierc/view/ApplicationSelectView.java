package com.moxi.haierc.view;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.moxi.haierc.R;
import com.moxi.haierc.adapter.AppListAdapter;
import com.moxi.haierc.model.AppModel;
import com.moxi.haierc.ports.OnClickCallBack;
import com.moxi.haierc.util.IndexApplicationUtils;
import com.mx.mxbase.utils.SharePreferceUtil;
import com.mx.mxbase.utils.Toastor;

import java.util.List;

/**
 * Created by King on 2017/10/11.
 */

public class ApplicationSelectView extends PopupWindow implements View.OnClickListener {
    private LayoutInflater mInflater;
    private Context context;
    private GridView gridviewApp;
    private AppListAdapter adapter;
    private int page = 0, current = -1;
    private int totalPage = 0;
    private int pageSize = 20;
    private TextView tvIndex;
    private OnClickCallBack onClickCallBack;
    private List<AppModel> listTemp;
    private String stuFlag = "";
    public SharePreferceUtil share;

    public ApplicationSelectView(Context context, OnClickCallBack onClickCallBack) {
        super(context);
        this.context = context;
        this.onClickCallBack = onClickCallBack;
        share = SharePreferceUtil.getInstance(context);
        stuFlag = share.getString("flag_version_stu");
        if (stuFlag.equals("")) {
            stuFlag = "标准版";
        }
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mContentView = mInflater.inflate(R.layout.popwin_application_select, null);
        setContentView(mContentView);
        setWidth(wm.getDefaultDisplay().getWidth() * 8 / 10);
        setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        setBackgroundDrawable(new ColorDrawable());
        initView(mContentView);
    }

    private void initView(View view) {
        ImageView tvLastPage = (ImageView) view.findViewById(R.id.img_app_pre);
        ImageView tvNextPage = (ImageView) view.findViewById(R.id.img_app_next);
        tvIndex = (TextView) view.findViewById(R.id.show_index);

        tvNextPage.setOnClickListener(this);
        tvLastPage.setOnClickListener(this);
        view.findViewById(R.id.tv_cancel).setOnClickListener(this);
        view.findViewById(R.id.tv_sure).setOnClickListener(this);

        gridviewApp = (GridView) view.findViewById(R.id.grid_view_select_apps);
        listTemp = IndexApplicationUtils.getInstance(context).getCurrentHideApps(stuFlag);
        adapter = new AppListAdapter(context, page, -1, listTemp);
        totalPage = listTemp.size() / pageSize + 1;
        tvIndex.setText(page + 1 + "/" + totalPage);
        gridviewApp.setAdapter(adapter);
        gridviewApp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                current = page * pageSize + position;
                adapter.updateAdapter(page, current);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_app_pre:
                if (page > 0) {
                    page--;
                    adapter.updateAdapter(page, current);
                }
                tvIndex.setText(page + 1 + "/" + totalPage);
                break;
            case R.id.img_app_next:
                if (page < totalPage - 1) {
                    page++;
                    adapter.updateAdapter(page, current);
                }
                tvIndex.setText(page + 1 + "/" + totalPage);
                break;
            case R.id.tv_cancel:
                this.dismiss();
                break;
            case R.id.tv_sure:
                if (current != -1) {
                    onClickCallBack.onClickCallBack(listTemp.get(current));
                    this.dismiss();
                } else {
                    Toastor.showToast(context, "未选中要替换的应用！");
                }
                break;
            default:
                break;
        }
    }
}
