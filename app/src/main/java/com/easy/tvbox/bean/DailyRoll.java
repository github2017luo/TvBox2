package com.easy.tvbox.bean;

public class DailyRoll {

    private long start;// 1560602571,
    private long end;// 1560602583,
    private long duration;// 12,
    private String source;//"http://azitai.oss-cn-shanghai.aliyuncs.com/xylr-video/2019-06-15-汉语/09.公益插播1_高清转码.mp4?auth_key=1560618306-0-0-833494bb49c6844d10550ee58d62b451"

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
