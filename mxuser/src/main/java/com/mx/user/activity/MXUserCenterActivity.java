package com.mx.user.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.mx.mxbase.base.BaseActivity;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.constant.Constant;
import com.mx.mxbase.interfaces.OnItemClickListener;
import com.mx.mxbase.utils.ActivitysManager;
import com.mx.mxbase.utils.GsonTools;
import com.mx.mxbase.utils.MXUamManager;
import com.mx.mxbase.utils.Toastor;
import com.mx.user.R;
import com.mx.user.application.UserApplication;
import com.mx.user.model.NewLoginUserModel;
import com.mx.user.model.UserInfoModel;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.Bind;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;

import static com.mx.user.activity.MXLoginActivity.isBack;

/**
 * 用户中心界面
 * Created by Archer on 16/8/18.
 */
public class MXUserCenterActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.tv_base_mid_title)
    TextView tvMidTitle;
    @Bind(R.id.ll_base_back)
    LinearLayout llBack;
    @Bind(R.id.tv_base_back)
    TextView tvBack;
    @Bind(R.id.tv_user_center_edit)
    TextView tvEdit;
    @Bind(R.id.ll_user_center_login_out)
    LinearLayout llLoginOut;
    @Bind(R.id.tv_user_center_email)
    TextView tvEmail;
    @Bind(R.id.tv_user_center_mobile)
    TextView tvMobile;
    @Bind(R.id.tv_user_center_username)
    TextView tvName;
    @Bind(R.id.tv_user_center_school)
    TextView tvSchool;
    @Bind(R.id.tv_user_center_grade)
    TextView tvGrade;
    @Bind(R.id.civ_user_center_logo)
    CircleImageView circleImageView;
    @Bind(R.id.ll_user_center)
    LinearLayout lluserCenter;
    @Bind(R.id.ll_base_right)
    LinearLayout llRight;
    @Bind(R.id.tv_base_right)
    TextView tvRight;
    @Bind(R.id.ll_user_center_stu_info)
    LinearLayout llUserCenterStuInfo;
    @Bind(R.id.img_user_center_bottom_line)
    ImageView imgBottomLine;
    @Bind(R.id.tv_user_center_bind_dd)
    TextView tvBindDD;

    private NewLoginUserModel userModel;
    private PopupWindow mPopupWindow;
    private int[] res = new int[]{R.mipmap.mx_img_avatar_0, R.mipmap.mx_img_avatar_1, R.mipmap.mx_img_avatar_2, R.mipmap.mx_img_avatar_3};
    private BindLocalBroadcastReceiver localReceiver;

    @Override
    protected int getMainContentViewId() {
        return R.layout.mx_activity_user_center;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        init();
    }

    /**
     * 初始化视图
     */
    private void init() {

        localReceiver = new BindLocalBroadcastReceiver();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.moxi.bind.dd.user.ACTION");
        localBroadcastManager.registerReceiver(localReceiver, intentFilter);

        WindowManager windowManager = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
        //设置点击事件监听
        tvEdit.setOnClickListener(this);
        llLoginOut.setOnClickListener(this);
        llBack.setOnClickListener(this);
        circleImageView.setOnClickListener(this);
        llRight.setVisibility(View.VISIBLE);
        llRight.setOnClickListener(this);
        tvRight.setText("密码设置");
        tvBindDD.setOnClickListener(this);

        circleImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showChoseAvatar();
                return true;
            }
        });
        if (UserApplication.getInstance().getFlagStu().equals("教育版")) {
            llUserCenterStuInfo.setVisibility(View.VISIBLE);
            imgBottomLine.setVisibility(View.VISIBLE);
        } else {
            llUserCenterStuInfo.setVisibility(View.INVISIBLE);
            imgBottomLine.setVisibility(View.INVISIBLE);
        }
        llBack.setVisibility(View.VISIBLE);
        tvMidTitle.setText("个人中心");
        tvBack.setText("返回");
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        HashMap<String, String> info = new HashMap<>();
        info.put("appSession", MXUamManager.queryUser(this));
        if (Constant.CODE_CLIENT.equals("b")) {
            String temp = share.getString("user_info_json");
            userModel = GsonTools.getPerson(temp, NewLoginUserModel.class);
            if (userModel != null) {
                share.setCache(Constant.USER_INFO, GsonTools.obj2json(userModel));
                tvMobile.setText(userModel.getResult().getMobile());
                tvEmail.setText(userModel.getResult().getEmail());
                tvName.setText(userModel.getResult().getName());
                tvSchool.setText(userModel.getResult().getSchool());
                tvGrade.setText(userModel.getResult().getGrade());
                circleImageView.setImageResource(res[0]);
            }
        } else {
            String temp = share.getString("v3_user_info_json");
            UserInfoModel userInfoModel = GsonTools.getPerson(temp, UserInfoModel.class);
            if (userInfoModel != null) {
                if (userInfoModel.getResult().getDdUser() != null) {
                    tvBindDD.setText("解绑当当账号");
                    APPLog.e("isBack",MXLoginActivity.isBack);
                    if (MXLoginActivity.isBack) {
                        MXLoginActivity.isBack=false;
                        ActivitysManager.getAppManager().finishAllActivity();
                        this.finish();
                        return;
                    }
                } else {
                    tvBindDD.setText("绑定当当账号");
                }
                tvMobile.setText(userInfoModel.getResult().getMember().getMobile());
                tvEmail.setText(userInfoModel.getResult().getMember().getEmail());
                tvName.setText(userInfoModel.getResult().getMember().getName());
                tvSchool.setText(userInfoModel.getResult().getMember().getSchool());
                tvGrade.setText(userInfoModel.getResult().getMember().getGrade());
                int aaa = userInfoModel.getResult().getMember().getHeadPortrait();
                if (aaa != -99) {
                    circleImageView.setImageResource(res[aaa]);
                } else {
                    circleImageView.setImageResource(res[0]);
                }
            }
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Bundle outState, PersistableBundle
            outPersistentState) {

    }

    @Override
    public void onActivityRestoreInstanceState(Bundle savedInstanceState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(localReceiver);
    }

    @Override
    public void onBackPressed() {
        if (ddbdTipPop != null && ddbdTipPop.isShowing()) {
            ddbdTipPop.dismiss();
            ddbdTipPop = null;
        } else {
            LocalBroadcastManager.getInstance(MXUserCenterActivity.this).sendBroadcast(new Intent("com.moxi.destroy.user.ACTION"));
            MXUserCenterActivity.this.finish();
        }
    }

    public class BindLocalBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("com.moxi.bind.dd.user.ACTION")) {
                String type = intent.getStringExtra("action_type");
                showTipsDDTips(type);
            }
        }
    }

    /**
     * 显示当当账号绑定提示信息
     *
     * @param type
     */
    private PopupWindow ddbdTipPop;

    private void showTipsDDTips(String type) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.mx_dialog_bind_dd_user_success, null);
        if (ddbdTipPop == null) {
            ddbdTipPop = new PopupWindow(contentView, WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT, true);
        }
        ddbdTipPop.setContentView(contentView);
        contentView.findViewById(R.id.ll_close_tips_pop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ddbdTipPop != null) {
                    ddbdTipPop.dismiss();
                    ddbdTipPop = null;
                }
            }
        });
        ImageView imageView = (ImageView) contentView.findViewById(R.id.img_dd_user_tips);
        TextView tvStates = (TextView) contentView.findViewById(R.id.tv_dd_user_tips1);
        TextView tvDesc = (TextView) contentView.findViewById(R.id.tv_dd_user_tips2);
        if (type.equals("bind")) {
            imageView.setImageResource(R.mipmap.mx_dd_bind_success);
            tvStates.setText("恭喜您！");
            tvDesc.setText("已成功绑定当当账号！");
        } else {
            imageView.setImageResource(R.mipmap.mx_dd_unbind_success);
            tvStates.setText("已解除！");
            tvDesc.setText("不再关联当当账号！");
        }
        ddbdTipPop.setOutsideTouchable(false);
        ddbdTipPop.showAtLocation(lluserCenter, Gravity.CENTER, 0, 0);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_base_back:
                if (ddbdTipPop != null && ddbdTipPop.isShowing()) {
                    ddbdTipPop.dismiss();
                    ddbdTipPop = null;
                } else {
                    LocalBroadcastManager.getInstance(MXUserCenterActivity.this).sendBroadcast(new Intent("com.moxi.destroy.user.ACTION"));
                    MXUserCenterActivity.this.finish();
                }
                break;
            case R.id.tv_user_center_edit:
                startActivity(new Intent(this, MXEditInfomationActivity.class));
                break;
            case R.id.ll_user_center_login_out:
                //Todo 退出操作
                share.setCache(Constant.USER_INFO, "");
                MXUamManager.insertUam(this, Constant.MAIN_PACKAGE, "", "", "");
                startActivity(new Intent(this, MXLoginActivity.class));
                this.finish();
                break;
            case R.id.tv_user_center_bind_dd:
                if (tvBindDD.getText().toString().equals("绑定当当账号")) {
                    View contentView = LayoutInflater.from(this).inflate(R.layout.mx_dialog_chose_dd_username, null);
                    final PopupWindow ddbdPop = new PopupWindow(contentView,
                            WindowManager.LayoutParams.MATCH_PARENT,
                            WindowManager.LayoutParams.MATCH_PARENT,
                            true);
                    ddbdPop.setContentView(contentView);
                    ddbdPop.setOutsideTouchable(false);
                    ddbdPop.showAtLocation(lluserCenter, Gravity.CENTER, 0, 0);
                    contentView.findViewById(R.id.ll_close_pop).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (ddbdPop != null) {
                                ddbdPop.dismiss();
                            }
                        }
                    });
                    contentView.findViewById(R.id.tv_use_already_username).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (ddbdPop != null) {
                                ddbdPop.dismiss();
                            }
                            Intent bindIntent = new Intent(MXUserCenterActivity.this, DDUserBindActivity.class);
                            startActivity(bindIntent);
                        }
                    });
                    contentView.findViewById(R.id.tv_use_register_username).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (ddbdPop != null) {
                                ddbdPop.dismiss();
                            }
                            startActivity(new Intent(MXUserCenterActivity.this, RegisterDDUserNameActivity.class));
                        }
                    });
                } else {
                    Intent bindIntent = new Intent(MXUserCenterActivity.this, DDUnbindActivity.class);
                    bindIntent.putExtra("is_back", isBack);
                    startActivity(bindIntent);
                }
                break;
            case R.id.civ_user_center_logo:
                MXUamManager.querUserBId(this);
                MXUamManager.queryDDToken(this);
                MXUamManager.queryUser(this);
                break;
            case R.id.ll_base_right:
                startActivity(new Intent(this, ModifyPasswordActivity.class));
                break;
            default:
                break;
        }
    }

    private Display display;

    private void showChoseAvatar() {
        LayoutInflater mLayoutInflater = (LayoutInflater) this
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View avatar = mLayoutInflater.inflate(
                R.layout.mx_popwindow_modify_avatar, null);
        LinearLayout llparent = (LinearLayout) avatar.findViewById(R.id.ll_parent_avatar);
        RecyclerView gvAvatar = (RecyclerView) avatar.findViewById(R.id.gv_chose_avatar);
        gvAvatar.setLayoutManager(new GridLayoutManager(this, 3));
        final AvatarAdapter adapter = new AvatarAdapter(this);
        gvAvatar.setAdapter(adapter);
        llparent.setLayoutParams(new FrameLayout.LayoutParams((int) (display
                .getWidth() * 0.85), LinearLayout.LayoutParams.WRAP_CONTENT));
        mPopupWindow = new PopupWindow(avatar, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setOutsideTouchable(false);

        avatar.findViewById(R.id.tv_chose_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogShowOrHide(true, "头像更改中...");
                if (adapter.getChose() == -99) {
                    dialogShowOrHide(false, "头像更改中...");
                    if (mPopupWindow != null)
                        mPopupWindow.dismiss();
                    mPopupWindow = null;
                    Toastor.showToast(MXUserCenterActivity.this, "未选择头像");
                    return;
                }
                OkHttpUtils.post().url(Constant.MODIFY_USER_AVATAR).addParams("appSession", MXUamManager.queryUser(MXUserCenterActivity.this)).addParams("headPortrait", adapter.getChose() + "").build().connTimeOut(10000).execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (MXUserCenterActivity.this.isfinish)return;
                        dialogShowOrHide(false, "头像更改中...");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (MXUserCenterActivity.this.isfinish)return;
                        dialogShowOrHide(false, "头像更改中...");
                        try {
                            JSONObject j = new JSONObject(response);
                            int code = j.getInt("code");
                            if (code == 0) {
                                Toastor.showToast(MXUserCenterActivity.this, "头像更改成功");
                                circleImageView.setImageResource(res[adapter.getChose()]);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                if (mPopupWindow != null)
                    mPopupWindow.dismiss();
                mPopupWindow = null;
            }
        });

        avatar.findViewById(R.id.tv_chose_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPopupWindow != null)
                    mPopupWindow.dismiss();
                mPopupWindow = null;
            }
        });
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                adapter.setChose(position);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mPopupWindow.showAtLocation(lluserCenter, Gravity.CENTER, 0, 0);
    }

    class AvatarAdapter extends RecyclerView.Adapter {

        private OnItemClickListener onItemClickListener;
        private Context context;
        private int chose = -99;

        public AvatarAdapter(Context context) {
            this.context = context;
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }

        public void setChose(int chose) {
            this.chose = chose;
            this.notifyDataSetChanged();
        }

        public int getChose() {
            return chose;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.mx_item_user_center_avatar, null);
            return new AvatarViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            if (chose == position) {
                ((AvatarViewHolder) holder).imgChose.setVisibility(View.VISIBLE);
            } else {
                ((AvatarViewHolder) holder).imgChose.setVisibility(View.GONE);
            }
            ((AvatarViewHolder) holder).imgAvatar.setImageResource(res[position]);
            if (onItemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onItemClickListener.onItemClick(holder.itemView, position);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return 4;
        }

        class AvatarViewHolder extends RecyclerView.ViewHolder {
            ImageView imgAvatar;
            ImageView imgChose;

            public AvatarViewHolder(View itemView) {
                super(itemView);
                imgAvatar = (ImageView) itemView.findViewById(R.id.img_item_avatar);
                imgChose = (ImageView) itemView.findViewById(R.id.img_item_chose);
            }
        }
    }
}
