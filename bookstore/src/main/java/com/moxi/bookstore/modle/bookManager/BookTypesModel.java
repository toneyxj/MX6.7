package com.moxi.bookstore.modle.bookManager;

import com.mx.mxbase.model.BaseModel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Archer on 16/8/2.
 */
public class BookTypesModel extends BaseModel implements Serializable {
    //返回书籍类型列表
    private List<BookType> result;

    public List<BookType> getResult() {
        return result;
    }

    public void setResult(List<BookType> result) {
        this.result = result;
    }

    public class BookType implements Serializable {
        //书籍类型id
        private String id;
        //书籍类型名称
        private String name;
        //书籍排序编号
        private String orderNo;
        //书籍状态
        private String state;
        //选中的标识
        private boolean index;

        public boolean isIndex() {
            return index;
        }

        public void setIndex(boolean index) {
            this.index = index;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getOrderNo() {
            return orderNo;
        }

        public void setOrderNo(String orderNo) {
            this.orderNo = orderNo;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }
    }
}
