package com.spockchain.wallet.utils;

import com.spockchain.wallet.entity.Address;
import com.spockchain.wallet.utils.bip44.HexUtils;

import java.math.BigInteger;

public class PlotIdUtils {
    public static String getPlotIdFromSpockAddress(String address) {
        if (!Address.isSpockAddress(address)) {
            return null;
        }
        address = address.toUpperCase().replaceFirst("SPOCK-", "");
        byte[] addressInBytes = HexUtils.toBytes(address);
        return new BigInteger(HexUtils.toHex(addressInBytes, addressInBytes.length - 8, 8), 16).toString();
    }
}
