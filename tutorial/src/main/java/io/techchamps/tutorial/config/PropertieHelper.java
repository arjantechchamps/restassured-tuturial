package io.techchamps.tutorial.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertieHelper {

    private static final Properties properties;
    private static final Logger logger = LoggerFactory.getLogger(PropertieHelper.class);
    private static final String DEFAULT_PROPERTIES_FILE = "test_env.properties";
    private static final String ENV_VARIABLE_NAME = "CONFIG_PROPERTIES_FILE";

    static {
        properties = new Properties();
        String propertiesFile = System.getenv(ENV_VARIABLE_NAME);
        if (propertiesFile == null || propertiesFile.isEmpty()) {
            propertiesFile = DEFAULT_PROPERTIES_FILE;
        }

        try (InputStream input = PropertieHelper.class.getClassLoader().getResourceAsStream(propertiesFile)) {
            if (input == null) {
                throw new RuntimeException("Sorry, unable to find " + propertiesFile);
            }
            properties.load(input);
        } catch (IOException ex) {
            logger.error(String.valueOf(ex));
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static int getIntProperty(String key) {
        return Integer.parseInt(properties.getProperty(key));
    }
}
