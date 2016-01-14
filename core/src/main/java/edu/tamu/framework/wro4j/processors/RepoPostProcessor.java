package edu.tamu.framework.wro4j.processors;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.apache.commons.io.IOUtils;

import ro.isdc.wro.model.resource.SupportedResourceType;
import ro.isdc.wro.model.resource.ResourceType;
import ro.isdc.wro.model.resource.processor.ResourcePostProcessor;

@SupportedResourceType(ResourceType.CSS)
public class RepoPostProcessor implements ResourcePostProcessor {
	
	public void process(final Reader reader, final Writer writer) throws IOException {
		//TODO hook into a DB repo to get theme specific SCSS vars

		//add the custom content
		writer.append("/*This filter added these variables to the SCSS:\n\n ");
		writer.append("* $primary: #500000;\n");
		writer.append("* $secondary: #3c0000;\n");
		writer.append("* $linkColor: #337ab7;\n");
		writer.append("* $baseFontSize: 14px;\n");
		writer.append("*/\n\n");
		
		writer.append("$primary: #500000;");
		writer.append("$secondary: #3c0000;");
		writer.append("$linkColor: #337ab7;");
		writer.append("$baseFontSize: 14px;");
		
		//read in the merged SCSS and add it after the custom content 
		writer.append(IOUtils.toString(reader));
		reader.close();
		writer.close();
	}  
}
