package com.moxi.bookstore.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.moxi.bookstore.BookstoreApplication;
import com.moxi.bookstore.bean.LoginUserData;
import com.moxi.bookstore.bean.Message.LoginUserMsg;
import com.moxi.bookstore.request.NetUtil;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.utils.GsonTools;
import com.mx.mxbase.utils.StartActivityUtils;
import com.mx.mxbase.utils.Toastor;
import com.mx.mxbase.view.AlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2016/9/23.
 */
public class ToolUtils {
   private static ToolUtils intence;
    LoginUserData data;
    private long notifiyTime=0;
    public  static final int noEfficacy=10003;
    public static ToolUtils getIntence(){
        if (null==intence){
            synchronized (ToolUtils.class){
                if (null==intence)
                    intence=new ToolUtils();
            }
        }
        return intence;
    }
    public void clearLoginUserData(){
        intence=null;
    }

    private ToolUtils(){}

    public String formatPrice(double d){
        int num=(int) (d*100)%10;
        String price;
        if (num==0){
            price=d+"0";
        }else
            price=d+"";
        return price;
    }

    //年月日
    public String dateToStr1(Long time){
        Date date=new Date(time);
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        String str=(new SimpleDateFormat("yyyy-MM-dd")).format(date);
        return str;
    }

    public void showSoftInput(View view , Context ctx){
        InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view,InputMethodManager.SHOW_FORCED);
    }

    public void hidSoftInput(EditText editText,Context ctx){
        InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    Toast toast;
    public  void ToastUtil(Context context,String str){
        if (null==toast){
            toast=Toast.makeText(context,"",Toast.LENGTH_SHORT);
        }
        toast.setText(str);
        toast.show();
    }

    public LoginUserData getLoginData(Context ctx){
        String value= com.mx.mxbase.utils.MXUamManager.queryDDToken(ctx);
        if (value==null||value.isEmpty()){
            return null;
        }
        LoginUserMsg msg=GsonTools.getPerson(value,LoginUserMsg.class);
        if (null!=msg){
            data=msg.getData();
            return data;
        }else
            return null;

    }
    public LoginUserData getToken(Context context){
        if (data==null){
            getLoginData(context);
        }
        return data;
    }

    /**
     * 判断是否绑定当当账号
     * @param ctx
     * @return true绑定当当账号，false没有绑定当当账号
     */
    public boolean getBindingDDUser(Context ctx){
        String value= com.mx.mxbase.utils.MXUamManager.querUserBId(ctx);
       boolean is=false;
        try {
            JSONObject object=new JSONObject(value);
            JSONObject result=object.getJSONObject("result");
            JSONObject ddUser= result.getJSONObject("ddUser");
            is=ddUser!=null;
        } catch (JSONException e) {
//            e.printStackTrace();
            return false;
        }
        return is;
    }

    /**
     * 提示前往绑定当当账号
     * @param context
     * @return
     */
    public boolean showBindingDDUser(final Context context){
        boolean is=getBindingDDUser(context);
        if (!is){
            new AlertDialog(context).builder().setTitle("登录提示").setCancelable(false).setMsg("需要绑定当当账号，请前往绑定").
                    setNegativeButton("前往", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            StartActivityUtils.startDDUerBind(context);
                            ToolUtils.getIntence().clearLoginUserData();
                        }
                    }).setPositiveButton("放弃", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            }).show();
        }
        return is;
    }
    public String getTokenStr(Context context){
        if (data==null){
            getLoginData(context);
        }
        if (data!=null){
            APPLog.e("token",data.getToken());
            return data.getToken();
        }
        return null;
    }

    public boolean hasLogin(Context ctx){
       String usersession= com.mx.mxbase.utils.MXUamManager.queryUser(ctx);
        APPLog.e("session="+usersession);
        if (usersession.equals("")) {
            //未登录
            try {
                Intent intent = ctx.getPackageManager().getLaunchIntentForPackage(StartActivityUtils.UserPakege);
                String flag = ((BookstoreApplication) ctx.getApplicationContext()).getStuFlag();
                intent.putExtra("flag_version_stu", flag);
                intent.putExtra("is_back",true);
                clearLoginUserData();
                ctx.startActivity(intent);
            } catch (Exception e) {
                Toastor.showToast(ctx, "没有安装此模块");
            }
            return false;
        }else if (getToken(ctx)==null){
            //未绑定当当账号界面跳转
            startDDUserBind(ctx);
            return false;
        }
        return true;
    }
    public void startDDUserBind(Context context){

        clearLoginUserData();
        long curT=System.currentTimeMillis();
        if (curT-notifiyTime>1000) {
            ToolUtils.getIntence().clearLoginUserData();
            StartActivityUtils.startDDUerBind(context);
            clearLoginUserData();
//            startAppByPackage(context,StartActivityUtils.UserPakege, "com.mx.user.activity.DDUserBindActivity");
            notifiyTime=curT;
        }

    }

    public String getIMEINo(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = tm.getDeviceId();
        if (imei!=null&&TextUtils.isEmpty(imei)) {
           return imei;
        } else
            return "null";
    }

    public String getDeviceNo(Context context){
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        //序列号（sn）
        String sn = tm.getSimSerialNumber();
        if (sn!=null&&TextUtils.isEmpty(sn)) {
            return sn;
        } else
            return "null";
    }

    public String getEbookProgress(String json){
        String progress="";
        try {
            APPLog.e("progress:"+json);
            JSONObject jsonObject=new JSONObject(json);
            progress=jsonObject.getString("readerProgress");
        }catch (Exception e){
            APPLog.e(e.toString());
        }
        return progress;
    }

    /**
     * 获取文件下载提示语句
     * @param context 上下文
     * @param donwloadsize 下载文件大小
     * @return 返回描述文字
     */
    public String getDownloadHitn(Context context,long donwloadsize){
        String msg="";
        if (!NetUtil.checkNetworkInfo(context)){
            msg="请检查网络连接";
        }else if (donwloadsize<=0){
            msg="文件读取失败";
        }else {
            StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
            long size = statFs.getBlockSize();// 获取分区的大小
            long blocks = statFs.getAvailableBlocks();// 获取可用分区的个数
            long result = blocks * size;

            if (result<donwloadsize){
                msg="内存空间不足，下载书籍需要：" + Formatter.formatFileSize(context, donwloadsize);
            }else {
                msg="下载出小差了，麻烦您重新点击下载！！";
            }
        }
        return msg;
    }
    public String  downloadPathSpil(String filName){
        String str=filName.replaceAll("[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……& amp;*（）——+|{}【】‘；：”“’。，、？|-]", "");
        return str;
    }
    /**
     * 通过包名 类名启动app
     *
     * @param packName  包名 com.moxi.xxxx
     * @param className 类名
     */
    public void startAppByPackage(Context context,String packName, String className) {
        try {
            Intent sound = new Intent();
//            sound.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            sound.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName cnSound = new ComponentName(packName, className);
            sound.setComponent(cnSound);
            context.startActivity(sound);
        } catch (Exception e) {
            e.printStackTrace();
            Toastor.showToast(context, "启动失败，请检测是否正常安装");
        }
    }


}
