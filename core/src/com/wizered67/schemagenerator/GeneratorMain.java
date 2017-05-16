package com.wizered67.schemagenerator;

import com.badlogic.gdx.utils.XmlReader;

/**
 * Runs the generator program, first generating new config and then generating schemas.
 * @author Adam Victor
 */
public class GeneratorMain {
    public static void main(String[] args) {
        XmlReader xmlReader = new XmlReader();
        ConfigGenerator configGenerator = new ConfigGenerator(xmlReader);
        configGenerator.generateAll();
        ResourceSchemaGenerator resourceSchemaGenerator = new ResourceSchemaGenerator(xmlReader);
        resourceSchemaGenerator.generate();
    }
}
