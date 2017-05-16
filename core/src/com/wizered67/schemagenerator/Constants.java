package com.wizered67.schemagenerator;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Contains constants used by the whole project.
 * @author Adam Victor
 */
public class Constants {
    public static final String RESOURCE_CONFIG_XML = "Resources.xml";
    public static final String RESOURCE_DIRECTORIES_FILE = "ResourceDirectories.xml";
    public static final String CHARACTERS_FILE = "CharacterDefinitions.xml";
    public static final String LOADING_FILE = "LoadingGroups.xml";

    public static final String RESOURCE_CONFIG_ROOT_NAME = "resources";
    public static final String RESOURCE_ELEMENT_NAME = "resource";
    public static final String RESOURCE_NAME_ATTRIBUTE = "name";
    public static final String RESOURCE_NAME_ATTRIBUTE_INTERNAL = "0" + RESOURCE_NAME_ATTRIBUTE;
    public static final String RESOURCE_IDENTIFIER_ATTRIBUTE = "identifier";
    public static final String RESOURCE_IDENTIFIER_ATTRIBUTE_INTERNAL = "1" + RESOURCE_IDENTIFIER_ATTRIBUTE;

    public static final String ANIMATION_CLASS_NAME = "com.wizered67.game.assets.AnimationTextureAtlas";
    public static final String TEXTURE_CLASS_NAME = "com.badlogic.gdx.graphics.Texture";
    public static final String MUSIC_CLASS_NAME = "com.badlogic.gdx.audio.Music";
    public static final String SOUND_CLASS_NAME = "com.badlogic.gdx.audio.Sound";
    public static final String CONVERSATION_CLASS_NAME = "com.wizered67.game.conversations.Conversation";
    public static final String TEXTURE_ATLAS_CLASS_NAME = "com.badlogic.gdx.graphics.g2d.TextureAtlas";

    public static Set<String> getGroupsFromTextureAtlas(File atlasFile) {
        Set<String> groupNames = new HashSet<String>();
        try {
            Scanner scanner = new Scanner(atlasFile);
            scanner.next(); //skip over filename
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.indexOf(':') < 0 && !line.isEmpty()) { //no colon so it should be the name of an animation
                    groupNames.add(line);
                }
            }
        } catch (IOException io) {
            io.printStackTrace();
        }
        return groupNames;
    }
}
