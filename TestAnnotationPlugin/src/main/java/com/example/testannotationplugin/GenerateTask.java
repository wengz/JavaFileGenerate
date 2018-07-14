package com.example.testannotationplugin;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.File;

public class GenerateTask extends DefaultTask{

    private File generateDir;

    private File buildDir;

    public File getBuildDir() {
        return buildDir;
    }

    public void setBuildDir(File buildDir) {
        this.buildDir = buildDir;
    }

    @TaskAction
    public void greet (){
        Generator.generateCode(buildDir, generateDir);
    }

    public File getGenerateDir() {
        return generateDir;
    }

    public void setGenerateDir(File generateDir) {
        this.generateDir = generateDir;
    }

}
