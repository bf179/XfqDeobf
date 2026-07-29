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
package cc.ioctl.hook.entertainment

import cc.hicore.QApp.QAppUtils
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.fanqie.xfqdeobf.base.annotation.FunctionHookEntry
import com.fanqie.xfqdeobf.base.annotation.UiItemAgentEntry
import com.fanqie.xfqdeobf.dsl.FunctionEntryRouter
import com.fanqie.xfqdeobf.hook.CommonSwitchFunctionHook
import com.fanqie.xfqdeobf.util.Initiator
import com.fanqie.xfqdeobf.util.dexkit.DexKit
import com.fanqie.xfqdeobf.util.dexkit.NBaseChatPie_mosaic
import xyz.nextalone.util.hookBefore
import xyz.nextalone.util.throwOrTrue

//聊天界面顶栏群名字/好友昵称自动打码
@FunctionHookEntry
@UiItemAgentEntry
object AutoMosaicName : CommonSwitchFunctionHook(arrayOf(NBaseChatPie_mosaic)) {

    override val name = "昵称/群名片打码"

    override val uiItemLocation = FunctionEntryRouter.Locations.Entertainment.ENTERTAIN_CATEGORY

    override fun initOnce() = throwOrTrue {
        if (QAppUtils.isQQnt()) {
            hookNt()
            return@throwOrTrue
        }
        DexKit.requireMethodFromCache(NBaseChatPie_mosaic).hookBefore(this) {
            it.args[0] = true
        }
    }

    private fun hookNt() {
        val ll = arrayOf(
            // "com.tencent.mobileqq.aio.title.AIOTitleVM",
            "com.tencent.mobileqq.aio.msglist.holder.component.avatar.AIOAvatarContentComponent",
            "com.tencent.mobileqq.aio.msglist.holder.component.nick.block.MainNickNameBlock"
        ).map {
            Initiator.load(it)!!
        }
        ll.forEach { clazz ->
            clazz.findMethod {
                parameterTypes.size == 1 && parameterTypes[0] == Boolean::class.java
            }.hookBefore(this) {
                it.args[0] = true
            }
        }

    }
}
