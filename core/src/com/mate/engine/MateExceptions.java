package com.mate.engine;

import javax.swing.*;
import java.util.Map;

public class MateExceptions extends Exception {

    public static void jOption(String err) {
        int result = JOptionPane.showConfirmDialog(null,
                err,
                "DO YOU WANT TO EXIT THE PROGRAM?", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) System.exit(0);
    }

    public synchronized static void propertyMapException(Map<String, String> propertyMap, Class c) {
        propertyMap.put("distance", "0");
        propertyMap.put("color", "#ffffff00");
        propertyMap.put("cast", "false");

        propertyMap.put("spriteshadow", "false");
        propertyMap.put("shadowoffsetx", "0");
        propertyMap.put("shadowoffsety", "0");

        propertyMap.put("ambientlight", "#ffffff00");

        String err = "";

        err += "PROPERTY_MAP_EXCEPTION at class:\n";
        err += c.getName() + " at method:\n";
        err += c.getEnclosingMethod().getName() + "\n";
        err += "--> set propertyMap to default values. please check tilded properties.\n";
        err += "----------- \n";

        System.err.println(err);

        jOption(err);
    }

}
