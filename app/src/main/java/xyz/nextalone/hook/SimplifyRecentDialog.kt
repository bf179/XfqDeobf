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
import com.fanqie.xfqdeobf.util.QQVersion
import com.fanqie.xfqdeobf.util.TIMVersion
import com.fanqie.xfqdeobf.util.requireMinQQVersion
import com.fanqie.xfqdeobf.util.requireMinTimVersion
import xyz.nextalone.base.MultiItemDelayableHook
import xyz.nextalone.util.clazz
import xyz.nextalone.util.get
import xyz.nextalone.util.hookBefore
import xyz.nextalone.util.method
import xyz.nextalone.util.throwOrTrue
import com.fanqie.xfqdeobf.util.PlayQQVersion
import com.fanqie.xfqdeobf.util.requireRangePlayQQVersion

@FunctionHookEntry
@UiItemAgentEntry
object SimplifyRecentDialog : MultiItemDelayableHook("na_simplify_recent_dialog_multi") {

    override val preferenceTitle = "精简主页加号菜单"
    override val extraSearchKeywords: Array<String> = arrayOf("+号菜单")
    override val uiItemLocation = FunctionEntryRouter.Locations.Simplify.MAIN_UI_TITLE

    override val allItems = setOf("创建群聊", "创建频道", "加好友/群", "匹配聊天", "一起派对", "扫一扫", "面对面快传", "收付款")
    override val defaultItems = setOf<String>()

    override fun initOnce() = throwOrTrue {
        val methodName: String
        val titleName: String
        if (requireMinQQVersion(QQVersion.QQ_8_6_0) || requireMinTimVersion(TIMVersion.TIM_4_0_95_BETA)) {
            methodName = "conversationPlusBuild"
            titleName = "title"
        } else {
            methodName = "b"
            titleName = "a"
        }
        var target = "com/tencent/widget/PopupMenuDialog".clazz
        if (requireRangePlayQQVersion(PlayQQVersion.PlayQQ_8_2_11, PlayQQVersion.PlayQQ_8_2_11))
            target = "bfqp".clazz
        target?.method(
            methodName,
            4,
            target
        )?.hookBefore(this) {
            val list = (it.args[1] as List<*>).toMutableList()
            val iterator = list.iterator()
            while (iterator.hasNext()) {
                val string = iterator.next().get(titleName, String::class.java)
                if (activeItems.contains(string)) {
                    iterator.remove()
                }
            }
            it.args[1] = list.toList()
        }
    }

    override val isAvailable: Boolean get() = requireMinQQVersion(QQVersion.QQ_8_3_9) || requireMinTimVersion(TIMVersion.TIM_4_0_95_BETA) || requireRangePlayQQVersion(PlayQQVersion.PlayQQ_8_2_11, PlayQQVersion.PlayQQ_8_2_11)
}
