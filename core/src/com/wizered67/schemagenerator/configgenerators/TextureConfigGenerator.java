package com.wizered67.schemagenerator.configgenerators;

import com.badlogic.gdx.utils.XmlReader;
import com.wizered67.schemagenerator.ConfigGenerator;

/**
 * Generates Texture specific config.
 * @author Adam Victor
 */
public class TextureConfigGenerator implements SpecialConfigGenerator {
    private static final String TEXTURE_ELEMENT_NAME = "texture";
    private static final String GEN_MIP_MAPS_ATTRIBUTE = "genMipMaps";
    private static final String MIN_FILTER_ATTRIBUTE = "minFilter";
    private static final String MAG_FILTER_ATTRIBUTE = "magFilter";
    private static final String WRAP_U_ATTRIBUTE = "wrapU";
    private static final String WRAP_V_ATTRIBUTE = "wrapV";
    private static final String DEFAULT_FILTER = "Nearest";
    private static final String DEFAULT_WRAP = "ClampToEdge";
    private static final String[] TEXTURE_ATTRIBUTE_ORDERING = new String[] {GEN_MIP_MAPS_ATTRIBUTE, MIN_FILTER_ATTRIBUTE,
            MAG_FILTER_ATTRIBUTE, WRAP_U_ATTRIBUTE, WRAP_V_ATTRIBUTE};

    public TextureConfigGenerator(ConfigGenerator configGenerator) {
        configGenerator.makeElementPriorityMap(TEXTURE_ELEMENT_NAME, TEXTURE_ATTRIBUTE_ORDERING);
    }

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
