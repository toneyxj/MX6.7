打包更改配置流程：
1、检查注册界面接收验证码需要的包名 source传递值不一样
2、修改constant里面的参数配置（包括请求host_url，注册接口地址切换，验证码端区分，用户登录）
3、修改build.gradle配置文件里面的报名对应相关信息
4、打包除了海尔端的时候记得隐藏用户使用协议
5、修改老师学生区别
6、修改区别端的值 Constant.CODE_CLIENT的值


3.1:build.gradle相关参数说明
  端               墨希                海尔                 b端          b端老师    b端学生
版本号               6                  16                  1             2        2
 版本              1.0.5              1.1.5               1.0.1         1.0.1   1.0.1
 包名          com.moxi.mxuser      com.moxi.user     com.moxi.buser    tuser    suser