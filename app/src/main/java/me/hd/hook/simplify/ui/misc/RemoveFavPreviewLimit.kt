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
import com.fanqie.xfqdeobf.util.QQVersion
import com.fanqie.xfqdeobf.util.SyncUtils
import com.fanqie.xfqdeobf.util.requireMinQQVersion
import me.hd.util.hookAfterIfEnabled
import me.hd.util.parameters
import me.hd.util.returnType
import me.hd.util.singleMethod
import me.hd.util.toClass
import me.ketal.base.PluginDelayableHook
import xyz.nextalone.util.set

@FunctionHookEntry
@UiItemAgentEntry
object RemoveFavPreviewLimit : PluginDelayableHook("hd_fav_preview_limit") {
    override val preference = uiSwitchPreference {
        title = "移除收藏预览限制"
    }
    override val pluginID = "qqfav.apk"
    override val targetProcesses = SyncUtils.PROC_QQFAV
    override val uiItemLocation = FunctionEntryRouter.Locations.Simplify.UI_MISC
    override val isAvailable = requireMinQQVersion(QQVersion.QQ_8_9_85)

    override fun startHook(classLoader: ClassLoader): Boolean {
        "com.qqfav.FavoriteService".toClass(classLoader)
            .singleMethod {
                returnType("com.qqfav.data.FavoriteData".toClass(classLoader)) &&
                    parameters(Long::class.java, Boolean::class.java)
            }.hookAfterIfEnabled(this) { param ->
                param.result.set("mSecurityBeat", 0)
            }
        return true
    }
}
