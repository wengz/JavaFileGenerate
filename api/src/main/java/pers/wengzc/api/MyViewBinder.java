package pers.wengzc.api;

import android.app.Activity;

import java.util.HashMap;
import java.util.Map;

public class MyViewBinder {

    private static final ActivityViewFinder activityFinder = new ActivityViewFinder();
    private static final Map<String, ViewBinder> binderMap = new HashMap<>();

    public static void bind(Activity activity){
        bind(activity, activity, activityFinder);
    }

    private static void bind (Object host, Object object, ViewFinder finder){
        String className = host.getClass().getName();
        try{
            ViewBinder binder = binderMap.get(className);
            if (binder == null){
                Class<?> aClass = Class.forName(className+"$ViewBinder");
                binder = (ViewBinder) aClass.newInstance();
                binderMap.put(className, binder);
            }
            if (binder != null){
                binder.bindView(host, object, finder);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void unbind (Object host){
        String className = host.getClass().getName();
        ViewBinder binder = binderMap.get(className);
        if (binder != null){
            binder.unBindView(host);
        }
        binderMap.remove(className);
    }
}
