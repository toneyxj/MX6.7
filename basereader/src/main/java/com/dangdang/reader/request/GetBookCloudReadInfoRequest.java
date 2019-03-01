package com.dangdang.reader.request;

import android.os.Handler;
import android.os.Message;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangdang.reader.cloud.Status;
import com.dangdang.reader.domain.CloudDataList;
import com.dangdang.reader.dread.data.BookMark;
import com.dangdang.reader.dread.data.BookNote;
import com.dangdang.reader.dread.util.DreaderConstants;
import com.dangdang.reader.utils.DangdangConfig;
import com.dangdang.zframework.network.command.OnCommandListener;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by liupan on 2015/7/24.
 * 获取云端书签、笔记列表
 */
public class GetBookCloudReadInfoRequest extends BaseStringRequest {
    public static String ACTION = "getBookCloudSyncReadInfo";
    private String mProductId;
    private long mVersionTime;
    private Handler mHandler;

    public GetBookCloudReadInfoRequest(String productId, long versionTime, Handler handler) {
        mProductId = productId;
        mVersionTime = versionTime;
        mHandler = handler;
    }

    @Override
    public String getAction() {
        return ACTION;
    }


    @Override
    public String getPost() {
        return "&productId=" + mProductId + "&versionTime=" + mVersionTime;
//        return "&token=adef31566e580ee3f3a532806027d130" + "&productId=" + mProductId + "&versionTime=" + mVersionTime;
    }

//    @Override
//    public String getUrl() {
////        return "http://10.255.223.131/mobile/api2.do?action=" + getAction();
////        return "http://192.168.132.73:8080/mobile/api2.do?action="+getAction();
////        return "http://10.255.223.227:8090/mobile/api2.do?action="+getAction();
//        return DangdangConfig.SERVER_MOBILE_BOOK_CLOUD_API2_URL+"action="+getAction();
//    }


    @Override
    public void appendParams(StringBuilder buff) {

    }

    @Override
    protected void onRequestSuccess(OnCommandListener.NetResult netResult, JSONObject jsonObject) {
        CloudDataList cloudDataList = parseData(jsonObject);
        if (cloudDataList == null) {
            dealRequestDataFail();
        } else {
            result.setResult(cloudDataList);
            Message msg = mHandler.obtainMessage(RequestConstants.MSG_WHAT_REQUEST_DATA_SUCCESS, result);
            mHandler.sendMessage(msg);
        }
    }

    @Override
    protected void onRequestFailed(OnCommandListener.NetResult netResult, JSONObject jsonObject) {
        dealRequestDataFail();
    }

    private CloudDataList parseData(JSONObject jsonObject) {
        /**
         * {"data":{"currentDate":"2014-05-27 10:32:17",
         * "markInfo":[{"chaptersIndex":1,"characterIndex":213,"clientOperateTime":950370640221,"markId":14,"modifyTime":1401157923,"status":1},
         * {"chaptersIndex":1,"characterIndex":464,"clientOperateTime":950370641685,"markId":15,"modifyTime":1401157923,"status":1}],
         *
         * "noteInfo":[{"callOutInfo":"","chaptersIndex":1,"characterEndIndex":912,"characterStartIndex":809,
         * "clientOperateTime":950370651449,"modifyTime":1401157923,"noteId":17,
         * "noteInfo":"用。在读本编写中，我们对照《语文课程标准》，把现行教材中没有而课程标准要求的篇目作为首选内容，
         * 把教材涉及而学生还不够了解的内容尽量选入读本。这样，便于学生在学习过程中，课内课外纵横联系，加深理解。就阅读面和","status":1},
         *
         * {"callOutInfo":"hhhh","chaptersIndex":1,"characterEndIndex":1117,"characterStartIndex":1033,
         * "clientOperateTime":950370661039,"modifyTime":1401157923,"noteId":18,"noteInfo":"体现地方特色，培养学生热爱家乡的美好情怀。内乡历史悠久，人杰地灵，
         * 有闻名全国的清代县衙，有风景秀丽的宝天曼。生于斯长于斯的内乡县中小学生，应该了解家乡的丰饶美丽、多彩多","status":2}],"productId":"1900603751","systemDate":1401157937453,"versionTime":1401157923},"status":{"code":0},"systemDate":1401157937453}
         */
        if (jsonObject == null) {
            return null;
        }
//        try {
//            return JSON.parseObject(jsonObject.toString(),CloudDataList.class);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
        CloudDataList dataList = new CloudDataList();

        String productId = jsonObject.getString("productId");
        long versionTime = jsonObject.getLong("versionTime");

        JSONArray marksJson = jsonObject.getJSONArray("markInfo");
        List<BookMark> bookMarks = null;
        if(marksJson != null && marksJson.size() > 0){
            bookMarks = buildMarks(productId, marksJson);
        }

        JSONArray notesJson = jsonObject.getJSONArray("noteInfo");
        List<BookNote> bookNotes = null;
        if(notesJson != null && notesJson.size() > 0){
            bookNotes = buildNotes(productId, notesJson);
        }

        dataList.setVersionTime(versionTime);
        dataList.setBookMarks(bookMarks);
        dataList.setBookNotes(bookNotes);
        return dataList;
    }

    private List<BookNote> buildNotes(String productId, JSONArray notesJson) {
        List<BookNote> bookNotes = new ArrayList<BookNote>();
        try {
            for (int i = 0, len = notesJson.size(); i < len; i++) {
                JSONObject jsonObj = notesJson.getJSONObject(i);
                BookNote bookNote = new BookNote();

                bookNote.setBookId(productId);
                bookNote.setChapterIndex(jsonObj.getInteger("chaptersIndex"));
                bookNote.setNoteStart(jsonObj.getInteger("characterStartIndex"));
                bookNote.setNoteEnd(jsonObj.getInteger("characterEndIndex"));
                bookNote.setSourceText(jsonObj.getString("callOutInfo"));
                bookNote.setNoteText(jsonObj.getString("noteInfo"));
                bookNote.setNoteTime(jsonObj.getLong("clientOperateTime"));
                bookNote.setModifyTime(String.valueOf(jsonObj.getLong("modifyTime")));
                bookNote.setStatus(jsonObj.getString("status"));
                bookNote.setCloudStatus(String.valueOf(Status.CLOUD_YES));
                bookNote.setIsBought(1);//ReadInfo.BSTATUS_FULL
				String strBookModversion = jsonObj.getString("bookmodversion");
				if (strBookModversion == null || strBookModversion.isEmpty())
					strBookModversion = DreaderConstants.BOOK_MODIFY_VERSION;
				bookNote.setBookModVersion(strBookModversion);
                Integer drawlineColor = jsonObj.getInteger("drawLineColor");
                if (drawlineColor != null)
                    bookNote.setDrawLineColor(drawlineColor);

                bookNotes.add(bookNote);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bookNotes;
    }

    private List<BookMark> buildMarks(String productId, JSONArray marksJson) {
        List<BookMark> bookMarks = new ArrayList<BookMark>();
        try {
            for (int i = 0, len = marksJson.size(); i < len; i++) {
                  JSONObject jsonObj = marksJson.getJSONObject(i);
                BookMark bookMark = new BookMark();

                bookMark.setpId(productId);
                bookMark.setChapterIndex(jsonObj.getInteger("chaptersIndex"));
                bookMark.setElementIndex(jsonObj.getInteger("characterIndex"));
                bookMark.setMarkTime(jsonObj.getLong("clientOperateTime"));
                bookMark.setModifyTime(String.valueOf(jsonObj.getLong("modifyTime")));
                bookMark.setMarkText(jsonObj.getString("markInfo"));
                bookMark.setStatus(jsonObj.getString("status"));
                bookMark.setCloudStatus(String.valueOf(Status.CLOUD_YES));
                bookMark.setIsBought(1);//ReadInfo.BSTATUS_FULL
				String strBookModversion = jsonObj.getString("bookmodversion");
				if (strBookModversion == null || strBookModversion.isEmpty())
					strBookModversion = DreaderConstants.BOOK_MODIFY_VERSION;
				bookMark.setBookModVersion(strBookModversion);
			

                bookMarks.add(bookMark);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bookMarks;
    }

    private void dealRequestDataFail() {
        if (mHandler != null) {
            Message msg = mHandler.obtainMessage(RequestConstants.MSG_WHAT_REQUEST_DATA_FAIL);
            result.setExpCode(expCode);
            msg.obj = result;
            mHandler.sendMessage(msg);
        }
    }
}
