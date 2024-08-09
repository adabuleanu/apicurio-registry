package io.apicurio.registry.rules.compatibility.xsd;

import java.util.Set;

import io.apicurio.registry.util.SchemaUtils;

public class XsdCompatibilityCheckerLibrary {

    private final String oldSchema;
    private final String newSchema;

    public XsdCompatibilityCheckerLibrary(String oldSchema, String newSchema) {
        this.oldSchema = oldSchema;
        this.newSchema = newSchema;
    }

    public boolean validate() {
        try {
            boolean attributesCompatible = checkAttributeContent();
            boolean elementsCompatible = checkElementContent();
            return attributesCompatible && elementsCompatible;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean checkAttributeContent() throws Exception {
        Set<String> oldAttributes = SchemaUtils.getAttributesFromString(oldSchema);
        Set<String> newAttributes = SchemaUtils.getAttributesFromString(newSchema);

        boolean compatible = true;

        // Check that all attributes from the original schema are present in the new
        // schema
        for (String oldAttr : oldAttributes) {
            if (!newAttributes.contains(oldAttr)) {
                System.out.println("Missing attribute or incompatible attribute in new schema: " + oldAttr);
                compatible = false;
            }
        }

        // Check that no required attributes are added in the new schema that weren't in
        // the old schema
        for (String newAttr : newAttributes) {
            if (newAttr.endsWith(":required") && !oldAttributes.contains(newAttr)) {
                System.out.println("New required attribute not present in original schema: " + newAttr);
                compatible = false;
            }
        }

        return compatible;
    }

    private boolean checkElementContent() throws Exception {
        Set<String> oldElements = SchemaUtils.getElementsFromString(oldSchema);
        Set<String> newElements = SchemaUtils.getElementsFromString(newSchema);

        boolean compatible = true;

        // Check that all elements from the original schema are present in the new
        // schema
        for (String oldElem : oldElements) {
            if (!newElements.contains(oldElem)) {
                System.out.println("Missing element or incompatible element in new schema: " + oldElem);
                compatible = false;
            }
        }

        // Check that no required elements are added in the new schema that weren't in
        // the old schema
        for (String newElem : newElements) {
            if (!oldElements.contains(newElem) && newElem.endsWith(":1")) {
                System.out.println("New required element not present in original schema: " + newElem);
                compatible = false;
            }
        }

        return compatible;
    }
}
