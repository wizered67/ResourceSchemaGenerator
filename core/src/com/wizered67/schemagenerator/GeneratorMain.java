package com.wizered67.schemagenerator;

import com.badlogic.gdx.utils.XmlReader;

/**
 * Created by Adam on 5/15/2017.
 */
public class GeneratorMain {
    public static void main(String[] args) {
        XmlReader xmlReader = new XmlReader();
        ConfigGenerator configGenerator = new ConfigGenerator(xmlReader);
        configGenerator.generateAll();
    }
}
