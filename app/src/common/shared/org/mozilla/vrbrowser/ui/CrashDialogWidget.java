/* -*- Mode: Java; c-basic-offset: 4; tab-width: 4; indent-tabs-mode: nil; -*-
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.vrbrowser.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.vrbrowser.R;
import org.mozilla.vrbrowser.SessionStore;
import org.mozilla.vrbrowser.WidgetPlacement;
import org.mozilla.vrbrowser.audio.AudioEngine;

public class CrashDialogWidget extends UIWidget {
    private static final String LOGTAG = "VRB";

    private Button mLearnMoreButton;
    private Button mDontSendButton;
    private Button mSendDataButton;
    private AudioEngine mAudio;

    public CrashDialogWidget(Context aContext) {
        super(aContext);
        initialize(aContext);
    }

    public CrashDialogWidget(Context aContext, AttributeSet aAttrs) {
        super(aContext, aAttrs);
        initialize(aContext);
    }

    public CrashDialogWidget(Context aContext, AttributeSet aAttrs, int aDefStyle) {
        super(aContext, aAttrs, aDefStyle);
        initialize(aContext);
    }

    private void initialize(Context aContext) {
        inflate(aContext, R.layout.crash_dialog, this);

        mLearnMoreButton = findViewById(R.id.learnMoreButton);
        mDontSendButton = findViewById(R.id.dontSendButton);
        mSendDataButton = findViewById(R.id.sendDataButton);

        mLearnMoreButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAudio != null) {
                    mAudio.playSound(AudioEngine.Sound.CLICK);
                }

                GeckoSession session = SessionStore.get().getCurrentSession();
                if (session == null) {
                    int sessionId = SessionStore.get().createSession();
                    SessionStore.get().setCurrentSession(sessionId);
                }

                SessionStore.get().loadUri(getContext().getString(R.string.crash_dialog_learn_more_url));

                hide();
            }
        });
        mDontSendButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAudio != null) {
                    mAudio.playSound(AudioEngine.Sound.CLICK);
                }


            }
        });
        mSendDataButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAudio != null) {
                    mAudio.playSound(AudioEngine.Sound.CLICK);
                }


            }
        });

        mAudio = AudioEngine.fromContext(aContext);
    }

    @Override
    protected void initializeWidgetPlacement(WidgetPlacement aPlacement) {
        aPlacement.visible = false;
        aPlacement.width =  WidgetPlacement.dpDimension(getContext(), R.dimen.crash_dialog_width);
        aPlacement.height = WidgetPlacement.dpDimension(getContext(), R.dimen.crash_dialog_width);
        aPlacement.parentAnchorX = 0.5f;
        aPlacement.parentAnchorY = 0.5f;
        aPlacement.anchorX = 0.5f;
        aPlacement.anchorY = 0.5f;
        aPlacement.translationZ = WidgetPlacement.unitFromMeters(getContext(), R.dimen.crash_dialog_world_z);
    }
}
