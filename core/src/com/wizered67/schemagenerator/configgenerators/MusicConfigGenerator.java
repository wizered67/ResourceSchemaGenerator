package com.wizered67.schemagenerator.configgenerators;

import com.badlogic.gdx.utils.XmlReader;

/**
 * Generates config for music elements.
 * @author Adam Victor
 */
public class MusicConfigGenerator implements SpecialConfigGenerator {
    private static final String MUSIC_ELEMENT_NAME = "music";
    private static final String VOLUME_ATTRIBUTE = "volume";
    @Override
    public void generateConfig(XmlReader.Element newElement, String directory, String filename, String type) {
        XmlReader.Element subElement = new XmlReader.Element(MUSIC_ELEMENT_NAME, newElement);
        subElement.setAttribute(VOLUME_ATTRIBUTE, "1");
        newElement.addChild(subElement);
    }
}
