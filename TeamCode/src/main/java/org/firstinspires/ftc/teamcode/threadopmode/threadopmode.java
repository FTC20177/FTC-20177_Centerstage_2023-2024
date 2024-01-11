package org.firstinspires.ftc.teamcode.threadopmode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import java.util.ArrayList;
import java.util.List;

/**
 * A type of {@link OpMode} that contains threads to be ran in parallel periodically.
 * Register threads with {@link threadopmode#registerThread(TensorFlow_Thread)}
 */
public abstract class threadopmode extends OpMode {
    private List<TensorFlow_Thread> threads = new ArrayList<>();

    /**
     * Registers a new {@link TensorFlow_Thread} to be ran periodically.
     * Registered threads will automatically be started during {@link OpMode#start()} and stopped during {@link OpMode#stop()}.
     *
     * @param taskThread A {@link TensorFlow_Thread} object to be ran periodically.
     */
    public final void registerThread(TensorFlow_Thread taskThread) {
        threads.add(taskThread);
    }

    /**
     * Contains code to be ran before the OpMode is started. Similar to {@link OpMode#init()}.
     */
    public abstract void mainInit();
    /**
     * Contains code to be ran periodically in the MAIN thread. Similar to {@link OpMode#loop()}.
     */
    public abstract void mainLoop();

    /**
     * Should not be called by subclass.
     */
    @Override
    public final void init() {
        mainInit();
    }

    /**
     * Should not be called by subclass.
     */
    @Override
    public final void start() {
        for(TensorFlow_Thread taskThread : threads) {
            taskThread.start();
        }
    }

    /**
     * Should not be called by subclass.
     */
    @Override
    public final void loop() {
        mainLoop();


    }

    /**
     * Should not be called by subclass.
     */
    @Override
    public final void stop() {
        for(TensorFlow_Thread taskThread : threads) {
            taskThread.stop();
        }
    }
}