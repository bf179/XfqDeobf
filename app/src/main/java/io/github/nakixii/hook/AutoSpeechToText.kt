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

package io.github.nakixii.hook

import cc.ioctl.util.hookBeforeIfEnabled
import com.fanqie.xfqdeobf.base.annotation.FunctionHookEntry
import com.fanqie.xfqdeobf.base.annotation.UiItemAgentEntry
import com.fanqie.xfqdeobf.dsl.FunctionEntryRouter
import com.fanqie.xfqdeobf.hook.CommonSwitchFunctionHook
import com.fanqie.xfqdeobf.util.Initiator
import com.fanqie.xfqdeobf.util.QQVersion
import com.fanqie.xfqdeobf.util.requireMinQQVersion

@FunctionHookEntry
@UiItemAgentEntry
object AutoSpeechToText : CommonSwitchFunctionHook() {
    override val name = "语音消息自动转文本"
    override val uiItemLocation = FunctionEntryRouter.Locations.Auxiliary.MESSAGE_CATEGORY
    override val isAvailable = requireMinQQVersion(QQVersion.QQ_9_1_70)

    override fun initOnce(): Boolean {
        hookBeforeIfEnabled(Initiator.loadClass(
            "com.tencent.mobileqq.aio.msglist.holder.component.ptt.AIOPttContentComponent"
        ).declaredMethods.single {
            it.returnType == Boolean::class.java && it.parameterTypes.contentEquals(arrayOf(Boolean::class.java))
        }) { it.result = true }

        return true
    }
}
