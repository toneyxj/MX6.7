package com.mx.main.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mx.main.R;
import com.mx.main.model.MXAppDetail;
import com.mx.mxbase.constant.Constant;
import com.mx.mxbase.interfaces.OnItemClickListener;
import com.mx.mxbase.utils.AppUtil;
import com.mx.mxbase.utils.Log;
import com.mx.mxbase.view.AlertDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Archer on 16/7/29.
 */
public class MXApplicationActivity extends Activity implements View.OnClickListener {

    private ArrayList<MXAppDetail> appList;
    private List<String> appFilters;
    private RecyclerView recyclerView;
    private AppRecyclerAdapter appAdapter;
    private PackageInfo packageInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mx_activity_application);

        init();
    }

    /**
     * 初始化视图
     */
    private void init() {
        //绑定view
        recyclerView = (RecyclerView) findViewById(R.id.recycler_app);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));

        //获取手机安装程序
        getALlInstallApp();
        if (appList != null && appList.size() > 0) {
            appAdapter = new AppRecyclerAdapter(this);
            recyclerView.setAdapter(appAdapter);
        }

        //设置点击事件
        findViewById(R.id.rl_app_back_finish).setOnClickListener(this);
        appAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                try {
                    Intent intent = MXApplicationActivity.this.getPackageManager().getLaunchIntentForPackage(appList.get(position).getPackageName());
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onItemLongClick(View view, final int position) {
                new AlertDialog(MXApplicationActivity.this).builder().setTitle("提示").setMsg("是否确认删除此应用?").setCancelable(false).setNegativeButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AppUtil.uninstallApk(MXApplicationActivity.this, appList.get(position).getPackageName());
                        MXApplicationActivity.this.finish();
                    }
                }).setPositiveButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                }).show();
            }
        });
    }

    /**
     * 获取手机安装的应用程序
     */
    private void getALlInstallApp() {
        appList = new ArrayList<MXAppDetail>(); //用来存储获取的应用信息数据
        appFilters = new ArrayList<>();
        Collections.addAll(appFilters, Constant.appName);
        List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            MXAppDetail tmpInfo = new MXAppDetail();
            tmpInfo.setName(packageInfo.applicationInfo.loadLabel(getPackageManager()).toString());
            Log.e("名称：－－－－－", packageInfo.applicationInfo.loadLabel(getPackageManager()).toString());
            tmpInfo.setPackageName(packageInfo.packageName);
            Log.e("报名：－－－－－", packageInfo.packageName);
            tmpInfo.setVersionName(packageInfo.versionName);
            tmpInfo.setVersionCode(packageInfo.versionCode + "");
            tmpInfo.setIcon(packageInfo.applicationInfo.loadIcon(getPackageManager()));
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                //如果非系统应用全部显示
                appList.add(tmpInfo);
            } else {
                //过滤需要显示的应用
                if (appFilters.contains(tmpInfo.getName())) {
                    appList.add(tmpInfo);
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_app_back_finish:
                this.finish();
                break;
            default:
                break;
        }
    }

    class AppRecyclerAdapter extends RecyclerView.Adapter {
        private Context context;
        private OnItemClickListener onItemClickListener;

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }

        public AppRecyclerAdapter(Context context) {
            this.context = context;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHoleder(LayoutInflater.from(
                    context).inflate(R.layout.mx_recycler_app_item, parent,
                    false));
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            ((MyViewHoleder) holder).imgAppIcon.setImageDrawable(appList.get(position).getIcon());
            ((MyViewHoleder) holder).tvAppName.setText(appList.get(position).getName());
            if (onItemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onItemClickListener.onItemClick(holder.itemView, position);
                    }
                });
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        onItemClickListener.onItemLongClick(holder.itemView, position);
                        return false;
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return appList != null ? appList.size() : 0;
        }

        class MyViewHoleder extends RecyclerView.ViewHolder {

            ImageView imgAppIcon;
            TextView tvAppName;

            public MyViewHoleder(View itemView) {
                super(itemView);
                imgAppIcon = (ImageView) itemView.findViewById(R.id.img_recycler_item_app);
                tvAppName = (TextView) itemView.findViewById(R.id.tv_recycler_item_app);
            }
        }
    }
}
