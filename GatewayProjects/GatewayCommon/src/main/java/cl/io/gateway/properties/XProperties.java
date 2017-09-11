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
package cl.io.gateway.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

/**
 * Utility class for easier handling of property files. I thank my co-workers
 * who were the creators of this class.
 *
 * @author egacl
 */
public class XProperties extends Properties {

    private static final long serialVersionUID = 1L;

    protected String originalFilename = null;

    public XProperties() {
        this(null);
    }

    public XProperties(Properties defaultProperties) {
        super(defaultProperties);
    }

    /**
     * Returns the value of the key as Integer. Null if there is no value.
     *
     * @param key
     *            key to search
     * @return value as Integer object
     */
    public Integer getInteger(String key) {
        String val = super.getProperty(key);
        return (val == null) ? null : Integer.valueOf(val.trim());
    }

    /**
     * Returns the value of the key as Integer. Default value if there is no value.
     *
     * @param key
     *            key to search
     * @param defaultValue
     *            default value
     * @return value as Integer object
     */
    public Integer getInteger(String key, Integer defaultValue) {
        String val = super.getProperty(key);
        return (val == null || val.length() == 0) ? defaultValue : Integer.valueOf(val.trim());
    }

    /**
     * Returns the value of the key as Long. Null if there is no value.
     *
     * @param key
     *            key to search
     * @return value as Integer object
     */
    public Long getLong(String key) {
        String val = super.getProperty(key);
        return (val == null) ? null : Long.valueOf(val.trim());
    }

    /**
     * Returns the value of the key as Long. Default value if there is no value.
     *
     * @param key
     *            key to search
     * @param defaultValue
     *            default value
     * @return value as Long object
     */
    public Long getLong(String key, Long defaultValue) {
        String val = super.getProperty(key);
        return (val == null || val.length() == 0) ? defaultValue : Long.valueOf(val.trim());
    }

    /**
     * Returns the value of the key as Float. Null if there is no value.
     *
     * @param key
     *            key to search
     * @return value as Float object
     */
    public Float getFloat(String key) {
        String val = super.getProperty(key);
        return (val == null) ? null : Float.valueOf(val.trim());
    }

    /**
     * Returns the value of the key as Float. Default value if there is no value.
     *
     * @param key
     *            key to search
     * @param defaultValue
     *            default value
     * @return value as Float object
     */
    public Float getFloat(String key, Float defaultValue) {
        String val = super.getProperty(key);
        return (val == null || val.length() == 0) ? defaultValue : Float.valueOf(val.trim());
    }

    /**
     * Returns the value of the key as Double. Null if there is no value.
     *
     * @param key
     *            key to search
     * @return value as Double object
     */
    public Double getDouble(String key) {
        String val = super.getProperty(key);
        return (val == null) ? null : Double.valueOf(val.trim());
    }

    /**
     * Returns the value of the key as Double. Default value if there is no value.
     *
     * @param key
     *            key to search
     * @param defaultValue
     *            default value
     * @return value as Double object
     */
    public Double getDouble(String key, Double defaultValue) {
        String val = super.getProperty(key);
        return (val == null || val.length() == 0) ? defaultValue : Double.valueOf(val.trim());
    }

    /**
     * Returns the value of the key as Boolean. Null if there is no value.
     *
     * @param key
     *            key to search
     * @return value as Boolean object
     */
    public Boolean getBoolean(String key) {
        String val = super.getProperty(key);
        return (val == null) ? null : Boolean.valueOf(val.trim());
    }

    /**
     * Returns the value of the key as Boolean. Default value if there is no value.
     *
     * @param key
     *            key to search
     * @param defaultValue
     *            default value
     * @return value as Boolean object
     */
    public Boolean getBoolean(String key, Boolean defaultValue) {
        String val = super.getProperty(key);
        return (val == null || val.length() == 0) ? defaultValue : Boolean.valueOf(val.trim());
    }

    /**
     * Returns the value array for the given property. Assumes comma (,) * as
     * separator.
     *
     * @return String array values
     */
    public String[] getStringArray(String key) {
        String val = super.getProperty(key);
        return val == null ? null : val.split(",");
    }

    /**
     * Returns the value of the key. Fail if there is no value.
     *
     * @param key
     *            key to search
     * @return value as String
     * @throws IllegalArgumentException
     *             If the property is not found
     */
    public final String readMandatoryProperty(final String propertyKey) {
        final String value = this.getProperty(propertyKey);
        if (value == null || value.length() == 0) {
            throw new IllegalArgumentException(this.getOriginalFilename() + ": Missing property: " + propertyKey);
        }
        return value;
    }

    /**
     * Returns the name of the file from which this property object was loaded.
     *
     * @return the name of the file from which this property object was loaded
     */
    public String getOriginalFilename() {
        return this.originalFilename;
    }

    /**
     * Returns the subset of properties beginning with the specified string.
     *
     * @param prefix
     *            filter String
     * @return subset of properties beginning with the specified string
     */
    public XProperties filterProperties(String prefix) {
        Iterator<String> iterator = this.stringPropertyNames().iterator();
        XProperties returnProperties = new XProperties();
        while (iterator.hasNext()) {
            String current = iterator.next();
            if (current.startsWith(prefix))
                returnProperties.put(current, this.getProperty(current));
        }
        return returnProperties;
    }

    /**
     * Gets the list of subsets of properties, according to the given prefix and
     * separator. Example:<br>
     * <br>
     * If you have the following properties:
     *
     * <pre>
     * aaaaaa.bbbbbb.cccccc.iiiiii = valor
     * aaaaaa.bbbbbb.cccccc.eeeeee = valor
     * aaaaaa.bbbbbb.cccccc.ffffff = valor
     * aaaaaa.bbbbbb.dddddd.gggggg = valor
     * aaaaaa.bbbbbb.dddddd.hhhhhh = valor
     * </pre>
     *
     * Call <code>getSubsetList("aaaaaa.bbbbbb", '.')</code> returns this String
     * array:<br>
     *
     * <pre>
     * { "aaaaaa.bbbbbb.cccccc.", "aaaaaa.bbbbbb.dddddd." }
     * </pre>
     *
     * That corresponds to all subsets of aaaaaa.bbbbbb separating the sets by a '.'
     *
     * @param prefix
     *            prefix to filter and get subset
     * @param separator
     *            Character separator between the subsets.
     * @return Array of strings with prefixes corresponding to each subset found.
     */
    public String[] getSubsetList(String prefix, char separator) {
        if (!prefix.isEmpty() && prefix.charAt(prefix.length() - 1) != separator)
            prefix = prefix + separator;
        TreeSet<String> subset = new TreeSet<String>();
        for (String key : stringPropertyNames()) {
            if (key.startsWith(prefix)) {
                int endIndex = key.indexOf(separator, prefix.length());
                if (endIndex < 0)
                    endIndex = key.length() - 1;
                String sub = key.substring(0, endIndex + 1);
                subset.add(sub);
            }
        }
        return subset.toArray(new String[0]);
    }

    /**
     * Gets the property subsets according to the given prefix and separator.<br>
     * <br>
     * This call is equivalent to the following code:
     *
     * <pre>
     * ArrayList<XProperties> subsets = new ArrayList<XProperties>();
     * for(String subsetPrefix : getSubsetList(prefix, separator))
     * {
     *   subsets.add(filterProperties(subsetPrefix));
     * }
     * <br>
     * Where subsets would be the set of properties delivered.
     * </pre>
     *
     * @see #getSubsetList(String, char)
     * @see #filterProperties(String)
     * @param prefix
     *            prefix to filter subset.
     * @param separator
     *            Character separator between the subsets.
     * @return Arrangement with subsets of properties.
     */
    public List<XProperties> getSubsets(String prefix, char separator) {
        ArrayList<XProperties> subsets = new ArrayList<XProperties>();
        for (String subsetPrefix : getSubsetList(prefix, separator)) {
            subsets.add(filterProperties(subsetPrefix));
        }
        return subsets;
    }

    /**
     * Returns the Strings array with all the keys in the property map.
     *
     * @return Strings array with all the keys in the property map
     */
    public String[] getKeysArray() {
        Set<String> keys = this.stringPropertyNames();
        if (keys.isEmpty())
            return new String[0];
        String[] returnArray = new String[keys.size()];
        keys.toArray(returnArray);
        return returnArray;
    }

    /**
     * Returns the strings array of property values.
     *
     * @return strings array of property values
     */
    public String[] getValuesArray() {
        Collection<Object> values = this.values();
        if (values.isEmpty())
            return new String[0];
        String[] returnArray = new String[values.size()];
        values.toArray(returnArray);
        return returnArray;
    }

    /**
     * Load properties from a file or classpath/classloader.
     *
     * @param filePath
     *            property file path (optional)
     * @param fileName
     *            property file name
     * @param classResource
     *            class to get classloader
     * @return Object with properties contained in the file
     * @throws IOException
     *             If the properties file can open for reading
     * @throws FileNotFoundException
     *             If the properties file is not found
     */
    public static XProperties loadFromFileOrClasspath(String filePath, String fileName, Class<?> classResource)
            throws IOException {
        XProperties properties = null;
        if (filePath != null && !filePath.isEmpty()) {
            if (!filePath.endsWith(File.separator))
                filePath += File.separator;
            properties = tryLoadPropertiesFile(filePath + fileName);
        }
        if (properties == null) {
            InputStream propertiesFileStream = classResource.getResourceAsStream(fileName);
            try {
                if (propertiesFileStream == null) {
                    propertiesFileStream = classResource.getClassLoader().getResourceAsStream(fileName);
                }
                if (null != propertiesFileStream) {
                    properties = new XProperties();
                    properties.load(propertiesFileStream);
                }
            } finally {
                if (null != propertiesFileStream) {
                    propertiesFileStream.close();
                }
            }
        }
        if (properties == null) {
            throw new FileNotFoundException("Properties file is not found: " + fileName);
        }
        return properties;
    }

    /**
     * Load properties from a file.
     *
     * @param fileName
     *            file with property listing
     * @return Object with properties contained in the file
     * @throws IOException
     *             If the properties file can open for reading
     * @throws FileNotFoundException
     *             If the properties file is not found
     */
    public static XProperties loadPropertiesFile(String fileName) throws IOException {
        File f = new File(fileName);
        if (!f.exists())
            throw new FileNotFoundException("File " + fileName + " not found");
        if (!f.canRead())
            throw new IOException("File " + fileName + " can not open for reading");
        XProperties props = new XProperties();
        props.originalFilename = fileName;
        FileInputStream fis = new FileInputStream(f);
        try {
            props.load(fis);
        } catch (IOException e) {
            throw e;
        } finally {
            fis.close();
        }
        return props;
    }

    /**
     * Attempts to load properties from a file, if an error occurs returns null.
     *
     * @param fileName
     *            file with property listing
     * @return Object with properties contained in the file or null if an error
     *         occurs
     */
    public static XProperties tryLoadPropertiesFile(String fileName) {
        try {
            File f = new File(fileName);
            if (!f.exists()) {
                return null;
            }
            if (!f.canRead()) {
                return null;
            }
            XProperties props = new XProperties();
            props.originalFilename = fileName;
            FileInputStream fis = new FileInputStream(f);
            try {
                props.load(fis);
            } catch (IOException e) {
                // e.printStackTrace();
            } finally {
                fis.close();
            }
            return props;
        } catch (Exception e) {
            return null;
        }
    }
}
