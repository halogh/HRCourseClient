package com.zhuang.hrcourseclient;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.zhuang.dao.DaoImpl;
import com.zhuang.model.MySubject;
import com.zhuang.spider.HREduSystemSpider;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.zhuang.hrcourseclient", appContext.getPackageName());
    }
    @Test
    public void testCourse() throws IOException {
        List<MySubject> lists = HREduSystemSpider.getCourse("1640129305","16440513101465");
        DaoImpl.SaveCourceData(lists);
        List<MySubject> Courselists = DaoImpl.GetCourceData();
        for (MySubject list : Courselists) {
            System.out.println(list.toString());
        }
    }

}
