package pers.wengzc.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import jdk.nashorn.internal.ir.CatchNode;
import pers.wengzc.annotation.BindView;

@AutoService(Processor.class)
public class MyViewBinderProcessor extends AbstractProcessor{

    private Filer mFiler;
    private Elements mElementUtils;
    private Messager mMessager;
    private Map<String, AnnotatedClass> mAnnotatedClassMap;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mFiler = processingEnvironment.getFiler();
        mElementUtils = processingEnvironment.getElementUtils();
        mMessager = processingEnvironment.getMessager();
        mAnnotatedClassMap = new TreeMap<>();
    }

    private void processBindView (RoundEnvironment roundEnvironment) throws Exception {
        for (Element element : roundEnvironment.getElementsAnnotatedWith(BindView.class) ){
            AnnotatedClass annotatedClass = getAnnotatedClass(element);
            BindViewField bindViewField = new BindViewField(element);
            annotatedClass.addField(bindViewField);
        }
    }

    boolean flag = false;

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        System.out.println("-------- apt注解process方法 ----------");

        mAnnotatedClassMap.clear();
        try{
            processBindView(roundEnvironment);
        }catch (Exception e){
            e.printStackTrace();
        }
        for (AnnotatedClass annotatedClass : mAnnotatedClassMap.values()){
            try{
                annotatedClass.generateFile().writeTo(mFiler);
            }catch (Exception e){

            }
        }

        if (!flag){
            System.out.println("-------- 生成Ox类 ----------");

            try{
                buildOxJavaFile().writeTo(mFiler);
            }catch (Exception e){
                e.printStackTrace();
            }
            flag = true;
        }
        return true;
    }

    private JavaFile buildOxJavaFile (){
        MethodSpec.Builder bindViewMethod = MethodSpec.methodBuilder("sayHello")
                .addModifiers(Modifier.PUBLIC)
                .addStatement("System.out.println(\"Hello,ooxx\")");

        //generaClass
        TypeSpec injectClass = TypeSpec.classBuilder("Ox")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(bindViewMethod.build())
                .build();

        String packageName = "pers.wengzc.annotationprocessor";
        return JavaFile.builder(packageName, injectClass).build();
    }

    private AnnotatedClass getAnnotatedClass (Element element){
        TypeElement typeElement = (TypeElement) element.getEnclosingElement();
        String fullName = typeElement.getQualifiedName().toString();
        AnnotatedClass annotatedClass = mAnnotatedClassMap.get(fullName);
        if (annotatedClass == null){
            annotatedClass = new AnnotatedClass(typeElement, mElementUtils);
            mAnnotatedClassMap.put(fullName, annotatedClass);
        }
        return annotatedClass;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();
        types.add(BindView.class.getCanonicalName());
        return types;
    }
}
