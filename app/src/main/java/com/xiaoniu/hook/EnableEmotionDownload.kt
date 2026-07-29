/*
 * QAuxiliary - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2023 QAuxiliary developers
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

package com.xiaoniu.hook

import com.github.kyuubiran.ezxhelper.utils.hookReturnConstant
import com.fanqie.xfqdeobf.base.annotation.FunctionHookEntry
import com.fanqie.xfqdeobf.base.annotation.UiItemAgentEntry
import com.fanqie.xfqdeobf.dsl.FunctionEntryRouter
import com.fanqie.xfqdeobf.hook.CommonSwitchFunctionHook
import com.fanqie.xfqdeobf.util.QQVersion
import com.fanqie.xfqdeobf.util.TIMVersion
import com.fanqie.xfqdeobf.util.dexkit.DexKit
import com.fanqie.xfqdeobf.util.dexkit.EmotionDownloadEnableSwitch
import com.fanqie.xfqdeobf.util.requireMinQQVersion
import com.fanqie.xfqdeobf.util.requireMinTimVersion

@FunctionHookEntry
@UiItemAgentEntry
object EnableEmotionDownload : CommonSwitchFunctionHook(arrayOf(EmotionDownloadEnableSwitch)) {
    override val name = "允许保存图片表情"

    override fun initOnce(): Boolean {
        DexKit.requireMethodFromCache(EmotionDownloadEnableSwitch).hookReturnConstant(false)
        return true
    }

    override val uiItemLocation = FunctionEntryRouter.Locations.Auxiliary.MESSAGE_CATEGORY

    override val isAvailable = requireMinQQVersion(QQVersion.QQ_8_9_80) || requireMinTimVersion(TIMVersion.TIM_4_0_95_BETA)
}
