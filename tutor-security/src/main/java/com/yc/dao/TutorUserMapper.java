package com.yc.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yc.bean.TutorUser;
import com.yc.web.model.TutorUserVO;
import org.apache.ibatis.annotations.Param;


public interface TutorUserMapper extends BaseMapper<TutorUser> {
    public Integer regUser(TutorUserVO tutorUserVO);
    public void updateUserToTutor(@Param("tutorUser")TutorUser tutorUser);
}
