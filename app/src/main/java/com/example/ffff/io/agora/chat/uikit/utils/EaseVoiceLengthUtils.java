package io.agora.chat.uikit.utils;

import android.content.Context;

/**
 * use to control voice view's length
 */
public class EaseVoiceLengthUtils {
    /**
     * Get the length of the voice
     * @param context
     * @param voiceLength
     * @return
     */
    public static int getVoiceLength(Context context, int voiceLength) {
        float maxLength = io.agora.chat.uikit.utils.EaseUtils.getScreenInfo(context)[0] / 4 - io.agora.chat.uikit.utils.EaseUtils.dip2px(context, 10);
        float paddingLeft;
        if(voiceLength <= 20) {
            paddingLeft = voiceLength / 20f * maxLength + io.agora.chat.uikit.utils.EaseUtils.dip2px(context, 10);
        }else {
            paddingLeft = maxLength + io.agora.chat.uikit.utils.EaseUtils.dip2px(context, 10);
        }
        return (int) paddingLeft;
    }
}
