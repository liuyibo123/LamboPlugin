package com.lambo.toolwindow;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.*;
import com.intellij.ui.content.*;

import org.jetbrains.annotations.NotNull;


public class LamboToolWindowFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        SqlGeneratorToolWindow sqlGeneratorToolWindow = new SqlGeneratorToolWindow();
        ContentManager contentManager = toolWindow.getContentManager();
        Content sqlContent = contentManager.getFactory().createContent(sqlGeneratorToolWindow.getContext(),"sql生成",true);
        contentManager.addContent(sqlContent);
    }

    @Override
    public void init(ToolWindow window) {

    }

}
