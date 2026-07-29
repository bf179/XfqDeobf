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
package cc.ioctl.hook.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import cc.ioctl.util.HookUtils;
import com.fanqie.xfqdeobf.base.annotation.FunctionHookEntry;
import com.fanqie.xfqdeobf.base.annotation.UiItemAgentEntry;
import com.fanqie.xfqdeobf.dsl.FunctionEntryRouter.Locations.Simplify;
import com.fanqie.xfqdeobf.hook.CommonSwitchFunctionHook;
import com.fanqie.xfqdeobf.util.Initiator;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

//屏蔽抖动窗口 作用暂时不明
@UiItemAgentEntry
@FunctionHookEntry
public class DisableShakeWindow extends CommonSwitchFunctionHook {

    public static final DisableShakeWindow INSTANCE = new DisableShakeWindow();

    private DisableShakeWindow() {
        super("rq_disable_shake_window");
    }

    @NonNull
    @Override
    public String getName() {
        return "屏蔽抖动窗口";
    }

    @Nullable
    @Override
    public String getDescription() {
        return "作用暂时不明";
    }

    @NonNull
    @Override
    public String[] getUiItemLocation() {
        return Simplify.CHAT_OTHER;
    }

    @Override
    public boolean initOnce() {
        for (int i = 1; i < 4; i++) {
            for (Method m : Initiator
                .load("com.tencent.mobileqq.activity.aio.helper.AIOShakeHelper$" + i).getDeclaredMethods()) {
                if (m.getName().equals("run") && !Modifier.isStatic(m.getModifiers())) {
                    HookUtils.hookBeforeIfEnabled(this, m, param -> param.setResult(null));
                    return true;
                }
            }
        }
        return false;
    }
}
