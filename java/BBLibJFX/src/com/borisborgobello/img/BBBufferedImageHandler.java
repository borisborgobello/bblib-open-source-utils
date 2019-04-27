/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello.img;

import com.borisborgobello.utils.Callb3;
import java.awt.image.BufferedImage;

/**
 *
 * @author borisborgobello
 */
public class BBBufferedImageHandler {
    public final BufferedImage bi;

    public BBBufferedImageHandler(BufferedImage bi) {
        this.bi = bi;
    }
    
    public void forEach(Callb3<Integer, Integer, Integer> cb) {
        for (int j = 0; j < bi.getHeight(); j++) {
            for (int i = 0; i < bi.getWidth(); i++) {
                cb.run(i, j, bi.getRGB(i, j));
            }
        }
    }
}
