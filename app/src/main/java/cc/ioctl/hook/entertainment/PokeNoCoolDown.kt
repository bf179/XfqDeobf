/*
 * QAuxiliary - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2023 QAuxiliary developers
 * https://github.com/cinit/QAuxiliary
 *
 * This software is non-free but opensource software: you can redistribute it
 * and/or modify it under the terms of the qwq233 Universal License
 * as published on https://github.com/qwq233/license; either
 * version 2 of the License, or any later version and our EULA as published
 * by QAuxiliary contributors.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the qwq233 Universal License for more details.
 *
 * See
 * <https://github.com/qwq233/license>
 * <https://github.com/cinit/QAuxiliary/blob/master/LICENSE.md>.
 */

package cc.ioctl.hook.entertainment

import cc.ioctl.util.hookBeforeIfEnabled
import com.fanqie.xfqdeobf.base.annotation.FunctionHookEntry
import com.fanqie.xfqdeobf.base.annotation.UiItemAgentEntry
import com.fanqie.xfqdeobf.dsl.FunctionEntryRouter
import com.fanqie.xfqdeobf.hook.CommonSwitchFunctionHook
import com.fanqie.xfqdeobf.util.QQVersion
import com.fanqie.xfqdeobf.util.dexkit.DexKit
import com.fanqie.xfqdeobf.util.dexkit.PaiYiPaiHandler_canSendReq
import com.fanqie.xfqdeobf.util.requireMinQQVersion

@FunctionHookEntry
@UiItemAgentEntry
object PokeNoCoolDown : CommonSwitchFunctionHook(arrayOf(PaiYiPaiHandler_canSendReq)) {

    override val name = "去除戳一戳时间限制"

    override val description = "去除戳一戳的冷却时间限制(本来为10秒)"

    override val uiItemLocation: Array<String> = FunctionEntryRouter.Locations.Entertainment.ENTERTAIN_CATEGORY

    override val isAvailable: Boolean get() = requireMinQQVersion(QQVersion.QQ_8_5_0)

    override fun initOnce(): Boolean {
        // Lcom/tencent/mobileqq/paiyipai/PaiYiPaiHandler;->canSendReq(Ljava/lang/String;)Z
        val canSendReq = DexKit.requireMethodFromCache(PaiYiPaiHandler_canSendReq)
        hookBeforeIfEnabled(canSendReq) {
            it.result = true
        }
        return true
    }

}
