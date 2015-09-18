package com.onboard.frontend.configuration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.Servlet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.io.Resource;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Created by XingLiang on 2015/4/23.
 */
@Configuration
@ConditionalOnClass({ Servlet.class })
@ConditionalOnWebApplication
public class ThymleafConfiguration {

    @Autowired
    private ThymeleafProperties properties;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Autowired
    private ApplicationContext applicationContext;

    public static final String PRODUCT = "product";
    public static final String DEV = "dev";

    @Value("${profile}")
    private String profile;

    private final static String JS_PLUGIN_CLASSPATH = "/static/js/ng-modules/plugin";
    private final static String TEMPLATE_PLUGIN_CLASSPATH = "/templates/plugin";
    private final static String LESS_PLUGIN_CLASSPATH = "/static/less/plugin";

    @Bean
    @ConditionalOnMissingBean(name = "thymeleafViewResolver")
    @ConditionalOnProperty(name = "spring.thymeleaf.enabled", matchIfMissing = true)
    public ThymeleafViewResolver thymeleafViewResolver() {
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setTemplateEngine(this.templateEngine);
        resolver.setCharacterEncoding(this.properties.getEncoding());
        resolver.setContentType(appendCharset(this.properties.getContentType(), resolver.getCharacterEncoding()));
        resolver.setExcludedViewNames(this.properties.getExcludedViewNames());
        resolver.setViewNames(this.properties.getViewNames());
        setStaticVariables(resolver);
        resolver.setOrder(Ordered.LOWEST_PRECEDENCE - 5);
        return resolver;
    }

    private void setStaticVariables(ThymeleafViewResolver resolver) {
        Map<String, Object> staticVaribales = Maps.newHashMap();
        if (profile.equals(PRODUCT)) {
            staticVaribales.put("headTemplateName", "fragments/build/CommonHTMLHead");
        } else {
            staticVaribales.put("headTemplateName", "fragments/CommonHTMLHead");
        }
        includePluginStatics(staticVaribales);
        resolver.setStaticVariables(staticVaribales);
    }

    private void includePluginStatics(Map<String, Object> staticVaribales) {
        try {
            staticVaribales.put("jsPluginFiles", getAllFileNameByFolder(JS_PLUGIN_CLASSPATH, "js"));
            staticVaribales.put("templatePluginFiles", getAllFileNameByFolder(TEMPLATE_PLUGIN_CLASSPATH, "html"));
            staticVaribales.put("lessPluginFiles", getAllFileNameByFolder(LESS_PLUGIN_CLASSPATH, "less"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<String> getAllFileNameByFolder(String classPath, String postfix) throws IOException {
        Resource[] resources = applicationContext.getResources(String.format("classpath*:**%s**", classPath));
        List<String> result = Lists.newArrayList();
        for (Resource resource : resources) {
            File pluginFolder = resource.getFile();
            if (pluginFolder.exists() && pluginFolder.isDirectory()) {
                File[] files = pluginFolder.listFiles();
                for (File file : files) {
                    String filename = file.getName();
                    if (filename.endsWith(postfix)) {
                        if (postfix.equals("html")) {
                            result.add(String.format("%s/%s", classPath.replace("/templates/", ""),
                                    filename.replace("/templates/", "").replace(".html", "")));
                        } else {
                            result.add(String.format("%s/%s", classPath, filename));
                        }
                    }
                }
            }
        }
        return result;
    }

    private String appendCharset(String type, String charset) {
        if (type.contains("charset=")) {
            return type;
        }
        return type + ";charset=" + charset;
    }

}
