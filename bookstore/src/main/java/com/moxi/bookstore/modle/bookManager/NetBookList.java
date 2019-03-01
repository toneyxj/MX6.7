package com.moxi.bookstore.modle.bookManager;


import com.mx.mxbase.model.BaseModel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Archer on 16/8/3.
 */
public class NetBookList extends BaseModel implements Serializable {
    //每页显示条数
    private BookList result;

    public BookList getResult() {
        return result;
    }

    public void setResult(BookList result) {
        this.result = result;
    }

    public class BookList implements Serializable {
        private String page;//当前页码
        private String pageCount;//总页数
        private String rowCount;//一共多少条记录
        private String rows;
        private String fromRowId;
        private List<Book> list;

        public String getPage() {
            return page;
        }

        public void setPage(String page) {
            this.page = page;
        }

        public String getPageCount() {
            return pageCount;
        }

        public void setPageCount(String pageCount) {
            this.pageCount = pageCount;
        }

        public String getRowCount() {
            return rowCount;
        }

        public void setRowCount(String rowCount) {
            this.rowCount = rowCount;
        }

        public String getRows() {
            return rows;
        }

        public void setRows(String rows) {
            this.rows = rows;
        }

        public String getFromRowId() {
            return fromRowId;
        }

        public void setFromRowId(String fromRowId) {
            this.fromRowId = fromRowId;
        }

        public List<Book> getList() {
            return list;
        }

        public void setList(List<Book> list) {
            this.list = list;
        }

        public class Book {
            private String fromRowId;
            private String bookTypeId;
            private String bookTypeName;
            private String coverImage;
            private String desc;
            private String id;
            private String name;
            private String saveFile;
            private String state;
            private String uploadFile;

            public String getFromRowId() {
                return fromRowId;
            }

            public void setFromRowId(String fromRowId) {
                this.fromRowId = fromRowId;
            }

            public String getBookTypeId() {
                return bookTypeId;
            }

            public void setBookTypeId(String bookTypeId) {
                this.bookTypeId = bookTypeId;
            }

            public String getBookTypeName() {
                return bookTypeName;
            }

            public void setBookTypeName(String bookTypeName) {
                this.bookTypeName = bookTypeName;
            }

            public String getCoverImage() {
                return coverImage;
            }

            public void setCoverImage(String coverImage) {
                this.coverImage = coverImage;
            }

            public String getDesc() {
                return desc;
            }

            public void setDesc(String desc) {
                this.desc = desc;
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

            public String getSaveFile() {
                return saveFile;
            }

            public void setSaveFile(String saveFile) {
                this.saveFile = saveFile;
            }

            public String getState() {
                return state;
            }

            public void setState(String state) {
                this.state = state;
            }

            public String getUploadFile() {
                return uploadFile;
            }

            public void setUploadFile(String uploadFile) {
                this.uploadFile = uploadFile;
            }
        }
    }

}
