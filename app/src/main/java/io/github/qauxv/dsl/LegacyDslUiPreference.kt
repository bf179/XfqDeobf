package com.fanqie.xfqdeobf.dsl

import android.app.Activity
import android.content.Context
import android.view.View
import com.fanqie.xfqdeobf.base.IEntityAgent
import com.fanqie.xfqdeobf.base.ISwitchCellAgent
import com.fanqie.xfqdeobf.base.IUiItemAgent
import com.fanqie.xfqdeobf.hook.BaseFunctionHook
import kotlinx.coroutines.flow.StateFlow

fun BaseFunctionHook.uiSwitchPreference(init: UiSwitchPreferenceItemFactory.() -> Unit): IUiItemAgent {
    val uiSwitchPreferenceFactory = UiSwitchPreferenceItemFactory(this)
    uiSwitchPreferenceFactory.init()
    return uiSwitchPreferenceFactory
}

fun BaseFunctionHook.uiClickableItem(init: UiClickableItemFactory.() -> Unit): IUiItemAgent {
    val uiClickableItemFactory = UiClickableItemFactory(this)
    uiClickableItemFactory.init()
    return uiClickableItemFactory
}

class UiSwitchPreferenceItemFactory(receiver: BaseFunctionHook) : IUiItemAgent {
    lateinit var title: String
    var summary: String? = null

    override val titleProvider: (IEntityAgent) -> String = { title }
    override val summaryProvider: ((IEntityAgent, Context) -> String?) = { _, _ -> summary }
    override val valueState: StateFlow<String?>? = null
    override val validator: ((IUiItemAgent) -> Boolean) = { receiver.isAvailable }
    override val switchProvider: ISwitchCellAgent by lazy {
        object : ISwitchCellAgent {
            override val isCheckable: Boolean get() = receiver.isAvailable

            override var isChecked: Boolean
                get() = receiver.isEnabled
                set(value) {
                    receiver.isEnabled = value
                }
        }
    }
    override val onClickListener: ((IUiItemAgent, Activity, View) -> Unit)? = null
    override val extraSearchKeywordProvider: ((IUiItemAgent, Context) -> Array<String>?)? = null
}

class UiClickableItemFactory(receiver: BaseFunctionHook) : IUiItemAgent {
    lateinit var title: String
    var summary: String? = null

    override val titleProvider: (IEntityAgent) -> String = { title }
    override val summaryProvider: ((IEntityAgent, Context) -> String?) = { _, _ -> summary }
    override val valueState: StateFlow<String?>? = null
    override val validator: ((IUiItemAgent) -> Boolean) = { receiver.isAvailable }
    override val switchProvider: ISwitchCellAgent? = null
    override var onClickListener: ((IUiItemAgent, Activity, View) -> Unit)? = null
    override val extraSearchKeywordProvider: ((IUiItemAgent, Context) -> Array<String>?)? = null
}
