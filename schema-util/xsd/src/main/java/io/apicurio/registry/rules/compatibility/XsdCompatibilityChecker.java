/*
 * Copyright 2020 Red Hat
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.apicurio.registry.rules.compatibility;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Map;

import io.apicurio.registry.content.ContentHandle;
import io.apicurio.registry.rules.compatibility.xsd.XsdCompatibilityCheckerLibrary;
import org.jetbrains.annotations.NotNull;

/**
 * @author Ales Justin
 */
public class XsdCompatibilityChecker implements CompatibilityChecker {

    @Override
    public CompatibilityExecutionResult testCompatibility(CompatibilityLevel compatibilityLevel,
            List<ContentHandle> existingArtifacts, ContentHandle proposedArtifact,
            Map<String, ContentHandle> resolvedReferences) {
        requireNonNull(compatibilityLevel, "compatibilityLevel MUST NOT be null");
        requireNonNull(existingArtifacts, "existingArtifacts MUST NOT be null");
        requireNonNull(proposedArtifact, "proposedArtifact MUST NOT be null");

        if (existingArtifacts.isEmpty()) {
            return CompatibilityExecutionResult.compatible();
        }

        String oldSchema = existingArtifacts.get(existingArtifacts.size() - 1).content();
        String newSchema = proposedArtifact.content();

        switch (compatibilityLevel) {
            case BACKWARD: {
                return testBackward(oldSchema, newSchema);
            }
            // case BACKWARD_TRANSITIVE: {
            // return testBackwardTransitive(existingArtifacts, fileAfter);
            // }
            // case FORWARD: {
            // return testForward(fileBefore, fileAfter);
            // }
            // case FORWARD_TRANSITIVE: {
            // return testForwardTransitive(existingArtifacts, fileAfter);
            // }
            // case FULL: {
            // return testFull(fileBefore, fileAfter);
            // }
            // case FULL_TRANSITIVE: {
            // return testFullTransitive(existingArtifacts, fileAfter);
            // }
            default:
                return CompatibilityExecutionResult.compatible();
        }
    }

    @NotNull
    private CompatibilityExecutionResult testBackward(String oldSchema, String newSchema) {
        XsdCompatibilityCheckerLibrary checker = new XsdCompatibilityCheckerLibrary(oldSchema, newSchema);
        if (checker.validate()) {
            return CompatibilityExecutionResult.compatible();
        } else {
            return CompatibilityExecutionResult
                    .incompatible("The new version of the protobuf artifact is not backward compatible.");
        }
    }
}
