package at.ameise.devicelocation.util;

import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellLocation;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;

import at.ameise.devicelocation.model.CellTower;

/**
 * Created by johannes on 19.01.17.
 */

public class TelephonyUtil {

    private TelephonyUtil() {}

    /**
     * Creates a {@link CellTower} out of a {@link CellLocation}
     * @param cellLocation the cell location.
     * @return  the cell.
     */
    public static CellTower from(CellLocation cellLocation) {

        final CellTower cellTower;

        if(cellLocation instanceof CdmaCellLocation) {

            /**
             * Con not get all the required fields from CdmaCellLocation.
             */
            cellTower = null;

        } else if(cellLocation instanceof GsmCellLocation) {

            /**
             * Con not get all the required fields from GsmCellLocation.
             */
            cellTower = null;

        } else {

            cellTower = null;
        }

        return cellTower;
    }

    /**
     * Creates a {@link CellTower} out of a {@link CellInfo}
     * @param cellInfo the cell info.
     * @return  the cell.
     */
    public static CellTower from(CellInfo cellInfo) {

        final CellTower cellTower;

        if (cellInfo instanceof CellInfoGsm) {

            cellTower = new CellTower();

            // set required fields: radioType, MCC, MNC, LAC and CellId
            cellTower.setRadioType("gsm");
            cellTower.setMobileCountryCode(((CellInfoGsm)cellInfo).getCellIdentity().getMcc());
            cellTower.setMobileNetworkCode(((CellInfoGsm)cellInfo).getCellIdentity().getMnc());
            cellTower.setLocationAreaCode(((CellInfoGsm)cellInfo).getCellIdentity().getLac());
            cellTower.setCellId(((CellInfoGsm)cellInfo).getCellIdentity().getCid());

            // set additional fields
            cellTower.setSignalStrength(((CellInfoGsm)cellInfo).getCellSignalStrength().getDbm());
            cellTower.setPsc(((CellInfoGsm)cellInfo).getCellIdentity().getPsc());
            cellTower.setAge(cellInfo.getTimeStamp());

            // TODO timingAdvance missing, check if it can be accessed with GSM cell

        } else if (cellInfo instanceof CellInfoLte) {

            cellTower = new CellTower();

            // set required fields: radioType, MCC, MNC, LAC and CellId
            cellTower.setRadioType("lte");
            cellTower.setMobileCountryCode(((CellInfoLte)cellInfo).getCellIdentity().getMcc());
            cellTower.setMobileNetworkCode(((CellInfoLte)cellInfo).getCellIdentity().getMnc());
            cellTower.setLocationAreaCode(((CellInfoLte)cellInfo).getCellIdentity().getTac());
            cellTower.setCellId(((CellInfoLte)cellInfo).getCellIdentity().getCi());

            // set additional fields
            cellTower.setSignalStrength(((CellInfoLte)cellInfo).getCellSignalStrength().getDbm());
            cellTower.setPsc(((CellInfoLte)cellInfo).getCellIdentity().getPci());
            cellTower.setTimingAdvance(((CellInfoLte)cellInfo).getCellSignalStrength().getTimingAdvance());
            cellTower.setAge(cellInfo.getTimeStamp());

        } else if (cellInfo instanceof CellInfoCdma){

            /**
             * Cell type CDMA not supported by Mozilla Location Service.
             */
            cellTower = null;

        } else if (cellInfo instanceof CellInfoWcdma){

            cellTower = new CellTower();

            // set required fields: radioType, MCC, MNC, LAC and CellId
            cellTower.setRadioType("wcdma");
            cellTower.setMobileCountryCode(((CellInfoWcdma)cellInfo).getCellIdentity().getMcc());
            cellTower.setMobileNetworkCode(((CellInfoWcdma)cellInfo).getCellIdentity().getMnc());
            cellTower.setLocationAreaCode(((CellInfoWcdma)cellInfo).getCellIdentity().getLac());
            cellTower.setCellId(((CellInfoWcdma)cellInfo).getCellIdentity().getCid());

            // set additional fields
            cellTower.setSignalStrength(((CellInfoWcdma)cellInfo).getCellSignalStrength().getDbm());
            cellTower.setPsc(((CellInfoWcdma)cellInfo).getCellIdentity().getPsc());
            cellTower.setAge(cellInfo.getTimeStamp());

            // TODO timingAdvance missing, check if it can be accessed with WCDMA cell

        } else {

            cellTower = null;
        }

        return cellTower;
    }
}
