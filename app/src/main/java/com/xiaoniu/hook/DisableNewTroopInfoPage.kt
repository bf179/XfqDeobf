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

import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookReturnConstant
import com.github.kyuubiran.ezxhelper.utils.paramCount
import com.fanqie.xfqdeobf.base.annotation.FunctionHookEntry
import com.fanqie.xfqdeobf.base.annotation.UiItemAgentEntry
import com.fanqie.xfqdeobf.dsl.FunctionEntryRouter
import com.fanqie.xfqdeobf.hook.CommonSwitchFunctionHook
import com.fanqie.xfqdeobf.util.QQVersion
import com.fanqie.xfqdeobf.util.dexkit.DexKit
import com.fanqie.xfqdeobf.util.dexkit.TroopInfoCardPageABConfig
import com.fanqie.xfqdeobf.util.requireRangeQQVersion
import xyz.nextalone.util.throwOrTrue

@FunctionHookEntry
@UiItemAgentEntry
object DisableNewTroopInfoPage : CommonSwitchFunctionHook(arrayOf(TroopInfoCardPageABConfig)) {

    override val name = "禁用新版群资料页"
    override val description = "新版群资料页功能缺失，中看不中用，遂禁用之"
    override val uiItemLocation = FunctionEntryRouter.Locations.Auxiliary.GROUP_CATEGORY
    override val isAvailable = requireRangeQQVersion(QQVersion.QQ_8_9_78, QQVersion.QQ_9_0_71)

    override fun initOnce() = throwOrTrue {
        DexKit.requireClassFromCache(TroopInfoCardPageABConfig).findMethod {
            returnType == Boolean::class.java && paramCount == 0
        }.hookReturnConstant(false)
    }
}