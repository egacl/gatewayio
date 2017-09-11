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

class CustomClassLoader extends ClassLoader {

    private final String serviceId;

    private final String mainPath;

    private final String libsPath;

    private final String propsPath;

    private final ChildClassLoader childClassLoader;

    public CustomClassLoader(String serviceId, String mainPath, String libsPath, String propsPath, List<URL> classpath,
            ClassLoader parentClassLoader) {
        super(parentClassLoader);
        this.serviceId = serviceId;
        this.mainPath = mainPath;
        this.libsPath = libsPath;
        this.propsPath = propsPath;
        URL[] urls = classpath.toArray(new URL[classpath.size()]);
        childClassLoader = new ChildClassLoader(urls, new DetectClass(this.getParent()));
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getMainPath() {
        return mainPath;
    }

    public String getLibsPath() {
        return libsPath;
    }

    public String getPropsPath() {
        return propsPath;
    }

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

    private static class ChildClassLoader extends URLClassLoader {

        private final DetectClass realParent;

        public ChildClassLoader(URL[] urls, DetectClass realParent) {
            super(urls, null);
            this.realParent = realParent;
        }

        @Override
        public Class<?> findClass(String name) throws ClassNotFoundException {
            try {
                Class<?> loaded = super.findLoadedClass(name);
                if (loaded != null)
                    return loaded;
                return super.findClass(name);
            } catch (ClassNotFoundException e) {
                return realParent.loadClass(name);
            }
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
