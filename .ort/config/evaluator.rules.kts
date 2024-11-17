/*
 * Copyright (C) 2019 The ORT Project Authors (see <https://github.com/oss-review-toolkit/ort/blob/main/NOTICE>)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * License-Filename: LICENSE
 */

/*******************************************************
 * Example OSS Review Toolkit (ORT) .rules.kts file    *
 *                                                     *
 * Note this file only contains example how to write   *
 * rules. It's recommended you consult your own legal  *
 * when writing your own rules.                        *
 *******************************************************/

/**
 * Import the license classifications from license-classifications.yml.
 */

val permissiveLicenses = licenseClassifications.licensesByCategory["permissive"].orEmpty()

val copyleftLicenses = licenseClassifications.licensesByCategory["copyleft"].orEmpty()

val copyleftLimitedLicenses = licenseClassifications.licensesByCategory["copyleft-limited"].orEmpty()

val publicDomainLicenses = licenseClassifications.licensesByCategory["public-domain"].orEmpty()

// The complete set of licenses covered by policy rules.
val handledLicenses = listOf(
    permissiveLicenses,
    publicDomainLicenses,
    copyleftLicenses,
    copyleftLimitedLicenses
).flatten().let {
    it.getDuplicates().let { duplicates ->
        require(duplicates.isEmpty()) {
            "The classifications for the following licenses overlap: $duplicates"
        }
    }

    it.toSet()
}

/**
 * Return the Markdown-formatted text to aid users with resolving violations.
 */
fun PackageRule.howToFixDefault() = """
        A text written in MarkDown to help users resolve policy violations
        which may link to additional resources.
    """.trimIndent()

/**
 * Set of matchers to help keep policy rules easy to understand
 */

fun PackageRule.LicenseRule.isHandled() =
    object : RuleMatcher {
        override val description = "isHandled($license)"

        override fun matches() =
            license in handledLicenses && ("-exception" !in license.toString() || " WITH " in license.toString())
    }

/**
 * Example policy rules
 */

fun RuleSet.unhandledLicenseRule() = packageRule("UNHANDLED_LICENSE") {
    // Do not trigger this rule on packages that have been excluded in the .ort.yml.
    require {
        -isExcluded()
    }

    // Define a rule that is executed for each license of the package.
    licenseRule("UNHANDLED_LICENSE", LicenseView.CONCLUDED_OR_DECLARED_AND_DETECTED) {
        require {
            -isExcluded()
            -isHandled()
        }

        // Throw an error message including guidance how to fix the issue.
        error(
            "The license $license is currently not covered by policy rules. " +
                    "The license was ${licenseSource.name.lowercase()} in package " +
                    "${pkg.metadata.id.toCoordinates()}.",
            howToFixDefault()
        )
    }
}


/**
 * The set of policy rules.
 */
val ruleSet = ruleSet(ortResult, licenseInfoResolver, resolutionProvider) {
    // Rules which get executed for each package:
    unhandledLicenseRule()
}

// Populate the list of policy rule violations to return.
ruleViolations += ruleSet.violations
