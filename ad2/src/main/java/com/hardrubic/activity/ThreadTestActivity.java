package com.hardrubic.activity;

import android.os.Bundle;
import android.view.View;

import com.hardrubic.thread.TestCyclicBarrierAndCountDownLatch;
import com.hardrubic.thread.TestSemaphore;

import ad2.hardrubic.com.androiddemo20.R;

public class ThreadTestActivity extends TitleActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_test);

        findViewById(R.id.tv_cyclic_barrier).setOnClickListener(this);
        findViewById(R.id.tv_count_down_latch).setOnClickListener(this);
        findViewById(R.id.tv_semaphore).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        TestCyclicBarrierAndCountDownLatch testCyclicBarrierAndCountDownLatch = new TestCyclicBarrierAndCountDownLatch();
        switch (v.getId()){
            case R.id.tv_cyclic_barrier:
                testCyclicBarrierAndCountDownLatch.startTestCyclicBarrier();
                break;
            case R.id.tv_count_down_latch:
                testCyclicBarrierAndCountDownLatch.startTestCountDownLatch();
                break;
            case R.id.tv_semaphore:
                TestSemaphore testSemaphore = new TestSemaphore();
                testSemaphore.startTestSemaphore();
                break;
        }
    }
}
