package com.spockchain.wallet.utils;

import com.spockchain.wallet.entity.Address;
import com.spockchain.wallet.utils.bip44.HexUtils;

public class PlotIdUtils {
    public static String getPlotIdFromSpockAddress(String address) {
        if (!Address.isSpockAddress(address)) {
            return null;
        }
        address = address.toUpperCase().replaceFirst("SPOCK-", "");
        byte[] addressInBytes = HexUtils.toBytes(address);

        long plotId = 0;
        for (int i = 8; i > 0; i--) {
            plotId = (plotId << 8) + (addressInBytes[addressInBytes.length - i] & 0xff);
        }
        return String.valueOf(plotId);
    }
}
