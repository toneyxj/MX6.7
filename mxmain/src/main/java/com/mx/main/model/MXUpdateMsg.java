package com.mx.main.model;

import com.mx.mxbase.model.BaseModel;

/**
 * Created by Archer on 16/9/2.
 */
public class MXUpdateMsg extends BaseModel {
    private Update result;

    public Update getResult() {
        return result;
    }

    public void setResult(Update result) {
        this.result = result;
    }

    public class Update {
        private String desc;
        private String id;
        private String version;

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

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }
}
