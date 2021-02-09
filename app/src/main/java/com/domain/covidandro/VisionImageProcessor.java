/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.domain.covidandro;

import android.content.Context;
import android.os.Build.VERSION_CODES;

import androidx.annotation.RequiresApi;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageProxy;


import com.google.mlkit.common.MlKitException;

import java.util.concurrent.ExecutorService;

/**
 * An interface to process the images with different vision detectors and custom image models.
 */
public interface VisionImageProcessor {

    /**
     * Processes a bitmap image.
     */
//    void processBitmap(Bitmap bitmap, GraphicOverlay graphicOverlay);

    /**
     * Processes ByteBuffer image data, e.g. used for Camera1 live preview case.
     */


    /**
     * Processes ImageProxy image data, e.g. used for CameraX live preview case.
     */
    @RequiresApi(VERSION_CODES.KITKAT)
    void processImageProxy(ImageProxy image, GraphicOverlay graphicOverlay, ImageCapture imageCapture, ExecutorService executor, Context context)
            throws MlKitException;

    /**
     * Stops the underlying machine learning model and release resources.
     */
    void stop();
}
