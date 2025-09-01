# Proof of Concept for Access-Policy-Engine

This directory contains a minimal, functional Proof-of-Concept (PoC) for the Access-Policy-Engine.

## What it Demonstrates

This PoC proves the core architectural concept of the project: the ability of a core engine to dynamically load a declarative policy file and use it to filter accessibility scan results. It shows that the engine is **policy-agnostic** and that the "Trojan Horse" strategy is technically feasible.

## Prerequisites

1.  **Java JDK 17** or newer installed.
2.  **Google Chrome** browser installed.
3.  **ChromeDriver** installed and available in your system's PATH. (You can download it from [Google Chrome for Testing](https://googlechromelabs.github.io/chrome-for-testing/))

## How to Run

1.  Navigate to this directory (`/proof-of-concept`) in your terminal.
2.  Execute the Gradle wrapper to run the application with a specific policy.

**To run with the EAA policy:**
```bash
./gradlew run --args="eaa"