package com.dangdang.reader;

public interface Constants {
    /**
     * 广播常量
     */
    String ACTION_LOGIN_CANCEL = "com.dangdang.reader.action.login.cancel";
    String BROADCAST_DELETE_BOOK = "com.dangdang.reader.broadcast.delete.book";
    String BROADCAST_REFRESH_BOOKLIST = "com.dangdang.reader.broadcast.refresh.booklist";
    String BROADCAST_REFRESH_LIST = "com.dangdang.reader.broadcast.refresh.list";
    String BROADCAST_FINISH_READ = "com.dangdang.reader.broadcast.finish.read";
    String BROADCAST_FINISH_REORDER = "com.dangdang.reader.broadcast.finish.reorder";
    String BROADCAST_DOWNLOAD_BOOK_FINISH = "com.dangdang.reader.broadcast.download.book.finish";
    String BROADCAST_RECHARGE_SUCCESS = "com.dangdang.reader.broadcast.recharge_success";
    String BROADCAST_BUY_DIALOG_CANCEL = "com.dangdang.reader.broadcast.buy_dialog_cancel";
    String ACTION_REFRESH_ADAPTER = "com.dangdang.reader.refresh_adapter";
    String ACTION_REFRESH_USER_INFO = "android.dang.action.refresh.user.info";

    String DANGDANG_DEFAULT_USER = "dangdang_default_user";


    int FILE_MAX_SIZE = 1024;
    int UNKNOW_TYPE = 0;

    /**
     * 书架数据库常量
     */
    String JSON_SALEID = "saleid";
    // String JSON_GROUP = "group";
    String JSON_SIZE = "bookSize";
    String JSON_AUTHOR = "author";
    String JSON_DATE = "publishDate";
    String JSON_COVER = "cover";
    String JSON_OVERDUE = "overdue";
    String JSON_DESC = "desc";
    String JSON_LOCAL = "local";
    String JSON_SERVER = "server";
    String JSON_PRELOAD = "preload";
    String JSON_DEADLINE = "deadline";
    String JSON_DOWN_STATUS = "downStatus";

    String BORROW_TYPE = "1003";
    String BORROW_BEGIN_DATE = "createDate"; // 借阅开始时间
    String BORROW_DURATION = "borrowDuration";// 借阅时长
    String BORROW_APPEND = "canBorrow"; // 可以续借

    String SHELF_PRE = "shelf_pre";
    String SHELF_ORDER_TIME = "shelf_order_time";
    String INDEX = "index";
    String EDIT_MODE = "edit_mode";
    String ORDER_TYPE = "order_type";
    String EDIT_TYPE = "edit_type";

    int MSG_DELETE_ONE_BOOK = 0;
    int MSG_DELETE_ONE_GROUP = 1;
    int MSG_UPDATE_SERVER_MAX = 2;

    int MSG_WHAT_DELETE_RECORD = 3;
    int MSG_WHAT_SET_LOGIN_INFO = 4;

    int MSG_WHAT_REQUEST_DATA_ERROR = 5;
    int MSG_WHAT_REQUEST_DATA_SUCCESS = 6;

    int MSG_WHAT_GETCERT_SUCCESS = 0x1001;
    int MSG_WHAT_GETCERT_FAILED = 0x1002;

    String PERSONAL_UPDATE_INFOMATION = "personal_update_infomation";
    float OLD_FILE_VERSION_CODE = 1.2f;


    String OTHERS = "others"; // 偷来的
    String STEAL_PERCENT = "steal_percent"; // 偷来的比例
    String BOOK_ID = "book_id";
    String BOOK_NAME = "book_name";
    String BOOK_DIR = "book_dir";

    String MONTHLY_CHANNEL_NAME = "monthly_channel_name";//包月频道名称
    String MONTHLY_SYNC_TIME = "monthly_sys_time";//包月的书籍的上次同步时间
    String MONTHLY_CHANNEL_ID = "monthly_channel_id";//包月的频道信息
    String MONTHLY_AUTH_STATUS = "monthly_auth_status";//包月的状态，还了0， 包月中1， 购买2
}
