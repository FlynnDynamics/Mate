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

    }

}
