打包更改配置流程：
1、检测输出日志是否关闭
2、查看底层版本号配置
3、修改启动哪个MainActivity
4、去掉系统权限申明 manifest里面

3.1:build.gradle相关参数说明
  端               海尔C端                红军小学版本
版本号               202                    110
 版本          MXB970S_B2_V2.0.2       MXB970S_B3_V1.1.0
 包名          com.moxi.haierc          com.moxi.hjschool
 底层版本             9                        2

 //底层版本存放位置 com.mx.mxbase.constant LOCAL_VIR_VERSION

 5，墨希红军小学版本，更改设置界面中 logo （mx_img_logo_2.png->hj_img_logo.png）和名称(TOPSIR S201->B970SX );更改说明书（mx_instruction_20170925.pdf）。
 6,更改版本号显示为本地版本号显示。（去掉判断）。
 7，更改版本号：versionName "MXB970S_B3_V1.1.1"-》versionName "MXB970S_B4_V1.1.1"
 8,包名：com.moxi.hjschoolm  （墨希版本）
 9,红军版本还要修改user的启动包名
