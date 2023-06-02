package com.d6zone.android.app.widget.blurry.internal;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.view.animation.AlphaAnimation;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.AbstractDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.postprocessors.IterativeBoxBlurPostProcessor;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

/**
 * Copyright (C) 2018 Wasabeef
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public final class Helper {

  public static void setBackground(View v, Drawable drawable) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
      v.setBackground(drawable);
    } else {
      v.setBackgroundDrawable(drawable);
    }
  }

  public static boolean hasZero(int... args) {
    for (int num : args) {
      if (num == 0) {
        return true;
      }
    }
    return false;
  }

  public static void animate(View v, int duration) {
    AlphaAnimation alpha = new AlphaAnimation(0f, 1f);
    alpha.setDuration(duration);
    v.startAnimation(alpha);
  }

  public static void showUrlBlur(SimpleDraweeView draweeView, String url, int iterations, int blurRadius) {
    try {
      Uri uri = Uri.parse(url);
      ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
              .setPostprocessor(new IterativeBoxBlurPostProcessor(iterations, blurRadius))
              .build();
      AbstractDraweeController controller = Fresco.newDraweeControllerBuilder()
              .setOldController(draweeView.getController())
              .setImageRequest(request)
              .build();
      draweeView.setController(controller);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
