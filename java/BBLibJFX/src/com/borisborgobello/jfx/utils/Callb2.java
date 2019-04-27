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
public interface Callb2<T,U> {
    public void run(T t, U u);
}