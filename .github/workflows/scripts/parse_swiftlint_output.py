import re

def parse_swiftlint_output():
    try:
        # Read the output from SwiftLint
        with open("swiftlint_output.txt", "r") as f:
            output = f.read()
    except FileNotFoundError:
        print("The file 'swiftlint_output.txt' could not be found.")
        exit(1)

    # Find the summary message at the end of the output
    match = re.search(r"Done linting! Found (\d+) violations, (\d+) serious in (\d+) files", output)

    if match is None:
        print("Summary message not found in the SwiftLint output.")
        exit(1)

    # Extract values from the summary message
    violations = int(match.group(1))
    serious_violations = int(match.group(2))
    files = int(match.group(3))
    warnings = violations - serious_violations

    # Save the parsed information for later use
    with open("swiftlint_summary.txt", "w") as summary:
        if serious_violations == 0:
            summary.write(f"✅ SwiftLint Succeeded!\n\n")
        else:
            summary.write(f"🛑 SwiftLint Failed!\n\n")
            summary.write(f"⚠️ Warnings: {warnings}\n")
            summary.write(f"🚨 Errors: {serious_violations}\n")
            summary.write(f"📂 Files validated: {files}\n")

if __name__ == "__main__":
    parse_swiftlint_output()