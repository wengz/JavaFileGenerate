package pers.wengzc.annotationprocessor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.lang.reflect.Method;

import pers.wengzc.annotation.BindView;
import pers.wengzc.annotation.HelloAnnotation;
import pers.wengzc.api.MyViewBinder;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @HelloAnnotation
    @BindView(value = R.id.tv)
    public TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = findViewById(R.id.tv);

        mTextView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Log.d(TAG, "onClick: ");
                testFun();
            }
        });
    }

    private void testFun (){
        try{
            String className = getPackageName()+".Ox";
            Class clz = Class.forName(className);
            Object instance = clz.newInstance();
            Method method = clz.getMethod("sayHello");
            method.invoke(instance);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
