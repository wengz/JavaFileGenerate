package pers.wengzc.compiler;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import pers.wengzc.annotation.BindView;

public class BindViewField {

    private VariableElement mVariableElement;
    private int mResId;

    public BindViewField (Element element) throws Exception {
        if (element.getKind() != ElementKind.FIELD){
            throw new IllegalArgumentException("只有属性才可以被bindView注解");
        }
        mVariableElement = (VariableElement) element;

        BindView bindView = mVariableElement.getAnnotation(BindView.class);
        mResId = bindView.value();
        if (mResId < 0){
            new IllegalArgumentException("资源id不对");
        }
    }

    Name getFieldName (){
        return mVariableElement.getSimpleName();
    }

    int getResId (){
        return mResId;
    }

    TypeMirror getFieldType (){
        return mVariableElement.asType();
    }

}
