package at.ameise.devicelocation.account;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by mariogastegger on 23.01.17.
 */
public final class SecureRandomGenerator {

    private SecureRandom random = new SecureRandom();

    public String getPassword() {
        return new BigInteger(130, random).toString(32);
    }
}
