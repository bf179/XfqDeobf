/*
 * XfqDeobf - An Xposed module for QQ image deobfuscation
 */
package cc.ioctl.hook;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.fanqie.xfqdeobf.util.Initiator.load;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import cc.hicore.QApp.QAppUtils;
import cc.ioctl.util.HookUtils;
import cc.ioctl.util.HostInfo;
import cc.ioctl.util.LayoutHelper;
import cc.ioctl.util.Reflex;
import com.fanqie.xfqdeobf.activity.SettingsUiFragmentHostActivity;
import com.fanqie.xfqdeobf.base.annotation.FunctionHookEntry;
import com.fanqie.xfqdeobf.core.HookInstaller;
import com.fanqie.xfqdeobf.fragment.EulaFragment;
import com.fanqie.xfqdeobf.fragment.FuncStatusDetailsFragment;
import com.fanqie.xfqdeobf.hook.BasePersistBackgroundHook;
import com.fanqie.xfqdeobf.lifecycle.Parasitics;
import com.fanqie.xfqdeobf.step.Step;
import com.fanqie.xfqdeobf.util.Initiator;
import com.fanqie.xfqdeobf.util.LicenseStatus;
import com.fanqie.xfqdeobf.util.Log;
import com.fanqie.xfqdeobf.util.QQVersion;
import com.fanqie.xfqdeobf.util.dexkit.DexDeobfsProvider;
import com.fanqie.xfqdeobf.util.dexkit.DexKit;
import com.fanqie.xfqdeobf.util.dexkit.DexKitTargetSealedEnum;
import com.fanqie.xfqdeobf.util.dexkit.SimpleItemProcessor_Method;
import com.fanqie.xfqdeobf.util.dexkit.impl.DexKitDeobfs;
import com.fanqie.xfqdeobf.util.xpcompat.XC_MethodHook;
import com.fanqie.xfqdeobf.util.xpcompat.XposedBridge;
import com.fanqie.xfqdeobf.util.xpcompat.XposedHelpers;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import kotlin.collections.ArraysKt;
import kotlin.jvm.functions.Function0;
import org.luckypray.dexkit.DexKitBridge;
import org.luckypray.dexkit.query.FindMethod;
import org.luckypray.dexkit.query.matchers.MethodMatcher;
import org.luckypray.dexkit.result.MethodData;
import org.luckypray.dexkit.result.MethodDataList;

@FunctionHookEntry
public class SettingEntryHook extends BasePersistBackgroundHook {

    public static final SettingEntryHook INSTANCE = new SettingEntryHook();

    // am start "intent:#Intent;component=com.tencent.mobileqq/com.tencent.mobileqq.activity.QPublicFragmentActivity;S.public_fragment_class=com.tencent.mobileqq.setting.main.MainSettingFragment;end"

    private SettingEntryHook() {
    }

    @Override
    public boolean isPreparationRequired() {
        return isNeedFind();
    }

    private final Step mStep = new Step() {
        @Override
        public boolean step() {
            return doFindStep();
        }

        @Override
        public boolean isDone() {
            return !isNeedFind();
        }

        @Override
        public int getPriority() {
            return 0;
        }

        @Override
        public String getDescription() {
            return "查找设置入口相关类";
        }
    };

    @Override
    public Step[] makePreparationSteps() {
        return new Step[]{mStep};
    }

    private boolean isNeedFind() {
        return QAppUtils.isQQnt()
                && HostInfo.requireMinQQVersion(QQVersion.QQ_9_2_10)
                && DexKit.getMethodDescFromCacheImpl(SimpleItemProcessor_Method.INSTANCE) == null;
    }

    private boolean doFindStep() {
        DexDeobfsProvider.checkDeobfuscationAvailable();
        try (DexKitDeobfs dexKitDeobfs = DexKitDeobfs.newInstance()) {
            DexKitBridge bridge = dexKitDeobfs.getDexKitBridge();
            MethodDataList result = bridge.findMethod(FindMethod.create()
                    .matcher(MethodMatcher.create()
                            .addEqString("SimpleItemProcessor")
                    )
            );
            if (result.size() == 1) {
                MethodData methodData = result.get(0);
                SimpleItemProcessor_Method.INSTANCE.setDescCache(methodData.getDescriptor());
                Log.d("save id: " + DexKitTargetSealedEnum.INSTANCE.nameOf(SimpleItemProcessor_Method.INSTANCE) + ",method: " + methodData.getDescriptor());
                return true;
            }
            SimpleItemProcessor_Method.INSTANCE.setDescCache(DexKit.NO_SUCH_METHOD.toString());
            return false;
        }
    }

    @Override
    public boolean initOnce() throws Exception {
        android.util.Log.i("FanqieDebug", "[SettingEntryHook] initOnce() START");
        try {
            injectSettingEntryForMainSettingConfigProvider();
        } catch (Throwable t) {
            android.util.Log.e("FanqieDebug", "[SettingEntryHook] injectSettingEntry failed: " + t, t);
            throw t;
        }
        // Legacy path for QQ < 8.9.70
        Class<?> kQQSettingSettingActivity = Initiator._QQSettingSettingActivity();
        if (kQQSettingSettingActivity != null) {
            XposedHelpers.findAndHookMethod(kQQSettingSettingActivity, "doOnCreate", Bundle.class, mAddModuleEntry);
        }
        Class<?> kQQSettingSettingFragment = Initiator._QQSettingSettingFragment();
        if (kQQSettingSettingFragment != null) {
            Method doOnCreateView = kQQSettingSettingFragment.getDeclaredMethod("doOnCreateView",
                    LayoutInflater.class, ViewGroup.class, Bundle.class);
            XposedBridge.hookMethod(doOnCreateView, mAddModuleEntry);
        }
        android.util.Log.i("FanqieDebug", "[SettingEntryHook] initOnce() DONE");
        return true;
    }

    private void injectSettingEntryForMainSettingConfigProvider() throws ReflectiveOperationException {
        // QQ 8.9.70+
        Class<?> kMainSettingFragment = Initiator.load("com.tencent.mobileqq.setting.main.MainSettingFragment");
        if (kMainSettingFragment != null) {
            Class<?> kMainSettingConfigProvider = Initiator.load("com.tencent.mobileqq.setting.main.MainSettingConfigProvider");
            Class<?> kNewSettingConfigProvider = Initiator.load("com.tencent.mobileqq.setting.main.NewSettingConfigProvider");
            Class<?> kNewSettingConfigProviderObf = Initiator.load("com.tencent.mobileqq.setting.main.b");

            Method getItemProcessListOld = null;
            if (kMainSettingConfigProvider != null) {
                getItemProcessListOld = Reflex.findSingleMethod(kMainSettingConfigProvider, List.class, false, Context.class);
            }
            Method getItemProcessListNew = null;
            if (kNewSettingConfigProvider != null) {
                getItemProcessListNew = Reflex.findSingleMethod(kNewSettingConfigProvider, List.class, false, Context.class);
            }
            Method getItemProcessListNewObf = null;
            if (kNewSettingConfigProviderObf != null) {
                getItemProcessListNewObf = Reflex.findSingleMethod(kNewSettingConfigProviderObf, List.class, false, Context.class);
            }

            if (getItemProcessListOld == null && getItemProcessListNew == null && getItemProcessListNewObf == null) {
                throw new IllegalStateException("getItemProcessListOld == null && getItemProcessListNew == null && getItemProcessListNewObf == null");
            }
            Class<?> kAbstractItemProcessor = null;
            for (String possibleParent : new String[]{
                    "com.tencent.mobileqq.setting.main.processor.AccountSecurityItemProcessor",
                    "com.tencent.mobileqq.setting.main.processor.AboutItemProcessor"
            }) {
                Class<?> k = Initiator.load(possibleParent);
                if (k != null) {
                    kAbstractItemProcessor = k.getSuperclass();
                    break;
                }
            }
            if (kAbstractItemProcessor == null) {
                throw new IllegalStateException("kAbstractItemProcessor == null");
            }
            List<Class<?>> possibleSimpleItemProcessorCandidates = new ArrayList<>(6);
            final String[] possibleSimpleItemProcessorNames = new String[]{
                    "com.tencent.mobileqq.setting.processor.g",
                    "com.tencent.mobileqq.setting.processor.h",
                    "com.tencent.mobileqq.setting.processor.i",
                    "com.tencent.mobileqq.setting.processor.j",
                    "as3.i",
            };
            for (String name : possibleSimpleItemProcessorNames) {
                Class<?> klass = Initiator.load(name);
                if (klass != null && klass.getSuperclass() == kAbstractItemProcessor) {
                    possibleSimpleItemProcessorCandidates.add(klass);
                }
            }
            if (HostInfo.requireMinQQVersion(QQVersion.QQ_9_2_10)) {
                Method m = DexKit.loadMethodFromCache(SimpleItemProcessor_Method.INSTANCE);
                if (m != null) {
                    Class<?> klass = m.getDeclaringClass();
                    if (klass.getSuperclass() == kAbstractItemProcessor && !possibleSimpleItemProcessorCandidates.contains(klass)) {
                        possibleSimpleItemProcessorCandidates.add(klass);
                    }
                }
            }
            if (possibleSimpleItemProcessorCandidates.size() != 1) {
                throw new IllegalStateException("possibleSimpleItemProcessorCandidates.size() != 1, got " + possibleSimpleItemProcessorCandidates);
            }
            Class<?> kSimpleItemProcessor = possibleSimpleItemProcessorCandidates.get(0);
            Method setOnClickListener;
            {
                List<Method> candidates = ArraysKt.filter(kSimpleItemProcessor.getDeclaredMethods(), m -> {
                    Class<?>[] argt = m.getParameterTypes();
                    return m.getReturnType() == void.class && argt.length == 1 && Function0.class.getName().equals(argt[0].getName());
                });
                candidates.sort(Comparator.comparing(Method::getName));
                if (candidates.size() != 2 && candidates.size() != 1) {
                    throw new IllegalStateException("setOnClickListener candidates.size() != 1|2");
                }
                setOnClickListener = candidates.get(0);
            }
            Constructor<?> ctorSimpleItemProcessor;
            int ctorSimpleItemProcessorArgc;
            {
                Constructor<?> c = null;
                int i = 0;
                try {
                    c = kSimpleItemProcessor.getDeclaredConstructor(Context.class, int.class, CharSequence.class, int.class,
                            String.class);
                    i = 5;
                } catch (NoSuchMethodException ignored) {
                }
                if (c == null) {
                    c = kSimpleItemProcessor.getDeclaredConstructor(Context.class, int.class, CharSequence.class, int.class);
                    i = 4;
                }
                ctorSimpleItemProcessor = c;
                ctorSimpleItemProcessorArgc = i;
            }
            XC_MethodHook callback = HookUtils.afterAlways(this, 50, param -> {
                android.util.Log.i("FanqieDebug", "[SettingEntryHook] getItemProcessList callback fired");
                List<Object> result = (List<Object>) param.getResult();
                Context ctx = (Context) param.args[0];
                Class<?> kItemProcessorGroup = result.get(0).getClass();
                Constructor<?> ctor;
                try {
                    ctor = kItemProcessorGroup.getDeclaredConstructor(List.class, CharSequence.class, CharSequence.class);
                } catch (NoSuchMethodException e) {
                    ctor = kItemProcessorGroup.getDeclaredConstructor(List.class, CharSequence.class, CharSequence.class,
                            int.class, load("kotlin.jvm.internal.DefaultConstructorMarker"));
                }
                Parasitics.injectModuleResources(ctx.getResources());
                @SuppressLint("DiscouragedApi")
                int resId = ctx.getResources().getIdentifier("qui_tuning", "drawable", ctx.getPackageName());
                Object entryItem;
                if (ctorSimpleItemProcessorArgc == 5) {
                    entryItem = ctorSimpleItemProcessor.newInstance(ctx, com.fanqie.xfqdeobf.R.id.setting2Activity_settingEntryItem, "小番茄解混淆", resId, null);
                } else {
                    entryItem = ctorSimpleItemProcessor.newInstance(ctx, com.fanqie.xfqdeobf.R.id.setting2Activity_settingEntryItem, "小番茄解混淆", resId);
                }
                Class<?> thatFunction0 = setOnClickListener.getParameterTypes()[0];
                Object theUnit = thatFunction0.getClassLoader().loadClass("kotlin.Unit").getField("INSTANCE").get(null);
                ClassLoader hostClassLoader = Initiator.getHostClassLoader();
                Object func0 = Proxy.newProxyInstance(hostClassLoader, new Class<?>[]{thatFunction0}, (proxy, method, args) -> {
                    if (method.getName().equals("invoke")) {
                        onSettingEntryClick(ctx);
                        return theUnit;
                    }
                    return method.invoke(this, args);
                });
                setOnClickListener.invoke(entryItem, func0);
                ArrayList<Object> list = new ArrayList<>(1);
                list.add(entryItem);
                Object group;
                if (ctor.getParameterTypes().length == 5) {
                    group = ctor.newInstance(list, "", "", 6, null);
                } else {
                    group = ctor.newInstance(list, "", "");
                }
                boolean isNew = param.thisObject.getClass().getName().contains("NewSettingConfigProvider");
                int indexToInsert = isNew ? 2 : 1;
                result.add(indexToInsert, group);
                android.util.Log.i("FanqieDebug", "[SettingEntryHook] ENTRY INJECTED: 小番茄解混淆 at index=" + indexToInsert);
            });
            if (getItemProcessListOld != null) {
                XposedBridge.hookMethod(getItemProcessListOld, callback);
            }
            if (getItemProcessListNew != null) {
                XposedBridge.hookMethod(getItemProcessListNew, callback);
            }
            if (getItemProcessListNewObf != null) {
                XposedBridge.hookMethod(getItemProcessListNewObf, callback);
            }
        }
    }

    private final XC_MethodHook mAddModuleEntry = new XC_MethodHook(51) {
        @Override
        protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
            android.util.Log.i("FanqieDebug", "[SettingEntryHook] mAddModuleEntry callback (legacy QQ < 8.9.70)");
            try {
                final Activity activity;
                var thisObject = param.thisObject;
                if (thisObject instanceof Activity) {
                    activity = (Activity) thisObject;
                } else {
                    activity = (Activity) Reflex.invokeVirtual(thisObject, "getActivity");
                }
                Resources res = activity.getResources();
                Class<?> itemClass;
                View itemRef = null;
                {
                    Class<?> clz = load("com/tencent/mobileqq/widget/FormSimpleItem");
                    if (clz != null) {
                        for (Field f : thisObject.getClass().getDeclaredFields()) {
                            if (f.getType() == clz && !Modifier.isStatic(f.getModifiers())) {
                                f.setAccessible(true);
                                View v = (View) f.get(thisObject);
                                if (v != null && v.getParent() != null) {
                                    itemRef = v;
                                    break;
                                }
                            }
                        }
                    }
                }
                if (itemRef == null && (itemClass = load("com/tencent/mobileqq/widget/FormCommonSingleLineItem")) != null) {
                    itemRef = (View) Reflex.getInstanceObjectOrNull(activity, "a", itemClass);
                }
                if (itemRef == null) {
                    Class<?> clz = load("com/tencent/mobileqq/widget/FormCommonSingleLineItem");
                    if (clz == null) {
                        clz = load("com/tencent/mobileqq/widget/FormSimpleItem");
                    }
                    itemRef = (View) Reflex.getFirstNSFByType(activity, clz);
                }
                View item;
                if (itemRef == null) {
                    item = (View) Reflex.newInstance(load("com/tencent/mobileqq/widget/FormSimpleItem"), activity, Context.class);
                } else {
                    item = (View) Reflex.newInstance(itemRef.getClass(), activity, Context.class);
                }
                item.setId(com.fanqie.xfqdeobf.R.id.setting2Activity_settingEntryItem);
                Reflex.invokeVirtual(item, "setLeftText", "小番茄解混淆", CharSequence.class);
                Reflex.invokeVirtual(item, "setBgType", 2, int.class);
                if (HookInstaller.getFuncInitException() != null) {
                    Reflex.invokeVirtual(item, "setRightText", "[严重错误]", CharSequence.class);
                } else {
                    Reflex.invokeVirtual(item, "setRightText", "v" + com.fanqie.xfqdeobf.BuildConfig.VERSION_NAME, CharSequence.class);
                }
                item.setOnClickListener(v -> onSettingEntryClick(activity));
                if (itemRef != null && !HostInfo.isQQHD()) {
                    ViewGroup list = (ViewGroup) itemRef.getParent();
                    ViewGroup.LayoutParams reflp;
                    if (list.getChildCount() == 1) {
                        list = (ViewGroup) list.getParent();
                        reflp = ((View) itemRef.getParent()).getLayoutParams();
                    } else {
                        reflp = itemRef.getLayoutParams();
                    }
                    ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
                    int index = 0;
                    int account_switch = res.getIdentifier("account_switch", "id", list.getContext().getPackageName());
                    try {
                        if (account_switch > 0) {
                            View accountItem = (View) list.findViewById(account_switch).getParent();
                            if (accountItem != null && accountItem.getParent() != null) {
                                list = (ViewGroup) accountItem.getParent();
                            }
                            for (int i = 0; i < list.getChildCount(); i++) {
                                if (list.getChildAt(i) == accountItem) {
                                    index = i + 1;
                                    break;
                                }
                            }
                        }
                        if (index > list.getChildCount()) {
                            index = 0;
                        }
                    } catch (NullPointerException ignored) {
                    }
                    list.addView(item, index, lp);
                    fixBackgroundType(list, item, index);
                } else {
                    int qqsetting2_msg_notify = res.getIdentifier("qqsetting2_msg_notify", "id", activity.getPackageName());
                    if (qqsetting2_msg_notify == 0) {
                        throw new UnsupportedOperationException("R.id.qqsetting2_msg_notify not found");
                    } else {
                        ViewGroup vg = (ViewGroup) activity.findViewById(qqsetting2_msg_notify).getParent().getParent();
                        vg.addView(item, 0, new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
                    }
                }
                android.util.Log.i("FanqieDebug", "[SettingEntryHook] LEGACY ENTRY INJECTED");
            } catch (Throwable e) {
                traceError(e);
                throw e;
            }
        }
    };

    private void onSettingEntryClick(@NonNull Context context) {
        if (HookInstaller.getFuncInitException() != null) {
            SettingsUiFragmentHostActivity.startActivityForFragment(context, FuncStatusDetailsFragment.class,
                    FuncStatusDetailsFragment.getBundleForLocation(FuncStatusDetailsFragment.TARGET_INIT_EXCEPTION));
        } else if (LicenseStatus.hasUserAcceptEula()) {
            context.startActivity(new Intent(context, SettingsUiFragmentHostActivity.class));
        } else {
            SettingsUiFragmentHostActivity.startActivityForFragment(context, EulaFragment.class, null);
            if (context instanceof Activity) {
                ((Activity) context).finish();
            }
        }
    }

    private void fixBackgroundType(@NonNull ViewGroup parent, @NonNull View itemView, int index) {
        int lastClusterId = index - 1;
        if (lastClusterId < 0) {
            return;
        }
        try {
            Reflex.invokeVirtual(itemView, "setBgType", 0, int.class);
            android.widget.LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) itemView.getLayoutParams();
            lp.setMargins(0, LayoutHelper.dip2px(parent.getContext(), 15), 0, 0);
            parent.requestLayout();
        } catch (ReflectiveOperationException e) {
            Log.e(e);
        }
    }
}
