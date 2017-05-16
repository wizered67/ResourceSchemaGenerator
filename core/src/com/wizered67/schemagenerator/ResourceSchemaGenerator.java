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

    //private static final String ANIMATION_ATLAS_TYPE = "animationAtlasResource";
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

    private XmlWriter xmlWriter;
    private XmlReader xmlReader;

    private boolean strict = true;

    private Set<String> identifiers;
    private Map<String, Set<String>> categorizedIdentifiers;
    private Set<String> loadSet;

    public ResourceSchemaGenerator(XmlReader xmlReader) {
        this.xmlReader = xmlReader;
        identifiers = new HashSet<String>();
        categorizedIdentifiers = new HashMap<String, Set<String>>();
        loadSet = new HashSet<String>();
    }

    public void generate() {
        try {
            InputStream xmlFile = new FileInputStream(Constants.RESOURCE_DIRECTORIES_FILE);
            Element root = xmlReader.parse(xmlFile);
            String destination = root.getAttribute("schemaDest", "resourceSchema.xsd");
            StringWriter stringWriter = new StringWriter();
            xmlWriter = new XmlWriter(stringWriter);

            writeHeader();
            writeAny();
            collectResources(root);
            collectCharacters();
            collectLoadGroups();
            for (String type : categorizedIdentifiers.keySet()) {
                Set<String> identifiers = categorizedIdentifiers.get(type);
                if (identifiers == null) {
                    identifiers = new HashSet<String>();
                }
                writeIdentifiers(type, identifiers);
            }
            //write everything loadable to resource type.
            writeIdentifiers(RESOURCE_TYPE, loadSet);
            writeEnd();
            xmlWriter.close();
            FileWriter fileWriter = new FileWriter(destination, false);
            fileWriter.write(stringWriter.toString());
            fileWriter.close();
        } catch (IOException io) {
            io.printStackTrace();
        }

    }

    private void collectCharacters() {
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(Constants.CHARACTERS_FILE));
            Element characterRoot = xmlReader.parse(fileInputStream);
            Set<String> identifiers = new HashSet<String>();
            for (int c = 0; c < characterRoot.getChildCount(); c += 1) {
                Element character = characterRoot.getChild(c);
                if (!character.getName().equals("character")) {
                    System.err.println("Invalid element in 'characters' group.");
                }
                String id = character.getAttribute("id");
                if (identifiers.contains(id)) {
                    identifierError(id);
                }
                identifiers.add(id);
            }
            categorizedIdentifiers.put(CHARACTER_TYPE, identifiers);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    private void collectLoadGroups() {
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(Constants.LOADING_FILE));
            Element loadRoot = xmlReader.parse(fileInputStream);
            Set<String> identifiers = new HashSet<String>();
            for (int c = 0; c < loadRoot.getChildCount(); c += 1) {
                Element character = loadRoot.getChild(c);
                if (!character.getName().equals("load")) {
                    System.err.println("Invalid element in loading groups group.");
                }
                String id = character.getAttribute("name");
                if (identifiers.contains(id)) {
                    identifierError(id);
                }
                identifiers.add(id);
            }
            categorizedIdentifiers.put(GROUP_TYPE, identifiers);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    private void writeHeader() {
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

    private void writeAny() {
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

    private void writeEnd() {
        try {
            xmlWriter.pop();
            System.out.println("Finished writing schema.");
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    private void writeIdentifiers(String type, Collection<String> identifiers) {
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

    private void identifierError(String id) {
        System.err.println("Identifier '" + id + "' is already in use.");
    }

    private void simpleType(String name) {
        try {
            xmlWriter.element(SIMPLE_TYPE)
                .attribute("name", name);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    private void simpleType() {
        try {
            xmlWriter.element(SIMPLE_TYPE);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    private void restriction(String base) {
        try {
            xmlWriter.element(RESTRICTION)
                    .attribute("base", base);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    private void enumeration(String value) {
        try {
            xmlWriter.element(ENUMERATION)
                    .attribute("value", value)
                    .pop();
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    private void union(String type1, String type2) {
        try {
            xmlWriter.element(UNION)
                    .attribute("memberTypes", type1 + " " + type2)
                    .pop();
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    private void union(String type) {
        try {
            xmlWriter.element(UNION)
                    .attribute("memberTypes", type);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

}
