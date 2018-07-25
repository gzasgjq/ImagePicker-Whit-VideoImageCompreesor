package com.gjq.demo.beans;

/**
 * 创建人：gjq-t239
 * 创建时间：2018/03/08 11:59
 * 类描述：${INTRO}
 * <p>
 * 修改人：
 * 修改时间：
 * 修改备注：
 */

class TeamMatchListBean {

    /**
     * guestId : 14244
     * guestName : 天津权健
     * guestScore : 4
     * halfScore : 0-1
     * handicap : 0.25
     * homeId : 1654
     * homeName : 河南建业
     * homeScore : 0
     * leagueMatchName : 中超
     * matchCode : 2941746
     * matchTime : 18-03-02
     * status : 0
     */

    private int guestId;
    private String guestName;
    private int guestScore;
    private String halfScore;
    private double handicap;
    private int homeId;
    private String homeName;
    private int homeScore;
    private String leagueMatchName;
    private int matchCode;
    private String matchTime;
    private int status;

    public int getGuestId() {
        return guestId;
    }

    public void setGuestId(int guestId) {
        this.guestId = guestId;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public int getGuestScore() {
        return guestScore;
    }

    public void setGuestScore(int guestScore) {
        this.guestScore = guestScore;
    }

    public String getHalfScore() {
        return halfScore;
    }

    public void setHalfScore(String halfScore) {
        this.halfScore = halfScore;
    }

    public double getHandicap() {
        return handicap;
    }

    public void setHandicap(double handicap) {
        this.handicap = handicap;
    }

    public int getHomeId() {
        return homeId;
    }

    public void setHomeId(int homeId) {
        this.homeId = homeId;
    }

    public String getHomeName() {
        return homeName;
    }

    public void setHomeName(String homeName) {
        this.homeName = homeName;
    }

    public int getHomeScore() {
        return homeScore;
    }

    public void setHomeScore(int homeScore) {
        this.homeScore = homeScore;
    }

    public String getLeagueMatchName() {
        return leagueMatchName;
    }

    public void setLeagueMatchName(String leagueMatchName) {
        this.leagueMatchName = leagueMatchName;
    }

    public int getMatchCode() {
        return matchCode;
    }

    public void setMatchCode(int matchCode) {
        this.matchCode = matchCode;
    }

    public String getMatchTime() {
        return matchTime;
    }

    public void setMatchTime(String matchTime) {
        this.matchTime = matchTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
