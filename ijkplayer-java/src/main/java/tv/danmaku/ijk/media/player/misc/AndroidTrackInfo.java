/*
 * Copyright (C) 2015 Bilibili
 * Copyright (C) 2015 Zhang Rui <bbcallen@gmail.com>
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

package tv.danmaku.ijk.media.player.misc;

import android.annotation.TargetApi;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.os.Build;

/*
 * 视频跟踪信息类
 * 内部存放MediaPlayer.TrackInfo实例
 * 开放方法获取的是本类的数组：AndroidTrackInfo[]
 */

public class AndroidTrackInfo implements ITrackInfo {
    private final MediaPlayer.TrackInfo mTrackInfo;

    //开放类，传入MediaPlayer
    //        调用私有方法fromTrackInfo
    //        得到AndroidTrackInfo[]
    public static AndroidTrackInfo[] fromMediaPlayer(MediaPlayer mp) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            //调用私有类
            return fromTrackInfo(mp.getTrackInfo());

        return null;
    }

    //私有类，传入MediaPlayer.TrackInfo[]
    //        调用私有构造方法，构造AndroidTrackInfo
    //        得到AndroidTrackInfo[]
    private static AndroidTrackInfo[] fromTrackInfo(MediaPlayer.TrackInfo[] trackInfos) {
        if (trackInfos == null)
            return null;

        AndroidTrackInfo androidTrackInfo[] = new AndroidTrackInfo[trackInfos.length];
        for (int i = 0; i < trackInfos.length; ++i) {
            androidTrackInfo[i] = new AndroidTrackInfo(trackInfos[i]);
        }

        return androidTrackInfo;
    }

    //私有构造类，构造AndroidTrackInfo
    private AndroidTrackInfo(MediaPlayer.TrackInfo trackInfo) {
        mTrackInfo = trackInfo;
    }

    //获取视频格式
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public IMediaFormat getFormat() {
        //判断AndroidTrackInfo实例内部有没有存储MediaPlayer.TrackInfo实例
        if (mTrackInfo == null)
            return null;

        //判断版本是否低于KITKAT
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            return null;

        //从本类内存储的MediaPlayer.TrackInfo实例获取视频格式
        //获取的视频格式为空就返回空值
        MediaFormat mediaFormat = mTrackInfo.getFormat();
        if (mediaFormat == null)
            return null;

        //获取的视频格式不为空，用MediaFormat构造AndroidMediaFormat实例并返回
        return new AndroidMediaFormat(mediaFormat);
    }

    //获取视频语言信息
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public String getLanguage() {
        if (mTrackInfo == null)
            return "und";

        return mTrackInfo.getLanguage();
    }

    //获取视频信息跟踪类型
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public int getTrackType() {
        if (mTrackInfo == null)
            return MEDIA_TRACK_TYPE_UNKNOWN;

        return mTrackInfo.getTrackType();
    }

    //返回getClass().getSimpleName() + "{" + mTrackInfo.toString()或null + "}"
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public String toString() {
        StringBuilder out = new StringBuilder(128);
        out.append(getClass().getSimpleName());
        out.append('{');
        if (mTrackInfo != null) {
            out.append(mTrackInfo.toString());
        } else {
            out.append("null");
        }
        out.append('}');
        return out.toString();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public String getInfoInline() {
        if (mTrackInfo != null) {
            return mTrackInfo.toString();
        } else {
            return "null";
        }
    }
}
