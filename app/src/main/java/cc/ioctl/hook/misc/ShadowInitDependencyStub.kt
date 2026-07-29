/*
 * QAuxiliary - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2024 QAuxiliary developers
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

package cc.ioctl.hook.misc

import com.fanqie.xfqdeobf.base.annotation.FunctionHookEntry
import com.fanqie.xfqdeobf.hook.BasePersistBackgroundHook
import com.fanqie.xfqdeobf.step.DexDeobfStep
import com.fanqie.xfqdeobf.step.Step
import com.fanqie.xfqdeobf.util.dexkit.DexKit
import com.fanqie.xfqdeobf.util.dexkit.DexKitTarget
import com.fanqie.xfqdeobf.util.dexkit.NContactUtils_getBuddyName

@FunctionHookEntry
object ShadowInitDependencyStub : BasePersistBackgroundHook() {

    private val mDexDeobfIndexes: Array<DexKitTarget> = arrayOf(
        NContactUtils_getBuddyName
    )

    override fun initOnce(): Boolean {
        return true
    }

    override val isPreparationRequired: Boolean
        get() {
            return mDexDeobfIndexes.any {
                DexKit.isRunDexDeobfuscationRequired(it)
            }
        }

    override fun makePreparationSteps(): Array<Step> = mDexDeobfIndexes.map { DexDeobfStep(it) }.toTypedArray()

}
