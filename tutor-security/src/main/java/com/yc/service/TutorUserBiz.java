package com.yc.service;

import com.yc.web.model.TutorUserVO;

public interface TutorUserBiz {

    //ResuserVO是前端传给后端的数据
    public Integer regUser(TutorUserVO tutorUserVO);

    public void updateUserToTutor(TutorUserVO tutorUserVO);
}
