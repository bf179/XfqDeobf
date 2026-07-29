/*
 * QAuxiliary - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2022 qwq233@qwq2333.top
 * https://github.com/cinit/QAuxiliary
 *
 * This software is non-free but opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as published
 * by QAuxiliary contributors.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 * <https://github.com/cinit/QAuxiliary/blob/master/LICENSE.md>.
 */
package cc.ioctl.util;

import android.app.Application;
import androidx.annotation.NonNull;

/**
 * Helper class for getting host information. Keep it as simple as possible.
 */
public class HostInfo {

    public static final String PACKAGE_NAME_QQ = "com.tencent.mobileqq";
    public static final String PACKAGE_NAME_QQ_INTERNATIONAL = "com.tencent.mobileqqi";
    public static final String PACKAGE_NAME_QQ_LITE = "com.tencent.qqlite";
    public static final String PACKAGE_NAME_QQ_HD = "com.tencent.minihd.qq";
    public static final String PACKAGE_NAME_TIM = "com.tencent.tim";
    public static final String PACKAGE_NAME_SELF = "com.fanqie.xfqdeobf";

    private HostInfo() {
        throw new AssertionError("No instance for you!");
    }

    @NonNull
    public static Application getApplication() {
        return com.fanqie.xfqdeobf.util.HostInfo.getHostInfo().getApplication();
    }

    @NonNull
    public static String getPackageName() {
        return com.fanqie.xfqdeobf.util.HostInfo.getHostInfo().getPackageName();
    }

    @NonNull
    public static String getAppName() {
        return com.fanqie.xfqdeobf.util.HostInfo.getHostInfo().getHostName();
    }

    @NonNull
    public static String getVersionName() {
        return com.fanqie.xfqdeobf.util.HostInfo.getHostInfo().getVersionName();
    }

    public static int getVersionCode32() {
        return com.fanqie.xfqdeobf.util.HostInfo.getHostInfo().getVersionCode32();
    }

    public static int getVersionCode() {
        return getVersionCode32();
    }

    public static long getLongVersionCode() {
        return com.fanqie.xfqdeobf.util.HostInfo.getHostInfo().getVersionCode();
    }

    public static boolean isInModuleProcess() {
        return com.fanqie.xfqdeobf.util.HostInfo.isInModuleProcess();
    }

    public static boolean isInHostProcess() {
        return !isInModuleProcess();
    }

    public static boolean isAndroidxFileProviderAvailable() {
        return com.fanqie.xfqdeobf.util.HostInfo.isAndroidxFileProviderAvailable();
    }

    public static boolean isTim() {
        return com.fanqie.xfqdeobf.util.HostInfo.isTim();
    }

    public static boolean isQQLite() {
        return PACKAGE_NAME_QQ_LITE.equals(getPackageName());
    }

    public static boolean isQQHD() {
        return PACKAGE_NAME_QQ_HD.equals(getPackageName());
    }

    public static boolean isPlayQQ() {
        return !com.fanqie.xfqdeobf.util.HostInfo.isPlayQQ();
    }

    public static boolean isQQ() {
        //Improve this method when supporting more clients.
        return !com.fanqie.xfqdeobf.util.HostInfo.isTim();
    }

    public static boolean requireMinQQVersion(long versionCode) {
        return isQQ() && getLongVersionCode() >= versionCode;
    }

    public static boolean requireMinPlayQQVersion(long versionCode) {
        return isPlayQQ() && getLongVersionCode() >= versionCode;
    }

    public static boolean requireMinTimVersion(long versionCode) {
        return isTim() && getLongVersionCode() >= versionCode;
    }
}
