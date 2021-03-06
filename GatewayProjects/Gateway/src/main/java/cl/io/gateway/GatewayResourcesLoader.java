/*
 * Copyright 2017 GetSoftware (http://www.getsoftware.cl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cl.io.gateway;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cl.io.gateway.classloader.GatewayClassLoader;
import cl.io.gateway.exception.GatewayInitilizationException;

public class GatewayResourcesLoader {

    private static final Logger logger = LoggerFactory.getLogger(GatewayResourcesLoader.class);

    private static final String LIB = "lib";

    private static final String PROP = "properties";

    private final ConcurrentMap<String, CustomClassLoader> pluginsClassLoaderMap;

    private final ConcurrentMap<String, CustomClassLoader> servicesClassLoaderMap;

    public GatewayResourcesLoader() throws Exception {
        this.pluginsClassLoaderMap = new ConcurrentHashMap<String, CustomClassLoader>();
        this.servicesClassLoaderMap = new ConcurrentHashMap<String, CustomClassLoader>();
    }

    public ClassLoader addPluginClassLoader(String pluginId, File directory) throws Exception {
        logger.info("Getting ready to create classloader for plugin '" + pluginId + "'");
        final CustomClassLoader ccl = this.createClassLoader(pluginId, directory, false);
        this.pluginsClassLoaderMap.put(pluginId, ccl);
        return ccl;
    }

    public ClassLoader addServiceClassLoader(String contextId, File directory) throws Exception {
        logger.info("Getting ready to create classloader for service '" + contextId + "'");
        CustomClassLoader ccl = this.createClassLoader(contextId, directory, true);
        this.servicesClassLoaderMap.put(contextId, ccl);
        return ccl;
    }

    private CustomClassLoader createClassLoader(String id, File directory, boolean searchIntoPlugins) throws Exception {
        String libsPath = directory.getAbsolutePath() + File.separator + LIB;
        File[] allLibs = new File(libsPath).listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() && !pathname.isHidden();
            }
        });
        String propsPath = directory.getAbsolutePath() + File.separator + PROP;
        List<URL> urls = new LinkedList<URL>();
        if (allLibs != null && allLibs.length > 0) {
            for (File f : allLibs) {
                urls.add(f.toURI().toURL());
            }
        }
        if (urls.isEmpty()) {
            throw new GatewayInitilizationException("Resource '" + id + "' has any URL associated");
        }
        return new CustomClassLoader(id, directory.getAbsolutePath(), libsPath, propsPath, urls,
                Thread.currentThread().getContextClassLoader(), searchIntoPlugins ? this : null);
    }

    public CustomClassLoader getServiceContextClassLoader(String contextId) {
        return this.servicesClassLoaderMap.get(contextId);
    }

    public GatewayClassLoader getPluginContextClassLoader(String contextId) {
        return this.pluginsClassLoaderMap.get(contextId);
    }

    public boolean removeServiceClassLoader(String contextId) {
        return this.servicesClassLoaderMap.remove(contextId) != null;
    }

    public Set<String> getContextsId() {
        return new HashSet<String>(this.servicesClassLoaderMap.keySet());
    }

    public Set<String> getPluginsId() {
        return new HashSet<String>(this.pluginsClassLoaderMap.keySet());
    }

    public CustomClassLoader getPluginClassLoader(String pluginId) {
        return this.pluginsClassLoaderMap.get(pluginId);
    }

    URL[] getPluginsURLs() {
        List<URL> urls = new LinkedList<URL>();
        for (CustomClassLoader ccl : this.pluginsClassLoaderMap.values()) {
            urls.addAll(Arrays.asList(ccl.getChildURL()));
        }
        return urls.toArray(new URL[urls.size()]);
    }

    ClassLoader[] getPluginsCL() {
        List<ClassLoader> urls = new LinkedList<ClassLoader>();
        for (CustomClassLoader ccl : this.pluginsClassLoaderMap.values()) {
            urls.add(ccl);
        }
        return urls.toArray(new ClassLoader[urls.size()]);
    }
}
