package com.wizered67.schemagenerator.configgenerators;

import com.badlogic.gdx.utils.XmlReader;
import com.wizered67.schemagenerator.Constants;

/**
 * Used to generate special config for TextureAtlases -- in this case just changes identifier to not include .pack.
 * @author Adam Victor
 */
public class TextureAtlasConfigGenerator implements SpecialConfigGenerator {
    @Override
    public void generateConfig(XmlReader.Element newElement, String directory, String filename, String type) {
        if (!filename.endsWith(".pack")) {
            return;
        }
        String filenameWithoutExtension = filename.substring(0, filename.indexOf(".pack"));
        if (newElement.getAttributes().containsKey(Constants.RESOURCE_IDENTIFIER_ATTRIBUTE_INTERNAL)) {
            newElement.setAttribute(Constants.RESOURCE_IDENTIFIER_ATTRIBUTE_INTERNAL, filenameWithoutExtension);
        } else {
            newElement.setAttribute(Constants.RESOURCE_IDENTIFIER_ATTRIBUTE, filenameWithoutExtension);
        }
    }
}
