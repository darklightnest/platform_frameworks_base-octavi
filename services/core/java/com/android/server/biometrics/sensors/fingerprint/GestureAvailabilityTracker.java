/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.server.biometrics.sensors.fingerprint;

import android.hardware.fingerprint.IFingerprintClientActiveCallback;
import android.os.RemoteException;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Keeps track of sensor gesture availability (e.g. swipe), and notifies clients when its
 * availability changes
 */
class GestureAvailabilityTracker {
    private final CopyOnWriteArrayList<IFingerprintClientActiveCallback> mClientActiveCallbacks =
            new CopyOnWriteArrayList<>();

    void registerCallback(IFingerprintClientActiveCallback callback) {
        mClientActiveCallbacks.add(callback);
    }

    void removeCallback(IFingerprintClientActiveCallback callback) {
        mClientActiveCallbacks.remove(callback);
    }

    void notifyClientActiveCallbacks(boolean isActive) {
        for (IFingerprintClientActiveCallback callback : mClientActiveCallbacks) {
            try {
                callback.onClientActiveChanged(isActive);
            } catch (RemoteException re) {
                // If the remote is dead, stop notifying it
                mClientActiveCallbacks.remove(callback);
            }
        }
    }
}
