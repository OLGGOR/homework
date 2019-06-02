package ru.sberbank.school.task10;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FixedThreadPoolTest {
    private static ThreadPool threadPool;

    @BeforeAll
    static void init() {
        threadPool = new FixedThreadPool(3);
    }

    @Test
    void start() {
        for (int i = 0; i < 10; i++) {
            threadPool.execute(() -> {
                        try {
                            Thread.sleep(1000);
                            System.out.println(Thread.currentThread().getName() + " done");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
            );
        }
    }

}