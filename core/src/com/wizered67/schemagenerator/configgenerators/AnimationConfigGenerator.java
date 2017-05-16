package com.wizered67.schemagenerator.configgenerators;

import com.badlogic.gdx.utils.XmlReader;
import com.wizered67.schemagenerator.Constants;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Generates config elements for animations.
 * @author Adam Victor
 */
public class AnimationConfigGenerator implements SpecialConfigGenerator {
    private static final String ANIMATIONS_TAG_NAME = "animations";
    private static final String ANIMATION_SUB_TAG_NAME = "animation";
    private static final String FLIP_ATTRIBUTE_NAME = "flip";
    private static final String ID_ATTRIBUTE_NAME = "0id";
    private static final String FRAME_DURATION_ATTRIBUTE_NAME = "1frameDuration";
    private static final String PLAYMODE_ATTRIBUTE_NAME = "2playMode";
    private static final String DEFAULT_PLAYMODE = "NORMAL";
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
        File file = new File(directory + "/" + filename);
        Set<String> animationNames = Constants.getGroupsFromTextureAtlas(file);
        XmlReader.Element animationsElement = new XmlReader.Element(ANIMATIONS_TAG_NAME, newElement);
        newElement.addChild(animationsElement);
        animationsElement.setAttribute(FLIP_ATTRIBUTE_NAME, "false");

        for (String animationName : animationNames) {
            XmlReader.Element subElement = new XmlReader.Element(ANIMATION_SUB_TAG_NAME, animationsElement);
            subElement.setAttribute(ID_ATTRIBUTE_NAME, animationName);
            subElement.setAttribute(FRAME_DURATION_ATTRIBUTE_NAME, "1");
            subElement.setAttribute(PLAYMODE_ATTRIBUTE_NAME, DEFAULT_PLAYMODE);
            animationsElement.addChild(subElement);
        }
    }
}
