/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.twelvemonkeys.image;

import java.awt.image.IndexColorModel;
import java.awt.image.BufferedImage;

/**
 *
 * @author borisborgobello
 */
public class IndexImageBridge {
    public static IndexColorModel getICM(BufferedImage img, int nbColors) {
        //return IndexImage.getIndexColorModel(img, nbColors, true);
        return IndexImage.getIndexColorModel(img, nbColors, IndexImage.COLOR_SELECTION_QUALITY);
    }
}
