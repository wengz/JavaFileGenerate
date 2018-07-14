package com.example.testannotationplugin;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.AnnotationMemberDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import javax.lang.model.element.Modifier;

import pers.wengzc.annotation.HelloAnnotation;

public class Generator {

    public static File sBuildDir;
    public static File sGenerateDir;

    public static void generateCode (File buildDir, File generateDir){
        sBuildDir = buildDir;
        sGenerateDir = generateDir;
        try{
            String buildDirPath = buildDir.getAbsolutePath();

            String srcPath = buildDirPath+"/../src/main/java/";
            System.out.println("--------- src dir path ---------->>>"+srcPath);
            File srcDir = new File(srcPath);
            Collection<File> srcFiles =  FileUtils.listFiles(srcDir, null, true );
            for (File srcFile : srcFiles){
                System.out.println("------- srcFile ----------->>>"+srcFile.getAbsolutePath());
                analyzeJavaFile(srcFile.getAbsolutePath());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void analyzeJavaFile (String javaFilePath) throws Exception {
        FileInputStream fileInputStream = new FileInputStream(javaFilePath);
        CompilationUnit cu = JavaParser.parse(fileInputStream);
        System.out.println("--------analyzeJavaFile---------");
        System.out.println(cu.toString());
        searchNeed(cu);
    }


    private static void searchNeed (CompilationUnit cu) throws Exception {
        MarkerAnnotationExpr MarkerAnnotationExpr;
        NodeList<TypeDeclaration<?>> types = cu.getTypes();
        for (TypeDeclaration<?> type : types) {
            // Go through all fields, methods, etc. in this type
            NodeList<BodyDeclaration<?>> members = type.getMembers();
            for (BodyDeclaration<?> member : members) {
                if (member instanceof FieldDeclaration) {
                    FieldDeclaration fieldDeclaration = (FieldDeclaration) member;
                    Optional<AnnotationExpr> annotationExprOptional = fieldDeclaration.getAnnotationByClass(HelloAnnotation.class);
                    if (annotationExprOptional.isPresent()){
                        AnnotationExpr annotationExpr = annotationExprOptional.get();
                        System.out.println("---------- annotationExpr.getName() ----------------->>>"+annotationExpr.getName());
                        JavaFile javaFile = buildOxJavaFile();
                        javaFile.writeTo(sGenerateDir);
                    }
                }
            }
        }
    }

    private static JavaFile buildOxJavaFile (){
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
}
