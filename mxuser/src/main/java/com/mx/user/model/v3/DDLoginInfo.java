package com.mx.user.model.v3;

import java.io.Serializable;

/**
 * Created by King on 2017/12/4.
 */

public class DDLoginInfo implements Serializable {

    /**
     * code : 1
     * msg : 请输入验证码
     * result : {"codes":"亭,涛,偿,岷,淳,情,郁,汵,侣,见,钟,一","img":"iVBORw0KGgoAAAANSUhEUgAAAGYAAAAqCAYAAABBRS51AAABTklEQVR42u2ZSw7DIAwFc5ceqLfsNemmkaKqFLCNsclYyirKgjd5/nEchGmUT6BEIBj/ApVwDAGYIIJL0hnKBXABUAKnJ6AAhpCmMtLaIjBlMFDSGAbCJgWDSwKA+SX4lins8XyVXcBs16FFhPMt6i3BrP4prk9tDSOtMYCZBOqE1QMGxyxKbYBJCoZUhmP8uyCrg3iKsi2YIgxLKBoBa99uA8Zi3VF733MD6Xmu9GAsQbZc5lmXQs0DtWHOCoykTtFBTXbMiLjcnXQKvxLMds6xvLmzBKPpoJjWJ4Fp/f2tpgEwA8Vcsx6RdnMQEYDRiGs9lLq1tpHAaNYc17TUCzq0U2oXRhnB1GpM+vTlCcgCTE9HWHufsq54ucgaTPrVSSYX9Yp4OzCrBliN+2aABIxwIzwKFTCKdU+KueNOAydb4MCbAMAEArP7GVtjyBv3ZIc87upnkwAAAABJRU5ErkJggg==","systemDate":"1512353875011","currentDate":"2017-12-04 10:17:55"}
     */

    private int code;
    private String msg;
    private ResultBean result;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * codes : 亭,涛,偿,岷,淳,情,郁,汵,侣,见,钟,一
         * img : iVBORw0KGgoAAAANSUhEUgAAAGYAAAAqCAYAAABBRS51AAABTklEQVR42u2ZSw7DIAwFc5ceqLfsNemmkaKqFLCNsclYyirKgjd5/nEchGmUT6BEIBj/ApVwDAGYIIJL0hnKBXABUAKnJ6AAhpCmMtLaIjBlMFDSGAbCJgWDSwKA+SX4lins8XyVXcBs16FFhPMt6i3BrP4prk9tDSOtMYCZBOqE1QMGxyxKbYBJCoZUhmP8uyCrg3iKsi2YIgxLKBoBa99uA8Zi3VF733MD6Xmu9GAsQbZc5lmXQs0DtWHOCoykTtFBTXbMiLjcnXQKvxLMds6xvLmzBKPpoJjWJ4Fp/f2tpgEwA8Vcsx6RdnMQEYDRiGs9lLq1tpHAaNYc17TUCzq0U2oXRhnB1GpM+vTlCcgCTE9HWHufsq54ucgaTPrVSSYX9Yp4OzCrBliN+2aABIxwIzwKFTCKdU+KueNOAydb4MCbAMAEArP7GVtjyBv3ZIc87upnkwAAAABJRU5ErkJggg==
         * systemDate : 1512353875011
         * currentDate : 2017-12-04 10:17:55
         */

        private String codes;
        private String img;
        private String systemDate;
        private String currentDate;

        public String getCodes() {
            return codes;
        }

        public void setCodes(String codes) {
            this.codes = codes;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public String getSystemDate() {
            return systemDate;
        }

        public void setSystemDate(String systemDate) {
            this.systemDate = systemDate;
        }

        public String getCurrentDate() {
            return currentDate;
        }

        public void setCurrentDate(String currentDate) {
            this.currentDate = currentDate;
        }
    }
}
