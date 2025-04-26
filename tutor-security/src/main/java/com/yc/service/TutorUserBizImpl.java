package com.yc.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yc.bean.TutorUser;
import com.yc.dao.TutorUserMapper;
import com.yc.web.model.TutorUserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Transactional(readOnly = true)
@Service
public class TutorUserBizImpl implements TutorUserBiz , UserDetailsService {

    private TutorUserMapper tutorUserMapper;
    @Autowired   //利用set装配注入可以防止 ， 循环依赖问题
    public void setResuserBiz(TutorUserMapper tutorUserMapper) {
        this.tutorUserMapper = tutorUserMapper;
    }

    // 自动注入  Spring Security中的PasswordEncoder接口来加密用户密码
    private PasswordEncoder passwordEncoder;
    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    // 注册
    @Transactional
    @Override
    public Integer regUser(TutorUserVO tutorUserVO) {
        //判断用户是否存在
        QueryWrapper<TutorUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", tutorUserVO.getUserId());
        List<TutorUser> list = tutorUserMapper.selectList( queryWrapper );
        if(list.size() > 0){
            throw new RuntimeException("用户已存在");
        }
        TutorUser tutorUser = new TutorUser();
        tutorUser.setUserImg(tutorUserVO.getUserImg());
        tutorUser.setUserName( tutorUserVO.getUserName() );
        tutorUser.setUserId(tutorUserVO.getUserId());
        //对明文密码进行加密
        tutorUser.setPassword( passwordEncoder.encode( tutorUserVO.getPassWord() ) );
        tutorUser.setPhoneNumber(tutorUserVO.getPhoneNumber());
        tutorUser.setUserRealName(tutorUserVO.getUserRealName());
        tutorUser.setUserSex(tutorUserVO.getUserSex());
        tutorUser.setUserBornYear(tutorUserVO.getUserBornYear());
        tutorUser.setUserRegional(tutorUserVO.getUserRegional());
        tutorUser.setUserDegree(tutorUserVO.getUserDegree());
        tutorUser.setUserIdentity(tutorUserVO.getUserIdentity());
        tutorUser.setUserEmail( tutorUserVO.getUserEmail() );
        tutorUser.setUserRole( tutorUserVO.getUserRole() );
        tutorUser.setUid(tutorUserMapper.insert( tutorUser ));
        return tutorUser.getUid();
    }

    //申请成为教师
    @Transactional
    @Override
    public void updateUserToTutor(TutorUserVO tutorUserVO){
        TutorUser tutorUser = new TutorUser();
        tutorUser.setUid(tutorUserVO.getUid());
        tutorUser.setUserRealName(tutorUserVO.getUserRealName());
        tutorUser.setUserSex(tutorUserVO.getUserSex());
        tutorUser.setUserBornYear(tutorUserVO.getUserBornYear());
        tutorUser.setUserIdCard(tutorUserVO.getUserIdCard());
        tutorUser.setUserRegional(tutorUserVO.getUserRegional());
        tutorUser.setUserDegree(tutorUserVO.getUserDegree());
        tutorUser.setUserIdentity(tutorUserVO.getUserIdentity());
        tutorUser.setUserTeachImg(tutorUserVO.getUserTeachImg());
        tutorUser.setUserTeachWage(tutorUserVO.getUserTeachWage());
        tutorUser.setUserTeachGrade(tutorUserVO.getUserTeachGrade());
        tutorUser.setUserTeachType(tutorUserVO.getUserTeachType());
        tutorUser.setUserTeachWay(tutorUserVO.getUserTeachWay());
        tutorUser.setUserTeachMotto(tutorUserVO.getUserTeachMotto());
        tutorUser.setUserRole(tutorUserVO.getUserRole());
        tutorUserMapper.updateUserToTutor(tutorUser);
    }

    //用于从数据库中加载用户信息
    //loadUserByUsername(String username) 是 UserDetailsService 接口中的一个方法，Spring Security 在用户认证过程中会调用它
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        LambdaQueryWrapper<TutorUser> queryWrapper = new LambdaQueryWrapper<>();
        // queryWrapper.eq("username", resuserVO.getUsername());
        // 避免硬编码 Resuser::getUsername
        queryWrapper.eq( TutorUser::getUserId, userId );  //where username ='a'
        try {
            //这是一个userDetails对象
            // **** select * from resuser where username = ?  这里要确保 用户名唯一
            TutorUser user = tutorUserMapper.selectOne( queryWrapper );
            return user;
        }catch (Exception e){
            log.error("userid not found");
            return null;
        }

    }
}