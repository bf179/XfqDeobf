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

package com.xiaoniu.hook

import android.content.Context
import cc.ioctl.util.HostInfo.isQQ
import cc.ioctl.util.afterHookIfEnabled
import com.fanqie.xfqdeobf.util.xpcompat.XposedHelpers
import com.fanqie.xfqdeobf.base.annotation.FunctionHookEntry
import com.fanqie.xfqdeobf.base.annotation.UiItemAgentEntry
import com.fanqie.xfqdeobf.dsl.FunctionEntryRouter.Locations.Auxiliary
import com.fanqie.xfqdeobf.hook.CommonSwitchFunctionHook
import com.fanqie.xfqdeobf.util.Initiator

@FunctionHookEntry
@UiItemAgentEntry
object FullTroopMemberJoinTime : CommonSwitchFunctionHook() {

    override val name = "显示完整群成员入群时间"

    override val description = "群成员资料卡中的入群时间始终精确到日"

    override val uiItemLocation: Array<String> = Auxiliary.PROFILE_CATEGORY

    override val isAvailable: Boolean get() = isQQ()    //新版资料卡是什么时候出现的？

    override fun initOnce(): Boolean {
        XposedHelpers.findAndHookMethod(
            Initiator.loadClass("com/tencent/mobileqq/profilecard/component/troop/ElegantProfileTroopMemInfoComponent"), "getTroopMemeJoinTime",
            Context::class.java, Initiator.loadClass("com/tencent/mobileqq/profilecard/data/ProfileCardInfo"), callback
        )
        return true
    }

    private val callback = afterHookIfEnabled {
        val s = it.result as String
        if (s.endsWith("年加入该群")) {
            val profile = it.args[1].javaClass.getDeclaredField("troopMemberCard").apply { isAccessible = true }.get(it.args[1])
            val time = profile.javaClass.getDeclaredField("joinTime").apply { isAccessible = true }.get(profile) as Long * 1000
            it.result = android.text.format.DateFormat.format("yyyy年MM月dd日加入该群", time)
        }
    }
}
