package at.ameise.devicelocation.model;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

/**
 * Created by mariogastegger on 09.01.17.
 */
@Entity(
    // Whether getters and setters for properties should be generated if missing.
    generateGettersSetters = true
)
public class Report {

    @Id(autoincrement = true)
    private Long id;

    @NotNull
    private long timestamp;

    private Double gpsLatitude;
    private Double gpsLongitude;
    private Float gpsAccuracyRaw;

    private Float mlsLatitude;
    private Float mlsLongitude;
    private Float mlsAccuracyRaw;
    private String mlsFallback;

    private String mlsRequest;
    private String mlsResponse;

    @ToMany(referencedJoinProperty = "reportId")
    private List<CellTower> paramCellTowers;

    @ToMany(referencedJoinProperty = "reportId")
    private List<WifiAccessPoint> paramWifiAccessPoints;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 485466363)
    private transient ReportDao myDao;

    @Generated(hash = 731575467)
    public Report(Long id, long timestamp, Double gpsLatitude, Double gpsLongitude,
            Float gpsAccuracyRaw, Float mlsLatitude, Float mlsLongitude, Float mlsAccuracyRaw,
            String mlsFallback, String mlsRequest, String mlsResponse) {
        this.id = id;
        this.timestamp = timestamp;
        this.gpsLatitude = gpsLatitude;
        this.gpsLongitude = gpsLongitude;
        this.gpsAccuracyRaw = gpsAccuracyRaw;
        this.mlsLatitude = mlsLatitude;
        this.mlsLongitude = mlsLongitude;
        this.mlsAccuracyRaw = mlsAccuracyRaw;
        this.mlsFallback = mlsFallback;
        this.mlsRequest = mlsRequest;
        this.mlsResponse = mlsResponse;
    }

    @Generated(hash = 1739299007)
    public Report() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Double getGpsLatitude() {
        return this.gpsLatitude;
    }

    public void setGpsLatitude(Double gpsLatitude) {
        this.gpsLatitude = gpsLatitude;
    }

    public Double getGpsLongitude() {
        return this.gpsLongitude;
    }

    public void setGpsLongitude(Double gpsLongitude) {
        this.gpsLongitude = gpsLongitude;
    }

    public Float getGpsAccuracyRaw() {
        return this.gpsAccuracyRaw;
    }

    public void setGpsAccuracyRaw(Float gpsAccuracyRaw) {
        this.gpsAccuracyRaw = gpsAccuracyRaw;
    }

    public Float getMlsLatitude() {
        return this.mlsLatitude;
    }

    public void setMlsLatitude(Float mlsLatitude) {
        this.mlsLatitude = mlsLatitude;
    }

    public Float getMlsLongitude() {
        return this.mlsLongitude;
    }

    public void setMlsLongitude(Float mlsLongitude) {
        this.mlsLongitude = mlsLongitude;
    }

    public Float getMlsAccuracyRaw() {
        return this.mlsAccuracyRaw;
    }

    public void setMlsAccuracyRaw(Float mlsAccuracyRaw) {
        this.mlsAccuracyRaw = mlsAccuracyRaw;
    }

    public String getMlsFallback() {
        return this.mlsFallback;
    }

    public void setMlsFallback(String mlsFallback) {
        this.mlsFallback = mlsFallback;
    }

    public String getMlsRequest() {
        return this.mlsRequest;
    }

    public void setMlsRequest(String mlsRequest) {
        this.mlsRequest = mlsRequest;
    }

    public String getMlsResponse() {
        return this.mlsResponse;
    }

    public void setMlsResponse(String mlsResponse) {
        this.mlsResponse = mlsResponse;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1956385845)
    public List<CellTower> getParamCellTowers() {
        if (paramCellTowers == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            CellTowerDao targetDao = daoSession.getCellTowerDao();
            List<CellTower> paramCellTowersNew = targetDao
                    ._queryReport_ParamCellTowers(id);
            synchronized (this) {
                if (paramCellTowers == null) {
                    paramCellTowers = paramCellTowersNew;
                }
            }
        }
        return paramCellTowers;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 628397819)
    public synchronized void resetParamCellTowers() {
        paramCellTowers = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1294805026)
    public List<WifiAccessPoint> getParamWifiAccessPoints() {
        if (paramWifiAccessPoints == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            WifiAccessPointDao targetDao = daoSession.getWifiAccessPointDao();
            List<WifiAccessPoint> paramWifiAccessPointsNew = targetDao
                    ._queryReport_ParamWifiAccessPoints(id);
            synchronized (this) {
                if (paramWifiAccessPoints == null) {
                    paramWifiAccessPoints = paramWifiAccessPointsNew;
                }
            }
        }
        return paramWifiAccessPoints;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1330665960)
    public synchronized void resetParamWifiAccessPoints() {
        paramWifiAccessPoints = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1237337053)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getReportDao() : null;
    }

}
