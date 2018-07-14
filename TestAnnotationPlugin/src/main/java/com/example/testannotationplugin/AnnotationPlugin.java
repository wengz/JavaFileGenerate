package com.example.testannotationplugin;

import com.android.build.gradle.BaseExtension;
import com.android.build.gradle.api.AndroidSourceDirectorySet;
import com.android.build.gradle.api.AndroidSourceSet;
import com.android.build.gradle.internal.api.DefaultAndroidSourceSet;

import org.gradle.api.Action;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

import java.io.File;

public class AnnotationPlugin implements Plugin<Project> {

    private static final String PreBuildTaskName = "preBuild";
    private static final String JavaGenerateTaskName = "AnnotationGenerateTask";
    private static final String JavaGenerateDir = "/AnnotationGenerate";

    @Override
    public void apply(Project project) {

        try{
            BaseExtension androidExtension = (BaseExtension) project.getExtensions().getByName("android");

            File buildDir = project.getBuildDir();
            System.out.println("-------- buildDir -------->>>"+buildDir.getAbsolutePath());
            project.getTasks().create(JavaGenerateTaskName, GenerateTask.class, new Action<GenerateTask>() {
                @Override
                public void execute(GenerateTask generateTask) {}
            });

            GenerateTask generateTask = (GenerateTask) project.getTasks().getByName(JavaGenerateTaskName);
            final File myGenerateDir = new File(buildDir, JavaGenerateDir);
            generateTask.setGenerateDir(myGenerateDir);
            generateTask.setBuildDir(buildDir);

            Task preBuildTask = project.getTasks().getByName(PreBuildTaskName);
            preBuildTask.dependsOn(generateTask);

            //将java生成文件所在的目录加入项目源码编译路径
            androidExtension.sourceSets(new Action<NamedDomainObjectContainer<AndroidSourceSet>>() {
                @Override
                public void execute(NamedDomainObjectContainer<AndroidSourceSet> androidSourceSets) {
                    DefaultAndroidSourceSet defaultAndroidSourceSet = (com.android.build.gradle.internal.api.DefaultAndroidSourceSet) androidSourceSets.maybeCreate("main");
                    AndroidSourceDirectorySet androidSourceDirectorySet = defaultAndroidSourceSet.getJava();
                    androidSourceDirectorySet.srcDir(myGenerateDir);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
