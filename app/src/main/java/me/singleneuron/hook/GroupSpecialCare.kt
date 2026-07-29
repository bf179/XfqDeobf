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

package me.singleneuron.hook

import com.github.kyuubiran.ezxhelper.utils.hookAfter
import com.fanqie.xfqdeobf.util.xpcompat.XposedHelpers
import com.fanqie.xfqdeobf.base.annotation.FunctionHookEntry
import com.fanqie.xfqdeobf.base.annotation.UiItemAgentEntry
import com.fanqie.xfqdeobf.dsl.FunctionEntryRouter
import com.fanqie.xfqdeobf.hook.CommonSwitchFunctionHook
import com.fanqie.xfqdeobf.util.SyncUtils
import com.fanqie.xfqdeobf.util.dexkit.DexKit
import com.fanqie.xfqdeobf.util.dexkit.GroupSpecialCare_getCareTroopMemberMsgText
import xyz.nextalone.util.throwOrTrue
import java.util.concurrent.ConcurrentHashMap

@UiItemAgentEntry
@FunctionHookEntry
object GroupSpecialCare : CommonSwitchFunctionHook(
    SyncUtils.PROC_MAIN or SyncUtils.PROC_MSF,
    arrayOf(GroupSpecialCare_getCareTroopMemberMsgText)
) {

    override val name = "关闭群普通消息特别关心提示"
    override val description: String = "仅在特别关心发送群消息时提示，阻止群内存在特别关心消息时其他成员普通消息使用特别关心提示"

    override val uiItemLocation = FunctionEntryRouter.Locations.Auxiliary.NOTIFICATION_CATEGORY

    override fun initOnce() = throwOrTrue {

        DexKit.requireMethodFromCache(GroupSpecialCare_getCareTroopMemberMsgText).hookAfter { param ->
            val map: ConcurrentHashMap<String, Boolean> = XposedHelpers.getObjectField(param.thisObject, "h") as ConcurrentHashMap<String, Boolean>
            val frienduin: String = XposedHelpers.getObjectField(param.args[1], "frienduin") as String
            map.remove(frienduin)
        }

    }
}
