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

package com.fanqie.xfqdeobf.chainloader.detail.ui

import android.app.Activity
import android.view.View
import com.fanqie.xfqdeobf.activity.SettingsUiFragmentHostActivity
import com.fanqie.xfqdeobf.base.IUiItemAgent
import com.fanqie.xfqdeobf.base.annotation.FunctionHookEntry
import com.fanqie.xfqdeobf.base.annotation.UiItemAgentEntry
import com.fanqie.xfqdeobf.chainloader.detail.ExternalModuleManager
import com.fanqie.xfqdeobf.dsl.FunctionEntryRouter
import com.fanqie.xfqdeobf.hook.CommonConfigFunctionHook
import com.fanqie.xfqdeobf.util.SyncUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@FunctionHookEntry
@UiItemAgentEntry
object ExternalModuleConfigHook : CommonConfigFunctionHook(SyncUtils.PROC_ANY) {

    override val name: String = "加载外部插件"
    override val description: CharSequence = "加载兼容 QAuxiliary 私有 API 的第三方模块插件"

    // not used here
    override fun initOnce() = true
    override var isEnabled: Boolean
        get() = true
        set(value) {}
    override val uiItemLocation: Array<String> = FunctionEntryRouter.Locations.Auxiliary.EXPERIMENTAL_CATEGORY
    private val mStateFlow by lazy { MutableStateFlow(getStateFlowText()) }
    override val valueState: StateFlow<String?> by lazy { mStateFlow }

    private fun getStateFlowText(): String {
        try {
            val size = ExternalModuleManager.loadExternalModuleInfoList().size
            return if (size == 0) {
                "无"
            } else {
                "已启用 $size 个"
            }
        } catch (e: Exception) {
            // keep it simple, users may click this item to open the config page to see the full error message
            return "出错"
        }
    }

    fun notifyStateChanged() {
        mStateFlow.value = getStateFlowText()
    }

    override val onUiItemClickListener: (IUiItemAgent, Activity, View) -> Unit = { _, activity, _ ->
        SettingsUiFragmentHostActivity.startFragmentWithContext(activity, ExternalModuleConfigFragment::class.java)
    }

}
