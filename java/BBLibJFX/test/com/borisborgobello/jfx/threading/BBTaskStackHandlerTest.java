/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello.jfx.threading;

import com.borisborgobello.jfx.threading.BBTask;
import com.borisborgobello.jfx.threading.BBTaskStackHandler;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author borisborgobello
 */
public class BBTaskStackHandlerTest {
    
    public BBTaskStackHandlerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        com.sun.javafx.application.PlatformImpl.startup(()->{});
    }
    
    @AfterClass
    public static void tearDownClass() {
        com.sun.javafx.application.PlatformImpl.exit();
    }
    
    @Before
    public void setUp() {
        
    }
    
    @After
    public void tearDown() {
    }
    
    private void doTestTSH(int nbTasks, int nbThreads) throws Exception {
        System.out.println(String.format("nbTasks %d / nbThreads %d", nbTasks, nbThreads));
        AtomicInteger remaining = new AtomicInteger(nbTasks);
        CountDownLatch lock = new CountDownLatch(1);
        BBTaskStackHandler tsh = new BBTaskStackHandler("TASK") {
            @Override
            public void onTaskStackFinished() {
                lock.countDown();
            }
        };
        tsh.S_TSH_VERBOSE = false;
        
        ConcurrentHashMap<Integer,Integer> tasksDone = new ConcurrentHashMap<>();
        for (int i = 0; i < nbTasks; i++) {
            final int myData = i;
            tsh.addTask(new BBTask("TESTTASK") {
                @Override
                public void run() {
                    remaining.decrementAndGet();
                    tasksDone.put(myData, myData);
                    if ((int)(Math.random()+0.5) == 0) doneSuccess();
                    else doneFailed(new Exception("Bad luck"));
                }
            });
        }
        
        tsh.execute(nbThreads);
        
        lock.await(5000, TimeUnit.MILLISECONDS);
        assertEquals(0, remaining.get());
        assertEquals(nbTasks, tasksDone.size());
    }
    
    @Test
    public void testTaskStackHandler() throws Exception {
        System.out.println("testTaskStackHandler");
        
        for (int nbTasks = 0; nbTasks < 15; nbTasks++) {
            for (int nbThread = 1; nbThread < 10; nbThread++) {
                doTestTSH(nbTasks, nbThread);
            }
        }
        
        doTestTSH(300, 1);
        doTestTSH(200, 5);
    }
}
