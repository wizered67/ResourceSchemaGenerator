package com.wizered67.schemagenerator;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlWriter;
import com.wizered67.schemagenerator.configgenerators.AnimationConfigGenerator;
import com.wizered67.schemagenerator.configgenerators.MusicConfigGenerator;
import com.wizered67.schemagenerator.configgenerators.SpecialConfigGenerator;
import com.wizered67.schemagenerator.configgenerators.TextureConfigGenerator;

import java.io.*;
import java.util.*;

import static com.badlogic.gdx.utils.XmlReader.Element;
/**
 * Created by Adam on 5/15/2017.
 */
public class ConfigGenerator {
    private XmlReader xmlReader;
    private Map<String, SpecialConfigGenerator> specialConfigGenerators;

    public ConfigGenerator(XmlReader xmlReader) {
        this.xmlReader = xmlReader;
        specialConfigGenerators = new HashMap<String, SpecialConfigGenerator>();
        specialConfigGenerators.put(Constants.ANIMATION_CLASS_NAME, new AnimationConfigGenerator());
        specialConfigGenerators.put(Constants.TEXTURES_CLASS_NAME, new TextureConfigGenerator());
        specialConfigGenerators.put(Constants.MUSIC_CLASS_NAME, new MusicConfigGenerator());
    }

    public void generateAll() {
        try {
            Element resourceDirectoriesRoot = xmlReader.parse(new FileInputStream(new File(Constants.RESOURCE_DIRECTORIES_FILE)));
            for (int i = 0; i < resourceDirectoriesRoot.getChildCount(); i++) {
                Set<String> excludeSet = new HashSet<String>();
                Element directory = resourceDirectoriesRoot.getChild(i);
                String name = directory.getAttribute("name");
                String type = directory.getAttribute("type");
                for (int j = 0; j < directory.getChildCount(); j++) {
                    Element exclusion = directory.getChild(j);
                    excludeSet.add(exclusion.getText());
                }
                generateDirectoryConfig(name, type, excludeSet);
            }
        } catch (IOException io) {
            io.printStackTrace();
        }
        System.out.println("Config generated successfully.");
    }

    private void generateDirectoryConfig(String directoryName, String type, Set<String> excludeSet) {
        File directoryFile = new File(directoryName);
        //Get current config root and iterate through child resources, adding each one to a map between filename and config Element.
        Element currentConfigRoot;
        try {
            currentConfigRoot = xmlReader.parse(new FileInputStream(new File(directoryName, Constants.RESOURCE_CONFIG_XML)));
        } catch (IOException io) {
            currentConfigRoot = new Element(Constants.RESOURCE_CONFIG_ROOT_NAME, null);
        }
        Element newConfigRoot = new Element(Constants.RESOURCE_CONFIG_ROOT_NAME, null);
        Map<String, Element> currentConfigElements = new HashMap<String, Element>();
        if (currentConfigRoot != null) {
            for (int i = 0; i < currentConfigRoot.getChildCount(); i++) {
                Element configElement = currentConfigRoot.getChild(i);
                currentConfigElements.put(configElement.getAttribute(Constants.RESOURCE_NAME_ATTRIBUTE), configElement);
            }
        }
        List<Element> newConfigElements = new ArrayList<Element>();
        //Iterate through all files and add config element to list. If there is a current config element for the file,
        //use it. Otherwise generate a new one.
        File[] subFiles = directoryFile.listFiles();
        if (subFiles == null) {
            return;
        }
        for (File subFile : subFiles) {
            String name = subFile.getName();
            if (subFile.isFile() && !name.equals(Constants.RESOURCE_CONFIG_XML) && isValidType(excludeSet, name)) {
                if (currentConfigElements.containsKey(name)) {
                    newConfigElements.add(currentConfigElements.get(name));
                } else {
                    newConfigElements.add(generateConfigElement(directoryName, type, name, newConfigRoot));
                }
            }
        }
        //Sort config in alphabetical order by filename. Add all elements as children to new root and then write new root to file.
        Collections.sort(newConfigElements, new Comparator<Element>() {
            @Override
            public int compare(Element o1, Element o2) {
                String name1;
                String name2;
                if (o1.getAttributes().containsKey(Constants.RESOURCE_NAME_ATTRIBUTE)) {
                    name1 = o1.getAttribute(Constants.RESOURCE_NAME_ATTRIBUTE);
                } else {
                    name1 = o1.getAttribute(Constants.RESOURCE_NAME_ATTRIBUTE_INTERNAL);
                }
                if (o2.getAttributes().containsKey(Constants.RESOURCE_NAME_ATTRIBUTE)) {
                    name2 = o2.getAttribute(Constants.RESOURCE_NAME_ATTRIBUTE);
                } else {
                    name2 = o2.getAttribute(Constants.RESOURCE_NAME_ATTRIBUTE_INTERNAL);
                }
                return name1.compareTo(name2);
            }
        });
        for (Element element : newConfigElements) {
            newConfigRoot.addChild(element);
        }
        XmlWriter xmlWriter;
        //Get XmlWriter to use for writing config file in directory.
        StringWriter stringWriter = new StringWriter();
        xmlWriter = new XmlWriter(stringWriter);
        writeElement(xmlWriter, newConfigRoot);
        try {
            System.out.println(stringWriter.toString());
            FileWriter fileWriter = new FileWriter(new File(directoryFile, Constants.RESOURCE_CONFIG_XML));
            fileWriter.write(stringWriter.toString());
            fileWriter.close();
        } catch (IOException io) {
            io.printStackTrace();
            return;
        }
    }

    private boolean isValidType(Set<String> excludeSet, String fileName) {
        for (String exclusion : excludeSet) {
            if (fileName.endsWith(exclusion)) {
                return false;
            }
        }
        return true;
    }

    private Element generateConfigElement(String directoryName, String type, String fileName, Element newConfigRoot) {
        Element newElement = new Element(Constants.RESOURCE_ELEMENT_NAME, newConfigRoot);
        newElement.setAttribute("0" + Constants.RESOURCE_NAME_ATTRIBUTE, fileName);
        newElement.setAttribute("1" + Constants.RESOURCE_IDENTIFIER_ATTRIBUTE, fileName);
        //add special sub elements for type
        if (specialConfigGenerators.containsKey(type)) {
            specialConfigGenerators.get(type).generateConfig(newElement, directoryName, fileName, type);
        }
        return newElement;
    }

    private void writeElement(XmlWriter xmlWriter, Element element) {
        try {
            xmlWriter.element(element.getName());
            if (element.getText() != null && !element.getText().isEmpty()) {
                xmlWriter.text(element.getText());
            }
            ObjectMap<String, String> attributeMap = element.getAttributes();
            if (attributeMap != null) {
                Array<String> keysArray = attributeMap.keys().toArray();
                keysArray.sort(new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        return o1.compareTo(o2);
                    }
                });
                for (String attributeName : keysArray) {
                    String writeAttributeName = attributeName;
                    if (Character.isDigit(attributeName.charAt(0))) {
                        writeAttributeName = attributeName.substring(1);
                    }
                    xmlWriter.attribute(writeAttributeName, element.getAttribute(attributeName));
                }
            }
            for (int i = 0; i < element.getChildCount(); i++) {
                Element childElement = element.getChild(i);
                writeElement(xmlWriter, childElement);
            }
            xmlWriter.pop();
        } catch (IOException io) {
            io.printStackTrace();
            try {
                xmlWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
