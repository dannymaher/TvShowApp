package uk.ac.tees.tvshowapp;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * The ShakeDetector uses SensorEventListeners to detect when a value is changed beyond the
 * thresholds set, and react by calling the onShake() method
 */
public class ShakeDetector implements SensorEventListener {

    /**
     * Arbitrary G-Force threshold that must be passed for a shake to be detected
     */
    private static final float SHAKE_THRESHOLD_GRAVITY = 2.7F;
    /**
     * Interval between shakes where shakes won't be counted
     */
    private static final int SHAKE_INTERVAL_MS = 500;
    /**
     * If no shakes happen for this amount of ms, the shake count will reset to 0
     */
    private static final int SHAKE_COUNT_RESET_TIME_MS = 3000;

    private OnShakeListener listener;
    private long shakeTimestamp;
    private int shakeCount;

    /**
     * Sets up a listener to be added to the Shake Detector
     *
     * @param listener an OnShakeListener that can be used by the methods
     */
    public void setOnShakeListener(OnShakeListener listener) {
        this.listener = listener;
    }

    /**
     * A blank listener that will hold the basic onShake method
     */
    public interface OnShakeListener {
        /**
         * Called when a shake is detected by onSensorChanged()
         *
         * @param count number of shakes that have occured
         */
        void onShake(int count);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // This method is called if the accuracy of the sensor is changed, which is irrelevant to
        // this shake detector class
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (listener != null) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            float gX = x / SensorManager.GRAVITY_EARTH;
            float gY = y / SensorManager.GRAVITY_EARTH;
            float gZ = z / SensorManager.GRAVITY_EARTH;

            // gForce will be close to 1 when there is no movement.
            float gForce = (float) Math.sqrt(gX * gX + gY * gY + gZ * gZ);

            if (gForce > SHAKE_THRESHOLD_GRAVITY) {
                final long now = System.currentTimeMillis();
                // ignore shake events too close to each other (500ms)
                if (shakeTimestamp + SHAKE_INTERVAL_MS > now) {
                    return;
                }

                // reset the shake count after 3 seconds of no shakes
                if (shakeTimestamp + SHAKE_COUNT_RESET_TIME_MS < now) {
                    shakeCount = 0;
                }

                shakeTimestamp = now;
                shakeCount++;

                listener.onShake(shakeCount);
            }
        }
    }
}
