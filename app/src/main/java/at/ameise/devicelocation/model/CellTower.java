package at.ameise.devicelocation.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by mariogastegger on 09.01.17.
 */
@Entity
public class CellTower {

    @Id(autoincrement = true)
    private Long id;

    private long reportId;


    private String radioType;
    private int mobileCountryCode;
    private int mobileNetworkCode;
    private int locationAreaCode;
    private int cellId;
    private long age;
    private int psc;
    private int signalStrength;
    private int timingAdvance;
    @Generated(hash = 570172577)
    public CellTower(Long id, long reportId, String radioType,
            int mobileCountryCode, int mobileNetworkCode, int locationAreaCode,
            int cellId, long age, int psc, int signalStrength, int timingAdvance) {
        this.id = id;
        this.reportId = reportId;
        this.radioType = radioType;
        this.mobileCountryCode = mobileCountryCode;
        this.mobileNetworkCode = mobileNetworkCode;
        this.locationAreaCode = locationAreaCode;
        this.cellId = cellId;
        this.age = age;
        this.psc = psc;
        this.signalStrength = signalStrength;
        this.timingAdvance = timingAdvance;
    }
    @Generated(hash = 419680294)
    public CellTower() {
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
    public String getRadioType() {
        return this.radioType;
    }
    public void setRadioType(String radioType) {
        this.radioType = radioType;
    }
    public int getMobileCountryCode() {
        return this.mobileCountryCode;
    }
    public void setMobileCountryCode(int mobileCountryCode) {
        this.mobileCountryCode = mobileCountryCode;
    }
    public int getMobileNetworkCode() {
        return this.mobileNetworkCode;
    }
    public void setMobileNetworkCode(int mobileNetworkCode) {
        this.mobileNetworkCode = mobileNetworkCode;
    }
    public int getLocationAreaCode() {
        return this.locationAreaCode;
    }
    public void setLocationAreaCode(int locationAreaCode) {
        this.locationAreaCode = locationAreaCode;
    }
    public int getCellId() {
        return this.cellId;
    }
    public void setCellId(int cellId) {
        this.cellId = cellId;
    }
    public long getAge() {
        return this.age;
    }
    public void setAge(long age) {
        this.age = age;
    }
    public int getPsc() {
        return this.psc;
    }
    public void setPsc(int psc) {
        this.psc = psc;
    }
    public int getSignalStrength() {
        return this.signalStrength;
    }
    public void setSignalStrength(int signalStrength) {
        this.signalStrength = signalStrength;
    }
    public int getTimingAdvance() {
        return this.timingAdvance;
    }
    public void setTimingAdvance(int timingAdvance) {
        this.timingAdvance = timingAdvance;
    }

    @Override
    public String toString() {
        return "CellTower{" +
                "id=" + id +
                ", reportId=" + reportId +
                ", radioType='" + radioType + '\'' +
                ", mobileCountryCode=" + mobileCountryCode +
                ", mobileNetworkCode=" + mobileNetworkCode +
                ", locationAreaCode=" + locationAreaCode +
                ", cellId=" + cellId +
                ", age=" + age +
                ", psc=" + psc +
                ", signalStrength=" + signalStrength +
                ", timingAdvance=" + timingAdvance +
                '}';
    }
}
