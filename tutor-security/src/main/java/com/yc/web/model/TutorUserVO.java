package com.yc.web.model;

import lombok.Data;

@Data
public class TutorUserVO {
    private Integer uid; //主键
    private String userImg;  //用户头像
    private String userName; //用户名
    private String userId;   //用户账号
    private String passWord; //用户密码
    private String phoneNumber;  //手机号
    private String userRealName;  //真实姓名
    private String userSex; //用户性别
    private Integer userBornYear; //用户出生年份
    private String userRegional; //用户地域 省,市,区
    private String userDegree; //用户学历
    private String userIdentity;;  //用户身份
    private String userEmail;  //用户邮箱
}
