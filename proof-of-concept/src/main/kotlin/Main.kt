import com.deque.html.axecore.results.Results
import com.deque.html.axecore.selenium.AxeBuilder
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileInputStream

// A simple data class to hold the loaded policy details.
data class AccessibilityPolicy(val name: String, val description: String, val rules: List<String>)

fun main(args: Array<String>) {
    // Check if the policy name argument was provided.
    if (args.isEmpty()) {
        println("Error: Please provide a policy name as an argument (e.g., eaa or section508).")
        return
    }
    val policyName = args[0]

    // Select the policy file based on the argument.
    val policyFile = when (policyName.lowercase()) {
        "eaa" -> File("src/main/resources/eaa-poc-policy.yml")
        "section508" -> File("src/main/resources/section-508-poc.yml")
        else -> {
            println("Error: Unknown policy '$policyName'. Available options: eaa, section508.")
            return
        }
    }

    println("--- Access-Policy-Engine PoC ---")
    println("Running analysis for policy: $policyName")

    // 1. Load and parse the policy from the YAML file.
    val policy = loadPolicy(policyFile)
    println("Loaded policy: '${policy.name}'. Rules to check: ${policy.rules}")

    // Configure Chrome to run in headless mode (without a UI).
    val options = ChromeOptions().addArguments("--headless")
    val driver = ChromeDriver(options)

    try {
        // 2. Run the axe-core scan on the local test HTML file.
        val testFile = File("src/main/resources/test-page.html")
        val fullScanResults = runAxeScan(driver, testFile.toURI().toString())
        println("Full scan completed. Found ${fullScanResults.violations.size} total violations.")

        // 3. Filter the scan results according to the loaded policy.
        val filteredViolations = fullScanResults.violations.filter { violation ->
            policy.rules.contains(violation.id)
        }

        println("\n--- Compliance Results for '${policy.name}' policy ---")

        if (filteredViolations.isEmpty()) {
            println("✅ COMPLIANT! No violations defined in the policy were found.")
        } else {
            println("❌ NON-COMPLIANT! Found ${filteredViolations.size} violations defined in the policy:")
            filteredViolations.forEachIndexed { index, violation ->
                println("\nViolation #${index + 1}:")
                println("  Rule ID: ${violation.id}")
                println("  Description: ${violation.description}")
                println("  Help: ${violation.help}")
                println("  Help URL: ${violation.helpUrl}")
            }
        }
        println("\n-------------------------------------------------")

    } finally {
        // Always close the driver to free up resources.
        driver.quit()
    }
}

// A function to load and parse the policy file.
@Suppress("UNCHECKED_CAST")
fun loadPolicy(file: File): AccessibilityPolicy {
    val yaml = Yaml()
    val inputStream = FileInputStream(file)
    val data: Map<String, Map<String, Any>> = yaml.load(inputStream)
    val policyData = data["policy"] ?: throw IllegalArgumentException("Missing 'policy' key in YAML file")

    return AccessibilityPolicy(
        name = policyData["name"] as String,
        description = policyData["description"] as String,
        rules = policyData["rules"] as List<String>
    )
}

// A function to run the axe-core scan.
fun runAxeScan(driver: ChromeDriver, url: String): Results {
    driver.get(url)
    val axeBuilder = AxeBuilder()
    return axeBuilder.analyze(driver)
}