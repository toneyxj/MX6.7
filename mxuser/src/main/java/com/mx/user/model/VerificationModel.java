package com.mx.user.model;

import java.io.Serializable;

/**
 * Created by King on 2017/11/29.
 */

public class VerificationModel implements Serializable {

    /**
     * code : 0
     * msg : success
     * result : {"codes":"傣,汾,黔,兆,技,驴,程,罗,坭,洌,耿,穷","img":"iVBORw0KGgoAAAANSUhEUgAAAGYAAAAqCAYAAABBRS51AAABlklEQVR42u2bTRLCMAiFexcP5C29ZtSFO5PwCCk/ecw4rlpTPgIPGq+LprL2MXohKBjCIZh69ni+2u/DtJYA0gqoJjB62yF9zRxPQMFrCuEEkMaEELRfIRjH9EXnJ9k9uyDtlPblGzvunqBSlGACOoVNZSA4EhBlAGUZafxTZr01HgWmCS3CTnULrN1OyQwmHBTLxY7AeEep5DlvB9Nz2GzRkjSVCUx4KN/uVOrkERzJ2AMFgzhJ68xfd+4qVqSRMgKDXLNacO8AE0I9StLQDAAiRdHatgpmxcGuc62Rw7TpB4l0VJHdCcYVDlr00Few1s5D75t2vCKJYmvHaFKT9D5lDlOg9cM6RaEHIBBHp59vSR+4F6krwbDzIESJwaOFtI0I5qpk0tG3Jt/vBIOsIXVa08phpOBbgSlX/K3mQ9r+YrVGaUVLWgFg1Q/t+i1Eyh/7ilgjeXc1i6vTgqOgzIQCukOQ/qVEXel9R3xPf8Sp/FmEZQNjqcKs/ndzfPrspT2r4CIoxS6QnsRhaDulsNJ1h0Y7zt4rgJnNIgB2+QAAAABJRU5ErkJggg==","systemDate":"1512023318325","currentDate":"2017-11-30 14:28:38"}
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
         * codes : 傣,汾,黔,兆,技,驴,程,罗,坭,洌,耿,穷
         * img : iVBORw0KGgoAAAANSUhEUgAAAGYAAAAqCAYAAABBRS51AAABlklEQVR42u2bTRLCMAiFexcP5C29ZtSFO5PwCCk/ecw4rlpTPgIPGq+LprL2MXohKBjCIZh69ni+2u/DtJYA0gqoJjB62yF9zRxPQMFrCuEEkMaEELRfIRjH9EXnJ9k9uyDtlPblGzvunqBSlGACOoVNZSA4EhBlAGUZafxTZr01HgWmCS3CTnULrN1OyQwmHBTLxY7AeEep5DlvB9Nz2GzRkjSVCUx4KN/uVOrkERzJ2AMFgzhJ68xfd+4qVqSRMgKDXLNacO8AE0I9StLQDAAiRdHatgpmxcGuc62Rw7TpB4l0VJHdCcYVDlr00Few1s5D75t2vCKJYmvHaFKT9D5lDlOg9cM6RaEHIBBHp59vSR+4F6krwbDzIESJwaOFtI0I5qpk0tG3Jt/vBIOsIXVa08phpOBbgSlX/K3mQ9r+YrVGaUVLWgFg1Q/t+i1Eyh/7ilgjeXc1i6vTgqOgzIQCukOQ/qVEXel9R3xPf8Sp/FmEZQNjqcKs/ndzfPrspT2r4CIoxS6QnsRhaDulsNJ1h0Y7zt4rgJnNIgB2+QAAAABJRU5ErkJggg==
         * systemDate : 1512023318325
         * currentDate : 2017-11-30 14:28:38
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
