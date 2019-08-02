package top.takefly.core.pojo.log;

import java.util.Date;

/**
 * visitTime timestamp,
 * username VARCHAR2(50),
 * ip VARCHAR2(30),
 * url VARCHAR2(50),
 * executionTime int,
 * method VARCHAR2(200)
 */

public class SysLog {
    private String id;
    private Date visitTime;
    private String username;
    private String ip;
    private String url;
    private int executionTime;
    private String visitTimeStr;
    private String method;

    public Date getVisitTime() {
        return visitTime;
    }

    public void setVisitTime(Date visitTime) {
        this.visitTime = visitTime;
    }

    public String getVisitTimeStr() {
        if(visitTime!=null){
            visitTimeStr = visitTime.toString();
        }
        return visitTimeStr;
    }

    public void setVisitTimeStr(String visitTimeStr) {
        this.visitTimeStr = visitTimeStr;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(int executionTime) {
        this.executionTime = executionTime;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public String toString() {
        return "SysLog{" +
                "id='" + id + '\'' +
                ", visitTime=" + visitTime +
                ", username='" + username + '\'' +
                ", ip='" + ip + '\'' +
                ", url='" + url + '\'' +
                ", executionTime=" + executionTime +
                ", visitTimeStr='" + visitTimeStr + '\'' +
                ", method='" + method + '\'' +
                '}';
    }
}
