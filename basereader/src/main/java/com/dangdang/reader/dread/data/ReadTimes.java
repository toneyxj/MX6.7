package com.dangdang.reader.dread.data;

/**
 * 阅读时段
 * Created by Yhyu on 2016/1/20.
 */
public class ReadTimes {

    /**
     * productId : 1111
     * startTime : 1410859869011
     * endTime : 1410959869011
     */
    private String productId;
    private long startTime;
    private long endTime;

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getProductId() {
        return productId;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public static final String CreateReadTimesSql = "CREATE TABLE IF NOT EXISTS "
            + ReadTimesColumn.TableName + "("
            + ReadTimesColumn.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
            + ReadTimesColumn.COLUMN_PRODUCTID + " VARCHAR, "
            + ReadTimesColumn.COLUMN_STARTTIME + " VARCHAR, "
            + ReadTimesColumn.COLUMN_ENDTIME + " VARCHAR, "
            + ReadTimesColumn.COLUMN_USERID + " VARCHAR,"
            + ReadTimesColumn.COLUMN_EXP1 + " VARCHAR, "
            + ReadTimesColumn.COLUMN_EXP2 + " VARCHAR, "
            + ReadTimesColumn.COLUMN_EXP3 + " VARCHAR, "
            + ReadTimesColumn.COLUMN_EXP4 + " VARCHAR, "
            + ReadTimesColumn.COLUMN_EXP5 + " VARCHAR);";

    public interface ReadTimesColumn {
        String TableName = "readtimestable";
        String COLUMN_ID = "column_id";
        String COLUMN_PRODUCTID = "column_productid";
        String COLUMN_STARTTIME = "column_starttime";
        String COLUMN_ENDTIME = "column_endtime";
        String COLUMN_USERID = "column_userid";
        String COLUMN_EXP1 = "column_exp1";
        String COLUMN_EXP2 = "column_exp2";
        String COLUMN_EXP3 = "column_exp3";
        String COLUMN_EXP4 = "column_exp4";
        String COLUMN_EXP5 = "column_exp5";

    }
}
