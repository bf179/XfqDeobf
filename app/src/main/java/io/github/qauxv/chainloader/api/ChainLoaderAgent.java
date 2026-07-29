/*
 * QAuxiliary - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2025 QAuxiliary developers
 * https://github.com/cinit/QAuxiliary
 *
 * This software is an opensource software: you can redistribute it
 * and/or modify it under the terms of the General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version as published
 * by QAuxiliary contributors.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the General Public License for more details.
 *
 * You should have received a copy of the General Public License
 * along with this software.
 * If not, see
 * <https://github.com/cinit/QAuxiliary/blob/master/LICENSE.md>.
 */

package com.fanqie.xfqdeobf.chainloader.api;

import android.app.Application;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import com.fanqie.xfqdeobf.loader.hookapi.IHookBridge;
import com.fanqie.xfqdeobf.poststartup.StartupInfo;
import com.fanqie.xfqdeobf.util.HostInfo;
import com.fanqie.xfqdeobf.util.Initiator;
import com.fanqie.xfqdeobf.util.SyncUtils;

@Keep
public class ChainLoaderAgent {

    private ChainLoaderAgent() {
        throw new AssertionError("no instance");
    }

    @NonNull
    public static ClassLoader getModuleClassLoader() {
        ClassLoader cl = ChainLoaderAgent.class.getClassLoader();
        assert cl != null;
        return cl;
    }

    @NonNull
    public static ClassLoader getHostClassLoader() {
        return Initiator.getHostClassLoader();
    }

    @NonNull
    public static Application getHostApplication() {
        return HostInfo.hostInfo.getApplication();
    }

    @NonNull
    public static IHookBridge getHookBridge() {
        return StartupInfo.requireHookBridge();
    }

    @NonNull
    public static String getProcessName() {
        return SyncUtils.getProcessName();
    }

}
