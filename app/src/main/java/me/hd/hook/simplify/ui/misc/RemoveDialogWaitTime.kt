/*
 * QAuxiliary - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2026 QAuxiliary developers
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

package me.hd.hook.simplify.ui.misc

import com.fanqie.xfqdeobf.base.annotation.FunctionHookEntry
import com.fanqie.xfqdeobf.base.annotation.UiItemAgentEntry
import com.fanqie.xfqdeobf.dsl.FunctionEntryRouter
import com.fanqie.xfqdeobf.hook.CommonSwitchFunctionHook
import com.fanqie.xfqdeobf.util.QQVersion
import com.fanqie.xfqdeobf.util.SyncUtils
import com.fanqie.xfqdeobf.util.requireMinQQVersion
import me.hd.util.hookBeforeIfEnabled
import me.hd.util.name
import me.hd.util.parameters
import me.hd.util.singleMethod
import me.hd.util.toHostClass

@FunctionHookEntry
@UiItemAgentEntry
object RemoveDialogWaitTime : CommonSwitchFunctionHook(
    targetProc = SyncUtils.PROC_ANY
) {
    override val name = "移除弹窗等待时间"
    override val description = "移除弹窗中, 继续按钮的5s, 10s等待时间"
    override val uiItemLocation = FunctionEntryRouter.Locations.Simplify.UI_MISC
    override val isAvailable = requireMinQQVersion(QQVersion.QQ_8_9_88)

    override fun initOnce(): Boolean {
        "com.tencent.mobileqq.utils.DialogUtil".toHostClass()
            .singleMethod {
                name("createCountdownDialog") &&
                    parameters(null, null, null, null, null, null, Int::class.java, null, null, null)
            }.hookBeforeIfEnabled(this) { param ->
                param.args[6] = 0
            }
        return true
    }
}
