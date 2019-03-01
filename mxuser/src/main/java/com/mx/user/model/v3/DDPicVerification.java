package com.mx.user.model.v3;

import java.io.Serializable;

/**
 * Created by King on 2017/12/4.
 */

public class DDPicVerification implements Serializable {

    /**
     * code : 0
     * msg : success
     * result : {"codes":"缘,俸,塾,湖,遨,玉,瑨,金,园,良,曹,爱","img":"iVBORw0KGgoAAAANSUhEUgAAAGYAAAAqCAYAAABBRS51AAABfUlEQVR42u2bXQ4CMQiEvYsH8pZes8bEB7PpHxTosJ1JfLNblq9QqPXxoCiKok7T8/Uu3w89AQ6IoJQqPzGSQMFEwKEuKxUhYiimEQK6XU4/cfNnBaS051hApSK06F21qzcWEvzVKBQ4tfm1dvXGwQLpOWAXoNHq9gKzfUHOOn0XnJ4TpbaMxtRSZopuekf0tPY8qQ2zi297xKw4OBJObS5ttKC92/TkkoiIBDOCJH2GNELDIM1A0RpYhFpx0opNEpvTN5nWLzgCYrU3poFinaqsew6t82YaS/hTBRQwVqcT0qoMsuFEBlMrmSVOl6YzyDMyKyO9wEjS0Og7UF2/ppLaCWbW+Ss2wO81HqnMqvSO6JuOKpc1DrZOpzPPg4bTyuWRfYylcyRQWgUGBAiL8ymUzVSySP6BQNh/ZzAaG2DTl3cfg1z9pCuVV45CLH8eRoPictXKGwoqAGulvAt30pWjFIA8TxOogL2KgJiyqFbu5T+4qPNWPT1BQOn1AXsUc66B1pKiAAAAAElFTkSuQmCC","systemDate":"1512356891501","currentDate":"2017-12-04 11:08:11"}
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
         * codes : 缘,俸,塾,湖,遨,玉,瑨,金,园,良,曹,爱
         * img : iVBORw0KGgoAAAANSUhEUgAAAGYAAAAqCAYAAABBRS51AAABfUlEQVR42u2bXQ4CMQiEvYsH8pZes8bEB7PpHxTosJ1JfLNblq9QqPXxoCiKok7T8/Uu3w89AQ6IoJQqPzGSQMFEwKEuKxUhYiimEQK6XU4/cfNnBaS051hApSK06F21qzcWEvzVKBQ4tfm1dvXGwQLpOWAXoNHq9gKzfUHOOn0XnJ4TpbaMxtRSZopuekf0tPY8qQ2zi297xKw4OBJObS5ttKC92/TkkoiIBDOCJH2GNELDIM1A0RpYhFpx0opNEpvTN5nWLzgCYrU3poFinaqsew6t82YaS/hTBRQwVqcT0qoMsuFEBlMrmSVOl6YzyDMyKyO9wEjS0Og7UF2/ppLaCWbW+Ss2wO81HqnMqvSO6JuOKpc1DrZOpzPPg4bTyuWRfYylcyRQWgUGBAiL8ymUzVSySP6BQNh/ZzAaG2DTl3cfg1z9pCuVV45CLH8eRoPictXKGwoqAGulvAt30pWjFIA8TxOogL2KgJiyqFbu5T+4qPNWPT1BQOn1AXsUc66B1pKiAAAAAElFTkSuQmCC
         * systemDate : 1512356891501
         * currentDate : 2017-12-04 11:08:11
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
