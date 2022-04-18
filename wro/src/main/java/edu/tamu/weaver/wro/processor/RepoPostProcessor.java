package edu.tamu.weaver.wro.processor;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;

import edu.tamu.weaver.wro.service.ThemeManager;
import ro.isdc.wro.model.resource.ResourceType;
import ro.isdc.wro.model.resource.SupportedResourceType;
import ro.isdc.wro.model.resource.processor.ResourcePostProcessor;

@SupportedResourceType(ResourceType.CSS)
public class RepoPostProcessor implements ResourcePostProcessor {

    private ThemeManager themeManagerService;

    public RepoPostProcessor(ThemeManager themeManagerService) {
        setThemeManagerService(themeManagerService);
    }

    public void process(final Reader reader, final Writer writer) throws IOException {
        // read in the merged SCSS and add it after the custom SASS variables
        writer.append(getDynamicThemeContent());
        writer.append(IOUtils.toString(reader));
        reader.close();
        writer.close();
    }

    protected String getDynamicThemeContent() {
        HashMap<String,String> themeProperties = (HashMap<String,String>) themeManagerService.getThemeProperties();

        StringBuilder formattedProperties = new StringBuilder();
        StringBuilder formattedComments = new StringBuilder();
        formattedComments.append("/* The ThemeManagerService provided the following SASS vars:\n\n");
        themeProperties.forEach((f,v) -> {
            formattedProperties.append("$" + f + ": " + v + ";\n");
            formattedComments.append("* $" + f + ": " + v + ";\n");
        });
        formattedComments.append("*/\n\n"+formattedProperties);
        return formattedComments.toString();
    }

    protected ThemeManager getThemeManagerService() {
        return themeManagerService;
    }

    protected void setThemeManagerService(ThemeManager themeManagerService) {
        this.themeManagerService = themeManagerService;
    }
}
