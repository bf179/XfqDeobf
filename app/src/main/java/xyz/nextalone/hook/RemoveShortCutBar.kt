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
package xyz.nextalone.hook

import com.fanqie.xfqdeobf.base.annotation.FunctionHookEntry
import com.fanqie.xfqdeobf.base.annotation.UiItemAgentEntry
import com.fanqie.xfqdeobf.dsl.FunctionEntryRouter
import com.fanqie.xfqdeobf.hook.CommonSwitchFunctionHook
import com.fanqie.xfqdeobf.util.Initiator
import com.fanqie.xfqdeobf.util.QQVersion
import com.fanqie.xfqdeobf.util.dexkit.DexKit
import com.fanqie.xfqdeobf.util.dexkit.NTroopAppShortcutBarHelper_resumeAppShorcutBar
import com.fanqie.xfqdeobf.util.requireMinQQVersion
import xyz.nextalone.util.replace
import xyz.nextalone.util.throwOrTrue

@FunctionHookEntry
@UiItemAgentEntry
object RemoveShortCutBar : CommonSwitchFunctionHook(arrayOf(NTroopAppShortcutBarHelper_resumeAppShorcutBar)) {

    override val name = "隐藏文本框上方快捷方式"

    override val uiItemLocation = FunctionEntryRouter.Locations.Simplify.CHAT_OTHER

    override fun initOnce() = throwOrTrue {
        val resumeAppShorcutBar = DexKit.getMethodDescFromCache(NTroopAppShortcutBarHelper_resumeAppShorcutBar)
        resumeAppShorcutBar!!.getMethodInstance(Initiator.getHostClassLoader()).replace(this, null)
    }

    override val isAvailable = requireMinQQVersion(QQVersion.QQ_8_5_0)
}
