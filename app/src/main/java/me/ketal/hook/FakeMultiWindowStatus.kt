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

package me.ketal.hook

import android.app.Activity
import com.fanqie.xfqdeobf.util.SyncUtils
import com.fanqie.xfqdeobf.base.annotation.FunctionHookEntry
import com.fanqie.xfqdeobf.base.annotation.UiItemAgentEntry
import com.fanqie.xfqdeobf.dsl.FunctionEntryRouter
import com.fanqie.xfqdeobf.hook.CommonSwitchFunctionHook
import xyz.nextalone.util.replace
import xyz.nextalone.util.throwOrTrue

@FunctionHookEntry
@UiItemAgentEntry
object FakeMultiWindowStatus : CommonSwitchFunctionHook(
    SyncUtils.PROC_MAIN or SyncUtils.PROC_PEAK or SyncUtils.PROC_TOOL
) {
    override val name = "伪装处于非多窗口模式"
    override val description = "用于分屏状态使用一些功能,例如扫码"
    override val uiItemLocation = FunctionEntryRouter.Locations.Auxiliary.MISC_CATEGORY

    override fun initOnce() = throwOrTrue {
        Activity::class.java.getDeclaredMethod("isInMultiWindowMode")
            .replace(this, false)
    }
}
