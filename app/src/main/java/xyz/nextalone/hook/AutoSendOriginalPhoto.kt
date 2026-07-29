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

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import cc.hicore.QApp.QAppUtils
import cc.ioctl.util.hookAfterIfEnabled
import com.github.kyuubiran.ezxhelper.utils.hookAfter
import com.github.kyuubiran.ezxhelper.utils.hookBefore
import com.github.kyuubiran.ezxhelper.utils.hookReturnConstant
import com.github.kyuubiran.ezxhelper.utils.paramCount
import com.fanqie.xfqdeobf.base.annotation.FunctionHookEntry
import com.fanqie.xfqdeobf.base.annotation.UiItemAgentEntry
import com.fanqie.xfqdeobf.dsl.FunctionEntryRouter
import com.fanqie.xfqdeobf.hook.CommonSwitchFunctionHook
import com.fanqie.xfqdeobf.util.Initiator
import com.fanqie.xfqdeobf.util.QQVersion
import com.fanqie.xfqdeobf.util.SyncUtils
import com.fanqie.xfqdeobf.util.TIMVersion
import com.fanqie.xfqdeobf.util.dexkit.DexKit
import com.fanqie.xfqdeobf.util.dexkit.Hd_AutoSendOriginalPhoto_guildPicker_Method
import com.fanqie.xfqdeobf.util.dexkit.Hd_AutoSendOriginalPhoto_photoListPanel_Method
import com.fanqie.xfqdeobf.util.requireMinQQVersion
import com.fanqie.xfqdeobf.util.requireMinTimVersion
import xyz.nextalone.util.clazz
import xyz.nextalone.util.findHostView
import xyz.nextalone.util.get
import xyz.nextalone.util.hookAfter
import xyz.nextalone.util.method
import xyz.nextalone.util.throwOrTrue

@FunctionHookEntry
@UiItemAgentEntry
object AutoSendOriginalPhoto : CommonSwitchFunctionHook(
    targetProc = SyncUtils.PROC_MAIN or SyncUtils.PROC_PEAK,
    targets = arrayOf(
        Hd_AutoSendOriginalPhoto_guildPicker_Method,
        Hd_AutoSendOriginalPhoto_photoListPanel_Method
    )
) {

    override val name = "聊天自动发送原图"

    override val uiItemLocation = FunctionEntryRouter.Locations.Auxiliary.CHAT_CATEGORY

    override fun initOnce() = throwOrTrue {
        if (requireMinTimVersion(TIMVersion.TIM_4_0_95_BETA)) {
            //半屏相册
            val photoPanelVB = Initiator.loadClass("com.tencent.mobileqq.aio.panel.photo.PhotoPanelVB")
            val bindViewAndDataMethod = photoPanelVB.getDeclaredMethod("Q0")
            val setCheckedMethod = photoPanelVB.getDeclaredMethod("s", Boolean::class.java)
            hookAfterIfEnabled(bindViewAndDataMethod) { param ->
                setCheckedMethod.invoke(param.thisObject, true)
            }
            //全屏相册
            hookAfterIfEnabled("Lcom/tencent/qqnt/qbasealbum/model/Config;->z()Z".method) { param ->
                param.result = true
            }
            return@throwOrTrue
        }

        if (QAppUtils.isQQnt()) {
            //普通模式半屏Panel的原图勾选框
            val clz = "com.tencent.mobileqq.aio.panel.photo.PhotoPanelVB".clazz!!
            clz.declaredMethods.single { it.name == "handleUIState" }.hookAfter { param ->
                clz.declaredMethods.last { it.paramCount == 1 && it.parameterTypes[0] == Boolean::class.java }.invoke(param.thisObject, true)
            }
            if (requireMinQQVersion(QQVersion.QQ_8_9_78)) {
                //启动参数类-isQualityRaw
                "Lcom/tencent/qqnt/qbasealbum/model/Config;->s()Z".method.hookReturnConstant(true)
            } else {
                //新全屏相册活动
                "com.tencent.qqnt.qbasealbum.WinkHomeActivity".clazz!!.method("onCreate")!!.hookBefore {
                    val ctx = it.thisObject as Activity
                    ctx.intent.putExtra("key_is_quality_raw", true)
                }
            }

            //频道半屏
            "com.tencent.guild.aio.panel.photo.GuildPhotoPanelVB".clazz!!.method("e")!!.hookAfter {
                val xVar = it.thisObject.get("a")
                val checkBox = xVar.get("d") as CheckBox
                checkBox.isChecked = true
            }
            //频道全屏和群聊部分情况全屏
            DexKit.requireMethodFromCache(Hd_AutoSendOriginalPhoto_guildPicker_Method).hookBefore {
                it.args.forEachIndexed { index, arg ->
                    if (arg is Boolean) {
                        it.args[index] = true
                    }
                }
            }
        }

        //截至2023.6.21，仍有一些项目在使用旧版组件（如频道），故保留其他hook
        DexKit.requireMethodFromCache(Hd_AutoSendOriginalPhoto_photoListPanel_Method).hookAfter {
            val ctx = it.thisObject as View
            val sendOriginPhotoCheckbox = ctx.findHostView<CheckBox>("h1y")
            sendOriginPhotoCheckbox?.isChecked = true
        }
        if (requireMinQQVersion(QQVersion.QQ_8_2_0)) {
            val newPhotoClass = "com.tencent.mobileqq.activity.photo.album.NewPhotoPreviewActivity".clazz
            val newPhotoOnCreate =
                newPhotoClass?.method("doOnCreate", Boolean::class.java, Bundle::class.java) ?: newPhotoClass?.method("onCreate", Void.TYPE, Bundle::class.java)
            newPhotoOnCreate?.hookAfter(this) {
                val ctx = it.thisObject as Activity
                val checkBox = ctx.findHostView<CheckBox>("h1y")
                checkBox?.isChecked = true
            }
        }
    }
}
