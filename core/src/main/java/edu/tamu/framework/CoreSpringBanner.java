package edu.tamu.framework;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.jar.Manifest;

import org.springframework.boot.Banner;
import org.springframework.boot.ansi.AnsiElement;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.core.env.Environment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
public class CoreSpringBanner implements Banner {
	
    private static final String BOOT = " :: 01010111 01100101 01100001 01110110 01100101 01110010 :: ";

    private static final int STRAP_LINE_SIZE = 42;
    
    @Override
    @SuppressWarnings("deprecation")
    public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {
    	
    	ResourceLoader resourceLoader = new DefaultResourceLoader();
    	
    	try (
			InputStream bannerInputStream = resourceLoader.getResource("classpath:config/banner.txt").getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(bannerInputStream, Charset.forName("UTF-8"));
			BufferedReader bufferReader = new BufferedReader(inputStreamReader);
		) {
	    	String line;
	    	while ((line = bufferReader.readLine()) != null) {
	    		 out.println(line);
	        }
    	} catch (IOException e) {
			e.printStackTrace();
		}
        
        String version = this.getClass().getPackage().getImplementationVersion();
        
        version = environment.getProperty("info.build.version");
        
        // shouldn't ever be null, but just in case get it from the manifest
        if (version == null) {           
            Manifest manifest = getManifest(this.getClass());            
            version = manifest.getMainAttributes().getValue("Implementation-Version");
        }
        
        version = (version == null ? "" : " (v" + version + ")");
        String padding = "";
        while (padding.length() < STRAP_LINE_SIZE - (version.length() + BOOT.length())) {
            padding += " ";
        }
        
        out.println(AnsiOutput.toString(AnsiElement.GREEN, BOOT, AnsiElement.DEFAULT, padding, AnsiElement.FAINT, version));
        out.println();
    }
    
    private static Manifest getManifest(Class<?> clz) {
        String resource = "/" + clz.getName().replace(".", "/") + ".class";
        String fullPath = clz.getResource(resource).toString();
        String archivePath = fullPath.substring(0, fullPath.length() - resource.length());
        if (archivePath.endsWith("\\WEB-INF\\classes") || archivePath.endsWith("/WEB-INF/classes")) {
            archivePath = archivePath.substring(0, archivePath.length() - "/WEB-INF/classes".length()); // Required for war/jar files
        }

        try (InputStream input = new URL(archivePath + "/META-INF/MANIFEST.MF").openStream()) {
            return new Manifest(input);
        } catch (Exception e) {
            throw new RuntimeException("Loading MANIFEST for class " + clz + " failed!", e);
        }
    }
    
   
}