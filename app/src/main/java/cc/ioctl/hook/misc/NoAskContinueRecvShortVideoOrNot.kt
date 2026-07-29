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

package cc.ioctl.hook.misc

import android.content.Intent
import com.fanqie.xfqdeobf.util.xpcompat.XC_MethodHook
import com.fanqie.xfqdeobf.base.IDynamicHook
import com.fanqie.xfqdeobf.base.annotation.FunctionHookEntry
import com.fanqie.xfqdeobf.base.annotation.UiItemAgentEntry
import com.fanqie.xfqdeobf.dsl.FunctionEntryRouter
import com.fanqie.xfqdeobf.router.decorator.BaseSwitchFunctionDecorator
import com.fanqie.xfqdeobf.router.decorator.IStartActivityHookDecorator
import com.fanqie.xfqdeobf.router.dispacher.StartActivityHook
import com.fanqie.xfqdeobf.util.hostInfo


@UiItemAgentEntry
@FunctionHookEntry
object NoAskContinueRecvShortVideoOrNot : BaseSwitchFunctionDecorator(), IStartActivityHookDecorator {

    override val name = "去除是否继续接收弹窗"

    override val description = "去除\"WiFi已断开，是否继续使用移动流量下载视频？\"弹窗(自动停止)"

    override val dispatcher: IDynamicHook = StartActivityHook

    override val uiItemLocation: Array<String> = FunctionEntryRouter.Locations.Simplify.UI_MISC

    override fun onStartActivityIntent(intent: Intent, param: XC_MethodHook.MethodHookParam): Boolean {
        val pkgName = hostInfo.packageName
        if (intent.`package` == pkgName) {
            val target = intent.component?.className
            if (target == "com.tencent.mobileqq.activity.DialogActivity") {
                // The code is really messy in QQAppInterface and DialogActivity.
                // I can hardly understand what they are freaking doing.
                val isInterceptRequired = intent.hasExtra("continue_receive_short_video_or_not") ||
                        (intent.getIntExtra("key_dialog_type", 0) == 0)
                if (isInterceptRequired) {
                    param.result = null
                    return true
                }
            }
        }
        return false
    }

}
