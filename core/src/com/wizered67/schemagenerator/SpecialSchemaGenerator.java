package com.wizered67.schemagenerator;

import com.badlogic.gdx.utils.XmlReader;

import java.util.Map;
import java.util.Set;

/**
 * Created by Adam on 5/16/2017.
 */
public interface SpecialSchemaGenerator {
    void generate(Set<String> identifiers, Set<String> loadSet, Map<String, Set<String>> categorizedIdentifiers,
                  String type, XmlReader.Element resourceElement, String directory);
}
