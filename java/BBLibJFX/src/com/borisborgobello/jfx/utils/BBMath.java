/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello.jfx.utils;

/**
 *
 * @author borisborgobello
 */
public class BBMath {
    public static int max(int ... is) {
        int max = Integer.MIN_VALUE;
        for (int i : is) { max = Math.max(i, max); }
        return max;
    }
}
