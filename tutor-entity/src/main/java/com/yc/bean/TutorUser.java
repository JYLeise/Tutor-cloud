package com.yc.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TutorUser {
    @TableId(type= IdType.AUTO)
    private Integer uid; //用户序号（主键自增）
    private String userImg;  //用户头像
    private String userName; //用户名
    private String userId;   //用户账号
    private String passWord; //用户密码
    private String phoneNumber;  //手机号
    private String userRealName;  //真实姓名
    private String userSex; //用户性别
    private Integer userBornYear; //用户出生年份
    private String userIdCard; //用户身份证号
    private String userRegional; //用户地域 省,市,区
    private String userDegree; //用户学历
    private String userIdentity;;  //用户身份
    private String userEmail;  //用户邮箱
    private String userTeachImg; //用户肖像照片(教师)
    private String userTeachWage; //用户薪酬(教师)
    private String userTeachGrade; //用户教授年级(教师)
    private String userTeachType; //用户教授类型(教师)
    private String userTeachWay; //用户教授方式(教师)
    private String userTeachMotto; //用户座右铭(教师)
    private String userRole; //用户角色
}
