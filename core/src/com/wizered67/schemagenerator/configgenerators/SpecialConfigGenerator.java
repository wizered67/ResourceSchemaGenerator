package com.wizered67.schemagenerator.configgenerators;

import com.badlogic.gdx.utils.XmlReader;

/**
 * Interface for generating special config specific to a resource type.
 * @author Adam Victor
 */
public interface SpecialConfigGenerator {
    void generateConfig(XmlReader.Element newElement, String directory, String filename, String type);
}
