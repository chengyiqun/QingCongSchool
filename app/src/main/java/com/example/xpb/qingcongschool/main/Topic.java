package com.example.xpb.qingcongschool.main;

import java.io.Serializable;
import java.util.List;

public class Topic {

    /**
     * browseTimes : 0
     * commentTimes : 0
     * content : 测试测试。
     * dontMaskStranger : 0
     * likeTimes : 0
     * shareTimes : 0
     * status : 0
     * subjectID :
     * topicCommentList : [{"commentID":"186e8a35c1f047b8a065ed2eb95aabff","commentTime":"2018-05-27 05:34:13","commentType":0,"likeTimes":0,"replyTimes":0,"score":0,"shareTimes":0,"status":0,"topicID":"086e8a35c1f047b8a065ed2eb95aabff","userID":"00b18f51a2434a27913d9b03c2d02011","userName":"测试"}]
     * topicID : 086e8a35c1f047b8a065ed2eb95aabff
     * topicImageList : [{"imagePath":"topicPicture2018-05-27e892f95eb7ad45c48572ae10ff70eea1.png","imageType":0,"picOrder":0,"status":0,"topicID":"086e8a35c1f047b8a065ed2eb95aabff"},{"imagePath":"topicPicture2018-05-270e95ecc297464b7b86f3bde44f1be3ab.jpg","imageType":0,"picOrder":0,"status":0,"topicID":"086e8a35c1f047b8a065ed2eb95aabff"}]
     * topicTime : 2018-05-27 13:02:38
     * userID : 67b18f51a2434a27913d9b03c2d0201d
     * userName : 程义群
     */

    private int browseTimes;
    private int commentTimes;
    private String content;
    private int dontMaskStranger;
    private int likeTimes;
    private int shareTimes;
    private int status;
    private String subjectID;
    private String topicID;
    private String topicTime;
    private String userID;
    private String userName;
    private String avatarStoreName;
    private List<TopicCommentListBean> topicCommentList;
    private List<TopicImageListBean> topicImageList;

    public int getBrowseTimes() {
        return browseTimes;
    }

    public void setBrowseTimes(int browseTimes) {
        this.browseTimes = browseTimes;
    }

    public int getCommentTimes() {
        return commentTimes;
    }

    public void setCommentTimes(int commentTimes) {
        this.commentTimes = commentTimes;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getDontMaskStranger() {
        return dontMaskStranger;
    }

    public void setDontMaskStranger(int dontMaskStranger) {
        this.dontMaskStranger = dontMaskStranger;
    }

    public int getLikeTimes() {
        return likeTimes;
    }

    public void setLikeTimes(int likeTimes) {
        this.likeTimes = likeTimes;
    }

    public int getShareTimes() {
        return shareTimes;
    }

    public void setShareTimes(int shareTimes) {
        this.shareTimes = shareTimes;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getSubjectID() {
        return subjectID;
    }

    public void setSubjectID(String subjectID) {
        this.subjectID = subjectID;
    }

    public String getTopicID() {
        return topicID;
    }

    public void setTopicID(String topicID) {
        this.topicID = topicID;
    }

    public String getTopicTime() {
        return topicTime;
    }

    public void setTopicTime(String topicTime) {
        this.topicTime = topicTime;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAvatarStoreName() {
        return avatarStoreName;
    }

    public void setAvatarStoreName(String avatarStoreName) {
        this.avatarStoreName = avatarStoreName;
    }

    public List<TopicCommentListBean> getTopicCommentList() {
        return topicCommentList;
    }

    public void setTopicCommentList(List<TopicCommentListBean> topicCommentList) {
        this.topicCommentList = topicCommentList;
    }

    public List<TopicImageListBean> getTopicImageList() {
        return topicImageList;
    }

    public void setTopicImageList(List<TopicImageListBean> topicImageList) {
        this.topicImageList = topicImageList;
    }

    public static class TopicCommentListBean implements Serializable{
        @Override
        public String toString() {
            return "TopicCommentListBean{" +
                    "commentID='" + commentID + '\'' +
                    ", commentTime='" + commentTime + '\'' +
                    ", commentType=" + commentType +
                    ", likeTimes=" + likeTimes +
                    ", replyTimes=" + replyTimes +
                    ", score=" + score +
                    ", content='" + content + '\'' +
                    ", shareTimes=" + shareTimes +
                    ", status=" + status +
                    ", topicID='" + topicID + '\'' +
                    ", userID='" + userID + '\'' +
                    ", avatarStoreName='" + avatarStoreName + '\'' +
                    ", userName='" + userName + '\'' +
                    '}';
        }

        /**
         * commentID : 186e8a35c1f047b8a065ed2eb95aabff
         * commentTime : 2018-05-27 05:34:13
         * commentType : 0
         * likeTimes : 0
         * replyTimes : 0
         * score : 0
         * shareTimes : 0
         * status : 0
         * topicID : 086e8a35c1f047b8a065ed2eb95aabff
         * userID : 00b18f51a2434a27913d9b03c2d02011
         * userName : 测试
         */

        private String commentID;
        private String commentTime;
        private int commentType;
        private int likeTimes;
        private int replyTimes;
        private int score;
        private String content;
        private int shareTimes;
        private int status;
        private String topicID;
        private String userID;
        private String avatarStoreName;
        private String userName;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getAvatarStoreName() {
            return avatarStoreName;
        }

        public void setAvatarStoreName(String avatarStoreName) {
            this.avatarStoreName = avatarStoreName;
        }

        public String getCommentID() {
            return commentID;
        }

        public void setCommentID(String commentID) {
            this.commentID = commentID;
        }

        public String getCommentTime() {
            return commentTime;
        }

        public void setCommentTime(String commentTime) {
            this.commentTime = commentTime;
        }

        public int getCommentType() {
            return commentType;
        }

        public void setCommentType(int commentType) {
            this.commentType = commentType;
        }

        public int getLikeTimes() {
            return likeTimes;
        }

        public void setLikeTimes(int likeTimes) {
            this.likeTimes = likeTimes;
        }

        public int getReplyTimes() {
            return replyTimes;
        }

        public void setReplyTimes(int replyTimes) {
            this.replyTimes = replyTimes;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public int getShareTimes() {
            return shareTimes;
        }

        public void setShareTimes(int shareTimes) {
            this.shareTimes = shareTimes;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getTopicID() {
            return topicID;
        }

        public void setTopicID(String topicID) {
            this.topicID = topicID;
        }

        public String getUserID() {
            return userID;
        }

        public void setUserID(String userID) {
            this.userID = userID;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
    }

    public static class TopicImageListBean {
        @Override
        public String toString() {
            return "TopicImageListBean{" +
                    "imagePath='" + imagePath + '\'' +
                    ", imageType=" + imageType +
                    ", picOrder=" + picOrder +
                    ", status=" + status +
                    ", topicID='" + topicID + '\'' +
                    '}';
        }

        /**
         * imagePath : topicPicture2018-05-27e892f95eb7ad45c48572ae10ff70eea1.png
         * imageType : 0
         * picOrder : 0
         * status : 0
         * topicID : 086e8a35c1f047b8a065ed2eb95aabff
         */

        private String imagePath;
        private int imageType;
        private int picOrder;
        private int status;
        private String topicID;

        public String getImagePath() {
            return imagePath;
        }

        public void setImagePath(String imagePath) {
            this.imagePath = imagePath;
        }

        public int getImageType() {
            return imageType;
        }

        public void setImageType(int imageType) {
            this.imageType = imageType;
        }

        public int getPicOrder() {
            return picOrder;
        }

        public void setPicOrder(int picOrder) {
            this.picOrder = picOrder;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getTopicID() {
            return topicID;
        }

        public void setTopicID(String topicID) {
            this.topicID = topicID;
        }
    }

    @Override
    public String toString() {
        return "Topic{" +
                "browseTimes=" + browseTimes +
                ", commentTimes=" + commentTimes +
                ", content='" + content + '\'' +
                ", dontMaskStranger=" + dontMaskStranger +
                ", likeTimes=" + likeTimes +
                ", shareTimes=" + shareTimes +
                ", status=" + status +
                ", subjectID='" + subjectID + '\'' +
                ", topicID='" + topicID + '\'' +
                ", topicTime='" + topicTime + '\'' +
                ", userID='" + userID + '\'' +
                ", userName='" + userName + '\'' +
                ", avatarStoreName='" + avatarStoreName + '\'' +
                ", topicCommentList=" + topicCommentList.toString() +
                ", topicImageList=" + topicImageList.toString() +
                '}';
    }
}
