package ru.pavel_zhukoff;

import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.net.URL;
import java.util.Map;

public class Page {
    private String body;

    public Page(String body) {
        this.body = body;
    }

    public Page(String templateName, Map<String, Object> data) {
        URL url = Thread
                .currentThread()
                .getContextClassLoader()
                .getResource(Config.getProperty("template.directory") + File.separatorChar + templateName + ".ftl");
        System.out.println(url);
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_29);
        try {
            FileTemplateLoader templateLoader = new FileTemplateLoader(new File(url.getFile()));
            cfg.setTemplateLoader(templateLoader);
            Template template = cfg.getTemplate(templateName + ".ftl");
            Writer out = new StringWriter();
            try {
                template.process(data, out);
                body = out.toString();
            } catch (TemplateException e) {
                System.out.println("TEMPLATE DATA LOAD FAILED");
                e.printStackTrace();
            }
        } catch (IOException e) {
            System.out.println("TEMPLATE NOT FOUND!");
            e.printStackTrace();
        }

    }

    @Override
    public String toString() {
        return body;
    }
}
