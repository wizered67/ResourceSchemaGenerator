package com.wizered67.schemagenerator;

import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlWriter;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.badlogic.gdx.utils.XmlReader.Element;

/**
 * Reads Resources.xml file and uses it to generate a schema containing
 * enumerations for resources of each type.
 * @author Adam Victor
 */
public class ResourceSchemaGenerator {
    /*
    private static final String ANIMATION_ATLAS_TYPE = "animationAtlasResource";
    private static final String ANIMATIONS_TYPE = "animationResource";
    private static final String TEXTURES_TYPE = "textureResource";
    private static final String MUSIC_TYPE = "musicResource";
    private static final String SOUNDS_TYPE = "soundResource";
    private static final String CHARACTER_TYPE = "characterType";
    private static final String RESOURCE_TYPE = "resourceType";
    private static final String GROUP_TYPE = "resourceGroupType";
    private static final String CONVERSATIONS_TYPE = "conversationType";

    private static final String SIMPLE_TYPE = "xs:simpleType";
    private static final String RESTRICTION = "xs:restriction";
    private static final String STRING = "xs:string";
    private static final String ENUMERATION = "xs:enumeration";
    private static final String UNION = "xs:union";
    private static final String ANY_TYPE = "anyType";

    private static XmlWriter xmlWriter;

    private static Set<String> identifiers = new HashSet<String>();
    private static boolean strict;

    public static void main(String[] args) {
        try {
            InputStream xmlFile = new FileInputStream("Resources.xml");
            MixedXmlReader xmlReader = new MixedXmlReader();
            Element root = xmlReader.parse(xmlFile);
            strict = root.getBooleanAttribute("strict", false);
            String destination = root.getAttribute("dest", "resourceSchema.xsd");
            FileWriter writer = new FileWriter(destination, false);
            xmlWriter = new XmlWriter(writer);

            writeHeader();
            writeAny();
            writeAnimations(root);
            writeTextures(root);
            writeMusic(root);
            writeSounds(root);
            writeCharacters(root);
            writeConversations();
            writeResources();
            writeGroups(root);
            writeEnd();

            xmlWriter.close();
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    private static void writeHeader() {
        try {
            xmlWriter.element("xs:schema")
                    .attribute("attributeFormDefault", "unqualified")
                    .attribute("elementFormDefault", "qualified")
                    .attribute("xmlns:xs", "http://www.w3.org/2001/XMLSchema");
            System.out.println("Wrote header.");
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    private static void writeAny() {
        try {
            simpleType(ANY_TYPE);
                restriction(STRING);
                xmlWriter.pop();
            xmlWriter.pop();
            System.out.println("Wrote 'any' type.");
        } catch (IOException io) {
            io.printStackTrace();
        }
    }


    private static void writeAnimationNames(Map<String, String> resources) {
        Set<String> animationNames = new HashSet<String>();
        for (String identifier : resources.keySet()) {
            String filename = resources.get(identifier);
            String extension = "";
            int i = filename.lastIndexOf('.');
            if (i > 0) {
                extension = filename.substring(i + 1);
            }
            if (extension.equals("pack")) {
                File file = new File("Animations/" + filename);
                try {
                    Scanner scanner = new Scanner(file);
                    scanner.next(); //skip over filename
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine().trim();
                        if (line.indexOf(':') < 0 && !line.isEmpty()) { //no colon so it should be the name of an animation
                            animationNames.add(identifier + "_" + line);
                        }
                    }
                } catch (IOException io) {
                    io.printStackTrace();
                }
            }
        }
        writeIdentifiers(ANIMATIONS_TYPE, animationNames);
    }

    private static void writeCharacters(Element root) {
        Element tags = root.getChildByName(CHARACTERS_TAG);
        Set<String> identifiers = new HashSet<String>();
        for (int c = 0; c < tags.getChildCount(); c += 1) {
            Element character = tags.getChild(c);
            if (!character.getName().equals("character")) {
                System.err.println("Invalid element in 'characters' group.");
            }
            String id = character.getAttribute("id");
            if (identifiers.contains(id)) {
                identifierError(id);
            }
            identifiers.add(id);
        }
        writeIdentifiers(CHARACTER_TYPE, identifiers);
        System.out.println("Wrote characters.");
    }

    private static void writeEnd() {
        try {
            xmlWriter.pop();
            System.out.println("Finished writing schema.");
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    private static void writeIdentifiers(String type, Collection<String> identifiers) {
        try {
            simpleType(type);
                if (!strict) {
                union(ANY_TYPE);
                    simpleType(); }
                        restriction(STRING);
                            for (String identifier : identifiers) {
                                enumeration(identifier);
                            }
                        xmlWriter.pop();
                if (!strict) {
                    xmlWriter.pop();
                xmlWriter.pop(); }
            xmlWriter.pop();
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    private static void verifyResources(Map<String, String> resources, String directory) {
        for (String identifier : resources.keySet()) {
            if (identifiers.contains(identifier)) {
                identifierError(identifier);
            }
            identifiers.add(identifier);
            String filename = resources.get(identifier);
            File file = new File(directory + "/" + filename);
            if (!file.exists()) {
                System.err.println("Missing resource '" + directory + "/" + filename + "'.");
            }
        }
    }

    private static void verifyResources(Set<String> resources, String directory) {
        for (String identifier : resources) {
            if (identifiers.contains(identifier)) {
                identifierError(identifier);
            }
            identifiers.add(identifier);
            File file = new File(directory + "/" + identifier);
            if (!file.exists()) {
                System.err.println("Missing resource '" + directory + "/" + identifier + "'.");
            }
        }
    }

    private static void identifierError(String id) {
        System.err.println("Identifier '" + id + "' is already in use.");
    }

    private static void simpleType(String name) {
        try {
            xmlWriter.element(SIMPLE_TYPE)
                .attribute("name", name);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    private static void simpleType() {
        try {
            xmlWriter.element(SIMPLE_TYPE);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    private static void restriction(String base) {
        try {
            xmlWriter.element(RESTRICTION)
                    .attribute("base", base);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    private static void enumeration(String value) {
        try {
            xmlWriter.element(ENUMERATION)
                    .attribute("value", value)
                    .pop();
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    private static void union(String type1, String type2) {
        try {
            xmlWriter.element(UNION)
                    .attribute("memberTypes", type1 + " " + type2)
                    .pop();
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    private static void union(String type) {
        try {
            xmlWriter.element(UNION)
                    .attribute("memberTypes", type);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }
    */
}
