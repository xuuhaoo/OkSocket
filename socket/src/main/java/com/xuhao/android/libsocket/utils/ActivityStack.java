package com.xuhao.android.libsocket.utils;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuhao on 2017/7/6.
 */

public class ActivityStack {
    private static boolean isDebug;

    private static boolean isBackground = false;

    private final static List<Activity> INSTANCE_STACK = new ArrayList<>();

    private final static List<Activity> RESUME_LIST = new ArrayList<>();

    private final static List<OnStackChangedListener> LISTENERS = new ArrayList<>();

    private final static Application.ActivityLifecycleCallbacks LIFECYCLE_CALLBACKS = new Application
            .ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            pushInstance(activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {
            saveResume(activity);
        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {
            removeResume(activity);
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            popInstance(lastIndexOf(activity), false);
        }
    };

    private ActivityStack() {
    }

    public static void init(Application application) {
        init(application, false);
    }

    public static void init(Application application, boolean isDebug) {
        ActivityStack.isDebug = isDebug;
        application.registerActivityLifecycleCallbacks(LIFECYCLE_CALLBACKS);
    }

    public interface OnStackChangedListener {

        /**
         * 压栈回调
         *
         * @param activity 实例
         */
        void onPush(Activity activity);

        /**
         * 弹栈回调
         *
         * @param activity 实例
         */
        void onPop(Activity activity);

        /**
         * 最后一个实例被弹出,会在Onpop之后调用此方法
         *
         * @param lastActivity 最后一个被弹出的实例
         */
        void onStackGonnaEmpty(Activity lastActivity);

        /**
         * 当 App 至于后台时回调此方法
         */
        void onAppPause();

        /**
         * 当 App 从后台返回前台时回调此方法
         */
        void onAppResume();

    }

    /**
     * 事件适配器
     */
    public static abstract class OnStackChangedAdapter implements OnStackChangedListener {
        @Override
        public void onPush(Activity activity) {
            // Stub
        }

        @Override
        public void onPop(Activity activity) {
            // Stub
        }

        @Override
        public void onStackGonnaEmpty(Activity lastActivity) {
            // Stub
        }

        @Override
        public void onAppPause() {
            // Stub
        }

        @Override
        public void onAppResume() {
            // Stub
        }
    }

    public static void addStackChangedListener(OnStackChangedListener listener) {
        synchronized (LISTENERS) {
            LISTENERS.add(listener);
        }
    }

    public static void removeStackChangedListener(OnStackChangedListener listener) {
        synchronized (LISTENERS) {
            LISTENERS.remove(listener);
        }
    }

    public static void removeAllStackChangedListener() {
        synchronized (LISTENERS) {
            LISTENERS.clear();
        }
    }

    private static void notifyPushListener(final Activity activity) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                for (OnStackChangedListener listener : LISTENERS) {
                    try {
                        listener.onPush(activity);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private static void notifyPopListener(final Activity activity) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                for (OnStackChangedListener listener : LISTENERS) {
                    try {
                        listener.onPop(activity);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private static void notifyGonnaEmptyListener(final Activity activity) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                for (OnStackChangedListener listener : LISTENERS) {
                    try {
                        listener.onStackGonnaEmpty(activity);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private static void notifyAppPause() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                for (OnStackChangedListener listener : LISTENERS) {
                    try {
                        listener.onAppPause();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private static void notifyAppResume() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                for (OnStackChangedListener listener : LISTENERS) {
                    try {
                        listener.onAppResume();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public static String logStack() {
        StringBuilder builder = new StringBuilder("stack_bottom");
        for (Activity activity : INSTANCE_STACK) {
            builder.append("->" + activity.getClass().getSimpleName());
        }
        builder.append("->stack_head");
        logI("ActivityStack", builder.toString());
        return builder.toString();
    }

    /**
     * 压栈
     *
     * @param activity 需要压栈的Activity对象
     */
    private static void pushInstance(Activity activity) {
        synchronized (INSTANCE_STACK) {
            INSTANCE_STACK.add(activity);
            logI("ActivityStack", "pushInstance:" + activity.getClass().getSimpleName());
            logStack();
            notifyPushListener(activity);
        }
    }

    /**
     * 弹栈
     *
     * @return 栈顶的Activity对象
     */
    private static Activity popInstance() {
        Activity activity = popInstance(INSTANCE_STACK.size() - 1, false);
        return activity;
    }

    /**
     * 取栈内实例
     *
     * @param index 栈内下标
     * @return 实例对象
     */
    public static Activity takeInstance(int index) {
        Activity activity = INSTANCE_STACK.get(index);
        return activity;
    }

    /**
     * 取栈顶实例
     *
     * @return 实例对象
     */
    public static Activity takeInstance() {
        return takeInstance(INSTANCE_STACK.size() - 1);
    }

    /**
     * 弹栈
     *
     * @param index      弹出的下标
     * @param isClearTop 是否清除(关闭)该下标以上的Activity
     * @return 该index的Activity对象
     */
    private static Activity popInstance(int index, boolean isClearTop) {
        if (index == -1) {
            return null;
        }
        synchronized (INSTANCE_STACK) {
            try {
                Activity activity = INSTANCE_STACK.remove(index);
                if (isClearTop) {
                    clearUpByIndex(index - 1);
                }
                logI("ActivityStack", "popInstance:" + activity.getClass().getSimpleName());
                logStack();
                notifyPopListener(activity);
                if (size() == 0) {
                    notifyGonnaEmptyListener(activity);
                }
                return activity;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * 清空栈内该下标以上(到栈底)的所有实例
     *
     * @param index 下标
     */
    private static void clearUpByIndex(int index) {
        synchronized (INSTANCE_STACK) {
            if (index < 0) {
                return;
            }
            if (index >= INSTANCE_STACK.size()) {
                index = INSTANCE_STACK.size() - 1;
            }
            for (int i = index; i >= 0; i--) {
                Activity activity = INSTANCE_STACK.get(i);
                activity.finish();
            }
            if (index != 0) {
                INSTANCE_STACK.subList(0, index).clear();
            } else {
                INSTANCE_STACK.clear();
            }
        }
    }

    /**
     * 退出整个程序
     *
     * @param isDesc 是否倒序
     */
    public static void exitApplication(boolean isDesc) {
        synchronized (INSTANCE_STACK) {
            if (isDesc) {
                for (int i = INSTANCE_STACK.size() - 1; i >= 0; i--) {
                    Activity activity = INSTANCE_STACK.get(i);
                    activity.finish();
                }
            } else {
                for (int i = 0; i < INSTANCE_STACK.size(); i++) {
                    Activity activity = INSTANCE_STACK.get(i);
                    activity.finish();
                }
            }
            INSTANCE_STACK.clear();
        }
    }

    /**
     * 退出整个程序,倒序
     */
    public static void exitApplication() {
        exitApplication(true);
    }

    /**
     * 栈的大小
     *
     * @return Activity实例数
     */
    public static int size() {
        return INSTANCE_STACK.size();
    }

    /**
     * 从左到右查找Activity实例下标
     *
     * @param activity 实例对象
     * @return 对应栈内的下标数
     */
    public static int indexOf(Activity activity) {
        return INSTANCE_STACK.indexOf(activity);
    }

    /**
     * 从右到左查找Activity实例下标
     *
     * @param activity 实例对象
     * @return 对应栈内的下标数
     */
    public static int lastIndexOf(Activity activity) {
        return INSTANCE_STACK.lastIndexOf(activity);
    }

    /**
     * 根据Class从左到右查找
     *
     * @param clz 相应的Activity的Class
     * @return 对应栈内下标数
     */
    public static int indexOf(Class<? extends Activity> clz) {
        for (int i = 0; i < INSTANCE_STACK.size(); i++) {
            Class<? extends Activity> stackClz = INSTANCE_STACK.get(i).getClass();
            if (stackClz.equals(clz)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 根据Class从右到左查找
     *
     * @param clz 相应的Activity的Class
     * @return 对应栈内下标数
     */
    public static int lastIndexOf(Class<? extends Activity> clz) {
        for (int i = INSTANCE_STACK.size() - 1; i >= 0; i--) {
            Class<? extends Activity> stackClz = INSTANCE_STACK.get(i).getClass();
            if (stackClz.equals(clz)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 对应某一个Activity的数量
     *
     * @param clz
     * @return 数量
     */
    public static int sizeOf(Class<? extends Activity> clz) {
        int count = 0;
        for (int i = 0; i < INSTANCE_STACK.size(); i++) {
            String clzStr = INSTANCE_STACK.get(i).getLocalClassName();

            if (clzStr.equals(clz.getSimpleName())) {
                count++;
            }
        }
        return count;
    }

    /**
     * 保存唤醒的 activity
     *
     * @param activity
     */
    private static void saveResume(Activity activity) {
        synchronized (RESUME_LIST) {
            boolean isEmpty = RESUME_LIST.isEmpty();
            RESUME_LIST.add(activity);
            if (isEmpty) {
                logI("ActivityStack", "App resume");
                isBackground = false;
                notifyAppResume();
            } else {
                logI("ActivityStack", "saveResume:" + activity.getClass().getSimpleName());
            }
        }
    }

    /**
     * 删除唤醒的 activity
     *
     * @param activity
     */
    private static void removeResume(Activity activity) {
        synchronized (RESUME_LIST) {
            RESUME_LIST.remove(activity);
            if (RESUME_LIST.isEmpty()) {
                logI("ActivityStack", "App pause");
                isBackground = true;
                notifyAppPause();
            } else {
                logI("ActivityStack", "removeResume:" + activity.getClass().getSimpleName());
            }
        }
    }

    /**
     * 打印调试日志
     *
     * @param tag
     * @param msg
     */
    private static void logI(String tag, String msg) {
        if (isDebug) {
            Log.i(tag, msg);
        }
    }

    public static boolean isBackground() {
        return isBackground;
    }

}