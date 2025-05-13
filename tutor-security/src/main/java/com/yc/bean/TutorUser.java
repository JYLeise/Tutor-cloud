package com.yc.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

//PO类 ： 与数据表相关

@TableName("tutor_user")
@Data
public class TutorUser implements Serializable , UserDetails {

    @TableId(type = IdType.AUTO)
    @TableField("uid")
    private Integer uid;  //主键
    //@TableField("user_img")
    private String userImg;
    //@TableField("user_name")
    private String userName;
    //@TableField("user_id")
    private String userId;
   // @TableField("password")
    private String password;  //**** UserDetail接口要求密码必须是 password  而不是 pwd
    //@TableField("phone_number")
    private String phoneNumber;
    //@TableField("user_real_name")
    private String userRealName;
    //@TableField("user_sex")
    private String userSex;
    //@TableField("user_born_year")
    private Integer userBornYear;
    private String userIdCard;
    //@TableField("user_regional")
    private String userRegional;
   // @TableField("user_degree")
    private String userDegree;
    //@TableField("user_identity")
    private String userIdentity;
   // @TableField("user_email")
    private String userEmail;
    private String userTeachImg; //用户肖像照片(教师)
    private String userTeachWage; //用户薪酬(教师)
    private String userTeachGrade; //用户教授年级(教师)
    private String userTeachType; //用户教授类型(教师)
    private String userTeachWay; //用户教授方式(教师)
    private String userTeachMotto; //用户座右铭(教师)
    private String userRole; //用户角色

    @Override  //用户所拥有的权限  返回的列表中至少得有一个值  否则这个用户啥权限都没有
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(userRole));
    }

    @Override  //返回用户的密码
    public String getPassword() {
        return this.password;
    }
    @Override  //返回用户的用户名
    @JsonProperty("userName")  //返回到前端是驼峰命名
    public String getUsername() {
        return this.userName;
    }

    @Override  //账户是否未过期  返回 true 账户未过期
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override  // 返回 true  账户未被锁定
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override  // 密码是否未过期   返回 true 密码未过期
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override //账户是否可用    返回 true 账户可用
    public boolean isEnabled() {
        return true;
    }
}