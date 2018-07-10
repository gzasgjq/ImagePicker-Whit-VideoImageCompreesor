package com.dinpay.trip.testdemo.beans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 创建人：gjq-t239
 * 创建时间：2018/03/08 11:54
 * 类描述：${INTRO}
 * <p>
 * 修改人：
 * 修改时间：
 * 修改备注：
 */

public class BeanModel {

    /**
     * dishroad : {"guestMatchInfo":{"allMatchList":[{"guestId":14244,"guestName":"天津权健","guestScore":4,"halfScore":"0-1","handicap":0.25,"homeId":1654,"homeName":"河南建业","homeScore":0,"leagueMatchName":"中超","matchCode":2941746,"matchTime":"18-03-02","status":0},{"guestId":14244,"guestName":"天津权健","guestScore":1,"halfScore":"0-0","handicap":-0.5,"homeId":807,"homeName":"柏太阳神","homeScore":1,"leagueMatchName":"亚冠","matchCode":2938148,"matchTime":"18-02-20","status":0},{"guestId":7670,"guestName":"香港杰志","guestScore":0,"halfScore":"3-0","handicap":-2,"homeId":14244,"homeName":"天津权健","homeScore":3,"leagueMatchName":"亚冠","matchCode":2938146,"matchTime":"18-02-13","status":0},{"guestId":24668,"guestName":"塞列斯","guestScore":0,"halfScore":"1-0","handicap":-2,"homeId":14244,"homeName":"天津权健","homeScore":2,"leagueMatchName":"亚冠","matchCode":2934320,"matchTime":"18-01-30","status":2},{"guestId":14244,"guestName":"天津权健","guestScore":1,"halfScore":"1-0","handicap":-1.25,"homeId":1275,"homeName":"莫火车头","homeScore":1,"leagueMatchName":"俱乐部赛","matchCode":2933394,"matchTime":"18-01-23","status":0},{"guestId":14244,"guestName":"天津权健","guestScore":2,"halfScore":"2-2","handicap":-1.25,"homeId":1287,"homeName":"默德林","homeScore":3,"leagueMatchName":"俱乐部赛","matchCode":2933131,"matchTime":"18-01-20","status":0},{"guestId":14244,"guestName":"天津权健","guestScore":1,"halfScore":"3-0","handicap":-1.25,"homeId":769,"homeName":"布斯巴达","homeScore":4,"leagueMatchName":"俱乐部赛","matchCode":2930618,"matchTime":"18-01-17","status":1},{"guestId":14244,"guestName":"天津权健","guestScore":1,"halfScore":"1-1","handicap":-2.25,"homeId":979,"homeName":"巴塞尔","homeScore":1,"leagueMatchName":"俱乐部赛","matchCode":2930778,"matchTime":"18-01-14","status":0},{"guestId":14244,"guestName":"天津权健","guestScore":0,"halfScore":"1-0","handicap":-1.25,"homeId":189,"homeName":"圣保利","homeScore":1,"leagueMatchName":"俱乐部赛","matchCode":2930296,"matchTime":"18-01-11","status":0},{"guestId":14244,"guestName":"天津权健","guestScore":2,"halfScore":"0-1","handicap":0.5,"homeId":1603,"homeName":"广州恒大","homeScore":1,"leagueMatchName":"中超","matchCode":1701805,"matchTime":"17-11-04","status":0}],"teamId":14244,"teamMatchList":[{"guestId":14244,"guestName":"天津权健","guestScore":4,"halfScore":"0-1","handicap":0.25,"homeId":1654,"homeName":"河南建业","homeScore":0,"leagueMatchName":"中超","matchCode":2941746,"matchTime":"18-03-02","status":0},{"guestId":14244,"guestName":"天津权健","guestScore":1,"halfScore":"0-0","handicap":-0.5,"homeId":807,"homeName":"柏太阳神","homeScore":1,"leagueMatchName":"亚冠","matchCode":2938148,"matchTime":"18-02-20","status":0},{"guestId":14244,"guestName":"天津权健","guestScore":1,"halfScore":"1-0","handicap":-1.25,"homeId":1275,"homeName":"莫火车头","homeScore":1,"leagueMatchName":"俱乐部赛","matchCode":2933394,"matchTime":"18-01-23","status":0},{"guestId":14244,"guestName":"天津权健","guestScore":2,"halfScore":"2-2","handicap":-1.25,"homeId":1287,"homeName":"默德林","homeScore":3,"leagueMatchName":"俱乐部赛","matchCode":2933131,"matchTime":"18-01-20","status":0},{"guestId":14244,"guestName":"天津权健","guestScore":1,"halfScore":"3-0","handicap":-1.25,"homeId":769,"homeName":"布斯巴达","homeScore":4,"leagueMatchName":"俱乐部赛","matchCode":2930618,"matchTime":"18-01-17","status":1},{"guestId":14244,"guestName":"天津权健","guestScore":1,"halfScore":"1-1","handicap":-2.25,"homeId":979,"homeName":"巴塞尔","homeScore":1,"leagueMatchName":"俱乐部赛","matchCode":2930778,"matchTime":"18-01-14","status":0},{"guestId":14244,"guestName":"天津权健","guestScore":0,"halfScore":"1-0","handicap":-1.25,"homeId":189,"homeName":"圣保利","homeScore":1,"leagueMatchName":"俱乐部赛","matchCode":2930296,"matchTime":"18-01-11","status":0},{"guestId":14244,"guestName":"天津权健","guestScore":2,"halfScore":"0-1","handicap":0.5,"homeId":1603,"homeName":"广州恒大","homeScore":1,"leagueMatchName":"中超","matchCode":1701805,"matchTime":"17-11-04","status":0},{"guestId":14244,"guestName":"天津权健","guestScore":0,"halfScore":"1-0","handicap":0,"homeId":1637,"homeName":"江苏苏宁","homeScore":1,"leagueMatchName":"中超","matchCode":1701737,"matchTime":"17-09-28","status":1},{"guestId":14244,"guestName":"天津权健","guestScore":1,"halfScore":"2-0","handicap":0.5,"homeId":12,"homeName":"天津泰达","homeScore":4,"leagueMatchName":"中超","matchCode":1701772,"matchTime":"17-09-23","status":1}],"teamName":"天津权健"},"homeMatchInfo":{"allMatchList":[{"guestId":3248,"guestName":"蔚山现代","guestScore":0,"halfScore":"0-0","handicap":-0.75,"homeId":6917,"homeName":"全北现代","homeScore":2,"leagueMatchName":"韩职","matchCode":2940400,"matchTime":"18-03-01","status":0},{"guestId":6917,"guestName":"全北现代","guestScore":6,"halfScore":"0-5","handicap":1.5,"homeId":7670,"homeName":"香港杰志","homeScore":0,"leagueMatchName":"亚冠","matchCode":2938147,"matchTime":"18-02-20","status":0},{"guestId":807,"guestName":"柏太阳神","guestScore":2,"halfScore":"0-2","handicap":-0.75,"homeId":6917,"homeName":"全北现代","homeScore":3,"leagueMatchName":"亚冠","matchCode":2938145,"matchTime":"18-02-13","status":0},{"guestId":3177,"guestName":"水原三星","guestScore":3,"halfScore":"2-1","handicap":-0.25,"homeId":6917,"homeName":"全北现代","homeScore":2,"leagueMatchName":"韩职","matchCode":2915284,"matchTime":"17-11-19","status":1},{"guestId":6917,"guestName":"全北现代","guestScore":2,"halfScore":"0-0","handicap":0,"homeId":3248,"homeName":"蔚山现代","homeScore":1,"leagueMatchName":"韩职","matchCode":2915281,"matchTime":"17-11-05","status":0},{"guestId":13,"guestName":"济州联","guestScore":0,"halfScore":"0-0","handicap":-0.5,"homeId":6917,"homeName":"全北现代","homeScore":3,"leagueMatchName":"韩职","matchCode":2914974,"matchTime":"17-10-29","status":0},{"guestId":6917,"guestName":"全北现代","guestScore":4,"halfScore":"0-1","handicap":0.75,"homeId":14553,"homeName":"江原FC","homeScore":0,"leagueMatchName":"韩职","matchCode":2914971,"matchTime":"17-10-22","status":0},{"guestId":3161,"guestName":"首尔FC","guestScore":0,"halfScore":"0-0","handicap":-0.5,"homeId":6917,"homeName":"全北现代","homeScore":0,"leagueMatchName":"韩职","matchCode":2914968,"matchTime":"17-10-15","status":1},{"guestId":6917,"guestName":"全北现代","guestScore":1,"halfScore":"0-0","handicap":0.25,"homeId":13,"homeName":"济州联","homeScore":0,"leagueMatchName":"韩职","matchCode":1705515,"matchTime":"17-10-08","status":0},{"guestId":6917,"guestName":"全北现代","guestScore":1,"halfScore":"1-0","handicap":0.25,"homeId":3177,"homeName":"水原三星","homeScore":1,"leagueMatchName":"韩职","matchCode":1705547,"matchTime":"17-10-01","status":1}],"beyond":false,"continuousSpieltag":4,"continuousType":0,"historicHigh":4,"teamId":6917,"teamMatchList":[{"guestId":3248,"guestName":"蔚山现代","guestScore":0,"halfScore":"0-0","handicap":-0.75,"homeId":6917,"homeName":"全北现代","homeScore":2,"leagueMatchName":"韩职","matchCode":2940400,"matchTime":"18-03-01","status":0},{"guestId":807,"guestName":"柏太阳神","guestScore":2,"halfScore":"0-2","handicap":-0.75,"homeId":6917,"homeName":"全北现代","homeScore":3,"leagueMatchName":"亚冠","matchCode":2938145,"matchTime":"18-02-13","status":0},{"guestId":3177,"guestName":"水原三星","guestScore":3,"halfScore":"2-1","handicap":-0.25,"homeId":6917,"homeName":"全北现代","homeScore":2,"leagueMatchName":"韩职","matchCode":2915284,"matchTime":"17-11-19","status":1},{"guestId":13,"guestName":"济州联","guestScore":0,"halfScore":"0-0","handicap":-0.5,"homeId":6917,"homeName":"全北现代","homeScore":3,"leagueMatchName":"韩职","matchCode":2914974,"matchTime":"17-10-29","status":0},{"guestId":3161,"guestName":"首尔FC","guestScore":0,"halfScore":"0-0","handicap":-0.5,"homeId":6917,"homeName":"全北现代","homeScore":0,"leagueMatchName":"韩职","matchCode":2914968,"matchTime":"17-10-15","status":1},{"guestId":3165,"guestName":"大邱FC","guestScore":1,"halfScore":"1-1","handicap":-1.25,"homeId":6917,"homeName":"全北现代","homeScore":1,"leagueMatchName":"韩职","matchCode":1705540,"matchTime":"17-09-24","status":1},{"guestId":3163,"guestName":"尚州尚武","guestScore":2,"halfScore":"1-0","handicap":-1.5,"homeId":6917,"homeName":"全北现代","homeScore":1,"leagueMatchName":"韩职","matchCode":1705531,"matchTime":"17-09-20","status":1},{"guestId":14553,"guestName":"江原FC","guestScore":3,"halfScore":"4-1","handicap":-1,"homeId":6917,"homeName":"全北现代","homeScore":4,"leagueMatchName":"韩职","matchCode":1705518,"matchTime":"17-09-10","status":2},{"guestId":20086,"guestName":"光州FC","guestScore":1,"halfScore":"1-1","handicap":-1.5,"homeId":6917,"homeName":"全北现代","homeScore":3,"leagueMatchName":"韩职","matchCode":1705507,"matchTime":"17-08-19","status":0},{"guestId":3248,"guestName":"蔚山现代","guestScore":1,"halfScore":"0-0","handicap":-0.75,"homeId":6917,"homeName":"全北现代","homeScore":0,"leagueMatchName":"韩职","matchCode":1705497,"matchTime":"17-08-06","status":1}],"teamName":"全北现代"}}
     */

    private DishroadBean dishroad;

    public DishroadBean getDishroad() {
        return dishroad;
    }

    public void setDishroad(DishroadBean dishroad) {
        this.dishroad = dishroad;
    }

    public static class DishroadBean {
        /**
         * guestMatchInfo : {"allMatchList":[{"guestId":14244,"guestName":"天津权健","guestScore":4,"halfScore":"0-1","handicap":0.25,"homeId":1654,"homeName":"河南建业","homeScore":0,"leagueMatchName":"中超","matchCode":2941746,"matchTime":"18-03-02","status":0},{"guestId":14244,"guestName":"天津权健","guestScore":1,"halfScore":"0-0","handicap":-0.5,"homeId":807,"homeName":"柏太阳神","homeScore":1,"leagueMatchName":"亚冠","matchCode":2938148,"matchTime":"18-02-20","status":0},{"guestId":7670,"guestName":"香港杰志","guestScore":0,"halfScore":"3-0","handicap":-2,"homeId":14244,"homeName":"天津权健","homeScore":3,"leagueMatchName":"亚冠","matchCode":2938146,"matchTime":"18-02-13","status":0},{"guestId":24668,"guestName":"塞列斯","guestScore":0,"halfScore":"1-0","handicap":-2,"homeId":14244,"homeName":"天津权健","homeScore":2,"leagueMatchName":"亚冠","matchCode":2934320,"matchTime":"18-01-30","status":2},{"guestId":14244,"guestName":"天津权健","guestScore":1,"halfScore":"1-0","handicap":-1.25,"homeId":1275,"homeName":"莫火车头","homeScore":1,"leagueMatchName":"俱乐部赛","matchCode":2933394,"matchTime":"18-01-23","status":0},{"guestId":14244,"guestName":"天津权健","guestScore":2,"halfScore":"2-2","handicap":-1.25,"homeId":1287,"homeName":"默德林","homeScore":3,"leagueMatchName":"俱乐部赛","matchCode":2933131,"matchTime":"18-01-20","status":0},{"guestId":14244,"guestName":"天津权健","guestScore":1,"halfScore":"3-0","handicap":-1.25,"homeId":769,"homeName":"布斯巴达","homeScore":4,"leagueMatchName":"俱乐部赛","matchCode":2930618,"matchTime":"18-01-17","status":1},{"guestId":14244,"guestName":"天津权健","guestScore":1,"halfScore":"1-1","handicap":-2.25,"homeId":979,"homeName":"巴塞尔","homeScore":1,"leagueMatchName":"俱乐部赛","matchCode":2930778,"matchTime":"18-01-14","status":0},{"guestId":14244,"guestName":"天津权健","guestScore":0,"halfScore":"1-0","handicap":-1.25,"homeId":189,"homeName":"圣保利","homeScore":1,"leagueMatchName":"俱乐部赛","matchCode":2930296,"matchTime":"18-01-11","status":0},{"guestId":14244,"guestName":"天津权健","guestScore":2,"halfScore":"0-1","handicap":0.5,"homeId":1603,"homeName":"广州恒大","homeScore":1,"leagueMatchName":"中超","matchCode":1701805,"matchTime":"17-11-04","status":0}],"teamId":14244,"teamMatchList":[{"guestId":14244,"guestName":"天津权健","guestScore":4,"halfScore":"0-1","handicap":0.25,"homeId":1654,"homeName":"河南建业","homeScore":0,"leagueMatchName":"中超","matchCode":2941746,"matchTime":"18-03-02","status":0},{"guestId":14244,"guestName":"天津权健","guestScore":1,"halfScore":"0-0","handicap":-0.5,"homeId":807,"homeName":"柏太阳神","homeScore":1,"leagueMatchName":"亚冠","matchCode":2938148,"matchTime":"18-02-20","status":0},{"guestId":14244,"guestName":"天津权健","guestScore":1,"halfScore":"1-0","handicap":-1.25,"homeId":1275,"homeName":"莫火车头","homeScore":1,"leagueMatchName":"俱乐部赛","matchCode":2933394,"matchTime":"18-01-23","status":0},{"guestId":14244,"guestName":"天津权健","guestScore":2,"halfScore":"2-2","handicap":-1.25,"homeId":1287,"homeName":"默德林","homeScore":3,"leagueMatchName":"俱乐部赛","matchCode":2933131,"matchTime":"18-01-20","status":0},{"guestId":14244,"guestName":"天津权健","guestScore":1,"halfScore":"3-0","handicap":-1.25,"homeId":769,"homeName":"布斯巴达","homeScore":4,"leagueMatchName":"俱乐部赛","matchCode":2930618,"matchTime":"18-01-17","status":1},{"guestId":14244,"guestName":"天津权健","guestScore":1,"halfScore":"1-1","handicap":-2.25,"homeId":979,"homeName":"巴塞尔","homeScore":1,"leagueMatchName":"俱乐部赛","matchCode":2930778,"matchTime":"18-01-14","status":0},{"guestId":14244,"guestName":"天津权健","guestScore":0,"halfScore":"1-0","handicap":-1.25,"homeId":189,"homeName":"圣保利","homeScore":1,"leagueMatchName":"俱乐部赛","matchCode":2930296,"matchTime":"18-01-11","status":0},{"guestId":14244,"guestName":"天津权健","guestScore":2,"halfScore":"0-1","handicap":0.5,"homeId":1603,"homeName":"广州恒大","homeScore":1,"leagueMatchName":"中超","matchCode":1701805,"matchTime":"17-11-04","status":0},{"guestId":14244,"guestName":"天津权健","guestScore":0,"halfScore":"1-0","handicap":0,"homeId":1637,"homeName":"江苏苏宁","homeScore":1,"leagueMatchName":"中超","matchCode":1701737,"matchTime":"17-09-28","status":1},{"guestId":14244,"guestName":"天津权健","guestScore":1,"halfScore":"2-0","handicap":0.5,"homeId":12,"homeName":"天津泰达","homeScore":4,"leagueMatchName":"中超","matchCode":1701772,"matchTime":"17-09-23","status":1}],"teamName":"天津权健"}
         * homeMatchInfo : {"allMatchList":[{"guestId":3248,"guestName":"蔚山现代","guestScore":0,"halfScore":"0-0","handicap":-0.75,"homeId":6917,"homeName":"全北现代","homeScore":2,"leagueMatchName":"韩职","matchCode":2940400,"matchTime":"18-03-01","status":0},{"guestId":6917,"guestName":"全北现代","guestScore":6,"halfScore":"0-5","handicap":1.5,"homeId":7670,"homeName":"香港杰志","homeScore":0,"leagueMatchName":"亚冠","matchCode":2938147,"matchTime":"18-02-20","status":0},{"guestId":807,"guestName":"柏太阳神","guestScore":2,"halfScore":"0-2","handicap":-0.75,"homeId":6917,"homeName":"全北现代","homeScore":3,"leagueMatchName":"亚冠","matchCode":2938145,"matchTime":"18-02-13","status":0},{"guestId":3177,"guestName":"水原三星","guestScore":3,"halfScore":"2-1","handicap":-0.25,"homeId":6917,"homeName":"全北现代","homeScore":2,"leagueMatchName":"韩职","matchCode":2915284,"matchTime":"17-11-19","status":1},{"guestId":6917,"guestName":"全北现代","guestScore":2,"halfScore":"0-0","handicap":0,"homeId":3248,"homeName":"蔚山现代","homeScore":1,"leagueMatchName":"韩职","matchCode":2915281,"matchTime":"17-11-05","status":0},{"guestId":13,"guestName":"济州联","guestScore":0,"halfScore":"0-0","handicap":-0.5,"homeId":6917,"homeName":"全北现代","homeScore":3,"leagueMatchName":"韩职","matchCode":2914974,"matchTime":"17-10-29","status":0},{"guestId":6917,"guestName":"全北现代","guestScore":4,"halfScore":"0-1","handicap":0.75,"homeId":14553,"homeName":"江原FC","homeScore":0,"leagueMatchName":"韩职","matchCode":2914971,"matchTime":"17-10-22","status":0},{"guestId":3161,"guestName":"首尔FC","guestScore":0,"halfScore":"0-0","handicap":-0.5,"homeId":6917,"homeName":"全北现代","homeScore":0,"leagueMatchName":"韩职","matchCode":2914968,"matchTime":"17-10-15","status":1},{"guestId":6917,"guestName":"全北现代","guestScore":1,"halfScore":"0-0","handicap":0.25,"homeId":13,"homeName":"济州联","homeScore":0,"leagueMatchName":"韩职","matchCode":1705515,"matchTime":"17-10-08","status":0},{"guestId":6917,"guestName":"全北现代","guestScore":1,"halfScore":"1-0","handicap":0.25,"homeId":3177,"homeName":"水原三星","homeScore":1,"leagueMatchName":"韩职","matchCode":1705547,"matchTime":"17-10-01","status":1}],"beyond":false,"continuousSpieltag":4,"continuousType":0,"historicHigh":4,"teamId":6917,"teamMatchList":[{"guestId":3248,"guestName":"蔚山现代","guestScore":0,"halfScore":"0-0","handicap":-0.75,"homeId":6917,"homeName":"全北现代","homeScore":2,"leagueMatchName":"韩职","matchCode":2940400,"matchTime":"18-03-01","status":0},{"guestId":807,"guestName":"柏太阳神","guestScore":2,"halfScore":"0-2","handicap":-0.75,"homeId":6917,"homeName":"全北现代","homeScore":3,"leagueMatchName":"亚冠","matchCode":2938145,"matchTime":"18-02-13","status":0},{"guestId":3177,"guestName":"水原三星","guestScore":3,"halfScore":"2-1","handicap":-0.25,"homeId":6917,"homeName":"全北现代","homeScore":2,"leagueMatchName":"韩职","matchCode":2915284,"matchTime":"17-11-19","status":1},{"guestId":13,"guestName":"济州联","guestScore":0,"halfScore":"0-0","handicap":-0.5,"homeId":6917,"homeName":"全北现代","homeScore":3,"leagueMatchName":"韩职","matchCode":2914974,"matchTime":"17-10-29","status":0},{"guestId":3161,"guestName":"首尔FC","guestScore":0,"halfScore":"0-0","handicap":-0.5,"homeId":6917,"homeName":"全北现代","homeScore":0,"leagueMatchName":"韩职","matchCode":2914968,"matchTime":"17-10-15","status":1},{"guestId":3165,"guestName":"大邱FC","guestScore":1,"halfScore":"1-1","handicap":-1.25,"homeId":6917,"homeName":"全北现代","homeScore":1,"leagueMatchName":"韩职","matchCode":1705540,"matchTime":"17-09-24","status":1},{"guestId":3163,"guestName":"尚州尚武","guestScore":2,"halfScore":"1-0","handicap":-1.5,"homeId":6917,"homeName":"全北现代","homeScore":1,"leagueMatchName":"韩职","matchCode":1705531,"matchTime":"17-09-20","status":1},{"guestId":14553,"guestName":"江原FC","guestScore":3,"halfScore":"4-1","handicap":-1,"homeId":6917,"homeName":"全北现代","homeScore":4,"leagueMatchName":"韩职","matchCode":1705518,"matchTime":"17-09-10","status":2},{"guestId":20086,"guestName":"光州FC","guestScore":1,"halfScore":"1-1","handicap":-1.5,"homeId":6917,"homeName":"全北现代","homeScore":3,"leagueMatchName":"韩职","matchCode":1705507,"matchTime":"17-08-19","status":0},{"guestId":3248,"guestName":"蔚山现代","guestScore":1,"halfScore":"0-0","handicap":-0.75,"homeId":6917,"homeName":"全北现代","homeScore":0,"leagueMatchName":"韩职","matchCode":1705497,"matchTime":"17-08-06","status":1}],"teamName":"全北现代"}
         */

        private GuestMatchInfoBean guestMatchInfo;
        private HomeMatchInfoBean homeMatchInfo;

        public Map<String,Object> getAllMatchListBeanData(){
            Map<String, Object> result = new HashMap<>();
            List<AllMatchListBean> list = new ArrayList<>();
            list.addAll(homeMatchInfo.getAllMatchList());
            list.addAll(guestMatchInfo.getAllMatchList());
            result.put("allMatchList", list);
            result.put("teamList", Arrays.asList(homeMatchInfo.getTeamName(), guestMatchInfo.getTeamName()));
            return result;
        }

        public GuestMatchInfoBean getGuestMatchInfo() {
            return guestMatchInfo;
        }

        public void setGuestMatchInfo(GuestMatchInfoBean guestMatchInfo) {
            this.guestMatchInfo = guestMatchInfo;
        }

        public HomeMatchInfoBean getHomeMatchInfo() {
            return homeMatchInfo;
        }

        public void setHomeMatchInfo(HomeMatchInfoBean homeMatchInfo) {
            this.homeMatchInfo = homeMatchInfo;
        }

        public static class GuestMatchInfoBean {
            /**
             * allMatchList : [{"guestId":14244,"guestName":"天津权健","guestScore":4,"halfScore":"0-1","handicap":0.25,"homeId":1654,"homeName":"河南建业","homeScore":0,"leagueMatchName":"中超","matchCode":2941746,"matchTime":"18-03-02","status":0},{"guestId":14244,"guestName":"天津权健","guestScore":1,"halfScore":"0-0","handicap":-0.5,"homeId":807,"homeName":"柏太阳神","homeScore":1,"leagueMatchName":"亚冠","matchCode":2938148,"matchTime":"18-02-20","status":0},{"guestId":7670,"guestName":"香港杰志","guestScore":0,"halfScore":"3-0","handicap":-2,"homeId":14244,"homeName":"天津权健","homeScore":3,"leagueMatchName":"亚冠","matchCode":2938146,"matchTime":"18-02-13","status":0},{"guestId":24668,"guestName":"塞列斯","guestScore":0,"halfScore":"1-0","handicap":-2,"homeId":14244,"homeName":"天津权健","homeScore":2,"leagueMatchName":"亚冠","matchCode":2934320,"matchTime":"18-01-30","status":2},{"guestId":14244,"guestName":"天津权健","guestScore":1,"halfScore":"1-0","handicap":-1.25,"homeId":1275,"homeName":"莫火车头","homeScore":1,"leagueMatchName":"俱乐部赛","matchCode":2933394,"matchTime":"18-01-23","status":0},{"guestId":14244,"guestName":"天津权健","guestScore":2,"halfScore":"2-2","handicap":-1.25,"homeId":1287,"homeName":"默德林","homeScore":3,"leagueMatchName":"俱乐部赛","matchCode":2933131,"matchTime":"18-01-20","status":0},{"guestId":14244,"guestName":"天津权健","guestScore":1,"halfScore":"3-0","handicap":-1.25,"homeId":769,"homeName":"布斯巴达","homeScore":4,"leagueMatchName":"俱乐部赛","matchCode":2930618,"matchTime":"18-01-17","status":1},{"guestId":14244,"guestName":"天津权健","guestScore":1,"halfScore":"1-1","handicap":-2.25,"homeId":979,"homeName":"巴塞尔","homeScore":1,"leagueMatchName":"俱乐部赛","matchCode":2930778,"matchTime":"18-01-14","status":0},{"guestId":14244,"guestName":"天津权健","guestScore":0,"halfScore":"1-0","handicap":-1.25,"homeId":189,"homeName":"圣保利","homeScore":1,"leagueMatchName":"俱乐部赛","matchCode":2930296,"matchTime":"18-01-11","status":0},{"guestId":14244,"guestName":"天津权健","guestScore":2,"halfScore":"0-1","handicap":0.5,"homeId":1603,"homeName":"广州恒大","homeScore":1,"leagueMatchName":"中超","matchCode":1701805,"matchTime":"17-11-04","status":0}]
             * teamId : 14244
             * teamMatchList : [{"guestId":14244,"guestName":"天津权健","guestScore":4,"halfScore":"0-1","handicap":0.25,"homeId":1654,"homeName":"河南建业","homeScore":0,"leagueMatchName":"中超","matchCode":2941746,"matchTime":"18-03-02","status":0},{"guestId":14244,"guestName":"天津权健","guestScore":1,"halfScore":"0-0","handicap":-0.5,"homeId":807,"homeName":"柏太阳神","homeScore":1,"leagueMatchName":"亚冠","matchCode":2938148,"matchTime":"18-02-20","status":0},{"guestId":14244,"guestName":"天津权健","guestScore":1,"halfScore":"1-0","handicap":-1.25,"homeId":1275,"homeName":"莫火车头","homeScore":1,"leagueMatchName":"俱乐部赛","matchCode":2933394,"matchTime":"18-01-23","status":0},{"guestId":14244,"guestName":"天津权健","guestScore":2,"halfScore":"2-2","handicap":-1.25,"homeId":1287,"homeName":"默德林","homeScore":3,"leagueMatchName":"俱乐部赛","matchCode":2933131,"matchTime":"18-01-20","status":0},{"guestId":14244,"guestName":"天津权健","guestScore":1,"halfScore":"3-0","handicap":-1.25,"homeId":769,"homeName":"布斯巴达","homeScore":4,"leagueMatchName":"俱乐部赛","matchCode":2930618,"matchTime":"18-01-17","status":1},{"guestId":14244,"guestName":"天津权健","guestScore":1,"halfScore":"1-1","handicap":-2.25,"homeId":979,"homeName":"巴塞尔","homeScore":1,"leagueMatchName":"俱乐部赛","matchCode":2930778,"matchTime":"18-01-14","status":0},{"guestId":14244,"guestName":"天津权健","guestScore":0,"halfScore":"1-0","handicap":-1.25,"homeId":189,"homeName":"圣保利","homeScore":1,"leagueMatchName":"俱乐部赛","matchCode":2930296,"matchTime":"18-01-11","status":0},{"guestId":14244,"guestName":"天津权健","guestScore":2,"halfScore":"0-1","handicap":0.5,"homeId":1603,"homeName":"广州恒大","homeScore":1,"leagueMatchName":"中超","matchCode":1701805,"matchTime":"17-11-04","status":0},{"guestId":14244,"guestName":"天津权健","guestScore":0,"halfScore":"1-0","handicap":0,"homeId":1637,"homeName":"江苏苏宁","homeScore":1,"leagueMatchName":"中超","matchCode":1701737,"matchTime":"17-09-28","status":1},{"guestId":14244,"guestName":"天津权健","guestScore":1,"halfScore":"2-0","handicap":0.5,"homeId":12,"homeName":"天津泰达","homeScore":4,"leagueMatchName":"中超","matchCode":1701772,"matchTime":"17-09-23","status":1}]
             * teamName : 天津权健
             */

            private int teamId;
            private String teamName;
            private List<AllMatchListBean> allMatchList;
            private List<TeamMatchListBean> teamMatchList;

            public int getTeamId() {
                return teamId;
            }

            public void setTeamId(int teamId) {
                this.teamId = teamId;
            }

            public String getTeamName() {
                return teamName;
            }

            public void setTeamName(String teamName) {
                this.teamName = teamName;
            }

            public List<com.dinpay.trip.testdemo.beans.AllMatchListBean> getAllMatchList() {
                return allMatchList;
            }

            public void setAllMatchList(List<com.dinpay.trip.testdemo.beans.AllMatchListBean> allMatchList) {
                this.allMatchList = allMatchList;
            }

            public List<TeamMatchListBean> getTeamMatchList() {
                return teamMatchList;
            }

            public void setTeamMatchList(List<TeamMatchListBean> teamMatchList) {
                this.teamMatchList = teamMatchList;
            }
        }

        public static class HomeMatchInfoBean {
            /**
             * allMatchList : [{"guestId":3248,"guestName":"蔚山现代","guestScore":0,"halfScore":"0-0","handicap":-0.75,"homeId":6917,"homeName":"全北现代","homeScore":2,"leagueMatchName":"韩职","matchCode":2940400,"matchTime":"18-03-01","status":0},{"guestId":6917,"guestName":"全北现代","guestScore":6,"halfScore":"0-5","handicap":1.5,"homeId":7670,"homeName":"香港杰志","homeScore":0,"leagueMatchName":"亚冠","matchCode":2938147,"matchTime":"18-02-20","status":0},{"guestId":807,"guestName":"柏太阳神","guestScore":2,"halfScore":"0-2","handicap":-0.75,"homeId":6917,"homeName":"全北现代","homeScore":3,"leagueMatchName":"亚冠","matchCode":2938145,"matchTime":"18-02-13","status":0},{"guestId":3177,"guestName":"水原三星","guestScore":3,"halfScore":"2-1","handicap":-0.25,"homeId":6917,"homeName":"全北现代","homeScore":2,"leagueMatchName":"韩职","matchCode":2915284,"matchTime":"17-11-19","status":1},{"guestId":6917,"guestName":"全北现代","guestScore":2,"halfScore":"0-0","handicap":0,"homeId":3248,"homeName":"蔚山现代","homeScore":1,"leagueMatchName":"韩职","matchCode":2915281,"matchTime":"17-11-05","status":0},{"guestId":13,"guestName":"济州联","guestScore":0,"halfScore":"0-0","handicap":-0.5,"homeId":6917,"homeName":"全北现代","homeScore":3,"leagueMatchName":"韩职","matchCode":2914974,"matchTime":"17-10-29","status":0},{"guestId":6917,"guestName":"全北现代","guestScore":4,"halfScore":"0-1","handicap":0.75,"homeId":14553,"homeName":"江原FC","homeScore":0,"leagueMatchName":"韩职","matchCode":2914971,"matchTime":"17-10-22","status":0},{"guestId":3161,"guestName":"首尔FC","guestScore":0,"halfScore":"0-0","handicap":-0.5,"homeId":6917,"homeName":"全北现代","homeScore":0,"leagueMatchName":"韩职","matchCode":2914968,"matchTime":"17-10-15","status":1},{"guestId":6917,"guestName":"全北现代","guestScore":1,"halfScore":"0-0","handicap":0.25,"homeId":13,"homeName":"济州联","homeScore":0,"leagueMatchName":"韩职","matchCode":1705515,"matchTime":"17-10-08","status":0},{"guestId":6917,"guestName":"全北现代","guestScore":1,"halfScore":"1-0","handicap":0.25,"homeId":3177,"homeName":"水原三星","homeScore":1,"leagueMatchName":"韩职","matchCode":1705547,"matchTime":"17-10-01","status":1}]
             * beyond : false
             * continuousSpieltag : 4
             * continuousType : 0
             * historicHigh : 4
             * teamId : 6917
             * teamMatchList : [{"guestId":3248,"guestName":"蔚山现代","guestScore":0,"halfScore":"0-0","handicap":-0.75,"homeId":6917,"homeName":"全北现代","homeScore":2,"leagueMatchName":"韩职","matchCode":2940400,"matchTime":"18-03-01","status":0},{"guestId":807,"guestName":"柏太阳神","guestScore":2,"halfScore":"0-2","handicap":-0.75,"homeId":6917,"homeName":"全北现代","homeScore":3,"leagueMatchName":"亚冠","matchCode":2938145,"matchTime":"18-02-13","status":0},{"guestId":3177,"guestName":"水原三星","guestScore":3,"halfScore":"2-1","handicap":-0.25,"homeId":6917,"homeName":"全北现代","homeScore":2,"leagueMatchName":"韩职","matchCode":2915284,"matchTime":"17-11-19","status":1},{"guestId":13,"guestName":"济州联","guestScore":0,"halfScore":"0-0","handicap":-0.5,"homeId":6917,"homeName":"全北现代","homeScore":3,"leagueMatchName":"韩职","matchCode":2914974,"matchTime":"17-10-29","status":0},{"guestId":3161,"guestName":"首尔FC","guestScore":0,"halfScore":"0-0","handicap":-0.5,"homeId":6917,"homeName":"全北现代","homeScore":0,"leagueMatchName":"韩职","matchCode":2914968,"matchTime":"17-10-15","status":1},{"guestId":3165,"guestName":"大邱FC","guestScore":1,"halfScore":"1-1","handicap":-1.25,"homeId":6917,"homeName":"全北现代","homeScore":1,"leagueMatchName":"韩职","matchCode":1705540,"matchTime":"17-09-24","status":1},{"guestId":3163,"guestName":"尚州尚武","guestScore":2,"halfScore":"1-0","handicap":-1.5,"homeId":6917,"homeName":"全北现代","homeScore":1,"leagueMatchName":"韩职","matchCode":1705531,"matchTime":"17-09-20","status":1},{"guestId":14553,"guestName":"江原FC","guestScore":3,"halfScore":"4-1","handicap":-1,"homeId":6917,"homeName":"全北现代","homeScore":4,"leagueMatchName":"韩职","matchCode":1705518,"matchTime":"17-09-10","status":2},{"guestId":20086,"guestName":"光州FC","guestScore":1,"halfScore":"1-1","handicap":-1.5,"homeId":6917,"homeName":"全北现代","homeScore":3,"leagueMatchName":"韩职","matchCode":1705507,"matchTime":"17-08-19","status":0},{"guestId":3248,"guestName":"蔚山现代","guestScore":1,"halfScore":"0-0","handicap":-0.75,"homeId":6917,"homeName":"全北现代","homeScore":0,"leagueMatchName":"韩职","matchCode":1705497,"matchTime":"17-08-06","status":1}]
             * teamName : 全北现代
             */

            private boolean beyond;
            private int continuousSpieltag;
            private int continuousType;
            private int historicHigh;
            private int teamId;
            private String teamName;
            private List<AllMatchListBean> allMatchList;
            private List<TeamMatchListBean> teamMatchList;

            public boolean isBeyond() {
                return beyond;
            }

            public void setBeyond(boolean beyond) {
                this.beyond = beyond;
            }

            public int getContinuousSpieltag() {
                return continuousSpieltag;
            }

            public void setContinuousSpieltag(int continuousSpieltag) {
                this.continuousSpieltag = continuousSpieltag;
            }

            public int getContinuousType() {
                return continuousType;
            }

            public void setContinuousType(int continuousType) {
                this.continuousType = continuousType;
            }

            public int getHistoricHigh() {
                return historicHigh;
            }

            public void setHistoricHigh(int historicHigh) {
                this.historicHigh = historicHigh;
            }

            public int getTeamId() {
                return teamId;
            }

            public void setTeamId(int teamId) {
                this.teamId = teamId;
            }

            public String getTeamName() {
                return teamName;
            }

            public void setTeamName(String teamName) {
                this.teamName = teamName;
            }

            public List<com.dinpay.trip.testdemo.beans.AllMatchListBean> getAllMatchList() {
                return allMatchList;
            }

            public void setAllMatchList(List<com.dinpay.trip.testdemo.beans.AllMatchListBean> allMatchList) {
                this.allMatchList = allMatchList;
            }

            public List<TeamMatchListBean> getTeamMatchList() {
                return teamMatchList;
            }

            public void setTeamMatchList(List<TeamMatchListBean> teamMatchList) {
                this.teamMatchList = teamMatchList;
            }

        }
    }
}
