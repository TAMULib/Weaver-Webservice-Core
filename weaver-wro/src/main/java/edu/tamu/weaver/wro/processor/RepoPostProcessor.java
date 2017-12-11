package edu.tamu.weaver.wro.processor;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.apache.commons.io.IOUtils;

import edu.tamu.weaver.wro.service.ThemeManager;
import ro.isdc.wro.model.resource.ResourceType;
import ro.isdc.wro.model.resource.SupportedResourceType;
import ro.isdc.wro.model.resource.processor.ResourcePostProcessor;

@SupportedResourceType(ResourceType.CSS)
public class RepoPostProcessor implements ResourcePostProcessor {

	private ThemeManager themeManagerService;

	public RepoPostProcessor(ThemeManager themeManagerService) {
		super();
		this.themeManagerService = themeManagerService;
	}

	public void process(final Reader reader, final Writer writer) throws IOException {
		// read in the merged SCSS and add it after the custom content
		String resourceText = "/* The custom PostProcessor fetched the following SASS vars from the ThemeManagerService: */\n\n";
		resourceText += themeManagerService.getFormattedProperties();
		writer.append(resourceText);
		writer.append(IOUtils.toString(reader));
		reader.close();
		writer.close();
	}

}