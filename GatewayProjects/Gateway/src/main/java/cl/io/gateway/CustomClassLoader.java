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

import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import cl.io.gateway.classloader.GatewayClassLoader;

class CustomClassLoader extends ClassLoader implements GatewayClassLoader {

    private final String contextId;

    private final String mainPath;

    private final String libsPath;

    private final String propsPath;

    private final ChildClassLoader childClassLoader;

    public CustomClassLoader(String contextId, String mainPath, String libsPath, String propsPath, List<URL> classpath,
            ClassLoader parentClassLoader, GatewayResourcesLoader grl) {
        super(parentClassLoader);
        this.contextId = contextId;
        this.mainPath = mainPath;
        this.libsPath = libsPath;
        this.propsPath = propsPath;
        URL[] urls = classpath.toArray(new URL[classpath.size()]);
        childClassLoader = new ChildClassLoader(urls, new DetectClass(parentClassLoader), grl);
    }

    @Override
    public ClassLoader getClassLoader() {
        return this;
    }

    @Override
    public String getContextId() {
        return contextId;
    }

    @Override
    public String getMainPath() {
        return mainPath;
    }

    @Override
    public String getLibsPath() {
        return libsPath;
    }

    @Override
    public String getPropsPath() {
        return propsPath;
    }

    @Override
    public URL[] getChildURL() {
        return this.childClassLoader.getURLs();
    }

    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        try {
            return childClassLoader.findClass(name);
        } catch (ClassNotFoundException e) {
            return super.loadClass(name, resolve);
        }
    }

    // @Override
    // public synchronized Class<?> loadClass(String name) throws
    // ClassNotFoundException {
    // try {
    // return childClassLoader.findClass(name);
    // } catch (ClassNotFoundException e) {
    // return super.loadClass(name);
    // }
    // }
    private static class ChildClassLoader extends URLClassLoader {

        private final DetectClass realParent;

        private final GatewayResourcesLoader grl;

        public ChildClassLoader(URL[] urls, DetectClass realParent, GatewayResourcesLoader grl) {
            super(urls, null);
            this.grl = grl;
            this.realParent = realParent;
        }

        @Override
        public Class<?> findClass(String name) throws ClassNotFoundException {
            try {
                Class<?> loaded = super.findLoadedClass(name);
                if (loaded != null) {
                    return loaded;
                }
                return super.findClass(name);
            } catch (ClassNotFoundException e) {
                Class<?> cls = this.findIntoPlugins(name);
                if (cls == null) {
                    return realParent.loadClass(name);
                }
                return cls;
            }
        }

        private Class<?> findIntoPlugins(String name) {
            if (this.grl == null) {
                return null;
            }
            for (ClassLoader ccl : this.grl.getPluginsCL()) {
                try {
                    return ccl.loadClass(name);
                } catch (ClassNotFoundException e) {
                    continue;
                }
            }
            return null;
        }
    }

    private static class DetectClass extends ClassLoader {

        public DetectClass(ClassLoader parent) {
            super(parent);
        }

        @Override
        public Class<?> findClass(String name) throws ClassNotFoundException {
            return super.findClass(name);
        }
    }
}
