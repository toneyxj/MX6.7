package com.moxi.bookstore.modle.loging;

import com.mx.mxbase.model.BaseModel;

import java.io.Serializable;

/**
 * Created by Archer on 16/8/23.
 */
public class UserInfoModel extends BaseModel implements Serializable {
    private UserInfo result;

    public UserInfo getResult() {
        return result;
    }

    public void setResult(UserInfo result) {
        this.result = result;
    }

    public class UserInfo implements Serializable {

        /**
         * appSession : ewUiWgUEOpwfGJ2LaJzfeCoR6CK72Z1vUwKbDvKnA8c=
         * ddToken : {"data":{"unbindNum":17,"pubIdNum":39527957,"userPubId":"RBtXTiCExmRB7aIJorerUQ==","systemDate":"1512455713274","uniqueKey":"73fe126e6d295509a497106645bdc0e2","currentDate":"2017-12-05 14:35:13","user":{"phone":"158****9255","nickName":"158****9255","headportrait":"http://img7x3.ddimg.cn/imghead/24/32/6860168054313-1_e.png?1512455600287","introduct":"","vipType":"0","id":265078839,"displayId":6860168054313,"userName":"158****9255","email":"1582371925564776@ddmobilphone__user.com","registerDate":1511927104000},"token":"e_7ad68708f657b4b7ec7fc6030ca023d186ca1a18d2e0855a4b78b2379bc9c765"},"systemDate":1512455713274,"status":{"code":0}}
         * ddUser : {"data":{"unbindNum":18,"pubIdNum":39527957,"userPubId":"RBtXTiCExmRB7aIJorerUQ==","systemDate":"1512353836048","uniqueKey":"73fe126e6d295509a497106645bdc0e2","currentDate":"2017-12-04 10:17:16","user":{"phone":"158****9255","nickName":"158****9255","headportrait":"http://img7x3.ddimg.cn/imghead/24/32/6860168054313-1_e.png?1512353802843","introduct":"","vipType":"0","id":265078839,"displayId":6860168054313,"userName":"158****9255","email":"1582371925564776@ddmobilphone__user.com","registerDate":1511927104000},"token":"e_6b7179050f992a358865c7d0def27b5f12b6f7e14f799c9cac3902b356edd939"},"systemDate":1512353836048,"status":{"code":0}}
         * member : {"email":"","grade":"","headPortrait":0,"id":1,"mobile":"18607149219","name":"哈哈尔滨","password":"e10adc3949ba59abbe56e057f20f883e","regTime":"2017-04-14 10:57:59","school":"","sex":2,"state":0,"version":"v3"}
         */

        private String appSession;
        private String ddToken;
        private DdUserBean ddUser;
        private MemberBean member;

        public String getAppSession() {
            return appSession;
        }

        public void setAppSession(String appSession) {
            this.appSession = appSession;
        }

        public String getDdToken() {
            return ddToken;
        }

        public void setDdToken(String ddToken) {
            this.ddToken = ddToken;
        }

        public DdUserBean getDdUser() {
            return ddUser;
        }

        public void setDdUser(DdUserBean ddUser) {
            this.ddUser = ddUser;
        }

        public MemberBean getMember() {
            return member;
        }

        public void setMember(MemberBean member) {
            this.member = member;
        }

        public class DdUserBean {
            /**
             * data : {"unbindNum":18,"pubIdNum":39527957,"userPubId":"RBtXTiCExmRB7aIJorerUQ==","systemDate":"1512353836048","uniqueKey":"73fe126e6d295509a497106645bdc0e2","currentDate":"2017-12-04 10:17:16","user":{"phone":"158****9255","nickName":"158****9255","headportrait":"http://img7x3.ddimg.cn/imghead/24/32/6860168054313-1_e.png?1512353802843","introduct":"","vipType":"0","id":265078839,"displayId":6860168054313,"userName":"158****9255","email":"1582371925564776@ddmobilphone__user.com","registerDate":1511927104000},"token":"e_6b7179050f992a358865c7d0def27b5f12b6f7e14f799c9cac3902b356edd939"}
             * systemDate : 1512353836048
             * status : {"code":0}
             */

            private DataBean data;
            private long systemDate;
            private StatusBean status;

            public DataBean getData() {
                return data;
            }

            public void setData(DataBean data) {
                this.data = data;
            }

            public long getSystemDate() {
                return systemDate;
            }

            public void setSystemDate(long systemDate) {
                this.systemDate = systemDate;
            }

            public StatusBean getStatus() {
                return status;
            }

            public void setStatus(StatusBean status) {
                this.status = status;
            }

            public class DataBean {
                /**
                 * unbindNum : 18
                 * pubIdNum : 39527957
                 * userPubId : RBtXTiCExmRB7aIJorerUQ==
                 * systemDate : 1512353836048
                 * uniqueKey : 73fe126e6d295509a497106645bdc0e2
                 * currentDate : 2017-12-04 10:17:16
                 * user : {"phone":"158****9255","nickName":"158****9255","headportrait":"http://img7x3.ddimg.cn/imghead/24/32/6860168054313-1_e.png?1512353802843","introduct":"","vipType":"0","id":265078839,"displayId":6860168054313,"userName":"158****9255","email":"1582371925564776@ddmobilphone__user.com","registerDate":1511927104000}
                 * token : e_6b7179050f992a358865c7d0def27b5f12b6f7e14f799c9cac3902b356edd939
                 */

                private int unbindNum;
                private int pubIdNum;
                private String userPubId;
                private String systemDate;
                private String uniqueKey;
                private String currentDate;
                private UserBean user;
                private String token;

                public int getUnbindNum() {
                    return unbindNum;
                }

                public void setUnbindNum(int unbindNum) {
                    this.unbindNum = unbindNum;
                }

                public int getPubIdNum() {
                    return pubIdNum;
                }

                public void setPubIdNum(int pubIdNum) {
                    this.pubIdNum = pubIdNum;
                }

                public String getUserPubId() {
                    return userPubId;
                }

                public void setUserPubId(String userPubId) {
                    this.userPubId = userPubId;
                }

                public String getSystemDate() {
                    return systemDate;
                }

                public void setSystemDate(String systemDate) {
                    this.systemDate = systemDate;
                }

                public String getUniqueKey() {
                    return uniqueKey;
                }

                public void setUniqueKey(String uniqueKey) {
                    this.uniqueKey = uniqueKey;
                }

                public String getCurrentDate() {
                    return currentDate;
                }

                public void setCurrentDate(String currentDate) {
                    this.currentDate = currentDate;
                }

                public UserBean getUser() {
                    return user;
                }

                public void setUser(UserBean user) {
                    this.user = user;
                }

                public String getToken() {
                    return token;
                }

                public void setToken(String token) {
                    this.token = token;
                }

                public class UserBean {
                    /**
                     * phone : 158****9255
                     * nickName : 158****9255
                     * headportrait : http://img7x3.ddimg.cn/imghead/24/32/6860168054313-1_e.png?1512353802843
                     * introduct :
                     * vipType : 0
                     * id : 265078839
                     * displayId : 6860168054313
                     * userName : 158****9255
                     * email : 1582371925564776@ddmobilphone__user.com
                     * registerDate : 1511927104000
                     */

                    private String phone;
                    private String nickName;
                    private String headportrait;
                    private String introduct;
                    private String vipType;
                    private int id;
                    private long displayId;
                    private String userName;
                    private String email;
                    private long registerDate;

                    public String getPhone() {
                        return phone;
                    }

                    public void setPhone(String phone) {
                        this.phone = phone;
                    }

                    public String getNickName() {
                        return nickName;
                    }

                    public void setNickName(String nickName) {
                        this.nickName = nickName;
                    }

                    public String getHeadportrait() {
                        return headportrait;
                    }

                    public void setHeadportrait(String headportrait) {
                        this.headportrait = headportrait;
                    }

                    public String getIntroduct() {
                        return introduct;
                    }

                    public void setIntroduct(String introduct) {
                        this.introduct = introduct;
                    }

                    public String getVipType() {
                        return vipType;
                    }

                    public void setVipType(String vipType) {
                        this.vipType = vipType;
                    }

                    public int getId() {
                        return id;
                    }

                    public void setId(int id) {
                        this.id = id;
                    }

                    public long getDisplayId() {
                        return displayId;
                    }

                    public void setDisplayId(long displayId) {
                        this.displayId = displayId;
                    }

                    public String getUserName() {
                        return userName;
                    }

                    public void setUserName(String userName) {
                        this.userName = userName;
                    }

                    public String getEmail() {
                        return email;
                    }

                    public void setEmail(String email) {
                        this.email = email;
                    }

                    public long getRegisterDate() {
                        return registerDate;
                    }

                    public void setRegisterDate(long registerDate) {
                        this.registerDate = registerDate;
                    }
                }
            }

            public class StatusBean {
                /**
                 * code : 0
                 */
                private int code;

                public int getCode() {
                    return code;
                }

                public void setCode(int code) {
                    this.code = code;
                }
            }
        }

        public class MemberBean {
            /**
             * email :
             * grade :
             * headPortrait : 0
             * id : 1
             * mobile : 18607149219
             * name : 哈哈尔滨
             * password : e10adc3949ba59abbe56e057f20f883e
             * regTime : 2017-04-14 10:57:59
             * school :
             * sex : 2
             * state : 0
             * version : v3
             */

            private String email;
            private String grade;
            private int headPortrait;
            private int id;
            private String mobile;
            private String name;
            private String password;
            private String regTime;
            private String school;
            private int sex;
            private int state;
            private String version;

            public String getEmail() {
                return email;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public String getGrade() {
                return grade;
            }

            public void setGrade(String grade) {
                this.grade = grade;
            }

            public int getHeadPortrait() {
                return headPortrait;
            }

            public void setHeadPortrait(int headPortrait) {
                this.headPortrait = headPortrait;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getMobile() {
                return mobile;
            }

            public void setMobile(String mobile) {
                this.mobile = mobile;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getPassword() {
                return password;
            }

            public void setPassword(String password) {
                this.password = password;
            }

            public String getRegTime() {
                return regTime;
            }

            public void setRegTime(String regTime) {
                this.regTime = regTime;
            }

            public String getSchool() {
                return school;
            }

            public void setSchool(String school) {
                this.school = school;
            }

            public int getSex() {
                return sex;
            }

            public void setSex(int sex) {
                this.sex = sex;
            }

            public int getState() {
                return state;
            }

            public void setState(int state) {
                this.state = state;
            }

            public String getVersion() {
                return version;
            }

            public void setVersion(String version) {
                this.version = version;
            }
        }

        public class User {
            private String id;
            private String mobile;
            private String name;
            private String password;
            private String regTime;
            private String state;
            private String sex;
            private String email;

            public String getSex() {
                return sex;
            }

            public void setSex(String sex) {
                this.sex = sex;
            }

            public String getEmail() {
                return email;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getMobile() {
                return mobile;
            }

            public void setMobile(String mobile) {
                this.mobile = mobile;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getPassword() {
                return password;
            }

            public void setPassword(String password) {
                this.password = password;
            }

            public String getRegTime() {
                return regTime;
            }

            public void setRegTime(String regTime) {
                this.regTime = regTime;
            }

            public String getState() {
                return state;
            }

            public void setState(String state) {
                this.state = state;
            }
        }
    }
}
