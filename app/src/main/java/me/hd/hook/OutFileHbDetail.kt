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

package me.hd.hook

import android.widget.TextView
import com.github.kyuubiran.ezxhelper.utils.hookAfter
import com.xiaoniu.util.ContextUtils
import com.fanqie.xfqdeobf.base.annotation.FunctionHookEntry
import com.fanqie.xfqdeobf.base.annotation.UiItemAgentEntry
import com.fanqie.xfqdeobf.dsl.FunctionEntryRouter
import com.fanqie.xfqdeobf.hook.CommonSwitchFunctionHook
import com.fanqie.xfqdeobf.util.QQVersion
import com.fanqie.xfqdeobf.util.SyncUtils
import com.fanqie.xfqdeobf.util.Toasts
import com.fanqie.xfqdeobf.util.requireMinQQVersion
import xyz.nextalone.util.SystemServiceUtils.copyToClipboard
import xyz.nextalone.util.get
import xyz.nextalone.util.invoke
import xyz.nextalone.util.method
import java.io.File

@FunctionHookEntry
@UiItemAgentEntry
object OutFileHbDetail : CommonSwitchFunctionHook(
    targetProc = SyncUtils.PROC_ANY
) {
    override val name = "输出红包领取列表详情"
    override val description = "根据领取金额倒序排列, 输出详情到 QQ内部存储/cache/hd_temp/output.txt"
    override val uiItemLocation = FunctionEntryRouter.Locations.Auxiliary.EXPERIMENTAL_CATEGORY
    override val isAvailable = requireMinQQVersion(QQVersion.QQ_8_9_88)

    override fun initOnce(): Boolean {
        "Lcom/tenpay/sdk/activity/HbDetailActivity;->initToolBar(Landroid/view/View;)V".method.hookAfter { param ->
            val toolbarTitle = param.thisObject.get("toolbarTitle") as TextView
            toolbarTitle.setOnClickListener {
                val viewModel = param.thisObject.get("detailViewModel")!!
                val liveData = viewModel.invoke("getReceivers")!!
                val receivers = liveData.invoke("getValue") as List<*>
                val receiverInfoList = receivers.sortedByDescending { it.get("amount") as Int }.map {
                    "${it.get("createTime")}    ${it.get("recvName")}(${it.get("recvUin")})    ${it.get("amount")}"
                }
                val context = ContextUtils.getCurrentActivity()
                val outFile = File(context.externalCacheDir, "hd_temp/output.txt").apply { parentFile!!.mkdirs() }
                outFile.writeText(receiverInfoList.joinToString("\n"))
                copyToClipboard(context, outFile.absolutePath)
                Toasts.success(context, "已复制路径到剪贴板")
            }
        }
        return true
    }
}
