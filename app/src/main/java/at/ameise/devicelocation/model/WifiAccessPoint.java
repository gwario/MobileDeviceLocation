package at.ameise.devicelocation.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by mariogastegger on 09.01.17.
 */
@Entity
public class WifiAccessPoint {

    @Id(autoincrement = true)
    private Long id;

    private long reportId;

    private String macAddress;
    private long age;
    private int channel;
    private int frequency;
    private int signalStrength;
    private int signalToNoiseRatio;
    private String ssid;

    @Generated(hash = 968831861)
    public WifiAccessPoint(Long id, long reportId, String macAddress, long age,
            int channel, int frequency, int signalStrength, int signalToNoiseRatio,
            String ssid) {
        this.id = id;
        this.reportId = reportId;
        this.macAddress = macAddress;
        this.age = age;
        this.channel = channel;
        this.frequency = frequency;
        this.signalStrength = signalStrength;
        this.signalToNoiseRatio = signalToNoiseRatio;
        this.ssid = ssid;
    }
    @Generated(hash = 95104623)
    public WifiAccessPoint() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public long getReportId() {
        return this.reportId;
    }
    public void setReportId(long reportId) {
        this.reportId = reportId;
    }
    public String getMacAddress() {
        return this.macAddress;
    }
    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
    public int getChannel() {
        return this.channel;
    }
    public void setChannel(int channel) {
        this.channel = channel;
    }
    public int getFrequency() {
        return this.frequency;
    }
    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }
    public int getSignalStrength() {
        return this.signalStrength;
    }
    public void setSignalStrength(int signalStrength) {
        this.signalStrength = signalStrength;
    }
    public int getSignalToNoiseRatio() {
        return this.signalToNoiseRatio;
    }
    public void setSignalToNoiseRatio(int signalToNoiseRatio) {
        this.signalToNoiseRatio = signalToNoiseRatio;
    }
    public void setAge(long age) {
        this.age = age;
    }
    public String getSsid() {
        return ssid;
    }
    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    @Override
    public String toString() {
        return "WifiAccessPoint{" +
                "id=" + id +
                ", reportId=" + reportId +
                ", ssid=" +ssid +
                ", macAddress='" + macAddress + '\'' +
                ", age=" + age +
                ", channel=" + channel +
                ", frequency=" + frequency +
                ", signalStrength=" + signalStrength +
                ", signalToNoiseRatio=" + signalToNoiseRatio +
                '}';
    }
    public long getAge() {
        return this.age;
    }
}
