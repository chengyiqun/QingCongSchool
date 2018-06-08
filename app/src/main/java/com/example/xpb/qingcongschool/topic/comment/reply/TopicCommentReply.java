package com.example.xpb.qingcongschool.topic.comment.reply;

public class TopicCommentReply {
    String userID  ;
    String avatar_store_name;
    String topicID  ;
    String commentTime;
    String commentID ;
    String  userName;   //返回评论用户的名字
    String  toUserName;   //返回被评论用户的名字
    String rootComment ;//comment_type=1时有效
    String  toUserID ;
    int likeTimes   ;
    int replyTimes ;
    int shareTimes  ;
    String    content  ;
    String toContent;

    @Override
    public String toString() {
        return "TopicCommentReply{" +
                "userID='" + userID + '\'' +
                ", avatar_store_name='" + avatar_store_name + '\'' +
                ", topicID='" + topicID + '\'' +
                ", commentTime='" + commentTime + '\'' +
                ", commentID='" + commentID + '\'' +
                ", userName='" + userName + '\'' +
                ", toUserName='" + toUserName + '\'' +
                ", rootComment='" + rootComment + '\'' +
                ", toUserID='" + toUserID + '\'' +
                ", likeTimes=" + likeTimes +
                ", replyTimes=" + replyTimes +
                ", shareTimes=" + shareTimes +
                ", content='" + content + '\'' +
                ", toContent='" + toContent + '\'' +
                '}';
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getAvatar_store_name() {
        return avatar_store_name;
    }

    public void setAvatar_store_name(String avatar_store_name) {
        this.avatar_store_name = avatar_store_name;
    }

    public String getTopicID() {
        return topicID;
    }

    public void setTopicID(String topicID) {
        this.topicID = topicID;
    }

    public String getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    public String getRootComment() {
        return rootComment;
    }

    public void setRootComment(String rootComment) {
        this.rootComment = rootComment;
    }

    public String getToUserID() {
        return toUserID;
    }

    public void setToUserID(String toUserID) {
        this.toUserID = toUserID;
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

    public int getShareTimes() {
        return shareTimes;
    }

    public void setShareTimes(int shareTimes) {
        this.shareTimes = shareTimes;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getToContent() {
        return toContent;
    }

    public void setToContent(String toContent) {
        this.toContent = toContent;
    }
}
