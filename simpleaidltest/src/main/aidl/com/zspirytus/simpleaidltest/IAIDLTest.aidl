// IAIDLTest.aidl
package com.zspirytus.simpleaidltest;

import com.zspirytus.simpleaidltest.ICallback;

interface IAIDLTest {
    void testMethod(int a);
    void setCallback(ICallback callback);
}
