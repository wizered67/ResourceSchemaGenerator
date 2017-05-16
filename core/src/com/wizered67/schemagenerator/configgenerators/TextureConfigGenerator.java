package com.wizered67.schemagenerator.configgenerators;

import com.badlogic.gdx.utils.XmlReader;

/**
 * Generates Texture specific config.
 * @author Adam Victor
 */
public class TextureConfigGenerator implements SpecialConfigGenerator {
    private static final String TEXTURE_ELEMENT_NAME = "texture";
    private static final String GEN_MIP_MAPS_ATTRIBUTE = "0genMipMaps";
    private static final String MIN_FILTER_ATTRIBUTE = "1minFilter";
    private static final String MAG_FILTER_ATTRIBUTE = "2magFilter";
    private static final String WRAP_U_ATTRIBUTE = "3wrapU";
    private static final String WRAP_V_ATTRIBUTE = "4wrapV";
    private static final String DEFAULT_FILTER = "Nearest";
    private static final String DEFAULT_WRAP = "ClampToEdge";
    @Override
    public void generateConfig(XmlReader.Element newElement, String directory, String filename, String type) {
        XmlReader.Element subElement = new XmlReader.Element(TEXTURE_ELEMENT_NAME, newElement);
        subElement.setAttribute(GEN_MIP_MAPS_ATTRIBUTE, "false");
        subElement.setAttribute(MIN_FILTER_ATTRIBUTE, DEFAULT_FILTER);
        subElement.setAttribute(MAG_FILTER_ATTRIBUTE, DEFAULT_FILTER);
        subElement.setAttribute(WRAP_U_ATTRIBUTE, DEFAULT_WRAP);
        subElement.setAttribute(WRAP_V_ATTRIBUTE, DEFAULT_WRAP);
        newElement.addChild(subElement);
    }
}
