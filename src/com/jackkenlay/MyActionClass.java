package com.jackkenlay;

import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MyActionClass extends AnAction {

    private String currentDir = "";
    private String componentName = "";
    @Override
    public void actionPerformed(AnActionEvent e) {

        componentName = JOptionPane.showInputDialog(null,"Enter component name:","my-component");

        // TODO: insert action logic here

        /*
         * TO DO
         * Test Keyboard shortcut
         * Give options for config? IE clientlibs embed etc?
         * Ensure dialog is smooth experience with keyboard
         * Test export
         * make README
         * make decent JFrame inputdialog
         */
        this.currentDir = getCurrentWorkingDirectory(e);

        // create component directory
        createFolder(componentName);

        // create .content.xml
        String componentGroup = "Creditsafe";
        createContentXML(componentName,componentGroup);

        // create CQ Dialog
        String content = getCQDialogText(componentName);
        createFile(componentName + "/_cq_dialog.xml", content);

        // create JS, LESS configs/files
        String clientLibsCategory = "creditsafe.components.content.cta";
        createClientLibs(componentName, clientLibsCategory);

        // create EditConfig
        createEditConfig(componentName);

        // create HTML
        createHTML(componentName);
    }


    private void createContentXML(String componentName, String componentGroup) {
        String contentString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<jcr:root xmlns:cq=\"http://www.day.com/jcr/cq/1.0\"\n" +
                "          xmlns:jcr=\"http://www.jcp.org/jcr/1.0\"\n" +
                "          jcr:primaryType=\"cq:Component\"\n" +
                "          jcr:title=\""+componentName+"\"\n" +
                "          componentGroup=\""+componentGroup+"\"/>";
        createFile(componentName + "/.content.xml", contentString);

    }
    private void createEditConfig(String componentName) {
        String editConfigText = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<jcr:root xmlns:cq=\"http://www.day.com/jcr/cq/1.0\"\n" +
                "          xmlns:jcr=\"http://www.jcp.org/jcr/1.0\"\n" +
                "          jcr:primaryType=\"cq:EditConfig\"/>";

        createFile(componentName + "/_cq_editConfig.xml", editConfigText);
    }

    private void createHTML(String componentName) {
        String htmlText = "<h1>${properties.text || \"Hello\"}</h1>";
        createFile(componentName + "/"+componentName+".html", htmlText);
    }

    private void createClientLibs(String componentName, String clientLibCategory) {
        //doesnt make editor LESS OR JS

        String clientLibsContentXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<jcr:root xmlns:cq=\"http://www.day.com/jcr/cq/1.0\"\n" +
                "          xmlns:jcr=\"http://www.jcp.org/jcr/1.0\"\n" +
                "          jcr:primaryType=\"cq:ClientLibraryFolder\"\n" +
                "          categories=\""+clientLibCategory+"\"/>";

        // create clientLibs File
        createFolder(componentName + "/clientlibs");

        createFolder(componentName + "/clientlibs/site");
        createFile(componentName + "/clientlibs/site/.content.xml", clientLibsContentXML);
        createFolder(componentName + "/clientlibs/site/js");
        createFolder(componentName + "/clientlibs/site/less");

        createFolder(componentName + "/clientlibs/editor");
        createFile(componentName + "/clientlibs/editor/.content.xml", clientLibsContentXML);
        createFolder(componentName + "/clientlibs/editor/less");
        createFolder(componentName + "/clientlibs/editor/js");

        String lessFileText = "#base=less \n\n" + componentName + ".less";
        createFile(componentName + "/clientlibs/site/css.txt", lessFileText);
        createFile(componentName + "/clientlibs/site/less/"+componentName+".less", "");

        String jsFileText = "#base=js \n\n" + componentName + ".js";
        createFile(componentName + "/clientlibs/site/js.txt", jsFileText);
        createFile(componentName + "/clientlibs/site/js/"+componentName+".js", "console.log('loaded');");
    }

    private void createFile(String name, String fileContent) {
        String path = currentDir + name;

        BufferedWriter output = null;
        try {
            File file = new File(path);
            output = new BufferedWriter(new FileWriter(file));
            output.write(fileContent);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //this is awful
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    private void createFolder(String folderName) {
        String path = currentDir + folderName;
        new File(path).mkdirs();
    }

    private String getCurrentWorkingDirectory(AnActionEvent e) {
        VirtualFile file = DataKeys.VIRTUAL_FILE.getData(e.getDataContext());
        //VirtualFile folder = file.getParent();

        System.out.println("getCurrentWorkingDirectory returning: "+file.getPath());
        return file.getPath()+"/";
    }

    private String getCQDialogText(String componentName) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<jcr:root xmlns:jcr=\"http://www.jcp.org/jcr/1.0\" xmlns:nt=\"http://www.jcp.org/jcr/nt/1.0\"\n" +
                "	xmlns:sling=\"http://sling.apache.org/jcr/sling/1.0\" jcr:primaryType=\"nt:unstructured\"\n" +
                "	jcr:title=\""+componentName+"\" sling:resourceType=\"cq/gui/components/authoring/dialog\">\n" +
                "	<content jcr:primaryType=\"nt:unstructured\"\n" +
                "		sling:resourceType=\"granite/ui/components/coral/foundation/fixedcolumns\"\n" +
                "		margin=\"{Boolean}true\">\n" +
                "		<items jcr:primaryType=\"nt:unstructured\">\n" +
                "			<column jcr:primaryType=\"nt:unstructured\"\n" +
                "				sling:resourceType=\"granite/ui/components/coral/foundation/container\">\n" +
                "				<items jcr:primaryType=\"nt:unstructured\">\n" +
                "					<text jcr:primaryType=\"nt:unstructured\"\n" +
                "						sling:resourceType=\"granite/ui/components/coral/foundation/form/textfield\"\n" +
                "						emptyText=\"My Text\"\n" +
                "						fieldDescription=\"Add in custom text\"\n" +
                "						fieldLabel=\"Custom text\" name=\"./text\" />\n" +
                "				</items>\n" +
                "			</column>\n" +
                "		</items>\n" +
                "	</content>\n" +
                "</jcr:root>";
    }
}