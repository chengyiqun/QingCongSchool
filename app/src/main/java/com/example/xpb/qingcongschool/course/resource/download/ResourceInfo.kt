package com.example.xpb.qingcongschool.course.resource.download

import android.support.annotation.Keep

/**
 * Created by lenovo on 2017/10/27 0027.
 */
@Keep
class ResourceInfo {
    var fileName = ""//文件名
    var resourceStoreName = ""//存储在服务器的文件名
    var downloadTimes = 0//下载次数
    var resourceDescribe = ""//资源描述
    //private int comprehensiveScore=0;//资源评分。。。。。。。。。暂时未实现

    var commentTimes = 0//评论次数
    var uploadTime = ""//上传时间日期


    override fun toString(): String {
        return "ResourceInfo{" +
                "fileName='" + fileName + '\'' +
                ", resourceStoreName='" + resourceStoreName + '\'' +
                ", downloadTimes=" + downloadTimes +
                ", resourceDescribe='" + resourceDescribe + '\'' +
                ", commentTimes=" + commentTimes +
                ", uploadTime='" + uploadTime + '\'' +
                '}'
    }
}
